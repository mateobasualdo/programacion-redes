import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Locale;

/**
 * Servidor TCP secuencial (sin hilos): espera activa en accept() y atiende un unico cliente a la vez.
 * Uso: java Servidor [puerto]
 */
public class Servidor {

    private static final Logger LOG = new Logger("SERVIDOR");
    private static final int PUERTO_DEFECTO = 5000;
    private static final int MAX_COMPROBANTE = 5 * 1024 * 1024;
    private static final Path CARPETA_RECIBIDOS = Paths.get("recibidos");

    // Arreglos precargados para las pruebas (nombre <-> cantidad)
    private static final String[] PRODUCTOS = {"mouse", "teclado", "monitor", "notebook", "auriculares", "webcam"};
    private static final int[] CANTIDADES = {42, 17, 8, 5, 23, 0};

    // Usuarios de prueba
    private static final String USUARIO_OK = "cliente";
    private static final String CLAVE_OK = "1234";

    // Frases precargadas del chatbot
    private static final String MENU_SOPORTE =
            "Menu de Soporte: 1. Estado de Envio | 2. Devoluciones | 3. Reclamos. Por favor, introduzca un comando valido.";
    private static final String AYUDA =
            "Preguntas base guiadas:\n"
            + "  1. Estado de Envio  -> escriba '1'\n"
            + "  2. Devoluciones     -> escriba '2'\n"
            + "  3. Reclamos         -> escriba '3'\n"
            + "  4. Medios de pago   -> escriba 'pago'\n"
            + "Comandos: autenticar | ayuda | ver stock <producto> | subir comprobante <ruta> | ver historial de chat | salir";

    // Estado de la sesion del cliente actual
    private static boolean autenticado;
    private static String usuario;
    private static String nombreCliente;

    public static void main(String[] args) {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO_DEFECTO;
        LOG.separador("SERVIDOR E-COMMERCE  |  TCP puerto " + puerto + "  |  modo secuencial (sin hilos)");
        try (ServerSocket ss = new ServerSocket(puerto)) {
            LOG.log(Logger.Tipo.OK, "Servidor iniciado. Esperando clientes en el puerto " + puerto + "...");
            LOG.log(Logger.Tipo.INFO, "Stock precargado: " + resumenStock());
            while (true) {
                Socket s = ss.accept();
                atender(s);
                LOG.log(Logger.Tipo.INFO, "Volviendo a espera activa...");
            }
        } catch (IOException e) {
            LOG.log(Logger.Tipo.ERROR, "No se pudo operar el servidor: " + e.getMessage());
        }
    }

    private static void atender(Socket socket) {
        String remoto = socket.getRemoteSocketAddress().toString();
        autenticado = false;
        usuario = null;
        nombreCliente = null;
        LOG.log(Logger.Tipo.OK, "Conexion exitosa desde " + remoto);
        try (Canal canal = new Canal(socket, LOG)) {
            while (true) {
                String msg = canal.recibir();
                LOG.log(Logger.Tipo.RECIBIDO, "[" + tipoDe(msg) + "] " + descripcion(msg));
                if (msg.equals("BYE")) {
                    canal.enviar("OK|Hasta pronto. Cerrando conexion.");
                    LOG.log(Logger.Tipo.ENVIADO, "[OK] Hasta pronto. Cerrando conexion.");
                    break;
                }
                String respuesta = procesar(msg);
                canal.enviar(respuesta);
                LOG.log(respuesta.startsWith("ERR") ? Logger.Tipo.ERROR : Logger.Tipo.ENVIADO,
                        "[" + tipoDe(respuesta) + "] " + respuesta.substring(respuesta.indexOf('|') + 1));
            }
            LOG.log(Logger.Tipo.AVISO, "Cliente " + remoto + " finalizo la sesion (comando salir).");
        } catch (EOFException | SocketException e) {
            LOG.log(Logger.Tipo.ERROR, "Desconexion inesperada de " + remoto + ": " + e.getMessage());
        } catch (IOException e) {
            LOG.log(Logger.Tipo.ERROR, "Error de comunicacion con " + remoto + ": " + e.getMessage());
        } finally {
            LOG.log(Logger.Tipo.INFO, "Streams y socket de " + remoto + " cerrados.");
        }
    }

    // ---------- Protocolo: TIPO|payload ----------

    private static String procesar(String msg) {
        String[] p = msg.split("\\|", 3);
        switch (p[0]) {
            case "MSG":   return "BOT|" + chatbot(p.length > 1 ? p[1] : "");
            case "HELP":  return "OK|" + AYUDA;
            case "AUTH":  return autenticar(p);
            case "STOCK": return stock(p.length > 1 ? p[1] : "");
            case "UPLOAD": return subir(p);
            default:      return "ERR|Comando desconocido: " + p[0];
        }
    }

