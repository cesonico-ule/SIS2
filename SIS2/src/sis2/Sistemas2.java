package sistemas2;

import Modelo.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class Sistemas2 {

    static String fichero = "resources\\SistemasInformacionII.xlsx";
    static ManejadorExcel manejador = new ManejadorExcel();
    static ArrayList<String[]> datosTrabajadores = null;
    static ArrayList<EmpleadoWorbu> empleados = new ArrayList<>();
    static Map<String, double[]> categorias = null;
    static ArrayList<Double> trienios = null;
    static Map<Double, Double> brutoExcel = null;
    static SalarioDatos datosCuotas = null;

    public static void main(String[] args) {
        /*
            Primera practica - conexion con base de datos
            consultaDNI(peticionDatos());
            consultaDNI("09741138V");
         */
        try {
            datosTrabajadores = manejador.lecturaTrabajadores(fichero);
            categorias = manejador.leerCategorias(fichero);
            trienios = manejador.leerTrienios(fichero);
            brutoExcel = manejador.leerBruto(fichero);
            datosCuotas = manejador.leerCuotas(fichero);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] datos : datosTrabajadores) {
            EmpleadoWorbu aux = new EmpleadoWorbu(datos);
            empleados.add(aux);
        }

        /*
        _____________Fin obtencion de Datos_____________
         */
        boolean salir = false;
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Prueba");
        generaNominas("03","2018");
        
        
/*
        while (!salir) {
            System.out.println(
                    "¿Que desea hacer?\n"
                    + "1. Comprobar los DNIs\n"
                    + "2. Comprobar y generar las cuentas bancarias\n"
                    + "3. Generar los emails\n"
                    + "4. Tarea completa\n"
                    + "5. Generar nominas"
                    + "\n0. Salir\n");

            switch (sc.next()) {
                case "0":
                    System.out.println("Saliendo del programa...");
                    salir = true;
                    break;

                case "1":
                    System.out.println("Comenzando la comprobación de los DNIs...");
                    compruebaDNIs();
                    break;

                case "2":
                    System.out.println("Comprobando los datos de IBAN de las cuentas bancarias...");
                    compruebaIBANs();
                    break;

                case "3":
                    System.out.println("Generando los emails de empresa de los trabajadores...");
                    creaEMAILs();
                    break;

                case "4":
                    System.out.println("Realizando todas las tareas...");
                    compruebaDNIs();
                    compruebaIBANs();
                    creaEMAILs();
                    break;

                case "5":
                    System.out.println("*** Introduzca el mes y año de la nomina a calcular ***");
                    String[] parts = sc.next().split("/"); //parts[0]=mes parts[1]=año

                    if (parts[0].equals("06") || parts[0].equals("12")) {
                        //Se generan tambien las nominas de la extra para aquellos que no este prorrateada

                    } else {
                        //Default

                    }
                    break;
                case "9":
                    System.out.println("Prueba");
                    generaNominas("01/2001");
                    break;
            }
        }
*/
    }

    public static void compruebaDNIs() {
        ArrayList<String> lista = new ArrayList<>();
        ArrayList<EmpleadoWorbu> errorEmp = new ArrayList();

        for (EmpleadoWorbu str : empleados) {
            if (str.getDni() == null || str.getDni() == "") {//no tiene DNI - error
                System.out.println("DNI en blanco - Error");
                errorEmp.add(str);

            } else {
                char letra = Herramientas.calculoDNI(str.getDni());
                System.out.println("DNI leido: " + str.getDni().substring(0, 7));

                if (lista.contains(str.getDni())) {//DNI ya procesado - error
                    System.out.println("DNI ya procesado - Error");
                    errorEmp.add(str);

                } else {//entramos cuando hay que procesar el DNI
                    lista.add(str.getDni().substring(0, 7));
                    if (letra != str.getDni().charAt(8)) {//letra no coincide
                        System.out.println("Actualizando DNI");
                        ManejadorExcel.actualizarCelda(fichero, 3, str.getFila(), 7, str.getDni().replace(str.getDni().charAt(8), letra));
                    }
                    //DNI correcto - no hacer nada
                }
            }
        }
        Errores.generaErrorDNI(errorEmp);
    }

    public static void compruebaIBANs() {
        ArrayList<EmpleadoWorbu> errorEmp = new ArrayList();

        for (EmpleadoWorbu str : empleados) {
            String ccc = Herramientas.calculoCCC(str.getCodCuenta()); //anota el ccc correcto
            String iban = Herramientas.generaIBAN(ccc, str.getPaisCuenta());
            if (!ccc.equals(str.getCodCuenta())) {//el CC NO es correcto
                str.setCCCError(str.getCodCuenta());
                str.setCodCuenta(ccc);
                str.setIban(iban);
                errorEmp.add(str);
                ManejadorExcel.actualizarCelda(fichero, 3, str.getFila(), 9, ccc);
                ManejadorExcel.actualizarCelda(fichero, 3, str.getFila(), 11, iban);
            } else {
                str.setIban(iban);
                ManejadorExcel.actualizarCelda(fichero, 3, str.getFila(), 11, iban);
            }
        }
        Errores.generaErrorCCC(errorEmp);
    }

    public static void creaEMAILs() {
        HashMap<String, Integer> lista = new HashMap<>();
        String correo;

        //recoger los correos que ya existen
        for (EmpleadoWorbu str : empleados) {
            if (str.getEmail() != null) {
                String[] aux = str.getEmail().split("\\d");//correo
                String nume = str.getEmail().substring(aux[0].length(), aux[0].length() + 2);//numero
                int num = Integer.parseInt(nume);
                lista.put(aux[0], num + 1);
            }
        }
        //generar correos nuevos
        for (EmpleadoWorbu str : empleados) {
            if (str.getEmail() == null) { //solo generamos correo para los que no tienen
                if (str.getApellido2() == null) { //no tiene 2º apellido
                    // primera letra del nombre y del 1º apellido
                    correo = "" + str.getNombre().charAt(0) + str.getApellido1().charAt(0);

                } else //si tiene 2º apellido le ponemos tmb su letra
                {
                    correo = "" + str.getNombre().charAt(0) + str.getApellido1().charAt(0) + str.getApellido2().charAt(0);
                }

                if (lista.get(correo) == null) { //no hay ningun correo igual
                    lista.put(correo, 1);
                    correo += "00@";

                } else { //ya hay correos iguales
                    correo += String.format("%02d", lista.get(correo)) + "@";
                    lista.put(correo, lista.get(correo) + 1);
                }
                correo += str.getNombreEmpresa() + ".com";
                System.out.println(correo);
                str.setEmail(correo);
                ManejadorExcel.actualizarCelda(fichero, 3, str.getFila(), 12, correo);
            }
        }
    }

    public static String peticionDatos() {
        Scanner sc = new Scanner(System.in);

        //pedir dni del empleado
        System.out.println("Introduzca el DNI del trabajador solicitado.\n");
        return sc.nextLine();
    }

    public static void consultaDNI(String dni) {
        SessionFactory sf = null;
        Session sesion = null;
        Transaction tr = null; //No se usa en consultas
        Empresas emp = null;

        try {
            sf = HibernateUtil.getSessionFactory();
            sesion = sf.openSession();

            String consultaHQL = "FROM Trabajadorbbdd t WHERE t.nifnie = :param1"; //t es alias obligatorio

            Query query = sesion.createQuery(consultaHQL);
            query.setParameter("param1", dni);

            List<Trabajadorbbdd> listaResultado = query.list();

            //Tarea 1
            if (listaResultado.isEmpty()) {
                System.out.println("\nNo conocemos a nadie con esos datos");
            } else {
                Trabajadorbbdd ttbd = listaResultado.get(0);//solo puede haber un empleado con el dni

                System.out.println("Nombre:\t\t" + ttbd.getNombre());
                System.out.println("Apellidos:\t" + ttbd.getApellido1() + "\t" + ttbd.getApellido2());
                System.out.println("NIF:\t\t" + ttbd.getNifnie());
                System.out.println("Categoria:\t" + ttbd.getCategorias().getNombreCategoria());
                System.out.println("Empresa:\t" + ttbd.getEmpresas().getNombre());

                Set<Nomina> listaNominas = ttbd.getNominas();

                for (Nomina a : listaNominas) {
                    System.out.println("Año:\t" + a.getAnio() + "\tMes:\t" + a.getMes());
                    System.out.printf("Bruto:\t" + a.getBrutoNomina() + " €\n");
                }
                System.out.println("*******************************");
                emp = ttbd.getEmpresas();
            }

            //Tarea 2
            if (emp != null) {

                consultaHQL = "FROM Empresas e WHERE e.idEmpresa != :param1";

                query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", emp.getIdEmpresa());

                List<Empresas> listaResulEmp = query.list();
                for (Empresas ebd : listaResulEmp) {
                    System.out.println(ebd.getNombre());
                    ebd.setNombre(ebd.getNombre() + "_2021");
                    tr = sesion.beginTransaction();
                    sesion.save(ebd);
                    tr.commit();
                }

                //Tarea 3
                for (Empresas ebd : listaResulEmp) {
                    Set<Trabajadorbbdd> ts = ebd.getTrabajadorbbdds();

                    for (Trabajadorbbdd tb : ts) {
                        System.out.println("Borrando nominas del trabajador: " + tb.getIdTrabajador() + " de la empresa: " + ebd.getIdEmpresa());

                        tr = sesion.beginTransaction();
                        String HQLborrado = "DELETE Nomina n WHERE n.trabajadorbbdd.idTrabajador=:param3";
                        sesion.createQuery(HQLborrado).setParameter("param3", tb.getIdTrabajador()).executeUpdate();
                        tr.commit();
                    }
                    System.out.println("Borrando trabajadores de la empresa: " + ebd.getIdEmpresa());

                    tr = sesion.beginTransaction();
                    String HQLborrado = "DELETE Trabajadorbbdd t WHERE t.empresas.idEmpresa=:param4";
                    sesion.createQuery(HQLborrado).setParameter("param4", ebd.getIdEmpresa()).executeUpdate();
                    tr.commit();
                }
            }
            sesion.close();
            HibernateUtil.shutdown();
        } catch (Exception e) {
            sesion.close();
            HibernateUtil.shutdown();
            System.err.println("No está el horno para bollos\n" + e.getMessage());
        }

    }

    public static void generaNominas(String mes, String ano) {
        double calculoBase;
        double brutoAnual;
        double[] bruto;
        double irpf_p;
        double irpf_s;
        double brutoMes;
        int trienio = 0;
        boolean cambio_trienio = false;
        
        for (EmpleadoWorbu str : empleados) {
            //if 
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");
            LocalDate alta = LocalDate.parse(str.getFechaAltaEmpresa(), formato);
            LocalDate fecha = LocalDate.parse("01/"+mes+"/"+ano, formato);
            
            int anos = (int) ChronoUnit.YEARS.between(alta, fecha);
            int meses = (int) ChronoUnit.MONTHS.between(alta, fecha);
            //System.out.println(anos);
                
            
            
            
                    //= calculoTrienio(str, mes, ano);
            bruto = categorias.get(str.getCategoria());
            brutoAnual = bruto[0] + bruto[1]; //sin antiguedad ni prorrateo
/*
            if (anos<1){
                if (meses>5) meses++;
                
                irpf_p = (brutoAnual/12)*meses;
                irpf_s = (brutoAnual/14*meses) + (brutoAnual/14)*((meses-6)/6);
                
            }
*/
        if (!cambio_trienio){
            //hacer calculo trienio 0 de momento
            
            calculoBase = bruto[0]/14 + bruto[1]/14 + trienio + brutoAnual/84;
            
            double[] cuotas = datosCuotas.datosCuotas();
            for (int i=0; i<8; i++){
                cuotas[i] *= calculoBase/100;
            }
            System.out.println(")");
            SalarioDatos cuotasNomina = new SalarioDatos(cuotas);
            
            if (str.isProrrata()){
                
            }
            double tramo_irpf = (brutoAnual - brutoAnual%1000);
            double porcentajeIRPF = brutoExcel.get(tramo_irpf);
            
            
            
            
            System.out.println("CalcBase: "+brutoAnual);
            System.out.println("Tramo irpf: "+tramo_irpf);
            System.out.println("irpf: "+ porcentajeIRPF);

            System.out.println("-------------------------------------------------");
        }
        }
    }

    public static void calculoTrienio(EmpleadoWorbu emp, int mes, int ano) {

    }
}
