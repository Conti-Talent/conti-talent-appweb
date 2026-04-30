package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

/**
 * Cuerpo de creacion / actualizacion de usuario desde el panel admin.
 * Acepta el rol como `rolId` (FK) o como `rolCodigo` (texto: "ADMIN" / "POSTULANTE")
 * para flexibilidad del cliente. El servicio resolvera la FK a partir del codigo
 * cuando solo se envie este ultimo.
 */
public class UsuarioRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rolId;
    private String rolCodigo;
    private Boolean activo;

    public UsuarioRequest() {
    }

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

    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
