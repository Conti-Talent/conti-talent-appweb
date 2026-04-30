package com.conti_talent.springboot.appweb.conti_talent_web.dto.auth;

/**
 * Estructura del objeto de sesion devuelto al frontend tras login/registro.
 * Coincide 1:1 con la forma que se guarda en localStorage bajo la clave
 * `conti-talent:session` (ver auth.js -> setSession).
 *
 * El campo `rol` es el codigo del rol en minuscula ('admin', 'postulante')
 * para mantener compatibilidad exacta con el front actual:
 *   getSession()?.rol === 'admin'
 */
public class SesionDTO {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;

    public SesionDTO() {
    }

    public SesionDTO(String id, String nombre, String apellido, String email, String rol) {
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

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
