/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2;

//import javax.transaction.Transaction;

import java.util.List;
import javax.management.Query;

import net.sf.ehcache.hibernate.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Nomad
 */
public class Sistemas2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //Ctrl+Shift+i
        SessionFactory sf = null;
        Session sesion = null;
        Transaction tx = null;
        
        try{
            sf = HibernateUtil.getSessionFactory();
            sesion = sf.openSession();
            
            String consultaHQL = "FROM Trabajadorbbdd t";
            
            Query query = sesion.createQuery(consultaHQL);
            List<Trabajadorbbdd> listaResultado = query.list();
            
            for(Trabajadorbbdd tbd:listaResultado){
                System.out.println("nombre " + tbd.getnombre());
                System.out.println("NIF" + tbd.getNifnie());
                System.out.println("**************************");

            }
            
        } catch(Exception e){
            
        }
        
    }
    
}
