package sistemas2;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class EmpleadoWorbu {

    private String nombreEmpresa;
    private String cifEmpresa;
    
    private String categoria;
    
    private String fechaAltaEmpresa;
    private String apellido1;
    private String apellido2;
    private String nombre;
    private String dni;    
    private String codCuenta;
    private String iban;
    private String email;
    
    private String cccError;
    private String paisCuenta;
    private boolean prorrata;
    private int fila;

    public EmpleadoWorbu(String[] datos) {
        /*
        0 - Nombre empresa
        1 - Cif empresa
        2 - Categoria
        3 - FechaAltaEmpresa
        4 - Apellido1
        5 - Apellido2
        6 - Nombre
        7 - NIF/NIE
        8 - ProrrataExtra
        9 - CodigoCuenta
        10 - Pais Origen Cuenta Bancaria
        11 - IBAN
        12 - Email
        13 - Fila
         */
        actualizaDatos(datos);
    }

    public void actualizaDatos(String[] datos) {
        nombreEmpresa = datos[0];
        cifEmpresa = datos[1];
        categoria = datos[2];
        fechaAltaEmpresa = datos[3];
        apellido1 = datos[4];
        apellido2 = datos[5];
        nombre = datos[6];
        dni = datos[7];

        if (datos[8].equals("SI")) {
            prorrata = true;
        } else prorrata = false;        

        codCuenta = datos[9];
        paisCuenta = datos[10];
        iban = datos[11];
        email = datos[12];
        fila = Integer.valueOf(datos[13]);
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public void setCifEmpresa(String cifEmpresa) {
        this.cifEmpresa = cifEmpresa;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFechaAltaEmpresa(String fechaAltaEmpresa) {
        this.fechaAltaEmpresa = fechaAltaEmpresa;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setProrrata(boolean prorrata) {
        this.prorrata = prorrata;
    }

    public void setCodCuenta(String codCuenta) {
        this.codCuenta = codCuenta;
    }

    public void setPaisCuenta(String paisCuenta) {
        this.paisCuenta = paisCuenta;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }
    
    public void setCCCError(String cccError){
        this.cccError = cccError;
    }
    public String getCCCError(){
        return cccError;
    }
    
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getCifEmpresa() {
        return cifEmpresa;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFechaAltaEmpresa() {
        return fechaAltaEmpresa;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public boolean isProrrata() {
        return prorrata;
    }

    public String getCodCuenta() {
        return codCuenta;
    }

    public String getPaisCuenta() {
        return paisCuenta;
    }

    public String getIban() {
        return iban;
    }

    public String getEmail() {
        return email;
    }

    public int getFila() {
        return fila;
    }
}
