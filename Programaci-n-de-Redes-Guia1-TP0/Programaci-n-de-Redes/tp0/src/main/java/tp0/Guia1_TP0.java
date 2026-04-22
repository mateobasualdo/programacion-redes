package tp0;

import java.io.*;

public class Guia1_TP0 {

    public void ej1() throws IOException {
        Utils.mostrar("Organización:");
        String txt = Utils.leer().toUpperCase();

        String sigla = "";
        for(String p : txt.split(" ")){
            sigla += p.charAt(0);
        }

        Utils.mostrar(sigla);
    }

    public void ej2() throws IOException {
        Utils.mostrar("Palabra:");
        String p = Utils.leer();

        String inv = new StringBuilder(p).reverse().toString();

        Utils.mostrar(p.equalsIgnoreCase(inv) ? "Es palindromo" : "No es palindromo");
    }

    public void ej3() throws IOException {
        Utils.mostrar("Texto:");
        String t = Utils.leer().toLowerCase();

        int v = 0;
        for(char c : t.toCharArray()){
            if("aeiou".contains(""+c)) v++;
        }

        Utils.mostrar("Vocales: " + v);
    }

    public void ej4() throws IOException {
        Utils.mostrar("Texto:");
        String t = Utils.leer();

        Utils.mostrar("Buscar:");
        String b = Utils.leer();

        Utils.mostrar("Reemplazar por:");
        String r = Utils.leer();

        Utils.mostrar(t.replace(b, r));
    }

    public void ej5() throws IOException {
        Utils.mostrar("Email:");
        String e = Utils.leer();

        String user = e.substring(0, e.indexOf("@"));
        user = user.substring(0,1).toUpperCase() + user.substring(1).toLowerCase();

        Utils.mostrar("Hola " + user);
    }

    public void ej6() throws IOException {
        Utils.mostrar("Frase:");
        String t = Utils.leer();

        Utils.mostrar(t.trim().replaceAll(" +", " "));
    }

    public void ej7() throws IOException {
        Utils.mostrar("Contraseña:");
        String p = Utils.leer();

        boolean ok = p.length()>=8 && p.matches(".*\\d.*") && !p.toLowerCase().contains("clave");

        Utils.mostrar(ok ? "Segura" : "Insegura");
    }

    public void ej8() throws IOException {
        Utils.mostrar("Ruta archivo:");
        String r = Utils.leer();

        Utils.mostrar("Extensión: " + r.substring(r.lastIndexOf(".")+1));
    }

    public void ej9() throws IOException {
        Utils.mostrar("Texto:");
        String t = Utils.leer();

        String res = "";
        for(int i=0;i<t.length();i++){
            res += (i%2==0) ? Character.toUpperCase(t.charAt(i))
                            : Character.toLowerCase(t.charAt(i));
        }

        Utils.mostrar(res);
    }

    public void ej10() throws IOException {
        Utils.mostrar("Texto:");
        String t = Utils.leer();

        Utils.mostrar("Buscar:");
        String b = Utils.leer();

        int ini = t.indexOf(b);
        int fin = ini + b.length();

        Utils.mostrar("Inicio: " + ini + " Fin: " + fin);
    }

    public void ej11() throws IOException {
        Utils.mostrar("Producto:");
        String p = Utils.leer();

        Utils.mostrar("Cantidad:");
        int c = Integer.parseInt(Utils.leer());

        Utils.mostrar("Precio:");
        float pr = Float.parseFloat(Utils.leer());

        StringBuilder sb = new StringBuilder();
        sb.append(p).append(" x").append(c)
          .append(" = ").append(c * pr);

        Utils.mostrar(sb.toString());
    }

    public void ej12() throws IOException {
        Utils.mostrar("Nombre:");
        String n = Utils.leer();

        StringBuilder sb = new StringBuilder(n);
        sb.insert(0, "Sr./Sra. ");

        Utils.mostrar(sb.toString());
    }

    public void ej13() throws IOException {
        Utils.mostrar("Texto con 'error':");
        String t = Utils.leer();

        StringBuilder sb = new StringBuilder(t);
        int i = sb.indexOf("error");

        if(i != -1) sb.delete(i, i+5);

        Utils.mostrar(sb.toString());
    }

    public void ej14() throws IOException {
        Utils.mostrar("Palabra:");
        String p = Utils.leer();

        StringBuilder sb = new StringBuilder(p);
        sb.insert(0, "<b>");
        sb.append("</b>");

        Utils.mostrar(sb.toString());
    }

    public void ej15() throws IOException {
        Utils.mostrar("Palabra 1:");
        String a = Utils.leer();
        Utils.mostrar("Palabra 2:");
        String b = Utils.leer();
        Utils.mostrar("Palabra 3:");
        String c = Utils.leer();

        StringBuilder sb = new StringBuilder(a + "-" + b + "-" + c);

        int i = sb.indexOf(b);
        if(i != -1) sb.delete(i-1, i + b.length() + 1);

        Utils.mostrar(sb.toString());
    }
}