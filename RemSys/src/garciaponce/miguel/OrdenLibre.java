package garciaponce.miguel;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OrdenLibre extends Activity {
	private TextView SalidaOrden; 
	private EditText Orden;
	private Button BtnEjecutar;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordenlibre);
        Conexion.ACTIVIDAD=this;
        
        Orden = (EditText) findViewById(R.id.Orden);
        
        BtnEjecutar = (Button) findViewById(R.id.BotonEjecutar);
        
        BtnEjecutar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	ObtenerSalida();
            }
        });
    }
    

    private void ObtenerSalida(){
       	SalidaOrden  = (TextView) findViewById(R.id.SalidaOrden);

       	if(SalidaOrden.getText().length()>0)
       		SalidaOrden.setText("");
        	
       	try{
       		Conexion.salida.flush();
    		Conexion.salida.writeObject("OrdenLibre");
    		Conexion.salida.flush();
    		Conexion.salida.writeObject(Orden.getText().toString().trim());
    			
    		String mensaje = (String) Conexion.entrada.readObject();
    		while(!mensaje.equals("Fin_OrdenLibre")){
    			SalidaOrden.append(mensaje + "\n");
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