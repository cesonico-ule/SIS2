package sistemas2;

import Modelo.*;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

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
    static ArrayList<Integer> trienios = null;
    static TreeMap<Double, Double> brutoExcel = null;
    static SalarioDatos datosCuotas = null;
    
    static Map<String, Categorias> categorias = null;
    static Map<String, Empresas> empres = new HashMap<>(1);
    static Set<Trabajadorbbdd> trabajadores = null;
    static Set<Nomina> nominaa = new HashSet();


    public static void main(String[] args) {
        /*
            Primera practica - conexion con base de datos
            consultaDNI(peticionDatos());
            consultaDNI("09741138V");
         */
        try {
            //Lectura excel
            empleados = manejador.lecturaTrabajadores(fichero);
            trienios = manejador.leerTrienios(fichero);
            brutoExcel = manejador.leerBruto(fichero);
            datosCuotas = manejador.leerCuotas(fichero);
            //Creamos las categorias
            categorias = manejador.leerCategoria(fichero);

        } catch (IOException e) {
            e.printStackTrace();
        }
//      ___________________Fin obtencion de datos___________________

        compruebaDNIs();
        compruebaIBANs();
        creaEMAILs();

        //Creamos las empresas 
        empres = traductorEmpresas(empleados);
        //Creamos los trabajadores y los enlazamos a las empresas y categorias
        trabajadores = traductor(empleados);

        Scanner sc = new Scanner(System.in);
        System.out.println("*** Introduzca el mes y año de la nomina a calcular ***");
        generaNominas(sc.next(), false);
        generaBD();
        
        // <editor-fold defaultstate="collapsed" desc="Selector de opciones">
        /*         
        boolean continuar = true;        
        while (continuar) {
            System.out.println(
                    "\n¿Que desea hacer?\n"
                    + "1. Comprobar los DNIs\n"
                    + "2. Comprobar y generar las cuentas bancarias\n"
                    + "3. Generar los emails\n"
                    + "4. Tarea completa\n"
                    + "5. Generar nominas por pantalla\n"
                    + "5. Generar nominas en archivos txt"
                    + "\n0. Salir\n");

            switch (sc.next()) {
                case "0":
                    System.out.println("Saliendo del programa...");
                    continuar = false;
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
                    generaNominas(sc.next(), false);
                    break;

                case "6":
                    System.out.println("*** Introduzca el mes y año de la nomina a calcular ***");
                    generaNominas(sc.next(), true);
                    break;
                case "9":
                    System.out.println("Prueba");
                    generaNominas("12/2021", false);
                    break;
            }
        }*/ //</editor-fold>
    }

    public static Set<Trabajadorbbdd> traductor(ArrayList<EmpleadoWorbu> emp) {
        Set<Trabajadorbbdd> lista = new HashSet();
        Trabajadorbbdd aux = null;

        Empresas auxEmp = null;

        for (EmpleadoWorbu str : emp) {
            aux = new Trabajadorbbdd();
            aux.setNombre(str.getNombre());
            aux.setApellido1(str.getApellido1());
            aux.setApellido2(str.getApellido2());
            aux.setNifnie(str.getDni());
            aux.setEmail(str.getEmail());

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date fecha = formatter.parse(str.getFechaAltaEmpresa());
                aux.setFechaAlta(fecha);
            } catch (Exception e) {
                e.printStackTrace();
            }
            aux.setCodigoCuenta(str.getCodCuenta());
            aux.setIban(str.getIban());

            aux.setProrrata(str.isProrrata());
            aux.setFila(str.getFila());
            aux.setCccError(str.getCCCError());
            aux.setPaisCuenta(str.getPaisCuenta());

//            //Añadir trabajador a la empresa correcta
            if (empres.get(str.getCifEmpresa()) != null) {//control de errores
                Empresas e = empres.get(str.getCifEmpresa());
                Set f = e.getTrabajadorbbdds();
                aux.setEmpresas(e);
                f.add(aux);
                e.setTrabajadorbbdds(f);
            }

//            //Añadir trabajador a la categoria correcta
//            if (categorias.get(str.getCategoria()) != null) {//control de errores
                Categorias a = categorias.get(str.getCategoria());
//                Set b = a.getTrabajadorbbdds();
                aux.setCategorias(a);
//                b.add(aux);
//                a.setTrabajadorbbdds(b);
//            }

            lista.add(aux);
        }
        return lista;
    }

    //Crea entrada para cada empresa nueva, CIF no conocido
    public static Map<String, Empresas> traductorEmpresas(ArrayList<EmpleadoWorbu> emp){
        Empresas aux;

        for (EmpleadoWorbu str : emp) {
            aux = new Empresas();
            aux.setNombre(str.getNombreEmpresa());
            aux.setCif(str.getCifEmpresa());

            if (!empres.containsKey(str.getCifEmpresa())) {

                empres.put(str.getCifEmpresa(), aux);
            }
        }
        return empres;
    }
    
    public static void compruebaDNIs() {
        ArrayList<String> lista = new ArrayList<>();
        ArrayList<EmpleadoWorbu> errorEmp = new ArrayList();

        for (EmpleadoWorbu str : empleados) {
            if (str.getDni() == null || str.getDni() == "") {//no tiene DNI - error
//                System.out.println("DNI en blanco - Error");
                errorEmp.add(str);

            } else {
                char letra = Herramientas.calculoDNI(str.getDni());
//                System.out.println("DNI leido: " + str.getDni().substring(0, 8));

                if (lista.contains(str.getDni().substring(0, 8))) {//DNI ya procesado - error
//                    System.out.println("DNI ya procesado - Error");
                    errorEmp.add(str);

                } else {//entramos cuando hay que procesar el DNI
                    lista.add(str.getDni().substring(0, 8));
                    if (letra != str.getDni().charAt(8)) {//letra no coincide
//                        System.out.println("Actualizando DNI");
                        ManejadorExcel.actualizarCelda(fichero, 3, str.getFila(), 7, str.getDni().replace(str.getDni().charAt(8), letra));
                    }
                    //DNI correcto - no hacer nada
                }

            }

        }
        for (EmpleadoWorbu str : errorEmp) {
            empleados.remove(str);
        }
        if (!errorEmp.isEmpty()) {
            Errores.generaErrorDNI(errorEmp);
        }
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
        if (!errorEmp.isEmpty()) {
            Errores.generaErrorCCC(errorEmp);
        }
    }

    public static void creaEMAILs() {
        Map<String, Integer> lista = new HashMap<>();
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
                    lista.put(correo, lista.get(correo) + 1);
                    correo += String.format("%02d", lista.get(correo) - 1) + "@";

                }
                correo += str.getNombreEmpresa() + ".com";
