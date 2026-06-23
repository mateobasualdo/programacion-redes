package com.agenda.model;

/**
 * Modelo de datos que representa un contacto de la agenda.
 * La nota privada se mantiene en texto plano EN MEMORIA;
 * el cifrado/descifrado ocurre únicamente en la capa de persistencia.
 */
public class Contacto {

    private String nombre;
    private String telefono;
    private String email;
    private String notaPrivada;

    public Contacto(String nombre, String telefono, String email, String notaPrivada) {
        this.nombre      = nombre.trim();
        this.telefono    = telefono.trim();
        this.email       = email.trim();
        this.notaPrivada = notaPrivada.trim();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getNombre()      { return nombre; }
    public String getTelefono()    { return telefono; }
    public String getEmail()       { return email; }
    public String getNotaPrivada() { return notaPrivada; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setNombre(String nombre)           { this.nombre      = nombre.trim(); }
    public void setTelefono(String telefono)       { this.telefono    = telefono.trim(); }
    public void setEmail(String email)             { this.email       = email.trim(); }
    public void setNotaPrivada(String notaPrivada) { this.notaPrivada = notaPrivada.trim(); }

    @Override
    public String toString() {
        return "Contacto{nombre='" + nombre + "', telefono='" + telefono
                + "', email='" + email + "'}";
    }
}
