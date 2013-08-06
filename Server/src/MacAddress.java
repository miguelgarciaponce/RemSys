import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MacAddress {
	
	String MaC;
	
	public MacAddress() {
	
		InetAddress direccion;
		try {
			direccion = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(direccion);
			
			byte[] mac = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
			mac = ni.getHardwareAddress();
		

			MaC = toHexadecimal(mac);
			String macreem = MaC.replace("-", "");
			String macfinal = new String();
			
			int i;
			String insertar;
			for(i=0; i<macreem.length()-2;i++){
				insertar = macreem.substring(i,i+2);
				macfinal += insertar + "-";
				i++;
			}
			insertar = macreem.substring(i,macreem.length());
			macfinal +=insertar;
			MaC = macfinal;
				
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}catch (NullPointerException e){
			
		}
	}
	
	public static String toHexadecimal(byte[] datos) 
    { 
            String resultado=""; 
            ByteArrayInputStream input = new ByteArrayInputStream(datos); 
            String cadAux; 
            int leido = input.read(); 
            while(leido != -1) 
            { 

                    cadAux = Integer.toHexString(leido); 
                    if(cadAux.length() < 2) //Hay que a�adir un 0 
                            resultado += "0";
                   	resultado += "-";

                    resultado += cadAux; 
                    leido = input.read(); 
            } 
            return resultado; 
    }
}
