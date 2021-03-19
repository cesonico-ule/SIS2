/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2;

import modelo.HibernateUtil;
import org.hibernate.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import modelo.Empresas;
import modelo.Nomina;
import modelo.Trabajadorbbdd;






public class Sistemas2 {

    public static void main(String[] args) {
        consultaDNI(peticionDatos());
        //consultaDNI("09741138");
        
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
                // --- 1 ---
                for(Trabajadorbbdd tbd: listaResultado){
                    System.out.println("Nombre: " + tbd.getNombre());
                    System.out.println("Apellidos: " + tbd.getApellido1() + " " +tbd.getApellido2());
                    System.out.println("NIF: " + tbd.getNifnie());
                    System.out.println("Categoría: " + tbd.getCategorias().getNombreCategoria());
                    
                    Empresas e = tbd.getEmpresas();
                    
                    System.out.println("Empresa: " + tbd.getEmpresas().getNombre());
                    
                    Set<Nomina> listaNominas = tbd.getNominas();
                    
                    for(Nomina a: listaNominas){
                        
                        System.out.println("Año: " +a.getAnio()+" Mes: "+a.getMes());
                        System.out.println("Bruto: "+a.getBrutoNomina());
                        
                    }
                    System.out.println("**************************");
                    emp = tbd.getEmpresas();
                }
                // --- 2 ---
                if (emp != null){
                //actualizaEmpresaExcepto(emp);
                consultaHQL = "FROM Empresas e WHERE e.idEmpresa != :param1"; //t es alias obligatorio

                query = sesion.createQuery(consultaHQL);
                query.setParameter("param1", emp.getIdEmpresa());
                
                List<Empresas> listaResulEmp = query.list();
                for(Empresas ebd: listaResulEmp){
                    ebd.setNombre(ebd.getNombre()+"_2021");
                    System.out.println(ebd.getNombre());
                    tr = sesion.beginTransaction();
                    sesion.save(ebd);
                    tr.commit();
                }

                for (Empresas ebd : listaResulEmp) {
                        Set<Trabajadorbbdd> ts = ebd.getTrabajadorbbdds();
                        for(Trabajadorbbdd tb: ts){
                            Set<Nomina> listaNominas = tb.getNominas();
                            for(Nomina nom: listaNominas){
                                System.out.println(nom.toString());
                                tr = sesion.beginTransaction();                                
                                String HQLborrado = "DELETE Nomina n WHERE n.idNomina=:param3";
                                sesion.createQuery(HQLborrado).setParameter("param3", nom.getIdNomina()).executeUpdate();
                                tr.commit();                                
                            }
                            tr = sesion.beginTransaction();                                
                            String HQLborrado = "DELETE Trabajadorbbdd t WHERE t.idTrabajador=:param4";
                            sesion.createQuery(HQLborrado).setParameter("param4", tb.getIdTrabajador()).executeUpdate();
                            tr.commit();  
                        }


                }

            }
            sesion.close();
                
                // --- 3 ---
                //Empresas listaEmpresas = ;
                
                


                //for(Empresas emp: ){
                //    
                
                
            }
            HibernateUtil.shutdown();
        }catch(Exception e){
            System.err.println("Laemos liao" + e.getMessage());
        }
        
    }
  
}
