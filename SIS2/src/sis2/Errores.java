package sistemas2;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
 
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
 
public class Errores {
 
    public static void generaErrorDNI(String[] persona){
        String xmlFilePath = "resources\\ErroresDNI.xml";
        try {
            //ejemplo de xml
            
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
 
            // Elemento root
            Element root = document.createElement("Empresa");
            document.appendChild(root);
 
            // Elemento empleado
            Element empleado = document.createElement("Empleado");
 
            root.appendChild(empleado);
 
            // Le da un id
            Attr attr = document.createAttribute("id");
            attr.setValue("10");
            empleado.setAttributeNode(attr);
 
            //staff.setAttribute("id", "1")
 
            // Elemento Nombre
            Element firstName = document.createElement("Nombre");
            firstName.appendChild(document.createTextNode("Julio"));
            empleado.appendChild(firstName);
 
            // Elemento apellido
            Element lastname = document.createElement("Apellido");
            lastname.appendChild(document.createTextNode("Gutierrez"));
            empleado.appendChild(lastname);
 
            // Elemento email
            Element email = document.createElement("email");
            email.appendChild(document.createTextNode("JG00@Recursos.com"));
            empleado.appendChild(email);
 
            // Crea el XML
            // Transforma el objeto DOM en el archivo xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            DOMSource dom = new DOMSource(document);
            
            transformer.transform(dom, streamResult);
 
            System.out.println("Creado el archivo XML");
 
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
   
    public static void generaErrorCCC(){
        
    }
}
