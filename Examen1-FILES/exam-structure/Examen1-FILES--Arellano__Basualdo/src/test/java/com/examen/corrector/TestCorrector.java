package com.examen.corrector;

import com.examen.Estandarizador;
import com.examen.GestorPartidas;
import com.examen.Partida;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de correccion automatica para el Examen1-FILES (10 puntos).
 * <p>
 * Todos los tests trabajan en directorios temporales (@TempDir)
 * para no modificar ningun archivo del proyecto del alumno.
 * <p>
 * Cubre las 4 opciones del menu:
 *   1 - Mostrar todas las partidas
 *   2 - Eliminar 1 partida
 *   3 - Ver mes con mayor cantidad de victorias
 *   4 - Salir (guarda datos)
 * <p>
 * INSTRUCCIONES:
 * 1. Colocar esta clase en el proyecto del alumno
 * 2. Ejecutar: mvn test
 * 3. Revisar resultados en consola
 */
public class TestCorrector {

    @TempDir
    Path tempDir;

    private Path datOriginal;
    private Path csvEsperado;

    @BeforeEach
    void setUp() throws IOException {
        datOriginal = tempDir.resolve("juegos.dat");
        csvEsperado = tempDir.resolve("juegos.csv");

        // 10 partidas de prueba
        // Mes 04: 3 victorias  |  Mes 05: 1 victoria  |  Mes 06: 1 victoria
        // Mes 07: 1 victoria   |  Mes 08: 0 victorias
        String contenido = "date+hasWon+isFirstTower+isFirstBlood+kills+deaths+assists\n"
                + "05/04+1+0+1+4+7+5\n"
                + "12/04+1+0+1+6+11+6\n"
                + "20/04+1+0+0+8+3+10\n"
                + "03/05+0+1+0+2+9+3\n"
                + "15/05+1+0+1+5+6+8\n"
                + "10/06+1+0+1+7+4+9\n"
                + "22/06+0+1+0+3+8+4\n"
                + "05/07+1+0+1+6+3+11\n"
                + "18/07+0+0+0+2+10+3\n"
                + "01/08+0+0+1+3+9+4\n";
        Files.writeString(datOriginal, contenido);
    }

    // ============================================================
    //  OPCION 0 - Estandarizacion (previa al menu)
    // ============================================================

    @Test
    @DisplayName("[1pt] Estandarizador crea archivo juegos.csv")
    void testEstandarizadorCreaCsv() {
        Estandarizador est = new Estandarizador();
        est.estandarizar(datOriginal.toString());

        assertTrue(Files.exists(csvEsperado),
                "Debe existir juegos.csv en la misma ubicacion que el .dat original");
    }

    @Test
    @DisplayName("[1pt] Estandarizador elimina archivo .dat original")
    void testEstandarizadorBorraOriginal() {
        Estandarizador est = new Estandarizador();
        est.estandarizar(datOriginal.toString());

        assertFalse(Files.exists(datOriginal),
                "El archivo .dat original debe ser eliminado");
    }

    @Test
    @DisplayName("[1pt] Estandarizador usa separador ' ; ' y sin '+'")
    void testEstandarizadorSeparaCorrectamente() throws IOException {
        Estandarizador est = new Estandarizador();
        est.estandarizar(datOriginal.toString());

        List<String> lineas = Files.readAllLines(csvEsperado);
        assertTrue(lineas.size() > 1, "El CSV debe tener encabezados + al menos 1 linea");

        for (int i = 0; i < lineas.size(); i++) {
            assertTrue(lineas.get(i).contains(";"),
                    "Linea " + (i + 1) + " debe contener ' ; ' como separador");
            assertFalse(lineas.get(i).contains("+"),
                    "Linea " + (i + 1) + " NO debe contener el caracter '+'");
            assertTrue(lineas.get(i).split(";").length >= 7,
                    "Linea " + (i + 1) + " debe tener al menos 7 campos separados por ' ; '");
        }
    }

    // ============================================================
    //  OPCION 1 - Mostrar todas las partidas
    // ============================================================

    @Test
    @DisplayName("[1pt] GestorPartidas carga la cantidad correcta de partidas")
    void testCargaCantidadCorrecta() throws IOException {
        String csv = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n"
                + "05/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5\n"
                + "12/04 ; 1 ; 0 ; 1 ; 6 ; 11 ; 6\n";
        Files.writeString(csvEsperado, csv);

        GestorPartidas gestor = new GestorPartidas();
        gestor.cargar(csvEsperado.toString());

        assertEquals(2, gestor.cantidadPartidas(),
                "Debe cargar 2 partidas ignorando la linea de encabezados");
    }

