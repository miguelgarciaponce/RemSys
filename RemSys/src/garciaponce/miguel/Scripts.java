package garciaponce.miguel;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Scripts extends Activity {
	private Vector<String> Scripts;
	private ListView lstScripts;
	private int pos;
	private Button CrearScript;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scripts);
        Conexion.ACTIVIDAD=this; 
         
        Scripts = new Vector<String>(); 
        
        try {
        	// Enviamos el mensaje Navegacion para obtener los Scripts del directorio
        	Conexion.salida.flush();   
			Conexion.salida.writeObject("Scripts"); 
			
			String mensaje = (String) Conexion.entrada.readObject();
			while(!mensaje.equals("Fin_Scripts")){				
					Scripts.add(mensaje);
					mensaje = (String) Conexion.entrada.readObject();
			}
					 	
			
			// Establecemos el adaptador y registramos menu contextual
			AdaptadorScripts adaptador = new AdaptadorScripts(this);
		    lstScripts = (ListView)findViewById(R.id.ListaScripts);
		    lstScripts.setAdapter(adaptador);
		    registerForContextMenu(lstScripts);
		    
		    // Registramos posicion del elemento seleccionado
		    lstScripts.setOnItemLongClickListener(new OnItemLongClickListener() {
		    	  public boolean onItemLongClick(AdapterView<?> parent, final View v, int position, long id) {
			    	   // record position/id/whatever here
			    	   pos=position;
			    	   return false;
		    	  }
		     });
		    
		    // Boton CrearScript => nuevo layout
		    CrearScript = (Button) findViewById(R.id.CrearScript);
		    CrearScript.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
		            Intent intent = new Intent(Scripts.this , CrearScript.class);
			        startActivity(intent);
	            }
	        });
		     
   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
 }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
     
        MenuInflater inflater = getMenuInflater();
     
        if(v.getId() == R.id.ListaScripts){
        	// Segun sea el sistema, obtenemos los menus contextuales de los scripts a ejecutar
        	if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
        		if(Scripts.get(pos).toString().endsWith(".exe") || Scripts.get(pos).toString().endsWith(".bat") || Scripts.get(pos).toString().endsWith(".cmd") || Scripts.get(pos).toString().endsWith(".py") )
            		inflater.inflate(R.menu.menuscripts, menu);
        	}else{
        		if(Scripts.get(pos).toString().endsWith(".sh") || Scripts.get(pos).toString().endsWith(".py") || Scripts.get(pos).toString().endsWith(".run") || Scripts.get(pos).toString().endsWith(".bin") )
            		inflater.inflate(R.menu.menuscripts, menu);
        	
        	}
        }
    }
    
    // Menu contextual (Ejecutar, eliminar y verscript)
    public boolean onContextItemSelected(MenuItem item) {    	
		switch (item.getItemId()) {
			// Si pulsamos sobre ejecutar.
			case R.id.menuscript1:
				EjecutarScript();
		        return true;
		    
		        // Si pulsamos sobre Tranferir a SD-Card
			case R.id.menuscript2:
				EliminarScript();
		        return true;
		        
			case R.id.menuscript3:
				VerScript();
		        return true;
		        
			default: 
				return super.onContextItemSelected(item);
		}
    }

    private void VerScript(){
    	Intent intent = new Intent(Scripts.this,VerScript.class);
    	intent.putExtra("Fichero", Scripts.get(pos).toString());
    	startActivity(intent); 
    }
     
    
    private void EjecutarScript(){
    	
			try {
				Conexion.salida.flush();
				Conexion.salida.writeObject("EjecutarScript " + Scripts.get(pos).toString());
				
				Intent intent = new Intent(Scripts.this , Scripts.class);
	        	startActivity(intent);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
    	     
    } 

    // Boton BACK del teléfono
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	try {
				Conexion.salida.flush();
				Conexion.salida.writeObject("SalirScripts");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
        }
        Intent intent = new Intent(Scripts.this , Mainmenu.class);
    	startActivity(intent);
        return super.onKeyDown(keyCode, event);
    }
     
    private void EliminarScript(){
    	try {
			Conexion.salida.flush();
			Conexion.salida.writeObject("EliminarScript " + Scripts.get(pos).toString());
			
			String mensaje = (String) Conexion.entrada.readObject();
			if(mensaje.equals("FinEliminarScript")){
				Intent intent = new Intent(Scripts.this , Scripts.class);
	        	startActivity(intent);
	        }
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		} 
    }

    class AdaptadorScripts extends ArrayAdapter {
    	
    	Activity context;
    	
		AdaptadorScripts(Activity context) {
    		super(context, R.layout.script, Scripts);
    		this.context = context;
    	}     
    	
    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
			View item = convertView;
			ViewHolder holder;
    		
    		if(item == null)
    		{
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.script, null);
    			
    			holder = new ViewHolder();
    			holder.Nombre = (TextView)item.findViewById(R.id.NombreScript);
    			
    			
    			item.setTag(holder);
    		}
    		else
    		{
    			holder = (ViewHolder)item.getTag();
    		}
			
			holder.Nombre.setText(Scripts.get(position).toString());
			return(item);
		}
    }
    
    static class ViewHolder {
    	public TextView Nombre;
    	
    }
}