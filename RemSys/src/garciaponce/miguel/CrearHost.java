 package garciaponce.miguel;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CrearHost extends Activity {
	private EditText NombreMaquina;
    private EditText DirIP;
    private EditText Puerto;
    private EditText DirMac;
    private Button AgregaHost;
	private String NombreBD = "BDRemSys";
	private HostsSQL usdbh;
	private SQLiteDatabase db;
	private Toast toast;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creahost);
        
        // Accedemos a la BD.
        usdbh = new HostsSQL(CrearHost.this, NombreBD , null, 1);
        db = usdbh.getWritableDatabase();

        // Si pulsamos sobre agregarHost
        AgregaHost = (Button)findViewById(R.id.CreaHost);
    	AgregaHost.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// Obtenemos los datos y componentes.
            	String Usuario = SesionLocal.NombreUsuario;
            	NombreMaquina = (EditText) findViewById(R.id.NombreMaquina);
                DirIP = (EditText) findViewById(R.id.DirIP);
                Puerto = (EditText) findViewById(R.id.Puerto);
                DirMac = (EditText) findViewById(R.id.DirMAC);
                
            	// Ejecutamos la sentencia SQL para agregarla a la base de datos a la tabla Host.
                db.execSQL("INSERT INTO Hosts (usuario,nombrehost,dirip,puerto,dirmac) VALUES (\'" + Usuario.trim() + "\',\'" + NombreMaquina.getText().toString().trim() + 
                		"\',\'" + DirIP.getText().toString().trim() + "\',\'" + Puerto.getText().toString().trim() +  "\',\'" + DirMac.getText().toString().trim() + "\')");
                
                // Visualizamos un toast para informar de la accion anterior.
                toast = Toast.makeText(getApplicationContext(),"Host creado correctamente", Toast.LENGTH_SHORT);
                toast.show();
                
                // Cerramos la base de datos.
                db.close();
                
                // Volvemos al LAyout anterior.
                Intent intent = new Intent(CrearHost.this , ListaHosts.class);
		        startActivity(intent); 
                
            }
        });
            
        
        

        
    }
    
    
}