    @Test
    @DisplayName("[1pt] GestorPartidas carga cada campo con el valor correcto")
    void testCargaValoresCorrectos() throws IOException {
        String csv = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n"
                + "05/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5\n"
                + "20/04 ; 0 ; 1 ; 0 ; 10 ; 2 ; 8\n";
        Files.writeString(csvEsperado, csv);

        GestorPartidas gestor = new GestorPartidas();
        gestor.cargar(csvEsperado.toString());
        List<Partida> lista = gestor.getPartidas();

        assertEquals(2, lista.size(), "Debe haber 2 partidas cargadas");

        // Primera partida
        Partida p1 = lista.get(0);
        assertAll("Valores de la primera partida",
                () -> assertEquals("05/04", p1.getFecha(), "Fecha incorrecta"),
                () -> assertTrue(p1.isGano(), "Gano debe ser true (1)"),
                () -> assertFalse(p1.isPrimerTorreta(), "PrimerTorreta debe ser false (0)"),
                () -> assertTrue(p1.isPrimeraSangre(), "PrimeraSangre debe ser true (1)"),
                () -> assertEquals(4, p1.getAsesinatos(), "Asesinatos incorrectos"),
                () -> assertEquals(7, p1.getMuertes(), "Muertes incorrectas"),
                () -> assertEquals(5, p1.getAsistencias(), "Asistencias incorrectas")
        );

        // Segunda partida
        Partida p2 = lista.get(1);
        assertAll("Valores de la segunda partida",
                () -> assertEquals("20/04", p2.getFecha()),
                () -> assertFalse(p2.isGano()),
                () -> assertTrue(p2.isPrimerTorreta()),
                () -> assertFalse(p2.isPrimeraSangre()),
                () -> assertEquals(10, p2.getAsesinatos()),
                () -> assertEquals(2, p2.getMuertes()),
                () -> assertEquals(8, p2.getAsistencias())
        );
    }

    @Test
    @DisplayName("[0.5pt] mostrarTodos no lanza excepcion con datos cargados")
    void testMostrarTodosSinExcepcion() throws IOException {
        String csv = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n"
                + "05/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5\n";
        Files.writeString(csvEsperado, csv);

        GestorPartidas gestor = new GestorPartidas();
        gestor.cargar(csvEsperado.toString());

        assertDoesNotThrow(() -> gestor.mostrarTodos(),
                "mostrarTodos() no debe lanzar ninguna excepcion");
    }

    // ============================================================
    //  OPCION 2 - Eliminar una partida
    // ============================================================

    @Test
    @DisplayName("[1pt] eliminar reduce la cantidad de partidas")
    void testEliminarPartida() throws IOException {
        String csv = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n"
                + "05/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5\n"
                + "12/04 ; 1 ; 0 ; 1 ; 6 ; 11 ; 6\n"
                + "20/04 ; 1 ; 0 ; 0 ; 8 ; 3 ; 10\n";
        Files.writeString(csvEsperado, csv);

        GestorPartidas gestor = new GestorPartidas();
        gestor.cargar(csvEsperado.toString());
        assertEquals(3, gestor.cantidadPartidas(), "Deben cargarse 3 partidas");

        gestor.eliminar(1);
        assertEquals(2, gestor.cantidadPartidas(),
                "Despues de eliminar el indice 1 debe haber 2 partidas");

        // Verificar que se elimino la correcta (la del medio)
        assertEquals("05/04", gestor.getPartidas().get(0).getFecha(),
                "La primera partida debe ser la original (05/04)");
        assertEquals("20/04", gestor.getPartidas().get(1).getFecha(),
                "La segunda partida debe ser la que estaba en indice 2 (20/04)");
    }

    // ============================================================
    //  OPCION 3 - Ver mes con mayor cantidad de victorias
    // ============================================================

