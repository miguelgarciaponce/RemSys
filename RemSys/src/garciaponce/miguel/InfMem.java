package garciaponce.miguel;
 
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InfMem extends Activity {
	private String memftotal=null,memfdis=null,memvirtammax=null,memvirdis=null,memvirus=null;
    private TextView MemFisTot;
	private TextView MemFisDis;
	private TextView MemVirTot;
	private TextView MemVirDis;
	private TextView MemVirUs;
	private Button btnKb;
	private Button btnMb;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infmem);
        Conexion.ACTIVIDAD=this;
        
        // Obtenemos los componentes
        MemFisTot  = (TextView) findViewById(R.id.MemFisTot);
    	MemFisDis =(TextView) findViewById(R.id.MemFisDis);
    	MemVirTot = (TextView) findViewById(R.id.MemVirTot);
    	MemVirDis = (TextView) findViewById(R.id.MemVirDis);
    	MemVirUs = (TextView) findViewById(R.id.MemVirUs);
        
    	btnKb = (Button) findViewById(R.id.btnKb);
    	btnMb = (Button) findViewById(R.id.btnMb);
    	
    	// Mandamos la orden de MEmoria
        try {
        	Conexion.salida.flush();
			Conexion.salida.writeObject("Memoria");
			
			// Obtenemos los datos del servidor
			String mensaje = (String) Conexion.entrada.readObject();
			while(!mensaje.equals("Fin_Memoria")){
				memftotal=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				memfdis=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				memvirtammax=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				memvirdis=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				memvirus=mensaje;
				mensaje = (String) Conexion.entrada.readObject();
				
			} 
			
			// Dependiendo de el sistema operativo que tenga el sistema remoto, vamos a activar o desactivar  KB o MB.
			if(SesionLocal.ObtenerSistemaOperativo().equals("Linux"))
				btnKb.setEnabled(false);
			else
				btnMb.setEnabled(false);
			
			
			MemFisTot.setText(memftotal.trim());
			MemFisDis.setText(memfdis.trim());
	    	MemVirTot.setText(memvirtammax.trim());
	    	MemVirDis.setText(memvirdis.trim());
	    	MemVirUs.setText(memvirus.trim());
		
	    	// Actuamos segun la pulsacion de los botones KB y Mb, segun linux o Windows.
			btnMb.setOnClickListener(new OnClickListener() {
		          public void onClick(View v) {
						btnMb.setEnabled(false);
						btnKb.setEnabled(true);
						 
						if(SesionLocal.ObtenerSistemaOperativo().equals("Linux")){
							int res= Integer.valueOf(memftotal.substring(0, memftotal.length()-3))/1024;
							MemFisTot.setText(String.valueOf(res)+ " Mb");
							res= Integer.valueOf(memfdis.substring(0, memfdis.length()-3))/1024;
							MemFisDis.setText(String.valueOf(res)+ " Mb");
							res= Integer.valueOf(memvirtammax.substring(0, memvirtammax.length()-3))/1024;
	 						MemVirTot.setText(String.valueOf(res)+ " Mb");
							res= Integer.valueOf(memvirdis.substring(0, memvirdis.length()-3))/1024;
							MemVirDis.setText(String.valueOf(res)+ " Mb");
							res= Integer.valueOf(memvirus.substring(0, memvirus.length()-3))/1024;
							MemVirUs.setText(String.valueOf(res)+ " Mb");
						}else{
							MemFisTot.setText(memftotal.trim());
							MemFisDis.setText(memfdis.trim());
					    	MemVirTot.setText(memvirtammax.trim());
					    	MemVirDis.setText(memvirdis.trim());
					    	MemVirUs.setText(memvirus.trim());
						
						}
						
		         } 
		    });

			btnKb.setOnClickListener(new OnClickListener() {
		          public void onClick(View v) {
						btnMb.setEnabled(true);
						btnKb.setEnabled(false);
						
						if(SesionLocal.ObtenerSistemaOperativo().equals("Linux")){
							MemFisTot.setText(memftotal.trim());
							MemFisDis.setText(memfdis.trim());
					    	MemVirTot.setText(memvirtammax.trim());
					    	MemVirDis.setText(memvirdis.trim());
					    	MemVirUs.setText(memvirus.trim());
						
						}else{
							int res= Integer.valueOf(memftotal.substring(0, memftotal.length()-3).trim().replace(".", ""))*1024;
							MemFisTot.setText(String.valueOf(res)+ " Kb");
							res= Integer.valueOf(memfdis.substring(0, memfdis.length()-3).trim().replace(".", ""))*1024;
							MemFisDis.setText(String.valueOf(res)+ " Kb");
							res= Integer.valueOf(memvirtammax.substring(0, memvirtammax.length()-3).trim().replace(".", ""))*1024;
							MemVirTot.setText(String.valueOf(res)+ " Kb");
							res= Integer.valueOf(memvirdis.substring(0, memvirdis.length()-3).trim().replace(".", ""))*1024;
							MemVirDis.setText(String.valueOf(res)+ " Kb");
							res= Integer.valueOf(memvirus.substring(0, memvirus.length()-3).trim().replace(".", ""))*1024;
							MemVirUs.setText(String.valueOf(res)+ " Kb");
						}
						
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

}