//                System.out.println(correo);
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

    public static void generaNominas(String fecha, boolean archivo) {

        double calculoBase, brutoAnual, brutoMes;
        Categorias bruto;
        double[] cuotasCalculadas;
        double[] cuotas0;
        double irpf, calculoirpf, porcentajeIRPF;
        double cantidadProrrateo, salarioBase;
        int trienio, anos, meses, anos_enero, anos_dic;
        boolean cambio_trienio = false;
        String[] parte = fecha.split("/");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate fech = LocalDate.parse(parte[1] + "-" + parte[0] + "-01", formato);
        LocalDate enero = LocalDate.parse(parte[1] + "-01-01", formato);
        LocalDate dic = LocalDate.parse(parte[1] + "-12-01", formato);

        for (Trabajadorbbdd str : trabajadores) {

            Date date = str.getFechaAlta();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            LocalDate alta = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            anos = (int) ChronoUnit.YEARS.between(alta, fech);//antiguedad a dia de nomina
            meses = (int) ChronoUnit.MONTHS.between(alta, fech);//antiguedad a dia de nomina
            anos_enero = (int) ChronoUnit.YEARS.between(alta, enero);
            anos_dic = (int) ChronoUnit.YEARS.between(alta, dic);
            if (anos_enero != anos_dic) {
                cambio_trienio = true;
            }

            bruto = str.getCategorias();
            brutoAnual = bruto.getSalarioBaseCategoria() + bruto.getComplementoCategoria(); //sin antiguedad ni prorrateo
            trienio = trienios.get(anos / 3);
            brutoMes = bruto.getSalarioBaseCategoria() / 14 + bruto.getComplementoCategoria() / 14 + trienio;
            cantidadProrrateo = brutoMes / 6;
            calculoBase = brutoMes + cantidadProrrateo;

            if (!cambio_trienio) {//no hay cambio de trienio
                calculoirpf = brutoAnual + trienio * 14;

            } else if (anos > 0) {//hay cambio de trienio
                calculoirpf = brutoAnual + trienios.get(anos_enero / 3) * fech.getMonthValue() + trienio * (12 - fech.getMonthValue());

            } else {//recien contratado
                if (str.isProrrata()) {
                    calculoirpf = brutoAnual / 12 * meses;

                } else {
                    meses = 12 - alta.getMonthValue();
                    if (meses > 5) {
                        meses++;
                    }

                    calculoirpf = brutoAnual / 14 * meses;
                    if (meses - 6 > 0) {
                        calculoirpf += brutoAnual / 14 * ((meses - 6) / 6);
                    }
                }
            }
            porcentajeIRPF = brutoExcel.get(brutoExcel.ceilingKey(calculoirpf));
            salarioBase = bruto.getSalarioBaseCategoria() / 14;
            if (str.isProrrata()) {
                irpf = calculoBase * porcentajeIRPF / 100;
                brutoMes = calculoBase;

            } else {
                irpf = brutoMes * porcentajeIRPF / 100;
                cantidadProrrateo = 0;
            }

            cuotasCalculadas = datosCuotas.datosCuotas();
            cuotas0 = datosCuotas.datosCuotas();
            for (int i = 0; i < 8; i++) {
                cuotasCalculadas[i] *= calculoBase / 100;
                cuotas0[i] = 0.0;
            }
            double totalTrabajador = irpf;
            double totalEmpleador = 0;
            for (int i = 0; i < 3; i++) {
                totalTrabajador += cuotasCalculadas[i];
            }
            for (int i = 3; i < 8; i++) {
                totalEmpleador += cuotasCalculadas[i];
            }
            if (anos == 0) {

            }

//            // --- IMPRIMIR POR PANTALLA O TXT --- //
//            if (archivo) {
//                imprimirArchivo(str, cuotasCalculadas, bruto, calculoBase, fech, salarioBase, brutoMes,
//                        cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, totalTrabajador, totalEmpleador);
//
//                if ((parte[0].equals("6") || parte[0].equals("12")) && !str.isProrrata()) {
//                    imprimirArchivo(str, cuotas0, bruto, 0.0, fech, salarioBase, brutoMes,
//                            cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, irpf, 0.0);
//                }
//            } else {
//                imprimeNominas(str, cuotasCalculadas, bruto, calculoBase, fech, salarioBase, brutoMes,
//                        cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, totalTrabajador, totalEmpleador);
//
//                if ((parte[0].equals("6") || parte[0].equals("12")) && !str.isProrrata()) {
//                    imprimeNominas(str, cuotas0, bruto, 0.0, fech, salarioBase, brutoMes,
//                            cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, irpf, 0.0);
//                }
//            }

            //Los PDF siempre se imprimen
            generaNomi(str, cuotasCalculadas, bruto, calculoBase, parte, salarioBase, brutoMes,
                        cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, totalTrabajador, totalEmpleador);
            imprimirPDF(str, cuotasCalculadas, bruto, calculoBase, fech, salarioBase, brutoMes,
                        cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, totalTrabajador, totalEmpleador);
            
                if ((parte[0].equals("6") || parte[0].equals("12")) && !str.isProrrata()) {
                    generaNomi(str, cuotas0, bruto, 0.0, parte, salarioBase, brutoMes,
                            cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, irpf, 0.0);
                    imprimirPDF(str, cuotas0, bruto, 0.0, fech, salarioBase, brutoMes,
                            cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, irpf, 0.0);
                    
                }
        }
    }
   
    public static void generaNomi(Trabajadorbbdd str, double[] cuotasCalculadas, Categorias bruto,
            double calculoBase, String[] fecha, double salarioBase, double brutoMes, double cantidadProrrateo,
            int anos, int trienio, double porcentajeIRPF, double irpf, double totalTrabajador, double totalEmpleador) {
        // Genero objeto nomina
        Nomina nomina = new Nomina();
        //Trabajador
        nomina.setTrabajadorbbdd(str);
        //IRPF
        nomina.setImporteIrpf(irpf);
        nomina.setIrpf(porcentajeIRPF);
        //Bruto
        nomina.setBrutoAnual(bruto.getSalarioBaseCategoria());
        nomina.setBrutoNomina(brutoMes);
        //Liquido
        nomina.setLiquidoNomina(brutoMes - totalTrabajador);
        //Trienios
        nomina.setNumeroTrienios(anos / 3); //Esto podria dividido entre 3 y truncado
        nomina.setImporteTrienios(Double.valueOf(trienio));
        //Accidentes
        nomina.setAccidentesTrabajoEmpresario(datosCuotas.getAccidentesTrabajoEmpresario());
        nomina.setImporteAccidentesTrabajoEmpresario(cuotasCalculadas[7]);
        //Desempleo
        nomina.setDesempleoEmpresario(datosCuotas.getDesmpleoEmpresario());
        nomina.setDesempleoTrabajador(datosCuotas.getCuotaDesempleoTrabajador());
        nomina.setImporteDesempleoEmpresario(cuotasCalculadas[5]);
        nomina.setImporteDesempleoTrabajador(cuotasCalculadas[1]);
        //Formacion
        nomina.setFormacionEmpresario(datosCuotas.getFormacionEmpresario());
        nomina.setFormacionTrabajador(datosCuotas.getCuotaFormacionTrabajador());
        nomina.setImporteFormacionEmpresario(cuotasCalculadas[6]);
        nomina.setImporteFormacionTrabajador(cuotasCalculadas[2]);
        //Fogasa
        nomina.setFogasaempresario(datosCuotas.getFogasaEmpresario());
        nomina.setImporteFogasaempresario(cuotasCalculadas[4]);
        //Prorrateo
        nomina.setValorProrrateo(cantidadProrrateo);
        //Totales
        nomina.setCosteTotalEmpresario(totalEmpleador);
        //Fecha
        nomina.setMes(Integer.parseInt(fecha[0]));
        nomina.setAnio(Integer.parseInt(fecha[1]));
        //Seguridad Social
        nomina.setSeguridadSocialEmpresario(datosCuotas.getContingenciasComunesEmpresario());
        nomina.setSeguridadSocialTrabajador(datosCuotas.getCuotaObreraGeneralTrabajador());
        nomina.setImporteSeguridadSocialEmpresario(cuotasCalculadas[3]);
        nomina.setImporteSeguridadSocialTrabajador(cuotasCalculadas[0]);
        //Importes comunes
        nomina.setImporteComplementoMes(bruto.getComplementoCategoria() / 14);
        nomina.setImporteSalarioMes(salarioBase);
        nomina.setBaseEmpresario(calculoBase);

        //Introduce la nomina en el trabajador y al archivo
        str.getNominas().add(nomina);
        nominaa.add(nomina);
    }
    
    public static void generaBD() {
        SessionFactory sf = null;
        Session sesion = null;
        Transaction tr = null; //No se usa en consultas
        Map<String, Empresas> mapresas = new HashMap();
        Map<String, Categorias> mapegorias = new HashMap();
        Map<String, Trabajadorbbdd> maprajadores = new HashMap();
        Map<String, Nomina> mapronimas = new HashMap();
        String password;
        //String consultaHQL = "EXISTS(SELECT * from Empresas WHERE id = :param1)";

        try {
            sf = HibernateUtil.getSessionFactory();
            sesion = sf.openSession();

            // ||------- EMPRESAS -------||
            System.out.println("Introduciendo Empresas en la base de datos");
            for (Empresas emp : empres.values()) {
                String consultaHQL = "FROM Empresas t WHERE t.cif = :param1";

                Query query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", emp.getCif());

                Empresas empresas = (Empresas) query.uniqueResult();

                if (empresas != null) {
                    mapresas.put(emp.getCif(), empresas);

                } else {
                    tr = sesion.beginTransaction();
                    sesion.saveOrUpdate(emp);
                    tr.commit();
                }
            }

            // ||------- CATEGORIAS -------||
            System.out.println("Introduciendo Categorias en la base de datos");
            for (Categorias cat : categorias.values()) {
                String consultaHQL = "FROM Categorias t WHERE t.nombreCategoria = :param1";
                Query query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", cat.getNombreCategoria());

                Categorias cats = (Categorias) query.uniqueResult();

                if (cats != null) {
                    mapegorias.put(cat.getNombreCategoria(), cats);

                } else {
                    tr = sesion.beginTransaction();
                    sesion.saveOrUpdate(cat);
                    tr.commit();
                }
            }

            // ||------- TRABAJADORES -------||
            System.out.println("Introduciendo Trabajadores en la base de datos");
            for (Trabajadorbbdd str : trabajadores) {
                String consultaHQL = "FROM Trabajadorbbdd t WHERE (t.nombre = :param1) "
                        + "AND (t.nifnie = :param2) "
                        + "AND (t.fechaAlta = :param3)";
                Query query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", str.getNombre());
                query.setParameter("param2", str.getNifnie());
                query.setParameter("param3", str.getFechaAlta());

                Trabajadorbbdd trabs = (Trabajadorbbdd) query.uniqueResult();

                if (trabs != null) {
                    password = str.getNombre() + "+" + str.getNifnie() + "+" + str.getFechaAlta();
                    maprajadores.put(password, trabs);

                } else {
                    if (mapresas.containsKey(str.getEmpresas().getCif())) {
                        str.setEmpresas(mapresas.get(str.getEmpresas().getCif()));
                    }
                    if (mapegorias.containsKey(str.getCategorias().getNombreCategoria())) {
                        str.setCategorias(mapegorias.get(str.getCategorias().getNombreCategoria()));
                    }
                    tr = sesion.beginTransaction();
                    sesion.saveOrUpdate(str);
                    tr.commit();
                }
            }

            // ||------- NOMINAS -------||
            System.out.println("Introduciendo Nominas en la base de datos");
            for (Nomina n : nominaa) {
                String consultaHQL = "FROM Nomina t WHERE (t.mes = :param1) "
                        + "AND (t.anio = :param2) "
                        + "AND (t.brutoNomina = :param3) "
                        + "AND (t.liquidoNomina = :param4)";
                Query query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", n.getMes());
                query.setParameter("param2", n.getAnio());
                query.setParameter("param3", n.getBrutoNomina());
                query.setParameter("param4", n.getLiquidoNomina());

                //lista de nominas que son iguales excepto trabajador
                List<Nomina> noms = query.list();

                //comprobar que el trabajador no estaba ya en la base de datos
                password = n.getTrabajadorbbdd().getNombre() + "+"
                        + n.getTrabajadorbbdd().getNifnie() + "+"
                        + n.getTrabajadorbbdd().getFechaAlta();

                if (maprajadores.containsKey(password)) {
                    n.setTrabajadorbbdd(maprajadores.get(password));
                }

                if (noms.isEmpty()) {
                    //no hay ninguna nomina igual, check Tra ya en base de datos
                    tr = sesion.beginTransaction();
                    sesion.saveOrUpdate(n);
                    tr.commit();

                } else {//coincide mes, ano, bruto y liquido, comprobar si trabajador tmb
                    for (Nomina a : noms) {
                        if (!n.getTrabajadorbbdd().equals(a.getTrabajadorbbdd())) {
                            tr = sesion.beginTransaction();
                            sesion.saveOrUpdate(n);
                            tr.commit();

                        }
                    }
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

    public static void imprimeNominas(Trabajadorbbdd str, double[] cuotasCalculadas, Categorias bruto,
            double calculoBase, LocalDate fecha, double salarioBase, double brutoMes, double cantidadProrrateo,
            int anos, int trienio, double porcentajeIRPF, double irpf, double totalTrabajador, double totalEmpleador) {

        Locale SPAIN = new Locale("es", "ES");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String fechaAlta = formatter.format(str.getFechaAlta());

        if (calculoBase == 0.0) {
            System.out.println("\n********************EXTRA********************");
        }

        System.out.printf(String.format("NOMINA de: %s - %s\n", fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase(), fecha.getYear()));

        System.out.printf("Empresa: %s\n", str.getEmpresas().getNombre());
        System.out.printf("CIF: %s\n", str.getEmpresas().getCif());
        System.out.println("----------------------------------------------------");

        System.out.printf("Nombre: %s\n", str.getNombre());
        System.out.printf("Apellidos: %s %s\n", str.getApellido1(), str.getApellido2());
        System.out.printf("DNI: %s\n", str.getNifnie());
        System.out.printf("Fecha de alta: %s\n", fechaAlta);
        System.out.printf("Categoria: %s\n", str.getCategorias().getNombreCategoria());
        System.out.printf("Bruto Anual: %.2f€\n", bruto.getSalarioBaseCategoria());
        System.out.printf("IBAN: %s\n", str.getIban());

        System.out.printf("Salario Base: %.2f€\n", salarioBase);
        System.out.printf("Prorrateo: %.2f€\n", cantidadProrrateo);
        System.out.printf("Complemento: %.2f€\n", bruto.getComplementoCategoria() / 14);
        System.out.printf("Trienios: %d€\n", trienio);
        System.out.printf("Antiguedad: %d años\n", anos);
        System.out.println("----------------------------------------------------");

        //Total deducciones, total devengos, Liquido a percibir
        System.out.printf("DESCUENTO TRABAJADOR: \n");

        System.out.printf("Contingencias generales: %.2f%% de %.2f = %.2f€\n", datosCuotas.getCuotaObreraGeneralTrabajador(), calculoBase, cuotasCalculadas[0]);
        System.out.printf("Desempleo: %.2f%% de %.2f = %.2f€\n", datosCuotas.getCuotaDesempleoTrabajador(), calculoBase, cuotasCalculadas[1]);
        System.out.printf("Cuota formación: %.2f%% de %.2f = %.2f€\n", datosCuotas.getCuotaFormacionTrabajador(), calculoBase, cuotasCalculadas[2]);
        System.out.printf("IRPF: %.2f%% de %.2f = %.2f€\n", porcentajeIRPF, brutoMes, irpf);
        System.out.printf("Total ingresos trabajador: %.2f€\n", brutoMes);
        System.out.printf("Total deducciones trabajador: %.2f€\n", totalTrabajador);
        System.out.printf("Liquido trabajador: %.2f€\n", brutoMes - totalTrabajador);
        System.out.println("----------------------------------------------------");

        System.out.printf("EMPRESARIO BASE: %.2f€\n", calculoBase);

        System.out.printf("Contingencias comunes empresario: %.2f%% = %.2f€\n", datosCuotas.getContingenciasComunesEmpresario(), cuotasCalculadas[3]);
        System.out.printf("FOGASA: %.2f%% = %.2f€\n", datosCuotas.getFogasaEmpresario(), cuotasCalculadas[4]);
        System.out.printf("Desempleo: %.2f%% = %.2f€\n", datosCuotas.getDesmpleoEmpresario(), cuotasCalculadas[5]);
        System.out.printf("Formación: %.2f%% = %.2f€\n", datosCuotas.getFormacionEmpresario(), cuotasCalculadas[6]);
        System.out.printf("Accidentes de trabajo: %.2f%% = %.2f€\n", datosCuotas.getAccidentesTrabajoEmpresario(), cuotasCalculadas[7]);
        System.out.printf("Pagos empresario: %.2f€\n", totalEmpleador);
        System.out.printf("Total empresario: %.2f€\n", totalEmpleador + brutoMes);

        if (calculoBase == 0.0) {
            System.out.println("\n****************FIN_EXTRA********************\n");
        }
        System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");

    }

    public static void imprimirArchivo(Trabajadorbbdd str, double[] cuotasCalculadas, Categorias bruto,
            double calculoBase, LocalDate fecha, double salarioBase, double brutoMes, double cantidadProrrateo,
            int anos, int trienio, double porcentajeIRPF, double irpf, double totalTrabajador, double totalEmpleador) {

        Locale SPAIN = new Locale("es", "ES");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String fechaAlta = formatter.format(str.getFechaAlta());

        String file = str.getNifnie() + str.getNombre() + str.getApellido1() + str.getApellido2()
                + fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase() + fecha.getYear();

        if (calculoBase == 0.0) {
            file += "EXTRA";
        }

        try {

            FileWriter myWriter = new FileWriter(String.format("resources\\nominasTxt\\%s.txt", file));

            if (calculoBase == 0.0) {
                myWriter.write("********************EXTRA********************\n");
            }

            myWriter.write(String.format("NOMINA de: %s - %s\n", fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase(), fecha.getYear()));

            myWriter.write(String.format("Empresa: %s\n", str.getEmpresas().getNombre()));
            myWriter.write(String.format("CIF: %s\n", str.getEmpresas().getCif()));
            myWriter.write("----------------------------------------------------\n");

            myWriter.write(String.format("Nombre: %s\n", str.getNombre()));
            myWriter.write(String.format("Apellidos: %s %s\n", str.getApellido1(), str.getApellido2()));
            myWriter.write(String.format("DNI: %s\n", str.getNifnie()));
            myWriter.write(String.format("Fecha de alta: %s\n", fechaAlta));
            myWriter.write(String.format("Categoria: %s\n", str.getCategorias().getNombreCategoria()));
            myWriter.write(String.format("Bruto Anual: %.2f€\n", bruto.getSalarioBaseCategoria()));
            myWriter.write(String.format("IBAN: %s\n", str.getIban()));

            myWriter.write(String.format("Salario Base: %.2f€\n", salarioBase));
            myWriter.write(String.format("Prorrateo: %.2f€\n", cantidadProrrateo));
            myWriter.write(String.format("Complemento: %.2f€\n", bruto.getComplementoCategoria() / 14));
            myWriter.write(String.format("Trienios: %d€\n", trienio));
            myWriter.write(String.format("Antiguedad: %d años\n", anos));

            myWriter.write("----------------------------------------------------\n");

            //Total deducciones, total devengos, Liquido a percibir
            myWriter.write(String.format("DESCUENTO TRABAJADOR: \n"));

            myWriter.write(String.format("Contingencias generales: %.2f%% de %.2f = %.2f€\n", datosCuotas.getCuotaObreraGeneralTrabajador(), calculoBase, cuotasCalculadas[0]));
            myWriter.write(String.format("Desempleo: %.2f%% de %.2f = %.2f€\n", datosCuotas.getCuotaDesempleoTrabajador(), calculoBase, cuotasCalculadas[1]));
            myWriter.write(String.format("Cuota formación: %.2f%% de %.2f = %.2f€\n", datosCuotas.getCuotaFormacionTrabajador(), calculoBase, cuotasCalculadas[2]));
            myWriter.write(String.format("IRPF: %.2f%% de %.2f = %.2f€\n", porcentajeIRPF, brutoMes, irpf));
            myWriter.write(String.format("Total ingresos trabajador: %.2f€\n", brutoMes));
            myWriter.write(String.format("Total deducciones trabajador: %.2f€\n", totalTrabajador));
            myWriter.write(String.format("Liquido trabajador: %.2f€\n", brutoMes - totalTrabajador));
            myWriter.write("----------------------------------------------------\n");

            myWriter.write(String.format("EMPRESARIO BASE: %.2f€\n", calculoBase));

            myWriter.write(String.format("Contingencias comunes empresario: %.2f%% = %.2f€\n", datosCuotas.getContingenciasComunesEmpresario(), cuotasCalculadas[3]));
            myWriter.write(String.format("FOGASA: %.2f%% = %.2f€\n", datosCuotas.getFogasaEmpresario(), cuotasCalculadas[4]));
            myWriter.write(String.format("Desempleo: %.2f%% = %.2f€\n", datosCuotas.getDesmpleoEmpresario(), cuotasCalculadas[5]));
            myWriter.write(String.format("Formación: %.2f%% = %.2f€\n", datosCuotas.getFormacionEmpresario(), cuotasCalculadas[6]));
            myWriter.write(String.format("Accidentes de trabajo: %.2f%% = %.2f€\n", datosCuotas.getAccidentesTrabajoEmpresario(), cuotasCalculadas[7]));
            myWriter.write(String.format("Pagos empresario: %.2f€\n", totalEmpleador));
            myWriter.write(String.format("Total empresario: %.2f€\n", totalEmpleador + brutoMes));
            myWriter.close();

            System.out.println("Nomina guardada correctamente.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void imprimirPDF(Trabajadorbbdd str, double[] cuotasCalculadas, Categorias bruto,
            double calculoBase, LocalDate fecha, double salarioBase, double brutoMes, double cantidadProrrateo,
            int anos, int trienio, double porcentajeIRPF, double irpf, double totalTrabajador, double totalEmpleador) {

        String imagen = "resources\\logo.png";
        Locale SPAIN = new Locale("es", "ES");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String fechaAlta = formatter.format(str.getFechaAlta());

        String file = str.getNifnie() + str.getNombre() + str.getApellido1() + str.getApellido2()
                + fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase() + fecha.getYear();

        if (calculoBase == 0.0) {
            file += "EXTRA";
        }
        try {

            PdfWriter writer = new PdfWriter(String.format("resources\\nominas\\%s.pdf", file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.LETTER);

            Paragraph parrafo;
            Table tabla1, tabla2, tabla3;
            LineSeparator lineaHor = new LineSeparator(new SolidLine(1f));

            tabla2 = new Table(2);
            tabla2.useAllAvailableWidth();
            tabla2.setBorder(Border.NO_BORDER);

            //Impresion del logo            
            Cell celda = new Cell();
            celda.setBorder(Border.NO_BORDER);
            Image img = new Image(ImageDataFactory.create(imagen));
            img.setBorder(Border.NO_BORDER);
            celda.add(img);
            tabla2.addCell(celda);

            //Impresion de los datos de la empresa
            celda = new Cell();
            celda.setBorder(Border.NO_BORDER);
            celda.setVerticalAlignment(VerticalAlignment.MIDDLE);
            celda.setTextAlignment(TextAlignment.CENTER);
            parrafo = new Paragraph(
                    String.format("%s", str.getEmpresas().getNombre()))
                    .setFontSize(14);
            celda.add(parrafo);
            parrafo = new Paragraph(String.format("CIF: %s", str.getEmpresas().getCif()))
                    .setFontSize(14);
            celda.add(parrafo);
            tabla2.addCell(celda);
            doc.add(tabla2);

            tabla1 = new Table(1);
            tabla1.useAllAvailableWidth();
            tabla1.setBorder(Border.NO_BORDER);

            //Impresion de la fecha
            celda = new Cell();
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph();
            if (calculoBase == 0.0) {
                parrafo.add(String.format("NOMINA: EXTRA %s - %s",
                        fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).
                                toUpperCase(), fecha.getYear()));
            } else {
                parrafo.add(String.format("NOMINA: %s - %s",
                        fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).
                                toUpperCase(), fecha.getYear()));
            }
            parrafo.setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setUnderline()
                    .setBold();
            celda.add(parrafo);
            tabla1.addCell(celda);
            doc.add(tabla1);
            doc.add(lineaHor);

            //Imprimir Datos Trabajador
            tabla2 = new Table(2);
            tabla2.useAllAvailableWidth();

            celda = new Cell();
            celda.setWidth(250);
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add(String.format("%s %s %s", str.getNombre(), str.getApellido1(), str.getApellido2()))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("DNI: %s", str.getNifnie()))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("Fecha de alta: %s", fechaAlta))
                    .setFontSize(12);
            celda.add(parrafo);
            tabla2.addCell(celda);

            celda = new Cell();
            celda.setWidth(250);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            celda.setTextAlignment(TextAlignment.RIGHT);
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add(String.format("IBAN: %s", str.getIban()))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("Categoria: %s", str.getCategorias().getNombreCategoria()))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("Bruto anual: %s€", str.getCategorias().getSalarioBaseCategoria()))
                    .setFontSize(12);
            celda.add(parrafo);
            tabla2.addCell(celda);
            doc.add(tabla2);
            doc.add(lineaHor);

            //Salario base + comp + antiguedad+ prorrateo
            //Titulo
            tabla1 = new Table(1);
            celda = new Cell();
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add("Importes a percibir")
                    .setFontSize(15);
            celda.add(parrafo);
            tabla1.addCell(celda);
            doc.add(tabla1);
            //Campos
            tabla2 = new Table(2);
            tabla2.useAllAvailableWidth();
            celda = new Cell();
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add("Salario Base:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Prorrateo:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Complemento:\n")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("Antiguedad:\t\t\t\t\t\t\t\t\t\t\t\t\t %d años", anos))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Total devengos:")
                    .setFontSize(12);
            celda.add(parrafo);
            tabla2.addCell(celda);
            //Valores
            celda = new Cell();
            celda.setWidth(200);
            celda.setBorder(Border.NO_BORDER);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            celda.setTextAlignment(TextAlignment.RIGHT);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", salarioBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cantidadProrrateo))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", bruto.getComplementoCategoria() / 14))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%d€", trienio))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", brutoMes))
                    .setFontSize(12);
            celda.add(parrafo);
            tabla2.addCell(celda);
            doc.add(tabla2);
            doc.add(lineaHor);

            //Deducciones Trabajador
            //Titulo
            tabla1 = new Table(1);
            celda = new Cell();
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add("Deducciones")
                    .setFontSize(15);
            celda.add(parrafo);
            tabla1.addCell(celda);
            doc.add(tabla1);
            //Campos
            tabla3 = new Table(3);
            tabla3.useAllAvailableWidth();
            celda = new Cell();
            celda.setWidth(200);
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add("Contingencias generales:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Desempleo:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Cuota formación:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("IRPF:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Total deducciones:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Total devengos:")
                    .setFontSize(12);
            celda.add(parrafo);
            tabla3.addCell(celda);
            //Porcentajes
            celda = new Cell();
            celda.setWidth(150);
            celda.setBorder(Border.NO_BORDER);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getCuotaObreraGeneralTrabajador(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getCuotaDesempleoTrabajador(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getCuotaFormacionTrabajador(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", porcentajeIRPF, brutoMes))
                    .setFontSize(12);
            celda.add(parrafo);
            tabla3.addCell(celda);
            //Valores
            celda = new Cell();
            celda.setWidth(150);
            celda.setBorder(Border.NO_BORDER);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            celda.setTextAlignment(TextAlignment.RIGHT);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[0]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[1]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[2]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", irpf))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", totalTrabajador))
                    .setFontSize(12)
                    .setFontColor(ColorConstants.RED);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", brutoMes - totalTrabajador))
                    .setFontSize(12);
            celda.add(parrafo);
            tabla3.addCell(celda);
            doc.add(tabla3);
            doc.add(lineaHor);

            //Calculos Empresario
            //Titulo
            tabla2 = new Table(2);
            celda = new Cell();
            celda.setWidth(250);
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add("Empresario BASE:")
                    .setFontSize(15);
            celda.add(parrafo);
            tabla2.addCell(celda);
            celda = new Cell();
            celda.setWidth(250);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            celda.setTextAlignment(TextAlignment.RIGHT);
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", calculoBase))
                    .setFontSize(15);
            celda.add(parrafo);
            tabla2.addCell(celda);
            doc.add(tabla2);
            //Campos
            tabla3 = new Table(3);
            tabla3.useAllAvailableWidth();
            celda = new Cell();
            celda.setWidth(200);
            celda.setBorder(Border.NO_BORDER);
            parrafo = new Paragraph()
                    .add("Contingencias comunes:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Desempleo:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Formación:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Accidentes de trabajo:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("FOGASA:")
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add("Total empresario:")
                    .setFontSize(12);
            celda.add(parrafo);
            tabla3.addCell(celda);
            //Porcentajes
            celda = new Cell();
            celda.setWidth(150);
            celda.setBorder(Border.NO_BORDER);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getContingenciasComunesEmpresario(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getDesmpleoEmpresario(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getFormacionEmpresario(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getAccidentesTrabajoEmpresario(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f%% de %.2f", datosCuotas.getFogasaEmpresario(), calculoBase))
                    .setFontSize(12);
            celda.add(parrafo);
            tabla3.addCell(celda);
            //Valores
            celda = new Cell();
            celda.setWidth(150);
            celda.setBorder(Border.NO_BORDER);
            celda.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            celda.setTextAlignment(TextAlignment.RIGHT);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[3]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[5]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[6]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[7]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", cuotasCalculadas[4]))
                    .setFontSize(12);
            celda.add(parrafo);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", totalEmpleador))
                    .setFontSize(12)
                    .setFontColor(ColorConstants.RED);
            celda.add(parrafo);
            tabla3.addCell(celda);
            doc.add(tabla3);

            //Total coste empresario
            tabla2 = new Table(2);
            tabla2.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
            tabla2.setVerticalBorderSpacing(10);
            tabla2.useAllAvailableWidth();
            celda = new Cell();
            celda.setBorder(new SolidBorder(2));
            parrafo = new Paragraph()
                    .add("COSTE TOTAL TRABAJADOR:")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.RED);
            celda.add(parrafo);
            tabla2.addCell(celda);
            celda = new Cell();
            celda.setBorder(new SolidBorder(2));
            celda.setTextAlignment(TextAlignment.RIGHT);
            parrafo = new Paragraph()
                    .add(String.format("%.2f€", totalEmpleador + brutoMes))
                    .setFontSize(12)
                    .setFontColor(ColorConstants.RED);
            celda.add(parrafo);
            tabla2.addCell(celda);
            doc.add(tabla2);

            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}