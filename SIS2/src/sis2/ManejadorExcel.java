package sistemas2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Date;
import java.text.SimpleDateFormat;
;
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
        String[] valoresFilas = new String[14]; //hay 13 campos en la hoja el 14 es la fila
        
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(hoja-1);
        
        //estoy en la hoja que he querido leer
        //leo todas las filas
        
        for (Row fila : sheet){
            if(!isRowEmpty(fila)){
                for (Cell celda: fila){


                    if(celda.getRowIndex()!=0){ //no queremos los titulos de las tablas
                        if (celda.getColumnIndex()==3){ //esto tiene formato fecha
                            
                            Date date = celda.getDateCellValue();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            String fecha = formatter.format(date);
                            System.out.println(fecha);
                                                        
                            valoresFilas[celda.getColumnIndex()] = fecha;

                        }                        
                        else{
                            valoresFilas[celda.getColumnIndex()]=celda.getStringCellValue();
                        }
                    }
                }
                valoresFilas[13]= String.valueOf(fila.getRowNum());
                if (fila.getRowNum() != 0) {
                    valoresCeldas.add(valoresFilas.clone());
                    for (int k = 0; k < valoresFilas.length; k++) {
                        valoresFilas[k] = null;
                    }
                }
            }
        }
        workbook.close();
        return valoresCeldas;
    }
    
    private static boolean isRowEmpty(Row row) {
	boolean isEmpty = true;
        DataFormatter dataFormatter = new DataFormatter();

        if (row != null) {
            for (Cell cell : row) {
                if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
                    isEmpty = false;
                    break;
                }
            }
        }

        return isEmpty;
    }
    
    public static void actualizarCelda(String archivo, int hoja, int fila, int columna, String dato){

        FileInputStream file = null;
        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        Row row;
        Cell cell;
        FileOutputStream outFile;

        try {
            //declaraciones
            file = new FileInputStream(new File(archivo));
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheetAt(hoja - 1);
            row = sheet.getRow(fila);
            cell = row.getCell(columna);

            if (cell == null) {
                cell = row.createCell(columna);
            }
            //poner valores
            cell.setCellValue(dato);
            file.close();
            outFile = new FileOutputStream(new File(archivo));
            workbook.write(outFile);
            outFile.close();
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}