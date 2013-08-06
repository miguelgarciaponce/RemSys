package garciaponce.miguel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CrearUsuario extends Activity {
	private EditText Usuario;
    private EditText Contrasena;
    private EditText RepContrasena;
    private Button CrearUsu;
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
        setContentView(R.layout.creausuario);

        
        CrearUsu = (Button) findViewById(R.id.BotonCreaUsu);
        
        
        // Cuando le clickeemos en el boton crearUsuario
        CrearUsu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Usuario = (EditText) findViewById(R.id.editTextUsu);
                Contrasena = (EditText) findViewById(R.id.editTextCont);
                RepContrasena = (EditText) findViewById(R.id.editTextRepCont);
                
                // Comprobamos que el usuario no exista.
                usdbh = new UsuariosSQL(CrearUsuario.this, NombreBD , null, 1);
                db = usdbh.getWritableDatabase();
                db.execSQL("CREATE TABLE IF NOT EXISTS Usuarios (nombre TEXT,contrasena TEXT , PRIMARY KEY(nombre))");
                
                c = db.rawQuery(" SELECT nombre,contrasena FROM Usuarios WHERE nombre=\'" + Usuario.getText().toString() + "\'", null);
                
                switch(c.getCount()){
                	case 1:
                		Log.i("Fila BD ", "Ya existe usuario");
                		toast = Toast.makeText(getApplicationContext(),"El usuario ya existe.", Toast.LENGTH_SHORT);
                        toast.show();
                		
                		break;
                	case 0:
                		Log.i("Fila BD ", "No existe usuario");
                		if(Contrasena.getText().toString().equals(RepContrasena.getText().toString())){
                			//Si hemos abierto correctamente la base de datos
                            if(db != null)
                            {
                            	db.execSQL("INSERT INTO " + NombreTabla + " (nombre,contrasena) VALUES (\'" + Usuario.getText().toString().trim() + "\',\'" + Contrasena.getText().toString().trim() + "\')");
                                
                            }
                            toast = Toast.makeText(getApplicationContext(),"Usuario creado con éxito.", Toast.LENGTH_SHORT);
                            toast.show();
                            
                            Intent intent = new Intent(CrearUsuario.this , Login.class);
            		        startActivity(intent);
                        
                        }else{
                        	toast = Toast.makeText(getApplicationContext(),"Las contraseñas no coinciden", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                		break;
                	default:
                		Log.i("Fila BD ", "Usuario duplicado");
                		break;
                }
                db.close();
            }
            
        });
        
    }
}
