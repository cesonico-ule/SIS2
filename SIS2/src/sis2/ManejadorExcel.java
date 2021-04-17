package sistemas2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
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
    public ArrayList<String[]> lecturaHoja(String archivo, int hoja) throws IOException{
        
        ArrayList<String[]> valoresCeldas = new ArrayList<String[]>();
        String[] valoresFilas = new String[13]; //hay 13 campos en la hoja
        
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(hoja-1);
        
        //estoy en la hoja que he querido leer
        //leo todas las filas
        
        for (Row r : sheet){
            for (Cell c: r){
                
                if(c.getRowIndex()!=0){ //no queremos los titulos de las tablas
                    if (c.getColumnIndex()==3){ //esto tiene formato fecha
                        valoresFilas[c.getColumnIndex()]=c.getDateCellValue().toString();
                        System.out.println(c.getDateCellValue());
                    }
    /*                else if (c.getColumnIndex() == 9){//campo unicamente numerico - error si tratamos de leer un string
                        
                    }*/
                    else{
                        valoresFilas[c.getColumnIndex()]=c.getStringCellValue();
                        System.out.println(c.getStringCellValue());
                    }
		}
            }
            if (r.getRowNum() != 0) {
                valoresCeldas.add(valoresFilas.clone());
                for (int k = 0; k < valoresFilas.length; k++) {
                    valoresFilas[k] = null;
                }
            }
        }
        workbook.close();
        return valoresCeldas;
    }

}