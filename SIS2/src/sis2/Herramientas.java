package sis2;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class Herramientas {
    /*
     *Le envias el DNI bien formateado y te devuelve la letra que le corresponde
    */
    
    public static void main (String [] Args){
        String s = "11112223504444444444";
        System.out.println(generaIBAN(s, "ES"));
        
        /*
        System.out.println(generaEMAIL ("Cesar", "Bermejo", "", "Securitas"));
        calculoCCC(s);
        */
    }
    
    public static char calculoNIE(String num){
        char[] base = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J',
            'Z','S','Q','V','H','L','C','K','E'};
        int numero;
        switch(num.charAt(0)){
            case 'X'://X=0
                numero=Integer.parseInt(num.substring(1, 8));
                break;
                
            case 'Y'://Y=1
                numero=10000000+Integer.parseInt(num.substring(1, 8));
                break;
                
            case 'Z'://Z=2
                numero=20000000+Integer.parseInt(num.substring(1, 8));
                break;
                
            default:
                numero=Integer.parseInt(num.substring(0, 8));
        }
        numero=numero%23;
        return base[numero];
    }
    
    public static String calculoCCC(String numCuenta){
        
        //---Carga de substrings---
        String primer = "00".concat(numCuenta.substring(0, 8));
        String segun = numCuenta.substring(10, 20);
        String check = numCuenta.substring(8,10);
        //System.out.println(check + " esto es el check antiguo");
        
        int checkPrimer = check.charAt(0) - '0';
        
        int checkSegun = check.charAt(1) - '0';
        
        //---Comprobación usando el metodo bucle---
        int resultPrimer = bucleNum(primer);
        int resultSegun = bucleNum(segun);
        
        if(resultPrimer != checkPrimer || resultSegun != checkSegun){
            System.out.println("El codigo de cuenta NO es correcto. Se procede a corregirlo.\n");
            //---Primeros 8 digitos---
            String nuevoCodigo = numCuenta.substring(0, 8);
            
            //---Transformacion en String de los digitos de comprobacion---
            nuevoCodigo = nuevoCodigo.concat(String.valueOf(resultPrimer));
            
            nuevoCodigo = nuevoCodigo.concat(String.valueOf(resultSegun));
            
            //---Ultimos 10 digitos---
            nuevoCodigo = nuevoCodigo.concat(numCuenta.substring(10, 20));
            
            //---Llamar al metodo de corregir con el nuevoIban---
            corrigeNum(nuevoCodigo);
            return nuevoCodigo;
        } else {
            System.out.println("El codigo de cuenta es correcto.\n");
            return numCuenta;
        }
    }
    
    private static int calculateModulus97(String iban) {
        long total = 0;
        for (int i = 0; i < iban.length(); i++) {
            int charValue = Character.getNumericValue(iban.charAt(i));
            total = (charValue > 9 ? total * 100 : total * 10) + charValue;
            if (String.valueOf(total).length() > 9) {
                total = (total % 97);
            }
        }
        return (int)(total % 97);
    }
    
    public static String generaIBAN (String numCuenta, String pais){
        String IBANcalc = "";
        IBANcalc += numCuenta;
        
        //Al final se incluyen las letras del pais con su valor en char sumado a 10 para que la A valga 10
        IBANcalc += (pais.charAt(0)-'A'+10) + (pais.charAt(1)-'A'+10) + "00";
        
        //Sacamos el resto de dividir por 97 y obtenemos la diferencia entre ese y 98
        double resto = calculateModulus97(IBANcalc);
        resto = 98-resto;
        
        
        
        double auxDou=Double.parseDouble(IBANcalc);
        
	long auxInt1=(long) (auxDou%97);
        System.out.println(auxInt1);
	auxInt1=98-auxInt1;
                
        String checkNums;
        if(resto<10){
            checkNums = "0" + Double.toString(resto);

        } else {
            checkNums = Double.toString(resto);
        }
        
        //Ahora juntamos los valores
        String IBAN = pais + checkNums + numCuenta; 
        
        return IBAN;
    }
    
    public static String generaEMAIL (String nombre, String apellido1, String apellido2, String empresa){
        //Crea variable
        String email = "";
        
        //Primera letra del nombre
        email += nombre.charAt(0);
        
        //Primera letra del appellido
        email += apellido1.charAt(0);
        
        //Primera letra del segundo appellido (si este existe)
        if(!apellido2.equals(""))email += apellido2.charAt(0);
        
        // +++++++++++++++++ POR IMPLEMENTAR +++++++++++++++++++++++++++++++++++++++++++++++++++++
        //Cifra de repetición
        email += "00";
        
        //Nombre de la empresa como dominio
        email += ("@" + empresa + ".com");

        return email;
    }
    
    //------------ AUXILIAR -----------
    
    public static int bucleNum(String aux){
        int numero=0;
        int[] F ={1,2,4,8,5,10,9,7,3,6};
        
        //---Multiplicacion por la constante y sumatorio---
        for(int i=0;i<10;i++){
            int a = aux.charAt(i) - '0';
            numero+= F[i] * a;
            //System.out.println("\nbucle: "+(i+1));
            //System.out.println("a =  "+ a);
            //System.out.println("F =  "+ F[i]);
            //System.out.println("Suma: "+ numero);

        }
        
        //System.out.println("Tras el sumatorio: "+ numero);
        
        //---Operaciones finales---
        numero = numero%11;
        numero = 11-numero;
        
        //---Se sacan los numeros de dos cifras de la ecuacion---
        if(numero == 10){numero = 1;}
        else if(numero == 11){numero = 0;}
        
        //---Resultado---
        //System.out.println("Result: "+ numero);
        return numero;
    }
    
    // +++++++++++++++++ POR IMPLEMENTAR ++++++++++++++++++++++
    public static void corrigeNum (String numCuenta){
        
    }
    
}
