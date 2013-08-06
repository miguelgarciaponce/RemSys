package garciaponce.miguel;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class InfDiscos extends Activity {
	private Vector<Disco> discos;
	private ListView lstDiscos;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infdiscos);
        Conexion.ACTIVIDAD=this;
       
        discos = new Vector<Disco>(); 
           
        try {
        	// Enviamos el mensaje Navegacion para obtener las unidades (Mi PC) y empezar a visualizarlas.
        	Conexion.salida.flush(); 
			Conexion.salida.writeObject("InfDiscos");
			
			String mensaje = (String) Conexion.entrada.readObject();
			// Mientras no recibamos "FIn_InfDiscos", obtenemos los datos de nombre, tipo, ocupado y tamañototal.
			while(!mensaje.equals("Fin_InfDiscos")){				
					String nombre = mensaje;
					mensaje = (String) Conexion.entrada.readObject();
					String Tipo = mensaje;
					mensaje = (String) Conexion.entrada.readObject();
					long TamTotal = Long.parseLong(mensaje);
					mensaje = (String) Conexion.entrada.readObject();
					int Ocupado = Integer.parseInt(mensaje);
					Disco aux = new Disco(nombre,Tipo,Ocupado,TamTotal);
					discos.add(aux);
					mensaje = (String) Conexion.entrada.readObject();
			}
					
			// Establecemos adaptador de Discos y registramos MenuContextual.
			AdaptadorDisco adaptador = new AdaptadorDisco(this);
		    lstDiscos = (ListView)findViewById(R.id.ListaDiscos);
		    lstDiscos.setAdapter(adaptador);
		    registerForContextMenu(lstDiscos);
		    
		    
   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
 }
    
    

 class AdaptadorDisco extends ArrayAdapter {
    	
    	Activity context;
    	
		AdaptadorDisco(Activity context) {
    		super(context, R.layout.disco, discos);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
			View item = convertView;
			ViewHolder holder;
    		
    		if(item == null)
    		{
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.disco, null);
    			
    			holder = new ViewHolder();
    			holder.Nombre = (TextView)item.findViewById(R.id.NombreDisco);
    			holder.Tipo = (TextView)item.findViewById(R.id.TipoDisco);
    			holder.PorOcupado = (TextView)item.findViewById(R.id.PorcentajeOcupado);
    			holder.Tam = (TextView)item.findViewById(R.id.TamTotal);
    			
    			item.setTag(holder);
    		}
    		else
    		{
    			holder = (ViewHolder)item.getTag();
    		}
			
			holder.Nombre.setText(discos.elementAt(position).NombreDisco());
			holder.Tipo.setText(discos.elementAt(position).TipoSistemaDisco());
			holder.PorOcupado.setText(String.valueOf(discos.elementAt(position).PorUsado()) + "%");
			// Tamaño disco
			float tamaniototal = (float) discos.elementAt(position).Tamanio()/(float)1073741824;
			holder.Tam.setText(String.format("%.3f", tamaniototal) + " Gb");
			return(item);
		}
    }
    
    static class ViewHolder {
    	public TextView Nombre;
    	public TextView Tipo;
    	public TextView PorOcupado;
    	public TextView Tam;
    }
}