package garciaponce.miguel;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class InfSisOp extends Activity {
	private TextView PropiedadesSistemaOperativo; 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infsisop);
        Conexion.ACTIVIDAD=this;
        
        PropiedadesSistemaOperativo  = (TextView) findViewById(R.id.PropiedadesSistemaOperativo);
        
        // Mandamos orden para obtener la informacion
        try {
        	Conexion.salida.flush();
			Conexion.salida.writeObject("SistemaOperativo");
			 
			// Recibimos datos del sistema operativo
			String mensaje = (String) Conexion.entrada.readObject();
			while(!mensaje.equals("Fin_SistemaOperativo")){
				if(mensaje.startsWith("Path Librerías Java")){
					String datos[];
					datos = mensaje.split(";");

					PropiedadesSistemaOperativo.append(datos[0] + "\n" );
					for(int i =1;i<datos.length;i++){
						if(datos[i].length()>3)
							PropiedadesSistemaOperativo.append("\t" + datos[i].trim() + "\n" );
					}
					PropiedadesSistemaOperativo.append("\n");	  
					
				}else{
					PropiedadesSistemaOperativo.append(mensaje + "\n\n");  
				}
				mensaje = (String) Conexion.entrada.readObject();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}