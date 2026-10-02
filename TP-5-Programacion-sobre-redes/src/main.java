import clienteUDP.ClienteUDP;
import servidorUDP.ServidorUDP;

public class main {

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("servidor")) {
            ServidorUDP.main(args);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("cliente")) {
            ClienteUDP.main(args);
        } else {
            System.out.println("Uso: java main servidor | cliente");
        }
    }
}