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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InfProcesos extends Activity {
	private ListView lstOpciones;
	Vector<Proceso> procesos;
	private int pos;
	Toast toast;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infprocesos);
        
        // Creamos la estructura de datos.
		procesos = new Vector<Proceso>();
        
		// Mandamos orden de listar procesos
        try {
        	Conexion.salida.flush();
			Conexion.salida.writeObject("Listado");
			String mensaje = (String) Conexion.entrada.readObject();
			String[] datos;
			// Obtenemos los datos, introduciendolos en el vector.
			while(!mensaje.equals("fin")){				
					datos = mensaje.split(" ");
					if(!datos[0].equals("PID") && !datos[1].equals("CMD") && !datos[1].equals("SZ")){ 
						Proceso aux = new Proceso(datos[0],datos[1],datos[2]);
						procesos.add(aux);
					}
						
					mensaje = (String) Conexion.entrada.readObject();
			} 
				
			// Establecemos el adaptador de procesos y registramos menu contextual
			AdaptadorProcesos adaptador = new AdaptadorProcesos(this); 
		    lstOpciones = (ListView)findViewById(R.id.ListaProcesos);
		    lstOpciones.setAdapter(adaptador);
		    registerForContextMenu(lstOpciones); 
		    
		    // Si hacemos una pulsacion larga, registramos la posicion.
		    lstOpciones.setOnItemLongClickListener(new OnItemLongClickListener() {
		    	  public boolean onItemLongClick(AdapterView<?> parent, final View v, int position, long id) {
			    	   // record position/id/whatever here
			    	   pos=position;
			    	   return false;
		    	  }
		     });
   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block   
			e.printStackTrace();
		}      
        
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
     
        MenuInflater inflater = getMenuInflater();
     
        if(v.getId() == R.id.ListaProcesos)
        {
            inflater.inflate(R.menu.menuprocesos, menu);
        }
    }
    
    
    // Actuamos segun el menu contextual: eliminar proceso.
    @Override
    public boolean onContextItemSelected(MenuItem item) {    	
		switch (item.getItemId()) {
			case R.id.menuprocesos1:
				Toast toast1 = Toast.makeText(getApplicationContext(),"Se ha mandado la orden para eliminar el proceso " + procesos.get(pos).getNom(), Toast.LENGTH_LONG);
    	        toast1.show();
				try {
		        	Conexion.salida.flush();
					Conexion.salida.writeObject("taskkill /F /PID " + procesos.get(pos).getPID());	
					String mensaje = (String) Conexion.entrada.readObject();
					while(!mensaje.equals("fin_elimina_proceso")){
							mensaje = (String) Conexion.entrada.readObject();
					}
					toast1 = Toast.makeText(getApplicationContext(),"Se ha eliminado el proceso ", Toast.LENGTH_LONG);
	    	        toast1.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Cuando se elimina el proceso, recargamos el layout con los nuevos datos.(sin el proceso ya eliminado)
				Intent intent = new Intent(InfProcesos.this , InfProcesos.class);
            	startActivity(intent);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	Intent intent = new Intent(InfProcesos.this , Mainmenu.class);
        	startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    
    class AdaptadorProcesos extends ArrayAdapter {
    	
    	Activity context;
    	
		AdaptadorProcesos(Activity context) {
    		super(context, R.layout.proc, procesos);
    		this.context = context;
    	}
    	  
    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
			View item = convertView;
			ViewHolder holder;
    		
    		if(item == null)
    		{
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.proc, null);
    			
    			holder = new ViewHolder();
    			holder.DatoProc1 = (TextView)item.findViewById(R.id.DatoProc1);
    			holder.DatoProc2 = (TextView)item.findViewById(R.id.DatoProc2);
    			holder.DatoProc3 = (TextView)item.findViewById(R.id.DatoProc3);
    			
    			item.setTag(holder);
    		}
    		else
    		{
    			holder = (ViewHolder)item.getTag();
    		}
			
			holder.DatoProc1.setText(procesos.elementAt(position).getNom());
			holder.DatoProc2.setText(procesos.elementAt(position).getTam() + " Kb");
			holder.DatoProc3.setText("PID: " + procesos.elementAt(position).getPID());
			
			return(item);
		}
    }
    
    static class ViewHolder {
    	public TextView DatoProc1;
    	public TextView DatoProc2;
    	public TextView DatoProc3;
    }
}
