package garciaponce.miguel;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CrearFichero extends Activity {
	private Button CrearFich;
	private String ruta;
	private EditText NombreFichero;
	private EditText ContenidoFichero;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crearfichero);
        Conexion.ACTIVIDAD=this;  
        
        // Si le damos al boton crear fichero
        CrearFich = (Button) findViewById(R.id.btncrearfich);
        CrearFich.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	// Obtenemos la ruta
            	Intent intent =getIntent();
                ruta =intent.getExtras().getString("Ruta");
                // Obtenemos los datos de nombre del fichero y su contenido
            	NombreFichero = (EditText) findViewById(R.id.Nfich);
                ContenidoFichero = (EditText) findViewById(R.id.ConFich);
               
                // Mandamos los datos al servidor.
                try {
                	 	Conexion.salida.flush();
    				 	Conexion.salida.writeObject("CrearFichero");
    	    			Conexion.salida.flush();
    	    			Conexion.salida.writeObject(ruta); 
    	    			Conexion.salida.flush();
    	    			Conexion.salida.writeObject(NombreFichero.getText().toString());
    	    			Conexion.salida.flush();
    	    			Conexion.salida.writeObject(ContenidoFichero.getText().toString());
                } catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                // Volvemos al layout anterior, pasandole la ruta para que la actualice.
                Intent intent1 = new Intent(CrearFichero.this,Navegador.class);
                intent1.putExtra("IndicadorCrearFichero", true);
                intent1.putExtra("Ruta", ruta);
        		startActivity(intent1);
            }
        });
    }
   
}