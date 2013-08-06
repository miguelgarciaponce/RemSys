
import java.awt.*;
import javax.swing.*;

public class MostrarColores extends JFrame {


   public MostrarColores()
   {
      super( "Uso de los colores" );

      setSize( 400, 130 );
      setVisible( true );
   }


   public void paint( Graphics g )
   {

      super.paint( g );

      // establecer nuevo color de dibujo utilizando enteros
      g.setColor( new Color( 255, 0, 0 ) );
      g.fillRect( 25, 25, 100, 20 );
      g.drawString( "RGB actual: " + g.getColor(), 130, 40 );

      // establecer nuevo color de dibujo utilizando valores float
      g.setColor( new Color( 0.0f, 1.0f, 0.0f ) );
      g.fillRect( 25, 50, 100, 20 );
      g.drawString( "RGB actual: " + g.getColor(), 130, 65 );


      g.setColor( Color.BLUE );
      g.fillRect( 25, 75, 100, 20 );
      g.drawString( "RGB actual: " + g.getColor(), 130, 90 );

      // mostrar valores RGB individuales
      Color color = Color.MAGENTA;
      g.setColor( color );
      g.fillRect( 25, 100, 100, 20 );
      g.drawString( "Valores RGB: " + color.getRed() + ", " +
         color.getGreen() + ", " + color.getBlue(), 130, 115 );

   } // fin 
   // 
   public static void main( String args[] )
   {
      MostrarColores aplicacion = new MostrarColores();
      aplicacion.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
   }

} // fin de la clase MostrarColores
