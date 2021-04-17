package sis2;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class Herramientas {
    /*
        Descaregar JDom(xml) y ApachePoi(excel)
    */
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
    
    public static void calculoCCC(String numCuenta){
        
        //---Carga de substrings---
        String primer = "00".concat(numCuenta.substring(0, 8));
        String segun = numCuenta.substring(10, 20);
        String check = numCuenta.substring(8,10);
        //System.out.println(check + " esto es el check antiguo");
        
        int checkPrimer = check.charAt(0) - '0';
        
        int checkSegun = check.charAt(1) - '0';
        
        //---Comprobación usando el metodo bucle---
        int resultPrimer = bucle(primer);
        int resultSegun = bucle(segun);
        
        if(resultPrimer != checkPrimer || resultSegun != checkSegun){
            System.out.println("El IBAN NO es correcto. Se procede a corregirlo.\n");
            //---Primeros 8 digitos---
            String nuevoIban = numCuenta.substring(0, 8);
            
            //---Transformacion en String de los digitos de comprobacion---
            nuevoIban = nuevoIban.concat(String.valueOf(resultPrimer));
            
            nuevoIban = nuevoIban.concat(String.valueOf(resultSegun));
            
            //---Ultimos 10 digitos---
            nuevoIban = nuevoIban.concat(numCuenta.substring(10, 20));
            
            //---Llamar al metodo de corregir con el nuevoIban---
            corrige(nuevoIban);
            
        } else {
            System.out.println("El IBAN es correcto.\n");
        }

    }
    
    
    public static int bucle(String aux){
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
    public static void corrige (String iban){
        
    }
    
    public static void main (String [] Args){
        String s = new String();
        s = "99558741226555551120";
        calculoCCC(s);
        
    }
    

    
}
