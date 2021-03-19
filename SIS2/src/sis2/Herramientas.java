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
                numero=Integer.parseInt(num.substring(1, 7));
                break;
                
            case 'Y'://Y=1
                numero=10000000+Integer.parseInt(num.substring(1, 7));
                break;
                
            case 'Z'://Z=2
                numero=20000000+Integer.parseInt(num.substring(1, 7));
                break;
                
            default:
                numero=Integer.parseInt(num.substring(0, 7));
        }
        numero=numero%23;
        return base[numero];
    }
    
    public static int calculoCCC(String numCuenta){

        int numero=0;
        String aux = "00".concat(numCuenta.substring(0, 7));
        
        
        return 1;
    }
    public static int bucle(String aux){
        int numero=0;
        for(int i=0;i<10;i++){
            numero+=(aux.charAt(i)*((2^i) % 11));
            System.out.println("bucle: "+i);
        }
        return 0;
    }
    public static void main (String [] Args){
        
    }
    
}