    @Test
    @DisplayName("[2pt] mesMasVictorias devuelve el mes correcto")
    void testMesMasVictorias() throws IOException {
        String csv = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n"
                + "05/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5\n"
                + "12/04 ; 1 ; 0 ; 1 ; 6 ; 11 ; 6\n"
                + "20/04 ; 1 ; 0 ; 0 ; 8 ; 3 ; 10\n"
                + "03/05 ; 0 ; 1 ; 0 ; 2 ; 9 ; 3\n"
                + "15/05 ; 1 ; 0 ; 1 ; 5 ; 6 ; 8\n"
                + "10/06 ; 1 ; 0 ; 1 ; 7 ; 4 ; 9\n"
                + "22/06 ; 0 ; 1 ; 0 ; 3 ; 8 ; 4\n";
        Files.writeString(csvEsperado, csv);

        GestorPartidas gestor = new GestorPartidas();
        gestor.cargar(csvEsperado.toString());

        String resultado = gestor.mesMasVictorias();
        assertNotNull(resultado, "No debe retornar null");
        assertEquals("04", resultado,
                "El mes '04' tiene 3 victorias (vs '05'=1 y '06'=1), debe ser el ganador");
    }

    @Test
    @DisplayName("[0.5pt] mesMasVictorias sin datos cargados retorna 'SIN DATOS'")
    void testMesMasVictoriasSinDatos() {
        GestorPartidas gestor = new GestorPartidas();
        assertEquals("SIN DATOS", gestor.mesMasVictorias(),
                "Sin partidas debe retornar exactamente 'SIN DATOS'");
    }

    // ============================================================
    //  OPCION 4 - Salir (guardar datos)
    // ============================================================

    @Test
    @DisplayName("[1pt] guardar persiste los datos correctamente en el CSV")
    void testGuardarDatos() throws IOException {
        // Cargar datos
        String csvOriginal = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n"
                + "05/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5\n"
                + "12/04 ; 1 ; 0 ; 1 ; 6 ; 11 ; 6\n";
        Files.writeString(csvEsperado, csvOriginal);

        GestorPartidas gestor = new GestorPartidas();
        gestor.cargar(csvEsperado.toString());
        assertEquals(2, gestor.cantidadPartidas());

        // Agregar una partida manualmente y guardar
        gestor.getPartidas().add(new Partida("20/04", true, false, false, 8, 3, 10));

        Path csvGuardado = tempDir.resolve("juegos_guardado.csv");
        gestor.guardar(csvGuardado.toString());

        // Verificar que el archivo guardado existe
        assertTrue(Files.exists(csvGuardado), "El archivo guardado debe existir");

        // Leer el archivo guardado y verificar contenido
        List<String> lineas = Files.readAllLines(csvGuardado);
        assertTrue(lineas.size() >= 4, "El archivo guardado debe tener encabezados + 3 partidas");

        // Verificar encabezados
        assertTrue(lineas.get(0).toLowerCase().contains("fecha"),
                "La primera linea debe contener los encabezados");

        // Verificar que se puede recargar (opcion 4 completa el ciclo)
        GestorPartidas gestor2 = new GestorPartidas();
        gestor2.cargar(csvGuardado.toString());
        assertEquals(3, gestor2.cantidadPartidas(),
                "Al recargar el archivo guardado deben obtenerse las 3 partidas");
    }

    // ============================================================
    //  RESTRICCIONES
    // ============================================================

    @Test
    @DisplayName("[1pt] No se utiliza la clase Scanner en ningun archivo")
    void testNoUsaScanner() throws IOException {
        Path src = Path.of("src", "main", "java");
        assertTrue(Files.exists(src), "Debe existir el directorio src/main/java");

        List<Path> javaFiles = Files.walk(src)
                .filter(p -> p.toString().endsWith(".java"))
                .collect(Collectors.toList());

        assertFalse(javaFiles.isEmpty(), "Debe haber archivos .java en src/main/java");

        List<String> archivosConScanner = javaFiles.stream()
                .filter(archivo -> {
                    try {
                        String contenido = Files.readString(archivo);
                        return contenido.contains("java.util.Scanner")
                                || contenido.contains("new Scanner")
                                || contenido.contains("import java.util.Scanner");
                    } catch (IOException e) {
                        return false;
                    }
                })
                .map(Path::toString)
                .collect(Collectors.toList());

        if (!archivosConScanner.isEmpty()) {
            System.out.println("ARCHIVOS CON SCANNER PROHIBIDO:");
            archivosConScanner.forEach(a -> System.out.println("  - " + a));
        }

        assertTrue(archivosConScanner.isEmpty(),
                "No esta permitido usar la clase Scanner. Archivos infractores: " + archivosConScanner);
    }
}
