package garciaponce.miguel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
 
public class Login extends Activity {
	private EditText Usuario;
	private EditText Contrasena;
	private ImageButton AgregarUsuario;
	private Button Entrar;
	private String NombreBD = "BDRemSys";
	private String NombreTabla = "Usuarios"; 
	private UsuariosSQL usdbh;
	private SQLiteDatabase db;
	private Cursor c;  
	private Toast toast;    
    
    /** Called when the activity is first created. */ 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); 

        // Obtenemos los componentes
        AgregarUsuario = (ImageButton) findViewById(R.id.BotonAgregar);
        Entrar = (Button) findViewById(R.id.BotonEntrar);
        
        // Si pulsamos sobre el botón agregar
        AgregarUsuario.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Login.this , CrearUsuario.class);
		        startActivity(intent);            	
            }
        }); 
          
        // Si pulsamos sobre el botón entrar
        Entrar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Usuario = (EditText) findViewById(R.id.editTextUsuario);
                Contrasena = (EditText) findViewById(R.id.editTextContrasena);
                
                // Comprobamos si el usuario y contraseña se encunetra en la base de datos.
                if(ComprobarUsuario(Usuario.getText().toString().trim(),Contrasena.getText().toString().trim())){
                	
                	// Iniciamos el servicio para el control de la sesión (Nombreusuario)
                	Intent sesion = new Intent(Login.this, SesionLocal.class);
                	if(startService(sesion)==null){
                    	Log.i("SesionLocal","No se ha podido iniciar el servicio");
                    } else  {
                    	Log.i("SesionLocal","Servicio iniciado correctamente");
                    }
                    SesionLocal.NombreUsuario=Usuario.getText().toString().trim();
                	
                    // Una vez comprobada las credenciales, pasamos a la ListaHosts                    
                    Intent intent = new Intent(Login.this , ListaHosts.class);
    		        startActivity(intent);                	
                }else{
                	// Ha habido algun error en la comprobaciï¿½n de usuarios
                	toast = Toast.makeText(getApplicationContext(),"Credenciales incorrectas", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
    
    private boolean ComprobarUsuario(String usu,String contra){
    	String Ctr = null;
    	// Accedemos a la BD
    	usdbh = new UsuariosSQL(Login.this, NombreBD , null, 1);
        db = usdbh.getWritableDatabase();
        // Si no existe la creamos
        db.execSQL("CREATE TABLE IF NOT EXISTS Usuarios (nombre TEXT,contrasena TEXT , PRIMARY KEY(nombre))");
        // Ejecutamos la sentencia SQL
        c = db.rawQuery(" SELECT contrasena FROM " + NombreTabla + " WHERE nombre=\'" + usu + "\'", null);
         	
        
    	//Nos aseguramos de que existe al menos un registro
    	if(c.getCount()==0){
	    	 return false;
    	}else{
    		if (c.moveToFirst()) {
    			//Recorremos el cursor hasta que no haya más registros
       	     	do {
       	     		Ctr = c.getString(0);
       	     		Log.i("Comprobar Usuario ", "Ctr: " + Ctr + "contra: " + contra);
       	        } while(c.moveToNext());
    		}
    		 
    		db.close(); 
        	
    		// Si hemos encontrado alguna coincidencia (existe usuario), devolvemos true
        	if(Ctr.equals(contra)){
        		return true;
        	}  
    	}
		return false;	
    }
     
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	moveTaskToBack(true);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}
