package sistemas2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class ManejadorExcel {
    
    /*
     Recibe la hoja X del archivo Y como lista de datos
    */
    public ArrayList<String[]> lecturaTabla(String archivo, int hoja) throws IOException{
        
        ArrayList<String[]> valoresCeldas = new ArrayList<String[]>();
        
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(hoja-1);
        
        //estoy en la hoja que he querido leer
        //leo todas las filas
        DataFormatter dataFormatter = new DataFormatter(); //strings de celdas
        
        for (Row r: sheet) {
            for(Cell c: r) {
                //String cellValue = dataFormatter.formatCellValue(c);
                
            }
            
        }
        return null;
        
    }
}
