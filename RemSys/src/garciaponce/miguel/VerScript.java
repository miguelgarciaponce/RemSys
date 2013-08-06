package garciaponce.miguel;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class VerScript extends Activity {
	private EditText TextoVerScript;
	private TextView Titulo;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verscript);
        Conexion.ACTIVIDAD=this;  
        
        Intent intent = getIntent();
        String fichero =intent.getExtras().getString("Fichero");
        
        TextoVerScript = (EditText) findViewById(R.id.TextoVerScript);
        Titulo = (TextView) findViewById(R.id.TituloVerScript);
        Titulo.append(": " + fichero);
        try {
			Conexion.salida.flush();
			Conexion.salida.writeObject("VerScript");
			Conexion.salida.flush();
			Conexion.salida.writeObject(fichero);
			
			String mensaje = (String) Conexion.entrada.readObject();
			
			while(!mensaje.equals("FinLecturaScript")){		
				TextoVerScript.append(mensaje + "\n");
				mensaje = (String) Conexion.entrada.readObject();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		TextoVerScript.setKeyListener(null);
        
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	Intent intent = new Intent(VerScript.this , Scripts.class);
	        startActivity(intent);
	    }
        return super.onKeyDown(keyCode, event);
    }
    
}





