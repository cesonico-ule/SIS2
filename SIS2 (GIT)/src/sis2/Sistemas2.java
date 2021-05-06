package sistemas2;

import Modelo.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    public static void main(String[] args) {
        /*
            Primera practica - conexion con base de datos
            consultaDNI(peticionDatos());
            consultaDNI("09741138V");
        */
        try{
            datosTrabajadores = manejador.lecturaHoja(fichero, 3);
        }
        catch (IOException e) {
            e.printStackTrace();
	}
        
        boolean salir = false;
        Scanner sc = new Scanner(System.in);
        /*
        Segunda practica - Comprobacion de DNI, IBAN y generar correos
        while(!salir){
            System.out.println(
                    "¿Que desea hacer?\n"+
                    "1. Comprobar los DNIs\n"+
                    "2. Comprobar y generar las cuentas bancarias\n"+
                    "3. Generar los emails\n"+
                    "4. Tarea completa\n"+
                    "\n0. Salir\n");
            
            switch(sc.next()){
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
            }
        }
        */
        
            System.out.println("*** Introduzca el mes y año de la nomina a calcular ***");
             
            String[] parts = sc.next().split("-");
            
            if(parts[0].equals("06") || parts[0].equals("12")){
                //Se generan tambien las nominas de la extra para aquellos que no este prorrateada
                
            } else {
                //Default
                
            }
    }
    
    public static void compruebaDNIs(){
        ArrayList <String> lista = new ArrayList<>();
        for (String[] str : datosTrabajadores) {
            char letra = Herramientas.calculoNIE(str[7]);
            //compara la letra que deberia ser con la que tiene
            
            if (str[7] != null || str[7].length()!= 9 || "".equals(str[7])){
                //el dni no es valido
                
                //Errores.generarErrorDNI(str)
                
                
            }else{
                System.out.println("DNI leido: "+str[7]);
                if (lista.contains(str[7])){
                    //Errores.generarErrorDNI(str);
                }
                else{
                    lista.add(str[7]);
                    if (letra != str[7].charAt(8)){
                        //el dni no es correcto
                        str[7] = str[7].replace(str[7].charAt(8), letra);
                        //Errores.generarErrorDNI(str);
                    }
                }
            }
        }
    }
    
    public static void compruebaIBANs(){
        for (String[] str : datosTrabajadores) {
            String ccc = Herramientas.calculoCCC(str[9]); //anota el ccc correcto
            String iban = Herramientas.generaIBAN(ccc, str[10]);
            if (!ccc.equals(str[9])){//el CC NO es correcto
                //Errores.generaErrorCCC(str);
                //ManejadorExcel.actualizarCelda()
            }
            str[11] = iban;
            //ManejadorExcel.actualizarCelda()
                
            
        }
    }
    
    public static void creaEMAILs(){
        HashMap <String, Integer> lista = new HashMap<>();
        String correo;
        for (String[] str : datosTrabajadores) {
            
                                                //System.out.println("Paso 1");
                                                
            if (str[12] == null){//solo entra si no tiene email
                if (str[5] == null){//no tiene 2º apellido
                    correo = ""+str[6].charAt(0)+str[4].charAt(0);
                    
                                                    //System.out.println("Paso 2");
                                                    
                }else //tiene 2º apellido
                    correo = ""+str[6].charAt(0) + str[4].charAt(0) + str[5].charAt(0);
                
                                                    //System.out.println("Paso 3");

                if (lista.get(correo) == null){ //no hay ninguno mas
                    lista.put(correo, 1);
                    correo += "00@";

                }else {//ya hay alguno con ese nombre
                    lista.put(correo, lista.get(correo)+1);
                    correo += String.format("%02d", lista.get(correo)-1) + "@";
                }
                correo+= str[0] + ".com";
                System.out.println(correo);
                //ManejadorExcel.actualizarCelda(fichero, 3, hay_que_saber_la_fila, 12, correo);
            }
        }
    }
    
    public static String peticionDatos(){
        Scanner sc = new Scanner(System.in);
        
        //pedir dni del empleado
        System.out.println("Introduzca el DNI del trabajador solicitado.\n");
        return sc.nextLine();
    }
    
    public static void consultaDNI (String dni){
        SessionFactory sf = null;
        Session sesion = null;
        Transaction tr = null; //No se usa en consultas
        Empresas emp = null;
        
        try{
            sf = HibernateUtil.getSessionFactory();
            sesion = sf.openSession();

            
            String consultaHQL = "FROM Trabajadorbbdd t WHERE t.nifnie = :param1"; //t es alias obligatorio
            
            Query query = sesion.createQuery(consultaHQL);
            query.setParameter("param1", dni);
            
            List<Trabajadorbbdd> listaResultado = query.list();
            
            //Tarea 1
            if (listaResultado.isEmpty()){
                System.out.println("\nNo conocemos a nadie con esos datos");
            }
            else{
                Trabajadorbbdd ttbd = listaResultado.get(0);//solo puede haber un empleado con el dni
                
                System.out.println("Nombre:\t\t" + ttbd.getNombre());
                System.out.println("Apellidos:\t"+ ttbd.getApellido1()+"\t"+ttbd.getApellido2());
                System.out.println("NIF:\t\t" + ttbd.getNifnie());
                System.out.println("Categoria:\t"+ ttbd.getCategorias().getNombreCategoria());
                System.out.println("Empresa:\t"+ ttbd.getEmpresas().getNombre());
                
                Set<Nomina> listaNominas =  ttbd.getNominas();
                
                for(Nomina a: listaNominas ){
                    System.out.println("Año:\t"+ a.getAnio()+"\tMes:\t"+a.getMes());
                    System.out.printf("Bruto:\t"+a.getBrutoNomina()+" €\n");
                }
                System.out.println("*******************************");
                emp = ttbd.getEmpresas();
            }
            
            //Tarea 2
            if (emp != null){
                
                consultaHQL = "FROM Empresas e WHERE e.idEmpresa != :param1"; 

                query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", emp.getIdEmpresa());
                
                List<Empresas> listaResulEmp = query.list();
                for(Empresas ebd: listaResulEmp){
                            System.out.println(ebd.getNombre());
                    ebd.setNombre(ebd.getNombre()+"_2021");
                    tr = sesion.beginTransaction();
                    sesion.save(ebd);
                    tr.commit();
                }
                
                //Tarea 3
                for (Empresas ebd : listaResulEmp) {
                    Set<Trabajadorbbdd> ts = ebd.getTrabajadorbbdds();
                    
                    for (Trabajadorbbdd tb : ts) {
                        System.out.println("Borrando nominas del trabajador: "+tb.getIdTrabajador()+" de la empresa: "+ebd.getIdEmpresa());
                        
                        tr = sesion.beginTransaction();
                        String HQLborrado = "DELETE Nomina n WHERE n.trabajadorbbdd.idTrabajador=:param3";
                        sesion.createQuery(HQLborrado).setParameter("param3", tb.getIdTrabajador()).executeUpdate();
                        tr.commit();
                    }
                    System.out.println("Borrando trabajadores de la empresa: "+ebd.getIdEmpresa());

                    tr = sesion.beginTransaction();
                    String HQLborrado = "DELETE Trabajadorbbdd t WHERE t.empresas.idEmpresa=:param4";
                    sesion.createQuery(HQLborrado).setParameter("param4", ebd.getIdEmpresa()).executeUpdate();
                    tr.commit();
                }
            }
            sesion.close();
            HibernateUtil.shutdown();
        }
        catch(Exception e){
            sesion.close();
            HibernateUtil.shutdown();
            System.err.println("No está el horno para bollos\n" + e.getMessage());
        }
        
    }

    
}