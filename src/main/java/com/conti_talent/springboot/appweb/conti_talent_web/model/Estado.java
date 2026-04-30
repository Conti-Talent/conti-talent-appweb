package com.conti_talent.springboot.appweb.conti_talent_web.model;

/**
 * Entidad de dominio para Estado del flujo de seleccion. Representa una
 * etapa del proceso (POSTULADO, EN_EVALUACION, ENTREVISTA, etc.).
 *
 * Existe como tabla independiente para preparar la futura migracion a BD
 * relacional, donde Postulante tendra una FK -> Estado.
 *
 * El campo `codigo` es la llave logica (espejo de EstadoCodigo).
 * El campo `orden` permite renderizar los estados en secuencia en la UI.
 * El campo `terminal` indica si desde aqui no se puede avanzar.
 */
public class Estado {

    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int orden;
    private boolean terminal;
    private boolean activo;
    private long creadoEn;

    public Estado() {
    }

    public Estado(String id, String codigo, String nombre, String descripcion,
                  int orden, boolean terminal, boolean activo, long creadoEn) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
        this.terminal = terminal;
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

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public boolean isTerminal() { return terminal; }
    public void setTerminal(boolean terminal) { this.terminal = terminal; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
