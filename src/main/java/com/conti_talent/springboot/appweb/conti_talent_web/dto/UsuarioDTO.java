package com.conti_talent.springboot.appweb.conti_talent_web.dto;

/**
 * DTO de Usuario para exposicion externa.
 * IMPORTANTE: nunca incluye password.
 * Incluye el rol como objeto embebido (RolDTO) para evitar que el frontend
 * tenga que hacer un segundo request para obtener el nombre del rol.
 */
public class UsuarioDTO {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String rolId;
    private RolDTO rol;
    private boolean activo;
    private long creadoEn;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String id, String nombre, String apellido, String email,
                      String rolId, RolDTO rol, boolean activo, long creadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rolId = rolId;
        this.rol = rol;
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

    public String getRolId() { return rolId; }
    public void setRolId(String rolId) { this.rolId = rolId; }

    public RolDTO getRol() { return rol; }
    public void setRol(RolDTO rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
