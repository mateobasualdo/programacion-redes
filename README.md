# programacion-redes

Trabajos de programacion sobre redes - Mateo Basualdo

---

# Chatbot E-commerce - Sockets TCP (Java, sin hilos)

Archivos en `src/`: `Servidor`, `Cliente`, `Canal` (socket + cifrado), `Seguridad` (AES-256/CBC), `Logger` (ANSI).

## Compilar y ejecutar
```
javac -encoding UTF-8 -d out src/*.java
java -cp out Servidor 5000                                      # PC 1
java -cp out Cliente <IP_DEL_SERVIDOR> 5000 "Nombre Apellido"   # PC 2
```
Usar Windows Terminal / PowerShell (soportan colores ANSI). Abrir el puerto 5000 en el firewall del servidor.

## Comandos del cliente
`autenticar` (usuario `cliente`, clave `1234`) | `ayuda` | `ver stock <producto>` | `subir comprobante <ruta>` | `ver historial de chat` | `salir`

Productos: mouse, teclado, monitor, notebook, auriculares, webcam. `ver stock` y `subir comprobante` requieren autenticarse.
Los comprobantes se guardan en `recibidos/` del servidor.

