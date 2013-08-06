package garciaponce.miguel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
 
public class Conexion extends Service 
{
    public static Activity ACTIVIDAD; 
    public static Socket conexion=null;
    public static ObjectOutputStream salida;
	public static ObjectInputStream entrada;
	private static String IP;
	private static int sckt;
	private static boolean conectado=false;
    
	public static void establecerIP(String ip){
		IP=ip;
	}
	
	public static String ObtenerIP(){
		return IP;
	}
	
	public static boolean ObtenerEstado(){
		return conectado;
	}
	
	
	public static int ObtenerPuerto(){
		return sckt;
	}
	public static void establecerSocket(int socket){
		sckt=socket;
	}
	
	
    public static void establecerActividadPrincipal(Activity actividad)
    {
        Conexion.ACTIVIDAD=actividad;
    }
    
     
           
    public void onCreate()
    {
        super.onCreate();
 
        // Iniciamos el servicio
        Conexion.iniciarServicio();
 
        Log.i(getClass().getSimpleName(), "Servicio iniciado");
    }
 
    public void onDestroy()
    {
        super.onDestroy();
 
        // Detenemos el servicio
        Conexion.finalizarServicio();
 
        Log.i(getClass().getSimpleName(), "Servicio detenido");
    }
 
    public IBinder onBind(Intent intent)
    {
        // No usado de momento, sólo se usa si se va a utilizar IPC
        // (Inter-Process Communication) para comunicarse entre procesos
        return null;
    }
 
    public static void iniciarServicio()
    {
        try
        {
        	// Conectamos y obtenemos flujos.
            conexion = new Socket(IP,sckt);
            conectado=true;
            ObtenerFlujos(); 
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
 
    
    public static void ObtenerFlujos(){
    	try {
			salida = new ObjectOutputStream( conexion.getOutputStream() );
			salida.flush();
			entrada = new ObjectInputStream( conexion.getInputStream() );
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		
    	
    }
    
    
    public static void finalizarServicio()
    {
        try
        {
            conexion.close();
        }
        catch(Exception e)
        {
            
        }
    }
 
    
}