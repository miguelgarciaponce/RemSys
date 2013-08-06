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
 
public class SesionLocal extends Service 
{
    public static Activity ACTIVIDAD;
    public static String NombreUsuario;
    public static String SistemaOperativo;
    
    public static void establecerActividadPrincipal(Activity actividad)
    {
        SesionLocal.ACTIVIDAD=actividad;
    }
    
    public static void establecerSistemaOperativo(String Sis)
    {
        SesionLocal.SistemaOperativo=Sis;
    }
    
    public static String ObtenerSistemaOperativo()
    {
        return SistemaOperativo;
    }
 
    public void onCreate()
    {
        super.onCreate();
 
        // Iniciamos el servicio
        this.iniciarServicio();
 
        Log.i(getClass().getSimpleName(), "Servicio iniciado");
    }
 
    public void onDestroy()
    {
        super.onDestroy();
 
        // Detenemos el servicio
        this.finalizarServicio();
 
        Log.i(getClass().getSimpleName(), "Servicio detenido");
    }
 
    public IBinder onBind(Intent intent)
    {
        // No usado de momento, s�lo se usa si se va a utilizar IPC
        // (Inter-Process Communication) para comunicarse entre procesos
        return null;
    }
 
    public void iniciarServicio()
    {
    }
 
       
    
    public void finalizarServicio()
    {
       
    }
 
    
}