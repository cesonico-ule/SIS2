/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2;

import Modelo.*;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Santiago Ares Alfayate
 */
public class Sistemas21 {

    public static void main(String[] args) {
        //consultaDNI(peticionDatos());
        consultaDNI("09741138");
        
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
        Transaction tr = null;
            
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
                    System.out.println("Nombre:\t" + tbd.getNombre());
                    System.out.println("NIF:\t" + tbd.getNifnie());
                    System.out.println("**************************");
                }
            }
            HibernateUtil.shutdown();
        }
        catch(Exception e){
            System.err.println("No está el horno para bollos" + e.getMessage());
        }
    }
}
