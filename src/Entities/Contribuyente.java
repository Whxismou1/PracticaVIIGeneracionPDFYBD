
package Entities;

public class Contribuyente {

    private Long id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String NIFNIE;
    private String direccion;
    private String numero;
    private String paisCCC;
    private String CCC;
    private String IBAN;
    private String email;
    private String exencion;
    private String bonificacion;
    private String lecturaAnterior;
    private String lecturaActual;
    private String fechaAlta;
    private String fechaBaja;
    private String conceptosACobrar;

    public Contribuyente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getNIFNIE() {
        return NIFNIE;
    }

    public void setNIFNIE(String NIFNIE) {
        this.NIFNIE = NIFNIE;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPaisCCC() {
        return paisCCC;
    }

    public void setPaisCCC(String paisCCC) {
        this.paisCCC = paisCCC;
    }

    public String getCCC() {
        return CCC;
    }

    public void setCCC(String CCC) {
        this.CCC = CCC;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExencion() {
        return exencion;
    }

    public void setExencion(String exencion) {
        this.exencion = exencion;
    }

    public String getBonificacion() {
        return bonificacion;
    }

    public void setBonificacion(String bonificacion) {
        this.bonificacion = bonificacion;
    }

    public String getLecturaAnterior() {
        return lecturaAnterior;
    }

    public void setLecturaAnterior(String lecturaAnterior) {
        this.lecturaAnterior = lecturaAnterior;
    }

    public String getLecturaActual() {
        return lecturaActual;
    }

    public void setLecturaActual(String lecturaActual) {
        this.lecturaActual = lecturaActual;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(String fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    @Override
    public String toString() {
        return "Contribuyente{" + "id=" + id + ", nombre=" + nombre + ", apellido1=" + apellido1 + ", apellido2=" + apellido2 + ", NIFNIE=" + NIFNIE + ", direccion=" + direccion + ", numero=" + numero + ", paisCCC=" + paisCCC + ", CCC=" + CCC + ", IBAN=" + IBAN + ", email=" + email + ", exencion=" + exencion + ", bonificacion=" + bonificacion + ", lecturaAnterior=" + lecturaAnterior + ", lecturaActual=" + lecturaActual + ", fechaAlta=" + fechaAlta + ", fechaBaja=" + fechaBaja + ", conceptosACobrar=" + conceptosACobrar + '}';
    }


    public String getConceptosACobrar() {
        return conceptosACobrar;
    }

    public void setConceptosACobrar(String conceptosACobrar) {
        this.conceptosACobrar = conceptosACobrar;
    }
}