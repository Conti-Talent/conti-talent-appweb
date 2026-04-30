package com.conti_talent.springboot.appweb.conti_talent_web.model;

/**
 * Entidad Usuario. Representa una cuenta autenticable en el sistema.
 * El rol del usuario se referencia por FK ({@code rolId}) hacia la tabla
 * de roles, en lugar de almacenarlo como enum, para permitir extension
 * dinamica del catalogo y migracion limpia a base de datos relacional.
 */
public class Usuario {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rolId;
    private boolean activo;
    private long creadoEn;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String apellido, String email,
                   String password, String rolId, boolean activo, long creadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRolId() { return rolId; }
    public void setRolId(String rolId) { this.rolId = rolId; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