    private static String autenticar(String[] p) {
        if (p.length < 3) return "ERR|Formato de autenticacion invalido.";
        if (USUARIO_OK.equals(p[1]) && CLAVE_OK.equals(p[2])) {
            autenticado = true;
            usuario = p[1];
            return "OK|Autenticacion exitosa. Bienvenido, " + usuario + ".";
        }
        autenticado = false;
        return "ERR|Credenciales invalidas.";
    }

    private static String stock(String producto) {
        if (!autenticado) return "ERR|Acceso denegado. Use el comando 'autenticar' primero.";
        String buscado = producto.trim().toLowerCase(Locale.ROOT);
        for (int i = 0; i < PRODUCTOS.length; i++) {
            if (PRODUCTOS[i].equals(buscado)) {
                return CANTIDADES[i] > 0
                        ? "OK|Producto '" + PRODUCTOS[i] + "': " + CANTIDADES[i] + " unidades en stock."
                        : "OK|Producto '" + PRODUCTOS[i] + "': SIN STOCK (0 unidades).";
            }
        }
        return "ERR|Producto '" + producto + "' no encontrado. Disponibles: " + String.join(", ", PRODUCTOS);
    }

    private static String subir(String[] p) {
        if (!autenticado) return "ERR|Acceso denegado. Use el comando 'autenticar' primero.";
        if (p.length < 3) return "ERR|Formato de subida invalido.";
        try {
            String nombre = Paths.get(p[1]).getFileName().toString(); // evita path traversal
            byte[] datos = Base64.getDecoder().decode(p[2]);
            if (datos.length > MAX_COMPROBANTE) return "ERR|El archivo supera el maximo de 5 MB.";
            Files.createDirectories(CARPETA_RECIBIDOS);
            Path destino = CARPETA_RECIBIDOS.resolve(nombre);
            Files.write(destino, datos);
            return "OK|Comprobante '" + nombre + "' recibido (" + datos.length + " bytes) y guardado en " + destino + ".";
        } catch (IOException | IllegalArgumentException e) {
            return "ERR|No se pudo guardar el comprobante: " + e.getMessage();
        }
    }

    private static String chatbot(String texto) {
        String t = texto.trim().toLowerCase(Locale.ROOT);
        int i = t.indexOf("mi nombre es ");
        if (i >= 0) {
            nombreCliente = texto.trim().substring(i + "mi nombre es ".length()).trim();
            return "Hola " + nombreCliente + ", bienvenido a nuestro soporte de e-commerce. "
                    + "Escriba 'ayuda' para ver las preguntas base.";
        }
        if (t.contains("problema") || t.contains("pedido")) return MENU_SOPORTE;
        if (t.equals("1") || t.contains("envio"))
            return "Estado de Envio: ingrese su numero de pedido; los envios demoran de 3 a 5 dias habiles.";
        if (t.equals("2") || t.contains("devol"))
            return "Devoluciones: dispone de 30 dias desde la entrega. Suba su comprobante con 'subir comprobante <ruta>'.";
        if (t.equals("3") || t.contains("reclamo"))
            return "Reclamos: lamentamos el inconveniente. Un agente se contactara en menos de 24 horas.";
        if (t.contains("pago") || t.contains("precio"))
            return "Medios de pago: tarjeta de credito, debito, transferencia bancaria y billeteras virtuales.";
        if (t.contains("gracias")) return "A usted por contactarnos. Que tenga un excelente dia.";
        if (t.contains("hola")) return "Hola, en que podemos ayudarle? Escriba 'ayuda' para ver las opciones.";
        return "No entendi su consulta. " + MENU_SOPORTE;
    }

    // ---------- Utilidades de log ----------

    private static String tipoDe(String msg) {
        int i = msg.indexOf('|');
        return i < 0 ? msg : msg.substring(0, i);
    }

    /** Descripcion legible; no vuelca el contenido base64 de archivos. */
    private static String descripcion(String msg) {
        String[] p = msg.split("\\|", 3);
        switch (p[0]) {
            case "AUTH":   return "autenticar usuario=" + (p.length > 1 ? p[1] : "?") + " clave=****";
            case "UPLOAD": return "subir comprobante archivo=" + (p.length > 1 ? p[1] : "?")
                    + " (" + (p.length > 2 ? p[2].length() : 0) + " chars base64)";
            case "STOCK":  return "ver stock producto=" + (p.length > 1 ? p[1] : "");
            case "HELP":   return "ayuda";
            case "BYE":    return "salir";
            default:       return p.length > 1 ? p[1] : msg;
        }
    }

    private static String resumenStock() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PRODUCTOS.length; i++) {
            sb.append(i > 0 ? ", " : "").append(PRODUCTOS[i]).append('=').append(CANTIDADES[i]);
        }
        return sb.toString();
    }
}
