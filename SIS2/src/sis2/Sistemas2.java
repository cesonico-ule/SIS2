package sistemas2;

import Modelo.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
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
    static Map<String, double[]> categorias = null;
    static ArrayList<Integer> trienios = null;
    static TreeMap<Double, Double> brutoExcel = null;
    static SalarioDatos datosCuotas = null;

    public static void main(String[] args) {
        /*
            Primera practica - conexion con base de datos
            consultaDNI(peticionDatos());
            consultaDNI("09741138V");
         */
        try {
            //Lectura excel
            empleados = manejador.lecturaTrabajadores(fichero);
            categorias = manejador.leerCategorias(fichero);
            trienios = manejador.leerTrienios(fichero);
            brutoExcel = manejador.leerBruto(fichero);
            datosCuotas = manejador.leerCuotas(fichero);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
___________________Fin obtencion de datos___________________
         */

//        Practica 3 - Ejecucion para la entrega
        compruebaDNIs();
        compruebaIBANs();
        creaEMAILs();
        generaNominas("12/2021", true);

        boolean salir = false;
        Scanner sc = new Scanner(System.in);
        while (!salir) {
            System.out.println(
                    "¿Que desea hacer?\n"
                    + "1. Comprobar los DNIs\n"
                    + "2. Comprobar y generar las cuentas bancarias\n"
                    + "3. Generar los emails\n"
                    + "4. Tarea completa\n"
                    + "5. Generar nominas por pantalla"
                    + "5. Generar nominas en archivos"
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
        }
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
                System.out.println("DNI leido: " + str.getDni().substring(0, 8));

                if (lista.contains(str.getDni().substring(0, 8))) {//DNI ya procesado - error
                    System.out.println("DNI ya procesado - Error");
                    errorEmp.add(str);

                } else {//entramos cuando hay que procesar el DNI
                    lista.add(str.getDni().substring(0, 8));
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

    public static void generaNominas(String fecha, boolean archivo) {
        
        double calculoBase, brutoAnual, brutoMes;
        double[] bruto;double[] cuotasCalculadas;double[] cuotas0;
        double irpf, calculoirpf, porcentajeIRPF;
        double cantidadProrrateo,salarioBase;
        int trienio, anos, meses, anos_enero, anos_dic;
        boolean cambio_trienio = false;
        String[] parte = fecha.split("/");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        LocalDate fech = LocalDate.parse("01/" + fecha, formato);
        LocalDate enero = LocalDate.parse("01/01/" + parte[1], formato);
        LocalDate dic = LocalDate.parse("01/12/" + parte[1], formato);
        
        for (EmpleadoWorbu str : empleados) {

            LocalDate alta = LocalDate.parse(str.getFechaAltaEmpresa(), formato);

            anos = (int) ChronoUnit.YEARS.between(alta, fech);//antiguedad a dia de nomina
            meses = (int) ChronoUnit.MONTHS.between(alta, fech);//antiguedad a dia de nomina
            anos_enero = (int) ChronoUnit.YEARS.between(alta, enero);
            anos_dic = (int) ChronoUnit.YEARS.between(alta, dic);
            if (anos_enero != anos_dic) {
                cambio_trienio = true;
            }

            bruto = categorias.get(str.getCategoria());
            brutoAnual = bruto[0] + bruto[1]; //sin antiguedad ni prorrateo
            trienio = trienios.get(anos / 3);
            brutoMes = bruto[0] / 14 + bruto[1] / 14 + trienio;
            cantidadProrrateo = brutoMes / 6;
            calculoBase = brutoMes + cantidadProrrateo;

            if (!cambio_trienio) {//no hay cambio de trienio
                calculoirpf = brutoAnual + trienio * 14;

            } else if (anos > 0) {//hay cambio de trienio
                calculoirpf = brutoAnual + trienios.get(anos_enero / 3) * fech.getMonthValue() + trienio * (12 - fech.getMonthValue());

            } else {//recien contratado
                if (str.isProrrata()) {
                    calculoirpf = brutoAnual / 12 * meses;
//                    brutoMes = brutoAnual/12;
//                    if (meses>5) meses++;
//                    brutoMes *= meses;

                } else {
                    meses = 12 - alta.getMonthValue();
                    if (meses > 5) {
                        meses++;
                    }

                    calculoirpf = brutoAnual / 14 * meses;
//                    brutoMes = brutoAnual/14*meses;
                    if (meses - 6 > 0) {
                        calculoirpf += brutoAnual / 14 * ((meses - 6) / 6);
//                        brutoMes += brutoAnual / 14 * ((meses - 6) / 6);;
                    }
                }
            }
            porcentajeIRPF = brutoExcel.get(brutoExcel.ceilingKey(calculoirpf));
            salarioBase = bruto[0] / 14;
            if (str.isProrrata()) {
                irpf = calculoBase * porcentajeIRPF / 100;
                brutoMes = calculoBase;
//                salarioBase = bruto[0] / 14;

            } else {
                irpf = brutoMes * porcentajeIRPF / 100;
                //salarioBase = (bruto[0] - cantidadProrrateo) / 14;
                cantidadProrrateo = 0;
                //irpf se hace sobre salarioBase
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
            // --- IMPRIMIR --- //
            if (archivo) {
                imprimirArchivo(str, cuotasCalculadas, bruto, calculoBase, fech, salarioBase, brutoMes,
                        cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, totalTrabajador, totalEmpleador);

                if ((parte[0].equals("6") || parte[0].equals("12")) && !str.isProrrata()) {
                    imprimirArchivo(str, cuotas0, bruto, 0.0, fech, salarioBase, brutoMes,
                            cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, irpf, 0.0);
                }
            } else {
                imprimeNominas(str, cuotasCalculadas, bruto, calculoBase, fech, salarioBase, brutoMes,
                        cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, totalTrabajador, totalEmpleador);

                if ((parte[0].equals("6") || parte[0].equals("12")) && !str.isProrrata()) {
                    imprimeNominas(str, cuotas0, bruto, 0.0, fech, salarioBase, brutoMes,
                            cantidadProrrateo, anos, trienio, porcentajeIRPF, irpf, irpf, 0.0);
                }
            }
        }
    }

    public static void imprimeNominas(EmpleadoWorbu str, double[] cuotasCalculadas, double[] bruto,
            double calculoBase, LocalDate fecha, double salarioBase, double brutoMes, double cantidadProrrateo,
            int anos, int trienio, double porcentajeIRPF, double irpf, double totalTrabajador, double totalEmpleador) {

        Locale SPAIN = new Locale("es", "ES");
        if (calculoBase == 0.0) {
            System.out.println("\n********************EXTRA********************");
        }

        System.out.printf(String.format("NOMINA de: %s - %s\n", fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase(), fecha.getYear()));

        System.out.printf("Empresa: %s\n", str.getNombreEmpresa());
        System.out.printf("CIF: %s\n", str.getCifEmpresa());
        System.out.println("----------------------------------------------------");

        System.out.printf("Nombre: %s\n", str.getNombre());
        System.out.printf("Apellidos: %s %s\n", str.getApellido1(), str.getApellido2());
        System.out.printf("DNI: %s\n", str.getDni());
        System.out.printf("Fecha de alta: %s\n", str.getFechaAltaEmpresa());
        System.out.printf("Categoria: %s\n", str.getCategoria());
        System.out.printf("Bruto Anual: %.2f€\n", bruto[0]);
        System.out.printf("IBAN: %s\n", str.getIban());

        System.out.printf("Salario Base: %.2f€\n", salarioBase);
        System.out.printf("Prorrateo: %.2f€\n", cantidadProrrateo);
        System.out.printf("Complemento: %.2f€\n", bruto[1] / 14);
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

    public static void imprimirArchivo(EmpleadoWorbu str, double[] cuotasCalculadas, double[] bruto,
            double calculoBase, LocalDate fecha, double salarioBase, double brutoMes, double cantidadProrrateo,
            int anos, int trienio, double porcentajeIRPF, double irpf, double totalTrabajador, double totalEmpleador) {

        Locale SPAIN = new Locale("es", "ES");

        String file = str.getDni() + str.getNombre() + str.getApellido1() + str.getApellido2()
                + fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase() + fecha.getYear();

        if (calculoBase == 0.0) {
            file += "EXTRA";
        }

        try {

            FileWriter myWriter = new FileWriter(String.format("resources\\nominas\\%s.txt", file));

            if (calculoBase == 0.0) {
                myWriter.write("********************EXTRA********************\n");
            }

            myWriter.write(String.format("NOMINA de: %s - %s\n", fecha.getMonth().getDisplayName(TextStyle.FULL, SPAIN).toUpperCase(), fecha.getYear()));

            myWriter.write(String.format("Empresa: %s\n", str.getNombreEmpresa()));
            myWriter.write(String.format("CIF: %s\n", str.getCifEmpresa()));
            myWriter.write("----------------------------------------------------\n");

            myWriter.write(String.format("Nombre: %s\n", str.getNombre()));
            myWriter.write(String.format("Apellidos: %s %s\n", str.getApellido1(), str.getApellido2()));
            myWriter.write(String.format("DNI: %s\n", str.getDni()));
            myWriter.write(String.format("Fecha de alta: %s\n", str.getFechaAltaEmpresa()));
            myWriter.write(String.format("Categoria: %s\n", str.getCategoria()));
            myWriter.write(String.format("Bruto Anual: %.2f€\n", bruto[0]));
            myWriter.write(String.format("IBAN: %s\n", str.getIban()));

            myWriter.write(String.format("Salario Base: %.2f€\n", salarioBase));
            myWriter.write(String.format("Prorrateo: %.2f€\n", cantidadProrrateo));
            myWriter.write(String.format("Complemento: %.2f€\n", bruto[1] / 14));
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
}
