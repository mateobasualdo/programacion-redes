package tp0;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Guia1_TP0 guia = new Guia1_TP0();
        int opcion;

        do {
            Utils.mostrar("\n===== MENÚ DE OPCIONES =====");
            Utils.mostrar("1  - Generar sigla");
            Utils.mostrar("2  - Verificar palíndromo");
            Utils.mostrar("3  - Contar vocales");
            Utils.mostrar("4  - Reemplazar palabra");
            Utils.mostrar("5  - Usuario de email");
            Utils.mostrar("6  - Limpiar espacios");
            Utils.mostrar("7  - Validar contraseña");
            Utils.mostrar("8  - Obtener extensión");
            Utils.mostrar("9  - Alternar may/min");
            Utils.mostrar("10 - Buscar posición");
            Utils.mostrar("11 - Ticket de compra");
            Utils.mostrar("12 - Agregar título");
            Utils.mostrar("13 - Quitar 'error'");
            Utils.mostrar("14 - Negrita HTML");
            Utils.mostrar("15 - Unir palabras");
            Utils.mostrar("0  - Salir");

            Utils.mostrar("Elegí una opción:");
            opcion = Integer.parseInt(Utils.leer());

            switch(opcion){
                case 1: guia.ej1(); break;
                case 2: guia.ej2(); break;
                case 3: guia.ej3(); break;
                case 4: guia.ej4(); break;
                case 5: guia.ej5(); break;
                case 6: guia.ej6(); break;
                case 7: guia.ej7(); break;
                case 8: guia.ej8(); break;
                case 9: guia.ej9(); break;
                case 10: guia.ej10(); break;
                case 11: guia.ej11(); break;
                case 12: guia.ej12(); break;
                case 13: guia.ej13(); break;
                case 14: guia.ej14(); break;
                case 15: guia.ej15(); break;
                case 0: Utils.mostrar("Programa finalizado"); break;
                default: Utils.mostrar("Opción inválida");
            }

        } while(opcion != 0);
    }
}
