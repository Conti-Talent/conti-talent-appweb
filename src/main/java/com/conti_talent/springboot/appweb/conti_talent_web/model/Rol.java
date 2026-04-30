package com.conti_talent.springboot.appweb.conti_talent_web.model;

/**
 * Entidad de dominio para Rol. Representa un perfil de acceso al sistema
 * (Administrador, Postulante, etc.). Esta entidad existe como tabla
 * independiente para preparar la futura migracion a base de datos relacional,
 * donde Usuario tendra una FK -> Rol.
 *
 * El campo `codigo` es la llave logica (espejo de RolCodigo) y debe ser unico.
 * El campo `nombre` es el texto visible en la interfaz de usuario.
 */
public class Rol {

    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private long creadoEn;

    public Rol() {
    }

    public Rol(String id, String codigo, String nombre, String descripcion,
               boolean activo, long creadoEn) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
