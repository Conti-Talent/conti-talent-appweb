package com.conti_talent.springboot.appweb.conti_talent_web.dto.auth;

import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.Rol;

/**
 * Estructura del objeto de sesión devuelto al frontend.
 * Coincide 1:1 con la forma que hoy se guarda en localStorage bajo la
 * clave `conti-talent:session` (ver auth.js -> setSession).
 */
public class SesionDTO {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;

    public SesionDTO() {
    }

    public SesionDTO(String id, String nombre, String apellido, String email, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
