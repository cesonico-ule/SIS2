package sistemas2;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Errores {
 
    public static void generaErrorDNI(ArrayList<EmpleadoWorbu> empList){
        String fileName = "resources\\ErroresDNI";
        String extension = ".xml";
        Document doc = new Document();
        
        doc.setRootElement(new Element("Trabajadores"));
        for(EmpleadoWorbu emp : empList){
            Element employee = new Element("Trabajador");
            employee.setAttribute("id",""+emp.getFila());
            employee.addContent(new Element("Nombre").setText(emp.getNombre()));
            employee.addContent(new Element("PrimerApellido").setText(emp.getApellido1()));
            employee.addContent(new Element("SegundoApellido").setText(emp.getApellido2()));
            employee.addContent(new Element("Empresa").setText(emp.getNombreEmpresa()));
            employee.addContent(new Element("Categoria").setText(emp.getCategoria()));
            doc.getRootElement().addContent(employee);
        }
        
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        
        try{
            File f = new File(fileName+extension);
            while (f.exists()) {
                fileName += "(1)";
                f = new File(fileName+extension);
            }
            xmlOutputter.output(doc, new FileOutputStream(fileName+extension));
        }
        catch (FileNotFoundException e){
            System.out.println("Archivo no encontrado");
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("Error IO");
            e.printStackTrace();
        }
    }
   
    public static void generaErrorCCC(ArrayList<EmpleadoWorbu> empList){
        String fileName = "resources\\ErroresCCC";        
        String extension = ".xml";

        Document doc = new Document();
        
        doc.setRootElement(new Element("Cuentas"));
        for(EmpleadoWorbu emp : empList){
            Element employee = new Element("Cuenta");
            employee.setAttribute("id",""+emp.getFila());
            employee.addContent(new Element("Nombre").setText(emp.getNombre()));
            employee.addContent(new Element("PrimerApellido").setText(emp.getApellido1()));
            employee.addContent(new Element("SegundoApellido").setText(emp.getApellido2()));
            employee.addContent(new Element("Empresa").setText(emp.getNombreEmpresa()));
            //employee.addContent(new Element("CuentaErronea").setText(emp.getCodCuentaError()));            
            employee.addContent(new Element("IBAN").setText(emp.getIban()));
            doc.getRootElement().addContent(employee);
        }
        
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        
        try{
            File f = new File(fileName+extension);
            while (f.exists()){
                fileName += "(1)";
                f = new File(fileName+extension);
            }
            xmlOutputter.output(doc, new FileOutputStream(fileName+extension));
        }
        catch (FileNotFoundException e){
            System.out.println("Archivo no encontrado");
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("Error IO");
            e.printStackTrace();
        }
    }
}

