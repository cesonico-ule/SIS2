/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2;

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
    
    public static void actualizaNombreExcepto(Empresas empresaExcepcion){
        System.out.println(empresaExcepcion.g)
    }
    public static void consultaDNI (String dni){
        SessionFactory sf = null;
        Session sesion = null;
        Transaction tr = null;
        List<Integer> empresasNO = new ArrayList<Integer>();

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
                for(Trabajadorbbdd tbd: listaResultado){
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
                        
            HibernateUtil.shutdown();
            //Tarea 2
            actualizaNombreExcepto(empresasNO);
        }
        catch(Exception e){
            System.err.println("No está el horno para bollos " + e.getMessage());
        }
    }
