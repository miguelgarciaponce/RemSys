package garciaponce.miguel;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CrearScript extends Activity {
	
	private Button TransferirScript;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crearscript);
        Conexion.ACTIVIDAD=this;  
         
        // Si pulsamos sobre transferirScript
        TransferirScript  = (Button) findViewById(R.id.TransferirScript);
        TransferirScript.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// Pedimos el nombre del script
            	final AlertDialog.Builder alert = new AlertDialog.Builder(CrearScript.this);
                alert.setMessage("Nombre del Script: ");
                final EditText input = new EditText(CrearScript.this);
                alert.setView(input);
                // Si pulsamos sobre Transferir con los datos introducidos.
        		alert.setPositiveButton("Transferir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	// Mandamos datos al servidor.
                    	try { 
                    		EditText TextoScript = (EditText) findViewById(R.id.TextoScript);
                    		Conexion.salida.flush(); 
                    		Conexion.salida.writeObject("TransferirScript");
                    		Conexion.salida.flush();
                    		Conexion.salida.writeObject(input.getText().toString());
                    		Conexion.salida.flush();
                    		Conexion.salida.writeObject(TextoScript.getText().toString());

                            
                        } catch (IOException e) {
                    		// TODO Auto-generated catch block  
                    		e.printStackTrace();
                    	} finally{
                    		// Al acabar, volvemos al layout  Scripts
                    		Intent intent = new Intent(CrearScript.this , Scripts.class);
            		        startActivity(intent);
                    	}
                    	
                    }
        	    });

                alert.setNegativeButton("Cancelar",
                		new DialogInterface.OnClickListener() {  
                            public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                    }
                    });
                alert.show();
                
            }
        });
       
 }
    
    
}





