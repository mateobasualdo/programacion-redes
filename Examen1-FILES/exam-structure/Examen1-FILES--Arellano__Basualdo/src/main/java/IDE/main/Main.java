package IDE.main;

import IDE.modelo.Socio;
import IDE.servicio.Club;

public class Main {
    public static void main(String[] args) {
        Club miClub = new Club("ET32 Tech Club");

        // Creamos objetos usando el constructor
        Socio s1 = new Socio("Gonzalo Cerna", 1001);
        Socio s2 = new Socio("Ana Perez", 1002);

        // Los agregamos al ArrayList del Club
        miClub.afiliarSocio(s1);
        miClub.afiliarSocio(s2);

        // Ejecutamos la lógica
        miClub.mostrarSocios();
    }
}