package garciaponce.miguel;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class InfRed extends Activity {
	private Vector<Red> DispRed;
	private ListView lstDispRed;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infred);
        Conexion.ACTIVIDAD=this;
        
        
        //Creamos las estructura de datos necesaria
        DispRed = new Vector<Red>();
        
        // Enviamos orden al servidor para obtener datos de red
        try {
        	Conexion.salida.flush();
			Conexion.salida.writeObject("Red");
			
			// Obtenemos los datos del servidor.
			String mensaje = (String) Conexion.entrada.readObject();
			while(!mensaje.equals("Fin_Red")){
				// Obtenemos los valores desde el servidor.
				String Adaptador = null,dirfis = null,dirip = null,descripcion = null,mask=null,estado=null,dhcp=null;
				Adaptador=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				dirfis=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				dirip=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				descripcion=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				mask=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				estado=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				dhcp=mensaje;
				
				Red aux = new Red(Adaptador,dirfis,dirip,descripcion,mask,estado,dhcp);
				DispRed.add(aux);
				mensaje = (String) Conexion.entrada.readObject();
			} 
			
			for(int i=0;i<DispRed.size();i++){
				if(DispRed.get(i).Adaptador().equals("") && DispRed.get(i).DirFis().equals("") && DispRed.get(i).DirIP().equals("") && DispRed.get(i).Descripcion().equals("") && DispRed.get(i).Mascara().equals("") && DispRed.get(i).Estado().equals("") && DispRed.get(i).DHCP().equals(""))
					DispRed.remove(i);
			}
			
		 
			// Establecemos adaptador			
			AdaptadorRed adaptador = new AdaptadorRed(this);
		    lstDispRed = (ListView)findViewById(R.id.ListaRed);
		    lstDispRed.setAdapter(adaptador);
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
    }
    
class AdaptadorRed extends ArrayAdapter {
    	
    	Activity context;
    	
		AdaptadorRed(Activity context) {
    		super(context, R.layout.red, DispRed);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
			View item = convertView;
			ViewHolder holder;
    		
    		if(item == null)
    		{
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.red, null);
    			
    			holder = new ViewHolder();
    			holder.Adaptador = (TextView)item.findViewById(R.id.Adaptador);
    			holder.DirFis = (TextView)item.findViewById(R.id.DirFisica);
    			holder.DirIP = (TextView)item.findViewById(R.id.DirIP);
    			holder.Descripcion = (TextView)item.findViewById(R.id.Descripcion);
    			holder.Mascara = (TextView)item.findViewById(R.id.Mascara);
    			holder.Estado = (TextView)item.findViewById(R.id.Estado);
    			holder.DHCP = (TextView)item.findViewById(R.id.DHCP);
    			holder.TextoDHCP = (TextView)item.findViewById(R.id.textView12);
    			holder.ImagenRed= (ImageView)item.findViewById(R.id.ImagenRed);
    			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);
    			holder.ImagenRed.setLayoutParams(layoutParams);
    			
    			item.setTag(holder);
    		} 
    		else
    		{
    			holder = (ViewHolder)item.getTag();
    		}
			
    		holder.Adaptador.setText(DispRed.elementAt(position).Adaptador());
			holder.DirFis.setText(DispRed.elementAt(position).DirFis());
			holder.DirIP.setText(DispRed.elementAt(position).DirIP());
			holder.Descripcion .setText(DispRed.elementAt(position).Descripcion());
			holder.Mascara.setText(DispRed.elementAt(position).Mascara() );
			if(holder.DirIP.getText().toString().trim().length()>0 ){
				holder.Estado.setText("Conectado");
			}else{
				holder.Estado.setText("Desconectado");
			}
			
			
			holder.DHCP.setText(DispRed.elementAt(position).DHCP());
			
			if(SesionLocal.ObtenerSistemaOperativo().equals("Linux")){
				
				holder.TextoDHCP.setVisibility(View.INVISIBLE);
			} 
			
			if(holder.Adaptador.getText().toString().startsWith("Adaptador de Ethernet") || holder.Adaptador.getText().toString().startsWith("eth") || holder.Adaptador.getText().toString().startsWith("lo")){
				holder.ImagenRed.setBackgroundResource(R.drawable.iconoethernet);
			}else{
				holder.ImagenRed.setBackgroundResource(R.drawable.iconowirelesss);
			}
			return(item);
		}
    }
    
    static class ViewHolder {
    	public TextView Adaptador;
    	public TextView DirFis;
    	public TextView DirIP;
    	public TextView Descripcion;
    	public TextView Mascara;
    	public TextView Estado;
    	public TextView DHCP;
    	public ImageView ImagenRed;
    	public TextView TextoDHCP;

    }
}

