package garciaponce.miguel;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListaHosts extends Activity {

	private ListView ListaHosts;
	private Vector<Host> Hosts;
	private ImageButton BtnAgregar;
	private int pos; 
	private String NombreBD = "BDRemSys";
	private String NombreTabla = "Hosts";
	private UsuariosSQL usdbh;
	private SQLiteDatabase db;
	private Cursor c;
	private Toast toast;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listahosts);
        
        // Creamos el Vector de Hosts y obtenemos los componentes del Layout
        Hosts = new Vector<Host>();
        ListaHosts = (ListView) findViewById(R.id.ListaHosts);
        BtnAgregar = (ImageButton) findViewById(R.id.BotonAgregarHost);
        
        
        // Acceder a los datos de la base de datos para almacenarlos en el vector Hosts
        usdbh = new UsuariosSQL(ListaHosts.this, NombreBD , null, 1);
        db = usdbh.getWritableDatabase();
        
        // Consulta para saber los host que pertenecen al usuario ya logeado
        // Creamos la table si no existe.
        db.execSQL( "CREATE TABLE IF NOT EXISTS Hosts (usuario TEXT,nombrehost TEXT,dirip TEXT,puerto TEXT,dirmac TEXT, PRIMARY KEY(usuario,nombrehost,dirip,puerto))");
        c = db.rawQuery("SELECT * FROM " + NombreTabla + " WHERE usuario=\'" + SesionLocal.NombreUsuario + "\'", null);
         
        if (c.moveToFirst()) {
   	     //Recorremos el cursor hasta que no haya más registros
   	     do {
   	    	 Host host = new Host(c.getString(1) ,c.getString(2) ,c.getString(3) ,c.getString(4) );
   	    	 Hosts.add(host);
   	    	 Log.i("ListaHosts", c.getString(0) + " " +c.getString(1) + " " +c.getString(2) + " " +c.getString(3) + " " +c.getString(4) );
   	     } while(c.moveToNext());
   	     
	    
   	    // Creamos el Adaptador        
        AdaptadorHosts adaptador = new AdaptadorHosts(this);
        ListaHosts.setAdapter(adaptador);
   	    // Registramos el menu Contextual
        registerForContextMenu(ListaHosts);

	    ListaHosts.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	  public boolean onItemLongClick(AdapterView<?> parent, final View v, int position, long id) {
		    	   // Cuando hacemos una pulsación larga, guardamos la posicion.
	    		  pos=position;
	    		  return false;
	    	  }
	     });
        }
        
        // Si le damos al boton agregar, cambiamos de Layaout para crear un nuevo host.
        BtnAgregar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(ListaHosts.this , CrearHost.class);
		        startActivity(intent);            	
            }
        });
        
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
     
        MenuInflater inflater = getMenuInflater();
     
        if(v.getId() == R.id.ListaHosts)
        {
            inflater.inflate(R.menu.menuhosts, menu);
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {    
    	// Menu contextual, Conectar, Eliminar y Encender.
		switch (item.getItemId()) {
			case R.id.menuhost1:
				Conectar();
				return true;
			case R.id.menuhost2:
				// Eliminar Host de la lista de hosts de la Base de Datos y volver a recargar el layout
				db.execSQL("DELETE FROM " + NombreTabla + " WHERE usuario=\'" + SesionLocal.NombreUsuario + "\' AND nombrehost=\'" + Hosts.get(pos).getNom() + "\'");
				db.close();
				// Recargamos el Layout
				Intent intent = new Intent(ListaHosts.this , ListaHosts.class);
		    	startActivity(intent);
				return true;
			case R.id.menuhost3:
				// Si encendemos el sistema, pasamos al siguiente intent los datos necesarios con IP y MAC.
				Intent intent1 = new Intent(ListaHosts.this , WOL.class);
				intent1.putExtra("DirIP", Hosts.get(pos).getIP());
				intent1.putExtra("DirMac", Hosts.get(pos).getMac());
				startActivity(intent1);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
    }
    
    class AdaptadorHosts extends ArrayAdapter {
    	
    	Activity context;
    	
		AdaptadorHosts(Activity context) {
    		super(context, R.layout.host, Hosts);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
			View item = convertView;
			ViewHolder holder;
    		
    		if(item == null)
    		{
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.host, null);
    			
    			holder = new ViewHolder();
    			holder.DatoHost1 = (TextView)item.findViewById(R.id.NombreHost);
    			holder.DatoHost2 = (TextView)item.findViewById(R.id.IPHost);
    			holder.DatoHost3 = (TextView)item.findViewById(R.id.PuertoHost);
    			holder.DatoHost4 = (TextView)item.findViewById(R.id.MacHost);
    			
    			item.setTag(holder);
    		}
    		else
    		{
    			holder = (ViewHolder)item.getTag();
    		}
			
			holder.DatoHost1.setText(Hosts.elementAt(position).getNom());
			holder.DatoHost2.setText(Hosts.elementAt(position).getIP());
			holder.DatoHost3.setText(Hosts.elementAt(position).getPuerto());
			holder.DatoHost4.setText(Hosts.elementAt(position).getMac());
			
			return(item);
		}
    }
    
    static class ViewHolder {
    	public TextView DatoHost1;
    	public TextView DatoHost2;
    	public TextView DatoHost3;
    	public TextView DatoHost4;
    }
    
    private void Conectar(){
    	toast = Toast.makeText(getApplicationContext(),"Conectando al host " + Hosts.get(pos).getIP(), Toast.LENGTH_LONG);
        toast.show();
        
        // Establecemos los parametros del Servicio Conexión
        Conexion.establecerIP(Hosts.get(pos).getIP());
        Conexion.establecerSocket(Integer.parseInt(Hosts.get(pos).getPuerto()));
        
        // Iniciamos el servicio Conexión 
        Intent servicio = new Intent(ListaHosts.this, Conexion.class);
		if(startService(servicio)==null){
        	Log.i("Conectar","No se ha podido iniciar el servicio Conexion");
        } else {
        	Log.i("Conectar","Servicio Conexion iniciado correctamente");
        }
        
		
		// Cambiamos el Layaout al menú principal, una vez hemos iniciado el servicio Conexion.
		Intent intent = new Intent(ListaHosts.this , Mainmenu.class);
    	startActivity(intent);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	Log.i("ListaHosts","Vuelve a login");
        	Intent intent = new Intent(ListaHosts.this , Login.class);
        	startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
