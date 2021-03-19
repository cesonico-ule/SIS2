/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas2;

import Modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Santiago Ares Alfayate
 */
public class Sistemas2 {

    public static void main(String[] args) {
        //consultaDNI(peticionDatos());
        consultaDNI("09741138V");
        
    }
    
    public static String peticionDatos(){
        
        Scanner sc = new Scanner(System.in);
        
        //pedir dni del empleado
        System.out.println("Introduzca el DNI del trabajador solicitado.\n");
        return sc.nextLine();
    }
    
    public static void actualizaEmpresaExcepto(Empresas empresaExcepcion){
        SessionFactory sf = null;
        Session sesion = null;
        Transaction tr = null; //No se usa
        
        try{
                            System.out.println("PRUebas 1");
            sf = HibernateUtil.getSessionFactory();
                            System.out.println("PRUebas 3");

            sesion = sf.openSession(); //no abre una nueva sesion

                            System.out.println("PRUebas 684");
            String consultaHQL = "FROM Empresas e WHERE e.idEmpresa != :param1"; //t es alias obligatorio
                            System.out.println("PRUebas 321");
            Query query = sesion.createQuery(consultaHQL);
            query.setParameter("param1", empresaExcepcion.getIdEmpresa());
                            System.out.println("PRUebas 987");
                            
            List<Empresas> listaResultado = query.list();
            /*
            for(Empresas ebd: listaResultado){
                            System.out.println("PRUebas 34");
                ebd.setNombre(ebd.getNombre()+"2021");
            }
            */
            HibernateUtil.shutdown();
        }
        catch(Exception e){
            HibernateUtil.shutdown();
            System.err.println("No está el horno para bollos " + e.getMessage());
        }
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
            if (listaResultado.isEmpty()){
                System.out.println("\nNo conocemos a nadie con esos datos");
            }
            else{
                Trabajadorbbdd ttbd = listaResultado.get(0);//solo puede haber un empleado con el dni
                
                System.out.println("Nombre:\t\t" + ttbd.getNombre());
                System.out.println("Apellidos:\t"+ ttbd.getApellido1()+"\t"+ttbd.getApellido2());
                System.out.println("NIF:\t\t" + ttbd.getNifnie());
                //System.out.println("Fecha incorporacion:\t"+ tbd.getFechaAlta());
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
            /*    for(Trabajadorbbdd tbd: listaResultado){
                    System.out.println("Nombre:\t\t" + tbd.getNombre());
                    System.out.println("Apellidos:\t"+ tbd.getApellido1()+"\t"+tbd.getApellido2());
                    System.out.println("NIF:\t\t" + tbd.getNifnie());
                    //System.out.println("Fecha incorporacion:\t"+ tbd.getFechaAlta());
                    System.out.println("Categoria:\t"+ tbd.getCategorias().getNombreCategoria());                    
                    System.out.println("Empresa:\t"+ tbd.getEmpresas().getNombre()); 
                    Set<Nomina> listaNominas =  tbd.getNominas();
                    for(Nomina a: listaNominas ){
                        System.out.println("Año:\t"+ a.getAnio()+"\tMes:\t"+a.getMes());
                        System.out.printf("Bruto:\t"+a.getBrutoNomina()+" €\n");
                    }
                    System.out.println("*******************************");
                    
                    
                    //Tarea 2
                    empresasNO.add(tbd.getEmpresas().getIdEmpresa());
                    
                }
            }
            */
            sesion.close();
            HibernateUtil.shutdown();
        }
        catch(Exception e){
            sesion.close();
            HibernateUtil.shutdown();
            System.err.println("No está el horno para bollos " + e.getMessage());
        }
        if (emp != null) actualizaEmpresaExcepto(emp);
    }
}