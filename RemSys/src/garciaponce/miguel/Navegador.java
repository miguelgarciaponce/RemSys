package garciaponce.miguel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Navegador extends Activity {
	private TextView Ruta;
	private String NuevaRuta;
	private GridView ListaFicheros;
	private Vector<Fichero> Ficheros;
	private Button btnCrearFichero;
	private Vector<String> RutaPrevias;
	private AdaptadorFicheros adaptador;
	private ImageButton ImagenFlechaIzq;
	private ImageButton ImagenFlechaDer;
	private String mensaje;
	private int pos;
	private boolean IndicadorOpcionPegar;
	private boolean IndicadorCopia;
	private boolean IndicadorCorte;
	private String FicheroOrigenPegado;
	private String argumentos;
	private boolean IndicadorCFichero=false;
	private String RutaCrearFichero;
	private Socket conexiontrans;
	private AlertDialog alertDialog;
	private ProgressDialog pDialog;
	private MiTareaAsincrona tarea1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navegador);
         
        // Inicialización
        IndicadorOpcionPegar=false;
        RutaPrevias=new Vector<String>();
        Ficheros = new Vector<Fichero>();
        
        Ruta = (TextView) findViewById(R.id.TextViewRuta);
        

        // Comprobamos si hemos vuelto de la actividad CrearFichero
        Bundle b = this.getIntent().getExtras();
        if(b != null){
        	IndicadorCFichero = b.getBoolean("IndicadorCrearFichero");
        	RutaCrearFichero = b.getString("Ruta");
        }
        
        // Si hemos venido de la actividad anterior, actualizamos ruta y GridView
        if(IndicadorCFichero){
        	Ruta.setText(RutaCrearFichero);
        	ActualizarGridViewAnterior();

        	// Establecemos el adaptador.
        	adaptador = new AdaptadorFicheros(this);
	        ListaFicheros = (GridView)findViewById(R.id.GridViewFicheros);
		    ListaFicheros.setAdapter(adaptador);
    	
        }else{ // Sino, comenzamos una nueva navegación, estableciendo la ruta y obteniendo MiPc
        	
        	Ruta.setText("");
            try {
            	// Enviamos el mensaje Navegacion para obtener las unidades (Mi PC) y empezar a visualizarlas.
            	Conexion.salida.flush();
    			Conexion.salida.writeObject("Navegacion");
    			MostrarMiPc();
    		
            } catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
        	
        }
        
        // Registramos menu contextual para el GridView
        registerForContextMenu(ListaFicheros);
		
		// Cuando pulsemos sobre algun fichero, actualizamos ruta y GridView
	    ListaFicheros.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
	    	  public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {
		    	   // record position/id/whatever here
		    	   ActualizarRuta(parent, v, position,id);
		    	   ActualizarGridView(parent,v,position,id);
	    	  }
	     });
	    
	    // Al pulsar largo sobre cualquier elemento, guardamos su posicion
	    ListaFicheros.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	  public boolean onItemLongClick(AdapterView<?> parent, final View v, int position, long id) {
		    	   // record position/id/whatever here
		    	   pos=position;
		    	   return false;
	    	  }
	     });
	    
		// Pulsación del ImageButton FlechaIzquierda    
		ImagenFlechaIzq = (ImageButton)findViewById(R.id.ImageFlechaIzq);
		ImagenFlechaIzq.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	// Si no hay ruta especificada (principio), no hacemos nada
            	if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
            		//Windows
            		if(Ruta.getText().toString().length()!=0){    
            			//Guardamos la RutaPrevia para el botï¿½nFlechaDerecha
	            		NuevaRuta="";
	 				    int cont=RutaAnteriorGuardado();
	            		
	 				    // Si estamos listando una unidad, para volver a MiPc, recargamos el layout
	            		if(cont==0){
	            			MostrarMiPc();
	            			Ruta.setText("");
	            		}else{
							Ruta.setText(NuevaRuta);
							// Mandamos el directorio a listar.
							ActualizarGridViewAnterior();
			    		} 
            		}
            	}else{
            		//Linux
            		if(Ruta.getText().toString().length()>1){
            			String NuevaRuta="";
            			int cont=RutaAnteriorGuardado();
            			
            			// Separamos la ruta por el carácter "/"
            			String datos[] = Ruta.getText().toString().split("/");
            			for(int i=1;i<(datos.length-1);i++)
            				NuevaRuta=NuevaRuta + "/" + datos[i];
            			NuevaRuta = NuevaRuta.trim();
            			if(NuevaRuta.equals(""))
            				NuevaRuta="/";
            			Ruta.setText(NuevaRuta);
            			// Mandamos el directorio a listar.
            			ActualizarGridViewAnterior();
            		}
            		
            	}
            }
        });  
		
		// Flecha Derecha
		ImagenFlechaDer = (ImageButton)findViewById(R.id.ImageFlechaDer);
		ImagenFlechaDer.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	if(RutaPrevias.size()!=0){
            			if(RutaPrevias.lastElement().length()>Ruta.getText().length()){
	            			Ruta.setText(RutaPrevias.lastElement());
	            		}
	            		// Eliminamos el ultimo elemento de las rutas previas
	            		RutaPrevias.removeElementAt(RutaPrevias.size()-1);
	            		// Obtenemos los resultados y almancenamos en un vector Ficheros para visualizarlos en el GridView
						ActualizarGridViewAnterior();
            	}
            		
            	
            }  
        });  
		 
		// Boton Crear Fichero
		btnCrearFichero = (Button) findViewById(R.id.BotonCrearFichero);
		btnCrearFichero.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	// Nos cercioramos de que sea una ruta válida (no se puede crear en una ruta vacía)
            	if((Ruta.getText().length()>=3 && SesionLocal.ObtenerSistemaOperativo().equals("Windows")) || (Ruta.getText().length()>=1 && SesionLocal.ObtenerSistemaOperativo().equals("Linux"))){
	            		Intent intent = new Intent(Navegador.this,CrearFichero.class);
	            		intent.putExtra("Ruta", Ruta.getText().toString());
	            		startActivity(intent);
            	}
        }});  
        
    }
    
    
    
    private int RutaAnteriorGuardado(){
    	int cont = 0;
    	
    	if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
    		// Aádimos la ruta actual a las previas
    		RutaPrevias.add(Ruta.getText().toString());
    		
    		// Establecemos NuevaRuta a la RutaAnterior
    		String ruta[];
    		ruta=Ruta.getText().toString().split("\\\\");
    		cont=0;
    		for(int i=0;i<(ruta.length-1);i++){
    			if(NuevaRuta.length()==0){
    				NuevaRuta=ruta[i] + "\\";
    			}else{
    				NuevaRuta=NuevaRuta+ruta[i]+"\\";
    			}
    			cont++;
    		}
    	}else{
    		//Linux
			RutaPrevias.add(Ruta.getText().toString());
    	}
    	for(int i=0;i<RutaPrevias.size();i++)
    		Log.i("RutaPRevias", RutaPrevias.get(i));
    	return cont;

    }
    
    // Procedimiento para tratar con directorios . y ..
    private void ActualizarRutaAnterior(){
    	if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
    		String ruta[];
        	String NuevaRuta="";
    		ruta=Ruta.getText().toString().split("\\\\");
    		for(int i=0;i<(ruta.length-1);i++){
    			if(NuevaRuta.length()==0){
    				NuevaRuta=ruta[i] + "\\";
    			}else{
    				NuevaRuta=NuevaRuta+ruta[i]+"\\";
    			}
    		}
    		Ruta.setText(NuevaRuta);
    	}else{
    		//Linux
    	}
    	
    }
    
	private void ActualizarGridViewAnterior(){
    	try {
    		Conexion.salida.flush();
			Conexion.salida.writeObject("ActualizarGridView");
			Conexion.salida.flush();
			Conexion.salida.writeObject(Ruta.getText());
		   
			// Obtenemos los resultados y almancenamos en un vector Ficheros para visualizarlos en el GridView
			mensaje = (String) Conexion.entrada.readObject();
			Ficheros.clear();
			int cont=0;
			String[] datos_recibidos;
			while(!mensaje.equals("fin_listado_dir")){
				if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
					if(cont > 2 ){
						String NombreFichero="";
						datos_recibidos = mensaje.split(" ");
						for(int i=1;i<datos_recibidos.length;i++){
							NombreFichero = NombreFichero+" "+datos_recibidos[i];
						}
						Fichero aux = new Fichero(datos_recibidos[0].toString().trim(),NombreFichero.trim());
						Ficheros.add(aux);
					}
				}else{
					String NombreFichero="";
					datos_recibidos = mensaje.split(" ");
					for(int i=1;i<datos_recibidos.length;i++){
						NombreFichero = NombreFichero+" "+datos_recibidos[i];
					}
					Fichero aux = new Fichero(datos_recibidos[0].toString().trim(),NombreFichero.trim());
					Ficheros.add(aux);
				}
				cont++;
				mensaje = (String) Conexion.entrada.readObject();
			}
			   
			// Establecemos el adaptador con el nuevo Vector<Fichero> obtenido.
			ListaFicheros = (GridView)findViewById(R.id.GridViewFicheros);
			ListaFicheros.setAdapter(adaptador);
    	}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    // Funcion encargada de la actualización de la Ruta.
    private void ActualizarRuta(AdapterView<?> parent, final View v, int position, long id){
    	// Actualizamos la ruta
    	if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
    		if(Ruta.getText().length()==0){
 			   Ruta.setText(Ficheros.get(position).getNombre());
 		   	}else{
 			   // Si el fichero que pulsamos es .. , volvemos a la ruta anterior
 			   if(Ficheros.get(position).getNombre().equals("..")){
 				   ActualizarRutaAnterior();
 			   }else{
 				   // Si el fichero el cual pulsamos es un punto, no hacemos nada en el cambio de ruta
 				   if(Ficheros.get(position).getNombre().equals(".")){
 					   
 				   }else{
 					   Ruta.setText(Ruta.getText() + Ficheros.get(position).getNombre() + "\\");
 				   }
 			   }
 		   }
    		
    	}else{
    		//Linux
    		if(Ruta.getText().toString().length()>1){
    			Ruta.setText(Ruta.getText().toString() + "/" + Ficheros.get(position).getNombre());
    		}else{
    			Ruta.setText(Ruta.getText().toString() + Ficheros.get(position).getNombre());
    		}
    		
    	}
    }
    
    private void ActualizarGridView(AdapterView<?> parent, final View v, int position, long id){
    	// Mandamos el directorio a listar.
		try {
			Conexion.salida.flush();
			Conexion.salida.writeObject("ActualizarGridView");
			Conexion.salida.flush();
			Conexion.salida.writeObject(Ruta.getText());
			   
			   // Obtenemos los resultados y almancenamos en un vector Ficheros para visualizarlos en el GridView
			   mensaje = (String) Conexion.entrada.readObject();
			   Ficheros.clear();
			   int cont=0;
			   String[] datos_recibidos;
			   while(!mensaje.equals("fin_listado_dir")){				
					if(SesionLocal.ObtenerSistemaOperativo().equals("Windows")){
						if(cont > 2 ){
							String NombreFichero="";
							datos_recibidos = mensaje.split(" ");
							for(int i=1;i<datos_recibidos.length;i++){
			   					NombreFichero = NombreFichero+" "+datos_recibidos[i];
			   				}
			   				Fichero aux = new Fichero(datos_recibidos[0].toString().trim(),NombreFichero.trim());
			   				Ficheros.add(aux);
						}
					}else{
						String NombreFichero="";
						datos_recibidos = mensaje.split(" ");
						for(int i=1;i<datos_recibidos.length;i++){
		   					NombreFichero = NombreFichero+" "+datos_recibidos[i];
		   				}
		   				Fichero aux = new Fichero(datos_recibidos[0].toString().trim(),NombreFichero.trim());
		   				Ficheros.add(aux);
					}
				   	cont++;
					mensaje = (String) Conexion.entrada.readObject();
			   }
			    
			   ListaFicheros = (GridView)findViewById(R.id.GridViewFicheros);
			   ListaFicheros.setAdapter(adaptador);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
     
    
    private void MostrarMiPc(){
    	try {
    		// Si es Linux
			if(SesionLocal.ObtenerSistemaOperativo().equals("Linux"))
				Ruta.setText("/");
    		
    		Conexion.salida.flush();
			Conexion.salida.writeObject("MostrarMiPc");
			Ficheros.clear();
			
			// Obtenemos los datos del servidor.
			mensaje = (String) Conexion.entrada.readObject();
			String[] datos;
			while(!mensaje.equals("Fin_MostrarMiPc")){				
				datos = mensaje.split(" ");
				Fichero aux = new Fichero(datos[1].toString().trim(),datos[0].toString().trim());
				Ficheros.add(aux);
				Log.i("Ficheros",aux.getIcono() + " " + aux.getNombre());
				mensaje = (String) Conexion.entrada.readObject();
			}
			// Establecemos el adaptador
			adaptador = new AdaptadorFicheros(this);
	        ListaFicheros = (GridView)findViewById(R.id.GridViewFicheros);
		    ListaFicheros.setAdapter(adaptador);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
    }

    // Adaptador del GridView
    class AdaptadorFicheros extends ArrayAdapter {
    	
    	Activity context;
    	
		AdaptadorFicheros(Activity context) {
    		super(context, R.layout.fichero, Ficheros);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
			View item = convertView;
			ViewHolder holder;
    		
    		if(item == null)
    		{
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.fichero, null);
    			
    			holder = new ViewHolder();
    			holder.icono = (ImageView)item.findViewById(R.id.ImagenFichero);
    			holder.nombre = (TextView)item.findViewById(R.id.TextoFichero);
    			// Cambiamos el tamaï¿½o de la imagen del fichero.
    			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
    			holder.icono.setLayoutParams(layoutParams);
    			holder.nombre.setTextSize(11);
    			
    			item.setTag(holder);
    			
    		}
    		else
    		{
    			holder = (ViewHolder)item.getTag();
    		}
			 
			holder.nombre.setText(Ficheros.elementAt(position).getNombre());
			// PRocesamiento de iconos
			if(Ficheros.elementAt(position).getIcono().equals("Unidadfija")){
				holder.icono.setBackgroundResource(R.drawable.discoduro);
			}else{
				if(Ficheros.elementAt(position).getIcono().equals("CD-ROM")){
					holder.icono.setBackgroundResource(R.drawable.cdrom);
				}else{
					if(Ficheros.elementAt(position).getIcono().equals("<DIR>") || Ficheros.elementAt(position).getIcono().startsWith("d")){
						holder.icono.setBackgroundResource(R.drawable.carpeta);
					}else{
						if(Ficheros.elementAt(position).getNombre().endsWith(".exe")){
							holder.icono.setBackgroundResource(R.drawable.iconoexe);
						}else{
							if(Ficheros.elementAt(position).getNombre().endsWith(".zip") || Ficheros.elementAt(position).getNombre().endsWith(".rar") ){
								holder.icono.setBackgroundResource(R.drawable.iconozip);
							}else{
								if(Ficheros.elementAt(position).getNombre().endsWith(".txt")){
									holder.icono.setBackgroundResource(R.drawable.iconotxt);
								}else{
									if(Ficheros.elementAt(position).getNombre().endsWith(".jpg")){
										holder.icono.setBackgroundResource(R.drawable.icono_jpg);
									}else{
										if(Ficheros.elementAt(position).getNombre().endsWith(".mp3")){
											holder.icono.setBackgroundResource(R.drawable.iconomp3);
										}else{
											if(Ficheros.elementAt(position).getNombre().endsWith(".bat")){
												holder.icono.setBackgroundResource(R.drawable.icono_bat);
											}else{
												if(Ficheros.elementAt(position).getNombre().endsWith(".pdf")){
													holder.icono.setBackgroundResource(R.drawable.iconopdf);
												}else{
													if(Ficheros.elementAt(position).getNombre().endsWith(".dll")){
														holder.icono.setBackgroundResource(R.drawable.iconodll);
													}else{
														if(Ficheros.elementAt(position).getNombre().endsWith(".ini")){
															holder.icono.setBackgroundResource(R.drawable.iconoini);
														}else{
															if(Ficheros.elementAt(position).getNombre().endsWith(".png")){
																holder.icono.setBackgroundResource(R.drawable.iconopng);
															}else{
																if(Ficheros.elementAt(position).getNombre().endsWith(".java")){
																	holder.icono.setBackgroundResource(R.drawable.iconojava);
																}else{
																	if(SesionLocal.SistemaOperativo.equals("Windows"))
																		holder.icono.setBackgroundResource(R.drawable.iconoarchivowin);
																	else
																		holder.icono.setBackgroundResource(R.drawable.iconolinux);

																}
															}
															
														}
													}
												}
											}
										}
									}
								}
							}
						}
						
					}
				}
			}
				
			return(item); 
		}
    }
    
    static class ViewHolder {
    	public ImageView icono;
    	public TextView nombre;
    }
    
    // Pulsacion de tecla BACK (sale de navegación al MainMenu)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	try {
				Conexion.salida.flush();
				Conexion.salida.writeObject("Salir_Navegacion");
				Intent intent = new Intent(Navegador.this , Mainmenu.class);
	        	startActivity(intent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        	
        }
        return super.onKeyDown(keyCode, event);
    }
    
    // Tratamiento del menu contextual
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
     
        MenuInflater inflater = getMenuInflater();
     
        if(v.getId() == R.id.GridViewFicheros)
        {
        	// Ficheros exe, bat, exe, java , bin, sh , run
        	if(Ficheros.get(pos).getNombre().endsWith(".exe") || Ficheros.get(pos).getNombre().endsWith(".bat") || Ficheros.get(pos).getNombre().endsWith(".java") || Ficheros.get(pos).getNombre().endsWith(".sh") || Ficheros.get(pos).getNombre().endsWith(".run") || Ficheros.get(pos).getNombre().endsWith(".py") || Ficheros.get(pos).getNombre().endsWith(".bin")){
        		inflater.inflate(R.menu.menunavegadorejecutar, menu);
            }else{          	
            	// Ficheros txt, jpg, png , pdf
            	if(Ficheros.get(pos).getNombre().endsWith(".txt") || Ficheros.get(pos).getNombre().endsWith(".jpg") || Ficheros.get(pos).getNombre().endsWith(".mp3") || Ficheros.get(pos).getNombre().endsWith(".png") || Ficheros.get(pos).getNombre().endsWith(".pdf"))
            			inflater.inflate(R.menu.menunavegadortransferir, menu);
            		else{
            			// Demás ficheros (Cuya longitud de ruta sea mayor de 0)
            			if(Ruta.length()>0)
            				inflater.inflate(R.menu.menunavegador, menu);
            		}
            }
        } 
    }
    
    // TRatamiento del menu contextual    
    @Override
    public boolean onContextItemSelected(MenuItem item) {    	
		switch (item.getItemId()) {
			// Si pulsamos sobre ejecutar.
		case R.id.menunavegadorEjecutar:
			Ejecutar();
	        return true;
	    
	        // Si pulsamos sobre Tranferir a SD-Card
		case R.id.menunavegadorTransferir:
			pDialog = new ProgressDialog(Navegador.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Transfiriendo fichero " + Ficheros.get(pos).getNombre().toString());
	        pDialog.show();
	        pDialog.setCancelable(false);
			tarea1 = new MiTareaAsincrona();
			tarea1.execute();
			
	        return true;
		
	        // Si pulsamos sobre copiar
		case R.id.menunavegadorCopiar:
			IndicadorOpcionPegar=true;
			IndicadorCopia=true;
			IndicadorCorte=false;
			if(SesionLocal.ObtenerSistemaOperativo().equals("Windows"))
				FicheroOrigenPegado = Ruta.getText().toString()+Ficheros.get(pos).getNombre().toString();
			else
				FicheroOrigenPegado=Ruta.getText().toString()+"/"+Ficheros.get(pos).getNombre().toString();

			return true;
			// Si pulsamos sobre Cortar
		case R.id.menunavegadorCortar:
			IndicadorOpcionPegar=true;
			IndicadorCorte=true;
			IndicadorCopia=false;
			if(SesionLocal.ObtenerSistemaOperativo().equals("Windows"))
				FicheroOrigenPegado=Ruta.getText().toString()+Ficheros.get(pos).getNombre().toString();
			else
				FicheroOrigenPegado=Ruta.getText().toString()+"/"+Ficheros.get(pos).getNombre().toString();

			return true;
			// Si pulsamos sobre eliminar
		case R.id.menunavegadorEliminar:
			EliminarFichero();
	        return true;
	        // Si pulsamos sobre renombrar
		case R.id.menunavegadorRenombrar:
			RenombrarFichero();
	        return true;
		default:
			return super.onContextItemSelected(item);
		}
    }
    
    // Renombrar Fichero
    private void RenombrarFichero(){
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Nuevo nombre para el fichero " + Ficheros.get(pos).getNombre());
        final EditText input = new EditText(this);
        alert.setView(input);
		alert.setPositiveButton("Renombrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { 
            	try { 
            		Enviar("RenombrarFichero"); 
                	Enviar(Ruta.getText().toString());
                	Enviar(Ficheros.get(pos).getNombre().toString());
                	Enviar(input.getText().toString());
					mensaje = (String) Conexion.entrada.readObject();
					Toast toast1 = Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG);
	    	        toast1.show();
				} catch (OptionalDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					ActualizarGridViewAnterior();
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
    
    // EliminarFichero
    private void EliminarFichero(){
    	// Confirmacion
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Seguro que quieres eliminar el fichero " + Ficheros.get(pos).getNombre());
		alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	try {
            		// Enviar datos al Servidor.
            		Enviar("EliminarFichero");
            		if(SesionLocal.ObtenerSistemaOperativo().equals("Windows"))
            			Enviar(Ruta.getText().toString()+Ficheros.get(pos).getNombre().toString());
            		else
            			Enviar(Ruta.getText().toString()+"/"+Ficheros.get(pos).getNombre().toString());

					mensaje = (String) Conexion.entrada.readObject();
					Toast toast1 = Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG);
	    	        toast1.show();
				} catch (OptionalDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					// Actualizamos GridView
					ActualizarGridViewAnterior();

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
    
    
    // TransferirFichero => hilo que intenta conectar a un nuevo puerto abierto (11111)
    private class Transferirfichero implements Runnable{
    	private String ip;
    	private String nombre;
    	private int Puerto;
    	
    	Transferirfichero(String i,String n,int puerto){
    		ip=i;
    		nombre=n;
    		Puerto=puerto;
    	}

		public void run() {
			// TODO Auto-generated method stub

			try {
				conexiontrans = new Socket(ip,Puerto);
				File folder = new File(Environment.getExternalStorageDirectory() + "/RemSys/");
				
				// Si la carpeta no existe, se crea
				if (!folder.exists()) {
				    folder.mkdir();
				}
				
				// Abrimos fichero para escritura y mediante los flujos adecuados escribimos lo que nos venga de el
				File f = new File(folder.getAbsolutePath(), nombre);
				byte[] b = new byte[1024];
				int len = 0;
				int bytcount = 1024;
				FileOutputStream inFile = new FileOutputStream(f);
				InputStream is = conexiontrans.getInputStream();
				BufferedInputStream in2 = new BufferedInputStream(is, 1024);
				while ((len = in2.read(b, 0, 1024)) != -1) {
				      bytcount = bytcount + 1024;
				      inFile.write(b, 0, len);
				}
				// Cerramos flujos y socket
				in2.close();
			    inFile.close();
			    conexiontrans.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
		
    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {
  	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Enviar("TransferirFichero");
	    	Enviar(Ruta.getText().toString());
			Enviar(Ficheros.get(pos).getNombre().toString());
    	}
    	
    	
    	@Override
    	protected void onCancelled() {
    		
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
    		pDialog.dismiss();
    		Toast.makeText(Navegador.this, "Fichero " + Ficheros.get(pos).getNombre().toString() + " transferido", Toast.LENGTH_LONG).show();

    	}
    	
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				// Obtenemos mensaje de sicronizacion, en este momento el servidor espera una conexion por el puerto indicado
				mensaje = (String) Conexion.entrada.readObject();
				String[] datos = mensaje.split(":");
				// Creamos el hilo que va a servir para la transferencia.
				if(datos[0].equals("ListoTransferencia")){
					Log.i("TransferirFichero", "new Transferirfichero(" + Conexion.ObtenerIP() + "," + Ficheros.get(pos).getNombre().toString()+ "," + Integer.parseInt(datos[1]) + ");");
					Thread t = new Thread(new Transferirfichero(Conexion.ObtenerIP(),Ficheros.get(pos).getNombre().toString(),Integer.parseInt(datos[1])));
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		
    }
    
    


    // Ejecutar un fichero con argumentos.
    private void Ejecutar(){
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Introduce los parámetros para la ejecucion del fichero " + Ficheros.get(pos).getNombre());
		final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ejecutar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	argumentos = input.getText().toString().trim();
            	EjecutarFichero();
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
    
    private void EjecutarFichero(){
    	// Establece comunicacion y envia datos para la ejecución del fichero.
    		Enviar("EjecutarFichero");
    		Enviar(Ruta.getText().toString());
			Enviar(Ficheros.get(pos).getNombre().toString());
			Enviar(argumentos);
	}
    
    private void Enviar(String n){
    	try{
    		Conexion.salida.flush();
    		Conexion.salida.writeObject(n); 
    		
    	}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // Boton menu del teléfono para la opcion de pegado de ficheros.     
    @Override 
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	
    	if(IndicadorOpcionPegar && Ruta.getText().toString().length()>0) {
    		MenuInflater inflater = getMenuInflater();
    	    inflater.inflate(R.menu.menupegar, menu);

    	}  
    	return super.onPrepareOptionsMenu(menu);

    }  

    // PRocesamiento de pegado de fichero, distinguiendo si se ha copiado o cortado.
    private void PegarFichero(){
    	if(IndicadorCopia){  
    		Enviar("Copia"); 
    	}else{
    		Enviar("Corte");
    	}
		Enviar(FicheroOrigenPegado);
		Enviar(Ruta.getText().toString());   
		try {
			String mensaje = (String) Conexion.entrada.readObject();
			Toast toast1 = Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG);
	        toast1.show();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			// Actualizamos Gridview y indicadores.
			ActualizarGridViewAnterior();
			if(IndicadorCorte)  
				IndicadorOpcionPegar=IndicadorCorte=false;
		}
    }
    
    // Seleccion de la opcion de pegado.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPegar:
            	PegarFichero();
        }
        return true;
    }
}

