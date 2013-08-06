import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

class GetIP
{
	public String IPaddress;
	
	public GetIP(){
		try {
	    	URL url = new URL("http://www.adslayuda.com/ip.html");
	    	URLConnection urlConn = url.openConnection();
		    urlConn.setReadTimeout(60000);
		    urlConn.setConnectTimeout(60000);
		    Charset charset = Charset.forName("utf-8");
		    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), charset));
		    String line = br.readLine();
		    StringBuffer response = new StringBuffer();
		    while (line != null) {
		    	response = response.append(line);
		    	line = br.readLine();
		    }
		    String pattern = "</h2></b><h1>(.+?)</h1><br/>";
		    Matcher matcher = Pattern.compile(pattern).matcher(response.toString());
		    while (matcher.find()) {
		    	IPaddress = matcher.group(1);
		    }
		} catch(Exception e) {
			final JFrame VentanaPropiedades = new JFrame("Sin conexión de red");
			JOptionPane.showMessageDialog(VentanaPropiedades , "No se tiene acceso a Internet, porfavor, conéctese a la red y actualice el servidor..." );

			//e.printStackTrace();
		}
	}
 
 }