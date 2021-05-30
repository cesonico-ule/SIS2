package sistemas2;

import Modelo.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class ManejadorExcel {
    
    public ArrayList<EmpleadoWorbu> lecturaTrabajadores(String archivo) throws IOException{
        
        ArrayList<String[]> valoresCeldas = new ArrayList<>();
        String[] valoresFilas = new String[14]; //hay 13 campos en la hoja el 14 es la fila
        
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(2);
        
        //leo todas las filas
        for (Row fila : sheet){
            if(!isRowEmpty(fila)){
                for (Cell celda: fila){


                    if(celda.getRowIndex()!=0){ //no queremos los titulos de las tablas
                        if (celda.getColumnIndex()==3){ //esto tiene formato fecha
                            
                            Date date = celda.getDateCellValue();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String fecha = formatter.format(date);
                            //System.out.println(fecha);
                                                        
                            valoresFilas[celda.getColumnIndex()] = fecha;

                        }                        
                        else{
                            valoresFilas[celda.getColumnIndex()] = celda.getStringCellValue();
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
        ArrayList<EmpleadoWorbu> empleados = new ArrayList<>();
        for (String[] datos : valoresCeldas) {
            EmpleadoWorbu aux = new EmpleadoWorbu(datos);
            empleados.add(aux);
        }
        return empleados;
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
        
    public ArrayList<Integer> leerTrienios(String archivo)throws IOException{
        
        ArrayList<Integer> trienios = new ArrayList<>();
        trienios.add(0);
        
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(0);
        
        for (int i=1; i<19; i++){//recorre las 18 primeras filas
            Integer  valor = (int) sheet.getRow(i).getCell(6).getNumericCellValue(); //Valor salario
            
            trienios.add(valor);            
        }
        workbook.close();
        return trienios;
    }
    
    public TreeMap<Double, Double> leerBruto(String archivo)throws IOException{
        
        TreeMap<Double, Double> trienios = new TreeMap<>() ;
                
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(1);
        
        for (int i=1; i<50; i++){//recorre las 18 primeras filas
            double bruto = sheet.getRow(i).getCell(0).getNumericCellValue();//valor bruto
            double valor = sheet.getRow(i).getCell(1).getNumericCellValue(); //Valor valor
            
            trienios.put(bruto, valor);            
        }
        workbook.close();
        return trienios;
    }
    
    public SalarioDatos leerCuotas(String archivo)throws IOException{
        
        double[] cuotas = new double [8];
        
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(1);
        
        for (int i=0; i<8; i++){//recorre las 18 primeras filas            
            double valor = sheet.getRow(i).getCell(6).getNumericCellValue(); //Valor salario   
            
            cuotas[i] = valor;            
        }
        workbook.close();
        SalarioDatos aux = new SalarioDatos(cuotas);
        return aux;
    }
        
    public Map<String, Categorias> leerCategoria(String archivo)throws IOException{
        
        Map<String, Categorias> categorias = new HashMap();
        Categorias base = null;
                
        FileInputStream file = new FileInputStream(new File(archivo));
	XSSFWorkbook workbook = new XSSFWorkbook(file);
	XSSFSheet sheet = workbook.getSheetAt(0);
        
        for (int i=1; i<15; i++){//recorre las 14 primeras filas
            String categoria = sheet.getRow(i).getCell(0).getStringCellValue();//Nombre categoria
            double salario = sheet.getRow(i).getCell(1).getNumericCellValue(); //Valor salario
            double complemento = sheet.getRow(i).getCell(2).getNumericCellValue(); //Valor complemento
           
            base = new Categorias(categoria,salario,complemento);
            categorias.put(categoria,base);            
        }
        workbook.close();
        return categorias;
    }
}