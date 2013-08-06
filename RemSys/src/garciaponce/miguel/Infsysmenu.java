package garciaponce.miguel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Infsysmenu extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infsysmenu);
        Conexion.ACTIVIDAD=this;
        
        // Obtenemos componentes y pasamos al layout segun los botones pulsados.
        Button btnDiscos = (Button)findViewById(R.id.button1);
        btnDiscos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	            Intent intent = new Intent(Infsysmenu.this , InfDiscos.class);
		        startActivity(intent);
            }
        });
        
        Button btnRed = (Button)findViewById(R.id.button2);
        btnRed.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	            Intent intent = new Intent(Infsysmenu.this , InfRed.class);
		        startActivity(intent);
            }
        });
        
        Button btnSO = (Button)findViewById(R.id.Button01);
        btnSO.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	            Intent intent = new Intent(Infsysmenu.this , InfSisOp.class);
		        startActivity(intent);
            }
        });
        
        Button btnMem = (Button)findViewById(R.id.Button02);
        btnMem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	            Intent intent = new Intent(Infsysmenu.this , InfMem.class);
		        startActivity(intent);
            }
        });
    }
}
