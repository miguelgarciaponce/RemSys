package garciaponce.miguel;
 
import java.io.IOException;
import java.io.OptionalDataException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Mainmenu extends Activity {
	private Button btnInfSys;
	private Button btnProcesos;
	private Button btnNavegacion;
	private Button btnScripts;
	private Button btnOrdenLibre;
	private Button btnReinicio;
	private Button btnApagado;
	private Toast toast;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu); 
        Conexion.ACTIVIDAD=this;
        if(Conexion.conexion == null){
        	toast = Toast.makeText(getApplicationContext(),"No se ha conectado" , Toast.LENGTH_LONG);
        	toast.show();
        	Intent intent = new Intent(Mainmenu.this , Login.class);
	        startActivity(intent);
        	
        }else{
        	if(Conexion.conexion.isConnected()){
        		toast = Toast.makeText(getApplicationContext(),"Conectando al host " + Conexion.ObtenerIP() , Toast.LENGTH_LONG);
                toast.show();
        	}
        }	
       
        // Mandamos mensaje para obtener el sistema operativo del sistema remoto.
        try {
        	Conexion.salida.flush();
			Conexion.salida.writeObject("MainMenu");
        	String mensaje = (String) Conexion.entrada.readObject(); 
			SesionLocal.establecerSistemaOperativo(mensaje);
		} catch (OptionalDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(); 
		} catch (ClassNotFoundException e1) {  
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        // Obtenemos componentes gráficos y actuamos según botones.
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        btnInfSys = (Button)findViewById(R.id.BontonInfSistema);
        btnInfSys.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	            Intent intent = new Intent(Mainmenu.this , Infsysmenu.class);
		        startActivity(intent);
            }
        });
        
        btnProcesos = (Button)findViewById(R.id.BotonProcesos);
        btnProcesos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	            Intent intent = new Intent(Mainmenu.this , InfProcesos.class);
		        startActivity(intent);
            }
        });
        
        btnNavegacion = (Button)findViewById(R.id.BotonNavegacion);
        btnNavegacion.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Mainmenu.this , Navegador.class);
		        startActivity(intent);
            }
        });
        
        btnScripts = (Button)findViewById(R.id.BotonScripts);
        btnScripts.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Mainmenu.this , Scripts.class);
		        startActivity(intent);
           }
        }); 
        
        btnOrdenLibre = (Button)findViewById(R.id.BotonOrdenLibre);
        btnOrdenLibre.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Mainmenu.this , OrdenLibre.class);
		        startActivity(intent);
            }
        }); 
        
        btnReinicio = (Button)findViewById(R.id.BotonReiniciar);
        btnReinicio.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	alert.setMessage("¿Seguro que desea reiniciar el sistema?");
            	alert.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	try {
       						Conexion.salida.flush();
       						Conexion.salida.writeObject("Reiniciar");
       				  } catch (IOException e) {
       					  	// TODO Auto-generated catch block 
       					  e.printStackTrace();
       				  }
                    	
                    }
        	    });

                alert.setNegativeButton("Cancelar",
                		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            }
                		});
                		alert.show();
                	}
        		});
        
        btnApagado = (Button)findViewById(R.id.BotonApagar);
        btnApagado.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	alert.setMessage("¿Seguro que desea apagar el sistema?");
                alert.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	try {
       						Conexion.salida.flush();
       						Conexion.salida.writeObject("Apagar");
       				  } catch (IOException e) {
       					  	// TODO Auto-generated catch block
       					  e.printStackTrace();
       				  }
                    	
                    }
        	    });

                alert.setNegativeButton("Cancelar",
                		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            }
                		});
                		alert.show();
                	}
      	});
    }
         
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // Si pulsamos el boton BACK, finalizamos el servicio de conexion y volvemos al layout ListaHosts
        	
        	try {
            	Conexion.salida.flush();
    			Conexion.salida.writeObject("TerminarConexion");
    		} catch (OptionalDataException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace(); 
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
        	
        	Intent servicio = new Intent(Mainmenu.this, Conexion.class);
        	if(stopService(servicio))
            {
            	Log.i("Conectar","Servicio finalizado correctamente");
            }
            else
            {
            	Log.i("Conectar","No se ha podido finalizar servicio Conexion");
            }
 
        	
        	Intent intent = new Intent(Mainmenu.this , ListaHosts.class);
        	startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
