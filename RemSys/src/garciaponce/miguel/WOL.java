package garciaponce.miguel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WOL extends Activity {
	private EditText IPDif;
	private EditText DirMacWOL;
	private Button btnEncender;
    private String IP;
    private String MAC;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wakeonlan);

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
        	IP = extras.getString("DirIP");
        	MAC = extras.getString("DirMac");
        }
        
        
        IPDif = (EditText) findViewById(R.id.IPDifusion);
        DirMacWOL = (EditText) findViewById(R.id.DirMacWOL);
        IPDif.setText(IP);
        DirMacWOL.setText(MAC);
        
        
        btnEncender = (Button) findViewById(R.id.BtnEncender);
        
        btnEncender.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	WakeOnLan(IPDif.getText().toString(),DirMacWOL.getText().toString());
                
                Intent intent = new Intent(WOL.this , ListaHosts.class);
		        startActivity(intent); 
                
            }
        });
        
        
        

        
    }
    
    public static final int PORT = 9;    
        
    protected void WakeOnLan(String ipStr, String macStr) {
    	try {
    		// Construcción del paquete mágico
    		// El paquete mágico es una trama ethernet que comienza con 6 bytes de cabecera FF FF FF FF FF FF y sigue con 16 repeticiones de la dirección física MAC
                byte[] macBytes = getMacBytes(macStr);
                byte[] bytes = new byte[6 + 16 * macBytes.length];
                for (int i = 0; i < 6; i++) {
                    bytes[i] = (byte) 0xff;
                }
                for (int i = 6; i < bytes.length; i += macBytes.length) {
                    System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
                }
                
                InetAddress address = InetAddress.getByName(ipStr);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
                socket.close();
                
                Log.i("WOL","Wake-on-LAN packet sent.");
        }catch (Exception e) {
        		Log.i("WOL","Failed to send Wake-on-LAN packet: + e");
                
        }
            
   }
        
        private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
            byte[] bytes = new byte[6];
            String[] hex = macStr.split("(\\:|\\-)");
            // Si la longitud es distinta de 6 lanzamos excepción 
            if (hex.length != 6) {
                throw new IllegalArgumentException("Invalid MAC address.");
            }
            try {
            	// Convertimos a un vector de byte
                for (int i = 0; i < 6; i++) {
                    bytes[i] = (byte) Integer.parseInt(hex[i], 16);
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Direccion MAC hexadecimal invalida");
            }
            return bytes;
        }
        
       
    
    
}
