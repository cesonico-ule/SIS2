package sistemas2;


/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */

public class EmpleadoWorbu {
    
    public String nombreEmpresa;
    public String cifEmpresa;
    public String categoria;
    public String fechaAltaEmpresa;
    public String apellido1;
    public String apellido2;
    public String nombre;
    public String nif;
    public boolean prorrata;
    public String codCuenta;
    public String paisCuenta;
    public String iban;
    public String email;
    public int fila;
    
    public EmpleadoWorbu(String[] datos){
             
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
        
        nombreEmpresa = datos[0];
        cifEmpresa = datos[1];
        categoria = datos[2];
        fechaAltaEmpresa = datos[3];
        apellido1 = datos[4];
        apellido2 = datos[5];
        nombre = datos[6];
        nif = datos[7];
        prorrata = datos[8];
        codCuenta = datos[9];
        paisCuenta = datos[10];
        iban = datos[11];
        email = datos[12];
        fila = datos[13];
        
        
        /*
        Trabajadorbbdd empleado = new Trabajadorbbdd();
        Empresas empresa = new Empresas();
        Categorias categoria = new Categorias();
        
        empresa.setNombre(input.get(0));
        empresa.setCif(input.get(1));
        empleado.setEmpresas(empresa);
        
        categoria.setNombreCategoria(input.get(2));
        empleado.setCategorias(categoria);
        
        empleado.setFechaAlta(input.get(3)); //problema Date-String
        empleado.setApellido1(input.get(4));
        empleado.setApellido2(input.get(5));
        empleado.setNombre(input.get(6));
        empleado.setNifnie(input.get(7));
        
        
        empleado.setCodigoCuenta(input.get());
        
        empleado.setIban(input.get());
        
        /*
            int fila, String nombreEmpresa, String cifEmpresa, String categoria, Date fechaAlta,  String apellido1, String apellido2, String nombre, String nif,
            boolean prorrata, String codCuenta, String paisCuenta, String iban, String email){
        
        /*
        trabajadorbbdd:
        fechaAlta
        apellido1
        apellido2
        nombre
        nif
        codCuenta
        paisCuenta
        iban
        email
                
                empresa:
                nombreEmpresa
                cifEmpresa
        */
                        
    }
}
