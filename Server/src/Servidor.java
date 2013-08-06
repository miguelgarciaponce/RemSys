import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;


public class Servidor extends JFrame  {
	private static final long serialVersionUID = 1L;
	private JTextArea areaPantalla;
	private JButton Activar_Desactivar;
	private JTextField direccionIP;
	private JTextField mac;
	private InetAddress addr;
	private JTextField direccionlocal;
	private MacAddress dirmac;
	private ServerSocket servidor;
	private Socket conexion;
	private ServerSocket servidortrans;
	private Socket conexiontrans;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String mensaje;
    private Process p ;
    private InputStream is;
    private BufferedReader br;
    private String aux;
    private String nombreScript;
    private String DireccionScripts;
    private String SistemaOperativo;
    private boolean IndicadorPrueba;
    private volatile Thread HiloEjecutarServidor;
    private int Puerto =1234;
    private int PuertoTransferencia=11111;
    int cont=0;
    String excepcion="";
    // Fuentes
    Font boldUnderline = new Font("Comic Sans MS",Font.ROMAN_BASELINE, 15);
    Font FuenteAreaPantalla = new Font("Console",Font.HANGING_BASELINE, 13);
    Font FuenteTitulo = new Font("Comic Sans MS",Font.BOLD, 24);
    Font FuenteTituloSubventana = new Font("Comic Sans MS",Font.BOLD, 18);
   
    // Colores
    Color color = new Color(149,168,255);
    
    // Frame Servidor
    public Servidor(){
      super( "Servidor RemSys (Remote System Controller)");
      
      // Establecemos el Sistema Operativo
      if(System.getProperty("os.name" ).startsWith("Windows"))
 		   SistemaOperativo = "Windows";
 	   	else
 		   SistemaOperativo = "Linux";
      
      if(SistemaOperativo.equals("Windows"))
    	  setSize(950, 480);
      else
    	  setSize(1085, 480);
      
      
      // Establecemos el icono del programa Servidor
      ImageIcon icon = new ImageIcon(".\\Imagenes\\remsyslogo.png");
      setIconImage(icon.getImage().getScaledInstance( 190, 130 ,  java.awt.Image.SCALE_DEFAULT )); 
      
      
      setBackground(Color.CYAN);
      
      // Establecemos la posicion de la ventana principal del Servidor
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
      Point newLocation = new Point(middle.x - (getWidth() / 2), middle.y - (getHeight() / 2));
      setLocation(newLocation);


      JTextField Titulo = new JTextField("Monitor del Servidor RemSys");

      areaPantalla = new JTextArea(18,70);
      areaPantalla.setFont(FuenteAreaPantalla);
      areaPantalla.setBorder(BorderFactory.createLineBorder(Color.lightGray, 3));

      DefaultCaret caret = (DefaultCaret)areaPantalla.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      
      HiloEjecutarServidor = this.new HiloEjecutarServidor();

      JPanel panel1 = new JPanel();
      JPanel panel2 = new JPanel();
      JPanel panel3 = new JPanel();
      Container contenedor = getContentPane();
      
      panel3.add(Titulo);
     
      panel2.setBackground(color);
      panel3.setBackground(color);
      panel1.setBackground(color);

      
      
      ImageIcon cup = new ImageIcon(this.getClass().getResource("btnactualizar.png"));
      
      Image img = cup.getImage() ;  
      Image newimg = img.getScaledInstance( 150, 50 ,  java.awt.Image.SCALE_SMOOTH ) ;  
      cup = new ImageIcon( newimg );

      ManejadorBotones manejador = new ManejadorBotones();
      
      Activar_Desactivar = new JButton(cup);
      Activar_Desactivar.setPreferredSize(new Dimension(150, 50));             
      Activar_Desactivar.addActionListener(manejador); 
      Activar_Desactivar.setBorderPainted(false);    
      Activar_Desactivar.setContentAreaFilled(false);
      
      
      direccionlocal = new JTextField(16);
      direccionlocal.setEditable(false);
      try {
  		addr = InetAddress.getLocalHost();
  		direccionlocal.setText("IP Local: " + addr.getHostAddress().trim());
  		
      } catch (UnknownHostException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	  }
      
      
      dirmac= new MacAddress();
      mac = new JTextField(" MAC: " ,18);
	  mac.setEditable(false);
	  mac.setFont(boldUnderline);
      mac.setBackground(color);
      mac.setForeground(Color.WHITE);
      mac.setBorder(null);
      mac.setEditable(false);
      
      try{
    	  mac.setText(" MAC: " + dirmac.MaC.toUpperCase().trim());
    	  
      }catch(NullPointerException e){
    	  // No se tiene conexión y no se accede a InetAddress.getLocalHost() por lo tanto no obtiene la MAC.
      }
      
      
      
      direccionIP = new JTextField("IP Pública: " ,23);
      direccionIP.setFont(boldUnderline);
      direccionIP.setBackground(color);
      direccionIP.setForeground(Color.WHITE);
      direccionIP.setBorder(null);
      direccionIP.setEditable(false);
      
      try{
    	  GetIP ip = new GetIP();
          direccionIP.setText(direccionIP.getText().toString().concat(ip.IPaddress.toString().trim()));
          
      }catch(NullPointerException e){
    	  
      }
      
      
      Titulo.setFont(FuenteTitulo);
      Titulo.setBackground(color);
      Titulo.setForeground(Color.WHITE);
      Titulo.setBorder(null);
      Titulo.setEditable(false);
      
      
      
      
      direccionlocal.setFont(boldUnderline);
      direccionlocal.setBackground(color);
      direccionlocal.setForeground(Color.WHITE);
      direccionlocal.setBorder(null);
      direccionlocal.setEditable(false);
      
      panel2.add(new JScrollPane(areaPantalla));
      panel2.add(Activar_Desactivar);

      panel1.add( direccionlocal  );
      JTextField Espacio1 = new JTextField(5);
      Espacio1.setBackground(color);
      Espacio1.setBorder(null);
      Espacio1.setEditable(false);
      
      panel1.add(Espacio1);
      try{
    	  panel1.add(direccionIP);
      }catch(NullPointerException e){
    	  
      }
      JTextField Espacio2 = new JTextField(2);
      Espacio2.setBackground(color);
      Espacio2.setBorder(null);
      Espacio2.setEditable(false);
      panel1.add(Espacio2);

      try{
    	  panel1.add(mac);
      }catch(NullPointerException e){
    	  
      }
      
      CrearMenus();      
      
      
      contenedor.add(panel1, BorderLayout.PAGE_END);
      contenedor.add(panel2, BorderLayout.LINE_START );
      contenedor.add(panel3, BorderLayout.BEFORE_FIRST_LINE );

      setVisible( true );
      setResizable(false);
  	  areaPantalla.setEditable(false);
      
  
   } // fin del constructor de Servidor
	
	
	private void CrearMenus(){
		// Creación de menús
		JMenu menuConfigurarPuerto = new JMenu( "Configuración..." );
		menuConfigurarPuerto.setMnemonic( 'C' );

		// establecer elemento de menú Propiedades...
		JMenuItem ConfigurarPuerto = new JMenuItem( "Configurar puerto..." );
		ConfigurarPuerto.setMnemonic( 'P' );
		menuConfigurarPuerto.add( ConfigurarPuerto );
		
		JMenuItem LimpiarPantalla = new JMenuItem( "Limpiar pantalla" );
		LimpiarPantalla.setMnemonic( 'L' );
		menuConfigurarPuerto.add( LimpiarPantalla );
	      
		ConfigurarPuerto.addActionListener( new ActionListener() { 
			public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
				CrearVentanaConfigurarPuerto();
			}
	    	  
		}); // fin de la llamada a addActionListener
		
		
		LimpiarPantalla.addActionListener( new ActionListener() { 
			public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
				areaPantalla.setText("");
			}
	    	  
		}); // fin de la llamada a addActionListener

	      
	      JMenu menuPruebas = new JMenu( "Pruebas" );
	      menuPruebas.setMnemonic( 'P' );

	      // establecer elemento de menú Propiedades...
	      JMenuItem PruebaDiscos = new JMenuItem( "Discos" );
	      PruebaDiscos.setMnemonic( 'P' );
	      menuPruebas.add( PruebaDiscos );
	      
	      PruebaDiscos.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  IndicadorPrueba = true;
	    		  InformacionDiscos();
	  			  ReiniciarServidor();

	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      JMenuItem PruebaRed = new JMenuItem( "Red" );
	      PruebaRed.setMnemonic( 'R' );
	      menuPruebas.add( PruebaRed );
	      
	      PruebaRed.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  IndicadorPrueba = true;
	    		  InformacionRed();
	  			  ReiniciarServidor();
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      JMenuItem PruebaSistemaOperativo = new JMenuItem( "SistemaOperativo" );
	      PruebaSistemaOperativo.setMnemonic( 'S' );
	      menuPruebas.add( PruebaSistemaOperativo );
	      
	      PruebaSistemaOperativo.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  IndicadorPrueba = true;
	    		  InformacionSistemaOperativo();
	  			  ReiniciarServidor();
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      JMenuItem PruebaMemoria = new JMenuItem( "Memoria" );
	      PruebaMemoria.setMnemonic( 'P' );
	      menuPruebas.add( PruebaMemoria );
	      
	      PruebaMemoria.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  IndicadorPrueba = true;
	    		  InformacionMemoria();
	  			  ReiniciarServidor();


	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      JMenuItem PruebaProcesos = new JMenuItem( "Procesos" );
	      PruebaProcesos.setMnemonic( 'P' );
	      menuPruebas.add( PruebaProcesos );
	      
	      PruebaProcesos.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  IndicadorPrueba = true;
	    		  ListadoProcesos();
	  			  ReiniciarServidor();

	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      
	      JMenuItem PruebaOrdenLibre = new JMenuItem( "Orden Libre" );
	      PruebaOrdenLibre.setMnemonic( 'O' );
	      menuPruebas.add( PruebaOrdenLibre );
	      
	      PruebaOrdenLibre.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  IndicadorPrueba = true;
	    		  CrearVentanaOrdenLibre();
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      
	      
	      final JMenu PruebaScripts = new JMenu( "Scripts" );
	      PruebaScripts.setMnemonic( 'S' );
	      menuPruebas.add( PruebaScripts );
	      
	     
	      JMenuItem PruebaVerScripts = new JMenuItem( "Ver Script" );
	      PruebaVerScripts.setMnemonic( 'S' );
	      PruebaScripts.add( PruebaVerScripts );
	      
	      PruebaVerScripts.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  
	    		  IndicadorPrueba = true;
	    		  CrearVentanaScripts(1);
	    		  
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      JMenuItem PruebaEjecutarScripts = new JMenuItem( "Ejecutar Script" );
	      PruebaEjecutarScripts.setMnemonic( 'S' );
	      PruebaScripts.add( PruebaEjecutarScripts );
	      
	      PruebaEjecutarScripts.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  
	    		  IndicadorPrueba = true;
	    		  CrearVentanaScripts(2);
	    		  
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	      
	      
	      JMenu InformacionSistema = new JMenu( "Informacion del Sistema" );
	      InformacionSistema.setMnemonic( 'I' );

			// establecer elemento de menú Propiedades...
			JMenuItem Informacion = new JMenuItem( "Direcciones..." );
			Informacion.setMnemonic( 'f' );
			InformacionSistema.add( Informacion );
		      
			Informacion.addActionListener( new ActionListener() { 
				public void actionPerformed( ActionEvent evento ) {
		    		  // Ventana Propiedades
					CrearVentanaInformacionSistema();
				}
		    	  
			}); // fin de la llamada a addActionListener
	      

	      JMenuBar BarraMenus = new JMenuBar();  
	      setJMenuBar( BarraMenus );  
	      BarraMenus.add( menuConfigurarPuerto );    
	      BarraMenus.add( InformacionSistema );  
	      BarraMenus.add( menuPruebas );  
	      BarraMenus.setBackground(color);
	      BarraMenus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	      

	}

	
private void CrearVentanaConfigurarPuerto(){
		
		final JFrame VentanaPropiedades = new JFrame("Configurar puerto de escucha");
		VentanaPropiedades.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		VentanaPropiedades.setSize(350, 120);
		JPanel PanelPropiedades = new JPanel();
		JPanel PanelPropiedadesTransferencia = new JPanel();
	    JPanel PanelBotonesPropiedades = new JPanel();
	    
	    
	    Container contenedor = VentanaPropiedades.getContentPane();
	    VentanaPropiedades.setLocationRelativeTo(null);

	    // Creamos un componente; aquí hay polimorfismo...
	    final JTextField CampoTextoPuerto = new JTextField(6); // un área de texto
	    CampoTextoPuerto.setText(String.valueOf(Puerto));
	    CampoTextoPuerto.addFocusListener(new java.awt.event.FocusAdapter() {
	        public void focusGained(java.awt.event.FocusEvent evt) {
	        	CampoTextoPuerto.selectAll();
	        }
	    });
	    
	    
	    @SuppressWarnings("serial")
		Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
	        public void actionPerformed(ActionEvent evt) {
	            ((Component) evt.getSource()).transferFocus();
	        }
	    };
	    @SuppressWarnings("serial")
		Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
	        public void actionPerformed(ActionEvent evt) {
	            ((Component) evt.getSource()).transferFocusBackward();
	        }
	    };
	    
	    CampoTextoPuerto.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
	    CampoTextoPuerto.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);

	    
	    
	    
	    final JTextField CampoEtiquetaPuerto = new JTextField("Puerto:                           "); // un área de texto
	    CampoEtiquetaPuerto.setEditable(false);
	    CampoEtiquetaPuerto.setBorder(null);
	    CampoEtiquetaPuerto.setBackground(color);
	    CampoEtiquetaPuerto.setForeground(Color.white);
	    CampoEtiquetaPuerto.setFont(boldUnderline);
	    
	    final JTextField CampoTextoPuertoTransferencia = new JTextField(6);
	    CampoTextoPuertoTransferencia.setText(String.valueOf(PuertoTransferencia));
	    final JTextField CampoEtiquetaPuertoTransferencia = new JTextField("Puerto Transferencia: "); // un área de texto
	    CampoEtiquetaPuertoTransferencia.setEditable(false);
	    CampoEtiquetaPuertoTransferencia.setBorder(null);
	    CampoEtiquetaPuertoTransferencia.setBackground(color);
	    CampoEtiquetaPuertoTransferencia.setForeground(Color.white);
	    CampoEtiquetaPuertoTransferencia.setFont(boldUnderline);
	    // Añadir el componente al panel de la ventana
	    PanelPropiedades.add(CampoEtiquetaPuerto);
	    PanelPropiedades.add(CampoTextoPuerto);
	    PanelPropiedadesTransferencia.add(CampoEtiquetaPuertoTransferencia);
	    PanelPropiedadesTransferencia.add(CampoTextoPuertoTransferencia);
	    
	    // Botones Aceptar y Cancelar
	    JButton btnAceptar = new JButton("Aceptar");
	    JButton btnCancelar = new JButton("Cancelar");
	    PanelBotonesPropiedades.add(btnAceptar);
	    PanelBotonesPropiedades.add(btnCancelar);
	    
	    // Acciones de lo botones
	    btnAceptar.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  Puerto = Integer.parseInt(CampoTextoPuerto.getText().toString());
	    		  PuertoTransferencia = Integer.parseInt(CampoTextoPuertoTransferencia.getText().toString());
	    		  if(Puerto==PuertoTransferencia){
	    			  JOptionPane.showMessageDialog(VentanaPropiedades , "Puerto de escucha y transferencia iguales, por favor, modifique alguno de ellos");
		    		  
	    		  }else{
	    			  VentanaPropiedades.dispose();
	    			  JOptionPane.showMessageDialog(VentanaPropiedades , "El puerto de escucha se ha cambiado a : " + Puerto + " y el de transferencia al: " + PuertoTransferencia );
	    			  ReiniciarServidor();
	    		  }

	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	    
	    btnCancelar.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  VentanaPropiedades.dispose();
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	    
	    VentanaPropiedades.getRootPane().setDefaultButton(btnAceptar);
	    
	    PanelPropiedades.setBackground(color);
	    PanelPropiedadesTransferencia.setBackground(color);
	    PanelBotonesPropiedades.setBackground(color);
	    contenedor.add(PanelPropiedades, BorderLayout.NORTH);
	    contenedor.add(PanelPropiedadesTransferencia, BorderLayout.CENTER);
	    contenedor.add(PanelBotonesPropiedades,BorderLayout.SOUTH);
	    
	    // mostrar la ventana; igual que en AWT
	    VentanaPropiedades.setVisible(true);
	    VentanaPropiedades.setResizable(false);
	    CampoTextoPuerto.requestFocus();
	}
 

private void CrearVentanaInformacionSistema(){
	String DirMac = null;
	Vector<String> DireccionesMAC = new Vector<String>();
	String DirIP = null;
	Vector<String> DireccionesIPs = new Vector<String>();
	final JFrame VentanaPropiedades = new JFrame("Informacion de las direcciones del Sistema (MAC/IP)");
	VentanaPropiedades.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	if(SistemaOperativo.equals("Windows"))
		VentanaPropiedades.setSize(570, 150);
	else
		VentanaPropiedades.setSize(650, 150);

	JPanel PanelPropiedades = new JPanel();
    JPanel PanelBotonesPropiedades = new JPanel();
    JPanel PanelTitulo = new JPanel();

    
    Container contenedor = VentanaPropiedades.getContentPane();
    VentanaPropiedades.setLocationRelativeTo(null);

    try {	
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c " + "ipconfig /all"); 
		   }else{
			   String[] command = {"sh","-c","ifconfig"};
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   is = p.getInputStream();
		   
		   if(SistemaOperativo.equals("Windows"))
			   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		   else
			   br = new BufferedReader (new InputStreamReader (is));
		   
    
		   // Se lee la primera linea
		   aux = br.readLine();
    
		   // Mientras se haya leido alguna linea
		   
		   while (aux!=null) {
			   if(SistemaOperativo.equals("Windows")){
				   if(aux.startsWith("   Dirección física")){
					   String datos[];
					   datos=aux.split(":");
					   DirMac = datos[1];
					   if(DirMac.trim().length()<=17)
						   DireccionesMAC.add(DirMac.trim());
				   }
				   
				   if(aux.startsWith("   Dirección IPv4")){
					   String datos[];
					   datos=aux.split(":");
					   DirIP = datos[1];
					   DireccionesIPs.add(DirIP.trim().substring(0, DirIP.length()-13));
				   }
				   
			   }else{
				   if(aux.contains("direcciónHW")){
					   String datos[];
					   datos=aux.split(" ");
					   DirMac = datos[datos.length-1];
					   if(DirMac.trim().length()<=17)
						   DireccionesMAC.add(DirMac.trim());
				   }
				   if(aux.contains("Direc. inet")){
					   String datos[];
					   datos=aux.split(":");
					   DirIP = datos[1].substring(0,datos[1].length()-6);
					   DireccionesIPs.add(DirIP.trim());
				   }
			   }
				   
			   aux = br.readLine();   
		   }
	}catch ( IOException excepcionES ) {
			excepcionES.printStackTrace();
	} 
    // Creamos un componente; aquí hay polimorfismo...
    
    
    
    
    final JList ListaDirMacs = new JList(DireccionesMAC);
    DefaultListCellRenderer cellRenderer = (DefaultListCellRenderer)ListaDirMacs.getCellRenderer();
    cellRenderer.setHorizontalAlignment(SwingConstants .CENTER);
    ListaDirMacs.setVisibleRowCount(2);
    ListaDirMacs.setPrototypeCellValue("  C0-18-85-6E-3E-76  ");
    ListaDirMacs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListaDirMacs.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent evt) {
        	mac.setText(" MAC: " + (String)ListaDirMacs.getSelectedValue());
        }
      });
    
    final JList ListaDirIP = new JList(DireccionesIPs);
    cellRenderer = (DefaultListCellRenderer)ListaDirIP.getCellRenderer();
    cellRenderer.setHorizontalAlignment(SwingConstants .CENTER);
    ListaDirIP.setVisibleRowCount(2);
    ListaDirIP.setPrototypeCellValue("  255.255.255.255  ");

    ListaDirIP.setAlignmentY(CENTER_ALIGNMENT);
    ListaDirIP.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListaDirIP.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent evt) {
        	direccionlocal.setText("IP Local: " + (String)ListaDirIP.getSelectedValue());
        }
      });
    
    
    
    final JTextField CampoEtiquetaPuerto = new JTextField("Direcciones MAC : "); // un área de texto
    CampoEtiquetaPuerto.setEditable(false);
    CampoEtiquetaPuerto.setBorder(null);
    CampoEtiquetaPuerto.setBackground(color);
    CampoEtiquetaPuerto.setForeground(Color.white);
    CampoEtiquetaPuerto.setFont(boldUnderline);
    
    
    final JTextField CampoEtiquetaDirIps = new JTextField("Direcciones IPs : "); // un área de texto
    CampoEtiquetaDirIps.setEditable(false);
    CampoEtiquetaDirIps.setBorder(null);
    CampoEtiquetaDirIps.setBackground(color);
    CampoEtiquetaDirIps.setForeground(Color.white);
    CampoEtiquetaDirIps.setFont(boldUnderline);
    
    // Añadir el componente al panel de la ventana
    PanelPropiedades.add(CampoEtiquetaPuerto);
    PanelPropiedades.add(new JScrollPane(ListaDirMacs));
    
    // Botones Aceptar y Cancelar
    JButton btnAceptar = new JButton("Aceptar");
    JButton btnCancelar = new JButton("Cancelar");
    PanelBotonesPropiedades.add(btnAceptar);
    PanelBotonesPropiedades.add(btnCancelar);
    
    // Acciones de lo botones
    btnAceptar.addActionListener( new ActionListener() { 
    	  public void actionPerformed( ActionEvent evento ) {
    		  // Ventana Propiedades
    		  VentanaPropiedades.dispose();
    	  }
    	  
      }); // fin de la llamada a addActionListener
    
    btnCancelar.addActionListener( new ActionListener() { 
    	  public void actionPerformed( ActionEvent evento ) {
    		  // Ventana Propiedades
    		  VentanaPropiedades.dispose();
    	  }
    	  
      }); // fin de la llamada a addActionListener
    
    VentanaPropiedades.getRootPane().setDefaultButton(btnAceptar);
    
    JTextField Espacio = new JTextField(2);
    Espacio.setBackground(color);
    Espacio.setBorder(null);
    PanelPropiedades.add(Espacio);
    PanelPropiedades.add(CampoEtiquetaDirIps);
    PanelPropiedades.add(ListaDirIP);
    PanelPropiedades.setBackground(color);
    PanelBotonesPropiedades.setBackground(color);
    JTextField Titulo = new JTextField("Direcciones de red del sistema");
    Titulo.setFont(FuenteTituloSubventana);
    Titulo.setBackground(color);
    Titulo.setForeground(Color.white);
    Titulo.setBorder(null);
    Titulo.setEditable(false);
    
    PanelTitulo.add(Titulo);
    PanelTitulo.setBackground(color);
    
    contenedor.add(PanelTitulo, BorderLayout.NORTH);
    contenedor.add(PanelPropiedades, BorderLayout.CENTER);
    contenedor.add(PanelBotonesPropiedades,BorderLayout.SOUTH);
    
    // mostrar la ventana; igual que en AWT
    VentanaPropiedades.setVisible(true);
    VentanaPropiedades.setResizable(false);
}
	
	
	private void CrearVentanaScripts(final int i){
		final JFrame VentanaPropiedades = new JFrame("Scripts");
		VentanaPropiedades.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    VentanaPropiedades.setSize(350, 200);
	    
	    JPanel PanelPropiedades = new JPanel();
	    JPanel PanelBotonesPropiedades = new JPanel();
	    
	    Container contenedor = VentanaPropiedades.getContentPane();
	    VentanaPropiedades.setLocationRelativeTo(null);

	    // Lista de Scripts...
	    Vector<String> Scripts = new Vector<String>();
	    try {	
			   if(SistemaOperativo.equals("Windows")){
				   p = Runtime.getRuntime().exec ("cmd /c " + "dir /B " + DireccionScripts); 
			   }else{
				   String[] command = {"sh","-c","ls " + DireccionScripts};
				   p = Runtime.getRuntime().exec (command);
			   }
			   
			   is = p.getInputStream();
			   
			   if(SistemaOperativo.equals("Windows"))
				   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
			   else
				   br = new BufferedReader (new InputStreamReader (is));
			   
	       
			   // Se lee la primera linea
			   aux = br.readLine();
	       
			   // Mientras se haya leido alguna linea
			   
			   while (aux!=null) {
				   
				   Scripts.add(aux);
				   aux = br.readLine();   
	    	   }
		}catch ( IOException excepcionES ) {
	   			excepcionES.printStackTrace();
	   	} 
	    
    	JTextField EtiquetaScript = null;

	    switch(i){
	    case 1:
	    	 EtiquetaScript = new JTextField("Elija el Script que desea ver"); // un área de texto
	    	 break;
	    case 2:
	    	 EtiquetaScript = new JTextField("Elija el Script que desea ejecutar"); // un área de texto
	    	 break;
	    }
	    	
	    EtiquetaScript.setEditable(false);
	    EtiquetaScript.setBorder(null);
	    EtiquetaScript.setBackground(color);
	    EtiquetaScript.setForeground(Color.white);
	    EtiquetaScript.setFont(FuenteTituloSubventana);
	    
	    final JList ListaScripts = new JList(Scripts);
	    ListaScripts.setVisibleRowCount(4);
	    ListaScripts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListaScripts.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent evt) {
	        	nombreScript =  (String)ListaScripts.getSelectedValue();
	        }
	      });
	    
	    
	    
	    PanelPropiedades.add(EtiquetaScript);
	    PanelPropiedades.add(new JScrollPane(ListaScripts));
	    
	    // Botones Aceptar y Cancelar
	    JButton btnAceptar = new JButton("Aceptar");
	    JButton btnCancelar = new JButton("Cancelar");
	    PanelBotonesPropiedades.add(btnAceptar);
	    PanelBotonesPropiedades.add(btnCancelar);
	    
	    // Acciones de lo botones
	    btnAceptar.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
  		    	VentanaPropiedades.dispose();

  		    	switch(i){
	    		    case 1:
	  	    		  	VerScript(nombreScript);	 
	  	    		  	break;
	  	    		case 2:
	  	    		  	EjecutarScript(nombreScript);
	  	    		  	break;
	    		  }
	    		  
  		    	ReiniciarServidor();
	  			
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	    
	    btnCancelar.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  VentanaPropiedades.dispose();
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	    
	    
	    VentanaPropiedades.getRootPane().setDefaultButton(btnAceptar);
	   
	    contenedor.add(PanelPropiedades, BorderLayout.CENTER);
	    contenedor.add(PanelBotonesPropiedades,BorderLayout.SOUTH);
	    
	    PanelPropiedades.setBackground(color);
	    PanelBotonesPropiedades.setBackground(color);
	    
	    // mostrar la ventana; igual que en AWT
	    VentanaPropiedades.setVisible(true);
	    VentanaPropiedades.setResizable(false);
	}
	


	
	private void CrearVentanaOrdenLibre(){
		final JFrame VentanaPropiedades = new JFrame("Orden Libre");
		VentanaPropiedades.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    VentanaPropiedades.setSize(350, 100);
	    JPanel PanelPropiedades = new JPanel();
	    JPanel PanelBotonesPropiedades = new JPanel();
	    
	    Container contenedor = VentanaPropiedades.getContentPane();
	    VentanaPropiedades.setLocationRelativeTo(null);

	    
	    final JTextField Orden = new JTextField("Introduzca aquí la orden a ejecutar ",30); // un área de texto
	    Orden.addFocusListener(new java.awt.event.FocusAdapter() {
	        public void focusGained(java.awt.event.FocusEvent evt) {
	            Orden.selectAll();
	        }
	    });

	    
	    PanelPropiedades.add(Orden);
	    
	    // Botones Aceptar y Cancelar
	    JButton btnAceptar = new JButton("Aceptar");
	    JButton btnCancelar = new JButton("Cancelar");
	    PanelBotonesPropiedades.add(btnAceptar);
	    PanelBotonesPropiedades.add(btnCancelar);
	    
	    VentanaPropiedades.getRootPane().setDefaultButton(btnAceptar);
	    
	    
	    // Acciones de lo botones
	    btnAceptar.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  VentanaPropiedades.dispose();
	    		  EjecutarOrdenLibre(Orden.getText().toString());
	  		      ReiniciarServidor();

	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	    
	    btnCancelar.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent evento ) {
	    		  // Ventana Propiedades
	    		  VentanaPropiedades.dispose();
	    	  }
	    	  
	      }); // fin de la llamada a addActionListener
	    
	    PanelPropiedades.setBackground(color);
	    PanelBotonesPropiedades.setBackground(color);
	    
	   
	    contenedor.add(PanelPropiedades, BorderLayout.CENTER);
	    contenedor.add(PanelBotonesPropiedades,BorderLayout.SOUTH);
	    
	    // mostrar la ventana; igual que en AWT
	    VentanaPropiedades.setVisible(true);
	    VentanaPropiedades.setResizable(false);
	}

    
	private class ManejadorBotones implements ActionListener{

		public void actionPerformed(ActionEvent evento) {
			// TODO Auto-generated method stub
			try{
		    	  GetIP ip = new GetIP();
		          direccionIP.setText("IP Pública: " + ip.IPaddress.toString().trim());
		          dirmac= new MacAddress();
		          mac.setText(" MAC: " + dirmac.MaC.toUpperCase().trim());
		          addr = InetAddress.getLocalHost();
		          direccionlocal.setText("IP Local: " + addr.getHostAddress().trim());
		          salida.close();
		          servidor.close();
		          conexion.close();
		    }catch(NullPointerException | IOException e){
		    	  
		    }
			
			ReiniciarServidor();
			
			
		}
	}
	
	private void ReiniciarServidor(){
		if(!servidor.isClosed()){
			try {
				servidor.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		start();
	}
	
	
	public void start() {
		HiloEjecutarServidor = new HiloEjecutarServidor();
		HiloEjecutarServidor.start();
    }
	
	
	private class HiloEjecutarServidor extends Thread {
	    
		public void run() {
			EjecutarServidor(Puerto);
		}
		
	}
	
	public void EjecutarServidor(int Puerto){
	  	try{
	  		servidor= new ServerSocket(Puerto);
	  		areaPantalla.append( "\n\n   Esperando una conexión en el puerto " + String.valueOf(Puerto) + ".....\n\n");
	  		
	  		while(true){
	  			try {
	  				// Aceptamos la conexión
	  				conexion = servidor.accept();
	  				
	  				// Informamos de la Conexión recibida desde el terminal
	  				areaPantalla.append( "\n   Conexión " + " recibida de: " + conexion.getInetAddress().getHostName() + "\n");
		 	            
	  				EstablecerFlujos();
		 	            
	  				mensaje = (String) entrada.readObject();
	  				while(true){
	  					ProcesarEntrada(mensaje);
	  					mensaje = (String) entrada.readObject();
	  				}
	  			}  catch ( EOFException excepcionEOF ) {
	  				
	  				areaPantalla.append("\n\n   Se ha terminado la conexión... Reiniciando el servidor...\n");
	  				
	  			} catch (ClassNotFoundException e) {
	  				areaPantalla.append("\n   ClassNotFoundException (EjecutarServidor)");

	  			} finally{
	  				//salida.close();
	  				//servidor.close();
	  				ReiniciarServidor();
	  			}
	  		} 
	  	}catch ( IOException excepcionES ) {
	  			//excepcionES.printStackTrace();	
	  			
	  	}
	}

   private void CrearCarpetaScripts(){
	   DireccionScripts = System.getProperty("user.dir") + java.io.File.separator + "Scripts";
	   File CarpetaScritps = new File(DireccionScripts);
  
	   if(!CarpetaScritps.exists())
		   CarpetaScritps.mkdir();
   }
   
   
   
   
   private void EstablecerFlujos(){
	   // establecer flujo de salida para los objetos
       try {
    	   salida = new ObjectOutputStream( conexion.getOutputStream() );
    	   salida.flush(); // vaciar  búffer de salida para enviar informacion de encabezado
    	   
    	   entrada = new ObjectInputStream( conexion.getInputStream() );
       } catch (IOException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
       }
   }
  
   
   private void ProcesarEntrada(String orden) throws IOException{
	   if(orden.matches("MainMenu"))
		   Enviar(SistemaOperativo);
	   
	   if(orden.matches("TerminarConexion")){
		   areaPantalla.append("\n\n   Se ha terminado la conexión... Reiniciando el servidor...\n");
		   salida.close();
		   servidor.close();
	   }
	   
	   if(orden.matches("Listado")){
		   ListadoProcesos();		   
	   } 
       	
	   if(orden.startsWith("taskkill")){
		   ProcesarTaskKillWindows(orden);
	   }
	   
	   if(orden.matches("Navegacion")){
		   ProcesarNavegacion();
	   }
	   
	   if(orden.matches("InfDiscos")){
		   InformacionDiscos();
	   }
	   
	   if(orden.matches("Red")){
		   InformacionRed();
	   }
	   
	   if(orden.matches("Memoria")){
		   InformacionMemoria();
	   }
	   
	   if(orden.matches("SistemaOperativo")){
		   InformacionSistemaOperativo();
	   }
	   
	   if(orden.matches("OrdenLibre")){
		   OrdenLibre();
	   }
	   
	   if(orden.matches("Reiniciar")){
		   Reiniciar();
	   }
	   
	   if(orden.matches("Apagar")){
		   Apagar();
	   }
	   
	   if(orden.matches("Scripts")){
		   Scripts();
	   }
	   
	   if(orden.startsWith("EjecutarScript")){
		   String fichero = mensaje.replace("EjecutarScript ", "");
		   EjecutarScript(fichero);
	   }
	   
	   if(orden.startsWith("EliminarScript")){
		   String fichero = mensaje.replace("EliminarScript ", "");
		   EliminarScript(fichero);
	   }
	   
	   if(orden.equals("TransferirScript")){
		   try {
			   mensaje = (String) entrada.readObject();
			   String nombrefichero = mensaje;
			   mensaje = (String) entrada.readObject();
			   String texto = mensaje;
			   
			   TransferirScript(nombrefichero,texto);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	   
	   if(orden.equals("VerScript")){
		   try {
			mensaje = (String) entrada.readObject();
			String nombrefichero = mensaje;
			   
			VerScript(nombrefichero);
		   } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   }
	   }
   }
   
   
   private void EjecutarScript(String fichero){
	   try{
		   areaPantalla.append("\n\t· EjecutandoScript: "  + fichero +"\n");
		   if(SistemaOperativo.equals("Windows")){
			   if(fichero.endsWith("py")){
				   p = Runtime.getRuntime().exec ("cmd /c " + "cd \"" + DireccionScripts + "\" && python \"" +  fichero + "\"" );
			   }
			   if(fichero.endsWith("bat")){
				   areaPantalla.append("\n\t ·Se ejecuta: cd \"" + DireccionScripts + "\" &&  \"" + fichero + "\"");
				   p = Runtime.getRuntime().exec ("cmd /c " + "cd \"" + DireccionScripts + "\" &&  \"" + fichero + "\"");
				   
			   }
			   else{
				   p = Runtime.getRuntime().exec ("cmd /c \"" + DireccionScripts + "\\" + fichero + "\"");
			   }
		   }
		   else{
			   if(fichero.endsWith("bin") || fichero.endsWith("run") || fichero.endsWith("sh") ){
				   String[] command = {"sh","-c","cd \"" + DireccionScripts + "\" && chmod 777 " + "\"" + fichero + "\" && ./\"" + fichero + "\"" };
				   p = Runtime.getRuntime().exec (command);
			   }
			   if(fichero.endsWith("py")){
				   String[] command = {"sh","-c","cd \"" + DireccionScripts + "\" && chmod 777 " + "\"" + fichero + "\" && python \"" + fichero + "\"" };
				   p = Runtime.getRuntime().exec (command);
			   }
		   }
	   }catch ( IOException excepcionES ) {
  			excepcionES.printStackTrace();
	   } 

	   IndicadorPrueba=false;
   }
   
   private void EliminarScript(String fichero){
	   try{
		   areaPantalla.append("\n\t· EliminandoScript: " + fichero + "\n");
   		
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c " +"del  \"" + DireccionScripts + "\\" + fichero + "\"");
		   else{
			   String[] command = {"sh","-c","rm -f " + "\"" + DireccionScripts + "/" + fichero + "\"" };
			   p = Runtime.getRuntime().exec (command);
		   }
			   
		   if(!IndicadorPrueba)
			   Enviar("FinEliminarScript");
	   }catch ( IOException excepcionES ) {
  			excepcionES.printStackTrace();
	   } 

		IndicadorPrueba=false;
   }
   
   private void VerScript(String fichero){
	   File archivo = null;
	   FileReader fr = null;
	   BufferedReader br = null;
	   if(IndicadorPrueba){
		   areaPantalla.append("\n\n");
		   areaPantalla.append("/*************************************************************************************************************************************************/\n");
 		   areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:  VerScripts                                                                          */\n");
 		   areaPantalla.append("/*************************************************************************************************************************************************/\n");
 		  
	   }
	   
	   areaPantalla.append("\n\t· VerScript: " + fichero +"\n");
	   
	   try{
		         if(SistemaOperativo.equals("Windows"))
		        	 archivo = new File (DireccionScripts+"\\"+fichero);
		         else
		        	 archivo = new File (DireccionScripts+"/"+fichero);
		        	 
		         fr = new FileReader (archivo);
		         br = new BufferedReader(fr);

		         // Lectura del fichero
		         String linea;
		         while((linea=br.readLine())!=null){
		        	 if(!IndicadorPrueba)
		        		 Enviar(linea); 
		        	 areaPantalla.append("\t   " + linea + "\n");
		         }
		         if(!IndicadorPrueba)
		        	 Enviar("FinLecturaScript");
		      
	   }catch ( IOException excepcionES ) {
  			excepcionES.printStackTrace();
	   }finally{
       // En el finally cerramos el fichero, para asegurarnos
       // que se cierra tanto si todo va bien como si salta 
       // una excepcion.
       try{                    
          if( null != fr ){   
             fr.close();     
          }                  
       }catch (Exception e2){ 
          e2.printStackTrace();
       }
    }
	if(IndicadorPrueba){
		areaPantalla.append("\n");
		areaPantalla.append("/*************************************************************************************************************************************************/\n");
 	}
	IndicadorPrueba=false;
   }
   
   private void TransferirScript(String nombrefichero,String texto){
	   areaPantalla.append("\n\t· TransferirScript: " + nombrefichero + "\n   Contenido: \n");
	   
	   FileWriter fichero = null;
       PrintWriter pw = null;
       
       try {
    	   if(SistemaOperativo.equals("Windows"))
    		   fichero = new FileWriter(DireccionScripts+"\\"+nombrefichero);
    	   else
    		   fichero = new FileWriter(DireccionScripts+"/"+nombrefichero);
    	   
           pw = new PrintWriter(fichero);

           String data[];
           data=texto.split("\\n");
           for(int i=0;i<data.length;i++){
        	   areaPantalla.append("\n\t" + data[i]);
        	   pw.println(data[i]);
           }
           

        } catch (Exception e) {
           e.printStackTrace();
        } finally {
	       try {
	    	   // Nuevamente aprovechamos el finally para 
	           // asegurarnos que se cierra el fichero.
	           if (null != fichero)
	              fichero.close();
	           } catch (Exception e2) {
	              e2.printStackTrace();
	           }
        }

   }
   
   private void ListadoScripts(){
	   try {
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c " + "dir /B \"" + DireccionScripts + "\""); 
		   }else{
			   String[] command = {"sh","-c","ls \"" + DireccionScripts + "\""};
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   is = p.getInputStream();
		   
		   if(SistemaOperativo.equals("Windows"))
			   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		   else
			   br = new BufferedReader (new InputStreamReader (is));
		   
       
		   // Se lee la primera linea
		   aux = br.readLine();
       
		   // Mientras se haya leido alguna linea
		   
		   while (aux!=null) {
			   if(!IndicadorPrueba && (aux.endsWith(".py") || aux.endsWith(".sh")  || aux.endsWith(".bat")  || aux.endsWith(".run")  || aux.endsWith(".bin"))){
				   Enviar(aux);
				   areaPantalla.append("\n\t   " + aux  );
			   }
				   
			   aux = br.readLine();   
    	   }
		}catch ( IOException excepcionES ) {
   			excepcionES.printStackTrace();
   		} 
	  
	   if(!IndicadorPrueba)
		   Enviar("Fin_Scripts");
	   
	   IndicadorPrueba=false;
	   areaPantalla.append("\n\n");

   }
	   
   
   private void Scripts(){
	   areaPantalla.append("\n\t   DireccionScripts: " + DireccionScripts);
	   areaPantalla.append("\n\n\t· Obteniendo el listado de los Scripts");
	   areaPantalla.append("\n                      ------------------------------------------------ ");
	   
	   ListadoScripts();
	   
	   
   }
	   
   
   
   private void Reiniciar(){
	   areaPantalla.append("\nReiniciando...\n");
	   try {
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c shutdown -r");
		   else{
			   String[] command = {"sh","-c","reboot" };
			   p = Runtime.getRuntime().exec (command);
		   }
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   } 
  }
   
   
   private void Apagar(){
	   areaPantalla.append("\nApagando...\n");
	   try {
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c shutdown -s");
		   else{
			   String[] command = {"sh","-c","halt" };
			   p = Runtime.getRuntime().exec (command);
		   }
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }   
   }
   
   private void EjecutarOrdenLibre(String mensaje){
	   if(IndicadorPrueba){
		   areaPantalla.append("/*************************************************************************************************************************************************/\n");
		   areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:   Orden Libre                                                                        */\n");
		   areaPantalla.append("/*************************************************************************************************************************************************/\n");
	   }
	   
	   areaPantalla.append("\n   ---> Ejecutando orden libre: " + mensaje);
	   
	   try {
		   
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c " + mensaje);
		   else{
			   String[] command = {"sh","-c",mensaje };
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   is = p.getInputStream();
		   
		   if(SistemaOperativo.equals("Windows"))
			   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		   else
			   br = new BufferedReader (new InputStreamReader (is));
       
		   // Se lee la primera linea
		   aux = br.readLine();
       
		   // Mientras se haya leido alguna linea
		   while (aux!=null) {
			   if(!IndicadorPrueba)
				   Enviar(aux);
			   else
				   areaPantalla.append("\n" + aux);
			   aux = br.readLine();   
		   }
   		}catch ( IOException excepcionES ) {
   			excepcionES.printStackTrace();
   		}
	   if(IndicadorPrueba)
		   areaPantalla.append("\n\n/*************************************************************************************************************************************************/\n");
	   
	   
	   IndicadorPrueba=false;
   }
   
   private void OrdenLibre(){
	   try {
		   mensaje = (String) entrada.readObject();
		   
		   EjecutarOrdenLibre(mensaje);
   		}catch ( IOException excepcionES ) {
   			excepcionES.printStackTrace();
   		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	Enviar("Fin_OrdenLibre");
   }
   
   private void InformacionSistemaOperativo(){
	   if(IndicadorPrueba){
		   areaPantalla.append("\n\n/*************************************************************************************************************************************************/\n");
		   areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:  Informacion de Sistema Operativo                                        */\n");
		   areaPantalla.append("/*************************************************************************************************************************************************/\n");
  
	   }else{
	 		  areaPantalla.append("\n   ---> Obteniendo información del Sistema Operativo...\n\n");

	   }
	   
	   Vector<Propiedad> Propiedades =  new Vector<Propiedad>();
	   Propiedades.add(new Propiedad("Version Java" , "java.version")); 	
	   Propiedades.add(new Propiedad("Fabricante" ,"java.vendor")); 	
	   Propiedades.add(new Propiedad("URL Fabricante" ,"java.vendor.url")); 
	   Propiedades.add(new Propiedad("Directorio de instalación" ,"java.home"));
	   Propiedades.add(new Propiedad("JVM versión" ,"java.vm.specification.version"));
	   Propiedades.add(new Propiedad("Fabricante JVM" ,"java.vm.specification.vendor"));
	   Propiedades.add(new Propiedad("Nombre JVM" ,"java.vm.specification.name"));
	   Propiedades.add(new Propiedad("Version implementación JVM" ,"java.vm.version"));
	   Propiedades.add(new Propiedad("Fabricante implementación JVM" ,"java.vm.vendor"));
	   Propiedades.add(new Propiedad("Version implementación Java" ,"java.vm.name"));
	   Propiedades.add(new Propiedad("Version JRE" ,"java.specification.version"));
	   Propiedades.add(new Propiedad("Fabricante JRE" ,"java.specification.vendor"));
	   Propiedades.add(new Propiedad("Nombre JRE" ,"java.specification.name"));
	   Propiedades.add(new Propiedad("Numero de la versión de la clase Java" ,"java.class.version"));
	   Propiedades.add(new Propiedad("Java Path" ,"java.class.path"));
	   Propiedades.add(new Propiedad("Path Librerías Java" ,"java.library.path"));
       Propiedades.add(new Propiedad("Directorio temporal por defecto" ,"java.io.tmpdir"));
	   Propiedades.add(new Propiedad("Path del directorio de extensiones" ,"java.ext.dirs"));
	   Propiedades.add(new Propiedad("Nombre del Sistema Operativo" ,"os.name"));
	   Propiedades.add(new Propiedad("Arquitectura del Sistema Operativo" ,"os.arch"));
	   Propiedades.add(new Propiedad("Version del Sistema Operativo" ,"os.version"));
       Propiedades.add(new Propiedad("Nombre de la cuenta de usuario" ,"user.name"));
	   Propiedades.add(new Propiedad("Directorio de casa del usuario" ,"user.home"));
	   Propiedades.add(new Propiedad("Directorio actual de trabajo" ,"user.dir"));
																	   
	   if(!IndicadorPrueba){
		   for(int i=0;i<Propiedades.size();i++){
			   Enviar(Propiedades.get(i).Nombre() + ": " + System.getProperty(Propiedades.get(i).Orden()) );
			   areaPantalla.append("\t" + Propiedades.get(i).Nombre() + ": " + System.getProperty(Propiedades.get(i).Orden()) + "\n");
		   }
		   Enviar("Fin_SistemaOperativo");
	   }else{
		   for(int i=0;i<Propiedades.size();i++)
			   areaPantalla.append("\n" + Propiedades.get(i).Nombre() + ": " + System.getProperty(Propiedades.get(i).Orden()));
		   areaPantalla.append("\n");
	   }
	   
	   if(IndicadorPrueba)
		   areaPantalla.append("\n\n/*************************************************************************************************************************************************/\n");
	   
	   IndicadorPrueba=false;
	   
   }
   
   private void InformacionMemoria(){
	   String memftotal=null,memfdis=null,memvirtammax=null,memvirdis=null,memvirus=null;
	   if(IndicadorPrueba){
			  areaPantalla.append("\n\n/***************************************************************************************************************************************************/\n");
	 		  areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:  Informacion de Memoria                                                         */\n");
	 		  areaPantalla.append("/***************************************************************************************************************************************************/\n");
	 		  
	   }else{
	 		  areaPantalla.append("\n   ---> Obteniendo información de la memoria...\n");

	   }

	   try {
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c systeminfo");
		   else{
			   String[] command = {"sh","-c","cat /proc/meminfo | awk '{print $1,$2}'" };
			   p = Runtime.getRuntime().exec (command);
		   }
		   is = p.getInputStream();
		  
		   if(SistemaOperativo.equals("Windows"))
			   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		   else
			   br = new BufferedReader (new InputStreamReader (is));
		   
       
		   // Se lee la primera linea
		   aux = br.readLine();
       
		   // Mientras se haya leido alguna linea
		   while (aux!=null) {
			   if(SistemaOperativo.equals("Windows")){
				   if(aux.startsWith("Cantidad total de memoria física")){
					   String datos[];
					   datos=aux.split(":");
					   memftotal=datos[1];
				   }
				   
				   if(aux.startsWith("Memoria física disponible")){
	    				String datos[];
	    				datos=aux.split(":");
	    				memfdis=datos[1];
	    		   }
				   
				   if(aux.startsWith("Memoria virtual: tamaño")){
	    				String datos[];
	    				datos=aux.split(":");
	    				memvirtammax=datos[2];
				   }
				   if(aux.startsWith("Memoria virtual: disponible")){
	    				String datos[];
	    				datos=aux.split(":");
	    				memvirdis=datos[2];
				   }
				   if(aux.startsWith("Memoria virtual: en uso")){
	    				String datos[];
	    				datos=aux.split(":");
	    				memvirus=datos[2];
				   }
			   }else{
				   //Linux
				   if(aux.startsWith("MemTotal:")){
					   String datos[];
					   datos=aux.split(" ");
					   memftotal=datos[1] + " Kb";					   
				   }
				   if(aux.startsWith("MemFree:")){
					   String datos[];
					   datos=aux.split(" ");
					   memfdis=datos[1]+ " Kb";					   
				   }
				   if(aux.startsWith("VmallocTotal:")){
					   String datos[];
					   datos=aux.split(" ");
					   memvirtammax=datos[1]+ " Kb";					   
				   }
				   if(aux.startsWith("VmallocUsed:")){
					   String datos[];
					   datos=aux.split(" ");
					   memvirus=datos[1]+ " Kb";					   
				   }
				   if(aux.startsWith("VmallocChunk:")){
					   String datos[];
					   datos=aux.split(" ");
					   memvirdis=datos[1]+ " Kb";					   
				   }
					   
			   }     
			   	
			   aux = br.readLine();   
    			
	       }
	       
		   if(!IndicadorPrueba){
			   
			   Enviar(memftotal);
			   Enviar(memfdis);
			   Enviar(memvirtammax);
			   Enviar(memvirdis);
			   Enviar(memvirus);
		   }
		   
		   areaPantalla.append("\n\tMemoria Física Total: " + memftotal + "\n\tMemoria Física Disponible: " +  memfdis + "\n\tMemoria Virtual (Tamaño máximo): " +  memvirtammax  +
					   "\n\tMemoria Virtual Disponible: " + memvirdis + "\n\tMemoria Virtual Usada: " +  memvirus + "\n\n");
		   
	   
	   
	   }catch ( IOException excepcionES ) {
   			excepcionES.printStackTrace();
   		}
	   if(!IndicadorPrueba)
		   Enviar("Fin_Memoria");
	   else
		   areaPantalla.append("\n\n/***************************************************************************************************************************************************/\n");

	   IndicadorPrueba=false;
   }
   
   private void InformacionRed(){
	   if(IndicadorPrueba){
			  areaPantalla.append("\n\n/***************************************************************************************************************************************************/\n");
	 		  areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:  Informacion de Red                                                                */\n");
	 		  areaPantalla.append("/***************************************************************************************************************************************************/\n");
	 		  
	   }else{
	 		  areaPantalla.append("\n   ---> Obteniendo información de la red...\n");

	   }
	   
	   String res=null;
	   //String Adaptador = null,dirfis = null,dirip = null,descripcion = null,mask=null,estado=null,dhcp=null;
	   try {
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c ipconfig /all"); 
			   is = p.getInputStream();
			   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
	       
			   // Se lee la primera linea
			   aux = br.readLine();
	       
			   // Mientras se haya leido alguna linea
			   while (aux!=null) {
	    		   try{
	    			   while(aux != null && (!aux.startsWith("Adaptador de LAN") & !aux.startsWith("Adaptador de Ethernet"))){
	    				   aux=br.readLine();
	    			   }
	    			   
	    			   if(res!=null)
	    				   res=res+"\n";
	    			   
	    			   if(aux!=null){
	    				   areaPantalla.append("\n\t" + aux + "\n");
	    				   //Adaptador = aux;
	    				   if(res!=null)
	    					   res= res + aux + "\n";
	    				   else
	    					   res= aux +"\n";
	    				   aux=br.readLine();
	    			   }
	    			   
	    			   while(aux != null &&  (!aux.startsWith("Adaptador de LAN") & !aux.startsWith("Adaptador de Ethernet"))){
	    				   if(aux.startsWith("   Dirección física")){
	    					   areaPantalla.append("\t" + aux + "\n");
	    					   res= res + aux + "\n";
	    					   //dirfis = aux;
	    				   }
	    			   
		    			   if(aux.startsWith("   Dirección IPv4")){
		    				   areaPantalla.append("\t" + aux + "\n");
		    				   res= res + aux + "\n";
		    				   //dirip = aux;
		    			   }
		    			   
		    			   if(aux.startsWith("   Descripción")){
		    				   areaPantalla.append("\t" + aux + "\n");
		    				   res= res + aux + "\n";
		    				   //descripcion = aux;
		    			   }
		    			   
		    			   if(aux.startsWith("   Máscara")){
		    				   areaPantalla.append("\t" + aux + "\n");
		    				   res= res + aux + "\n";
		    				   //mask = aux;
		    			   }
		    			   
		    			   if(aux.startsWith("   Estado")){
		    				   areaPantalla.append("\t" + aux + "\n");
		    				   res= res + aux + "\n";
		    				   //estado  =aux;
		    			   }
		    			   
		    			   if(aux.startsWith("   DHCP")){
		    				   areaPantalla.append("\t" + aux + "\n");
		    				   res= res + aux + "\n";
		    				   //dhcp = aux;
		    			   }
		    			   
		    			   if(aux.startsWith("Adaptador de túnel"))
		    				   break;
	    				   
		    			   
		    			   
		    			   aux=br.readLine();
		    			   
	    			   }
	    			   
	    			   
	        		   
	    			   
	    		   }catch(NullPointerException e){
	    			   e.printStackTrace();
	    		   }
		       }
			   
			   
			   Vector<Integer> indices = new Vector<Integer>();
			   String[] datos;
			   datos = res.split("\n");
			   
			   for(int i=0;i<datos.length;i++){
				   if(datos[i].trim().length()==0)
					   indices.add(i);
				   //areaPantalla.append("\nDatos["+i + "]= " + datos[i]);
					   
			   }
			   
			   Vector<Red> AdaptadoresRed = new Vector<Red>();
			   
			   String Adaptador = null,dirfis = null,dirip = null,descripcion = null,mask=null,estado=null,dhcp=null;
			   for(int i=0;i<indices.get(0);i++){
				   
				   if(datos[i].contains("Adaptador"))
					   Adaptador=datos[i];
				   if(datos[i].contains("Dirección física"))
					   dirfis=datos[i];
				   if(datos[i].contains("Dirección IPv4"))
					   dirip=datos[i];
				   if(datos[i].contains("Descripción"))
					   descripcion=datos[i];
				   if(datos[i].contains("Máscara"))
					   mask=datos[i];
				   if(datos[i].contains("Estado"))
					   estado=datos[i];
				   if(datos[i].contains("DHCP"))
					   dhcp=datos[i];
				   
					   
			   }
			   
			   AdaptadoresRed.add(new Red(Adaptador,dirfis,dirip,descripcion,mask,estado,dhcp));
			   
			   Adaptador = dirfis = dirip = descripcion = mask= estado=dhcp=null;
			   
			   if(indices.size()>1){

				   	   
			   for(int i =0 ; i<indices.size()-1;i++){
				   for(int j=indices.get(i);j<indices.get(i+1);j++){
					   if(datos[j].contains("Adaptador"))
						   Adaptador=datos[j];
					   if(datos[j].contains("Dirección física"))
						   dirfis=datos[j];
					   if(datos[j].contains("Dirección IPv4"))
						   dirip=datos[j];
					   if(datos[j].contains("Descripción"))
						   descripcion=datos[j];
					   if(datos[j].contains("Máscara"))
						   mask=datos[j];
					   if(datos[j].contains("Estado"))
						   estado=datos[j];
					   if(datos[j].contains("DHCP"))
						   dhcp=datos[j];
				   }
				   AdaptadoresRed.add(new Red(Adaptador,dirfis,dirip,descripcion,mask,estado,dhcp));
				   
			   	}
			   }
			   
			   
			   Adaptador = dirfis = dirip = descripcion = mask= estado=dhcp=null;

			   for(int i=indices.get(indices.size()-1);i<datos.length;i++){
				   if(datos[i].contains("Adaptador"))
					   Adaptador=datos[i];
				   if(datos[i].contains("Dirección física"))
					   dirfis=datos[i];
				   if(datos[i].contains("Dirección IPv4"))
					   dirip=datos[i];
				   if(datos[i].contains("Descripción"))
					   descripcion=datos[i];
				   if(datos[i].contains("Máscara"))
					   mask=datos[i];
				   if(datos[i].contains("Estado"))
					   estado=datos[i];
				   if(datos[i].contains("DHCP"))
					   dhcp=datos[i];

			   }
			   AdaptadoresRed.add(new Red(Adaptador,dirfis,dirip,descripcion,mask,estado,dhcp));

			   for(int i=0;i<AdaptadoresRed.size();i++){
				   /*areaPantalla.append("\nAdaptador("+i+")\n");
				   areaPantalla.append("\n " +AdaptadoresRed.get(i).Adaptador() +
						   "\n " +AdaptadoresRed.get(i).DirFis() +
						   "\n " +AdaptadoresRed.get(i).DirIP() +
						   "\n " +AdaptadoresRed.get(i).Descripcion() +
						   "\n " +AdaptadoresRed.get(i).Mascara() +
						   "\n " +AdaptadoresRed.get(i).Estado() +
						   "\n " +AdaptadoresRed.get(i).DHCP());*/
				   // Enviar datos
    			   if(!IndicadorPrueba){
    				   String EnviarDatos[];
	    			   if(AdaptadoresRed.get(i).Adaptador()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).Adaptador().split(":");
	    				   Enviar(EnviarDatos[0]);
	    			   }else{
	    				   Enviar("");
	    			   }
	    			   
	    			   if(AdaptadoresRed.get(i).DirFis()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).DirFis().split(":");
	    				   Enviar(EnviarDatos[1]);
	    			   }else{
	    				   Enviar("");
	    			   }
	    			   
	    			   if(AdaptadoresRed.get(i).DirIP()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).DirIP().split(":");
	    				   Enviar(EnviarDatos[1]);
	    			   }else{
	    				   Enviar("");
	    			   }
	    			   
	    			   if(AdaptadoresRed.get(i).Descripcion()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).Descripcion().split(":");
	    				   Enviar(EnviarDatos[1]);
	    			   }else{
	    				   Enviar("");
	    			   }
	    			   
	    			   if(AdaptadoresRed.get(i).Mascara()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).Mascara().split(":");
	    				   Enviar(EnviarDatos[1]);
	    			   }else{
	    				   Enviar("");
	    			   }
	    			   
	    			   if(AdaptadoresRed.get(i).Estado()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).Estado().split(":");
	    				   Enviar(EnviarDatos[1]);
	    			   }else{
	    				   Enviar("");
	    			   }
	    			   
	    			   if(AdaptadoresRed.get(i).DHCP()!=null){
	    				   EnviarDatos = AdaptadoresRed.get(i).DHCP().split(":");
	    				   Enviar(EnviarDatos[1]);
	    			   }else{
	    				   Enviar("");
	    			   }
    			   }
				   
				}
				   
			   
			  
	
		   }else{
			   // Red en Linux
			   String Adaptador = null,dirfis = null,dirip = null,descripcion = null,mask=null,estado=null,dhcp=null;

			   String[] command = {"sh","-c","ifconfig | grep \"Link encap\" | awk '{print $1}'"};
			   p = Runtime.getRuntime().exec (command);
			   is = p.getInputStream();
			   br = new BufferedReader (new InputStreamReader (is));
	       
			   // Se lee la primera linea
			   aux = br.readLine();
	       
			   // Mientras se haya leido alguna linea
			   while (aux!=null) {
				   String[] command1 = {"sh","-c","ifconfig " + aux.trim()};
				   Process p1 = Runtime.getRuntime().exec (command1);
				   InputStream is1 = p1.getInputStream();
				   BufferedReader br1 = new BufferedReader (new InputStreamReader (is1));
		       
				   // Se lee la primera linea
				   
				   String aux1 = br1.readLine();
				   // Mientras se haya leido alguna linea
				   while (aux1!=null) {
					   if(aux1.contains("direcciónHW"))
						   dirfis = aux1.substring(aux1.trim().length()-17);
					   if(aux1.contains("Direc. inet")){
						   String datos[]=aux1.split(" ");
						   for(int cont=0;cont<datos.length;cont++){
							   if(datos[cont].startsWith("inet"))
								   dirip = datos[cont].substring(5);
							   
							   
							   if(datos[cont].startsWith("Másc:"))
								   mask = datos[cont].substring(5);
							   
						   }
					   }
		        		   
					  aux1 = br1.readLine();
				   }
				   
				   String[] command2 = {"sh","-c","ifplugstatus " + aux.trim()};
				   Process p2 = Runtime.getRuntime().exec (command2);
				   InputStream is2 = p2.getInputStream();
				   BufferedReader br2 = new BufferedReader (new InputStreamReader (is2));
		       
				   // Se lee la primera linea
				   
				   String aux2 = br2.readLine();
				   // Mientras se haya leido alguna linea
				   while (aux2!=null) {
					  if(aux2.endsWith( "link beat detected"))
						  estado="Conectado";
					  else
						  estado="Desconectado";
					  
					  aux2 = br2.readLine();
				   }

				   Adaptador = aux;
				   if(aux.startsWith("eth"))
					   descripcion = "Adaptador alámbrico";
				   
				   if(aux.startsWith("lo"))
					   descripcion = "Interfaz de Retorno";
				   
				   if(aux.startsWith("wlan"))
					   descripcion = "Adaptador inalámbrico";
				   
				   
				   areaPantalla.append("\n\tAdaptador: " + Adaptador + "\n\tDireccion física: " + dirfis + "\n\tDirección IP: " +  dirip + "\n\tDescripción: " + descripcion + "\n\tMáscara: " + mask + "\n\tEstado: " + estado + "\n\n");
				   
				   if(!IndicadorPrueba){
					   if(Adaptador!=null)    				   
	    				   Enviar(Adaptador);
	    			   else
	    				   Enviar("");
	    			   
	    			   if(dirfis!=null)
	    				   Enviar(dirfis);
	    			   else
	    				   Enviar("");
	    			   
	    			   if(dirip!=null)
	    				   Enviar(dirip);
	    			   else
	    				   Enviar("");
	    			   
	    			   if(descripcion!=null)
	    				   Enviar(descripcion);
	    			   else
	    				   Enviar("");
	    			   
	    			   if(mask!=null)
	    				   Enviar(mask);
	    			   else
	    				   Enviar("");
	    			   
	    			   if(estado!=null)
	    				   Enviar(estado);
	    			   else
	    				   Enviar("");
	    			   
	    			   if(dhcp!=null)
	    				   Enviar(dhcp);
	    			   else
	    				   Enviar("");
	    			   
				   }
				   
				   aux = br.readLine();
	        		
			   }
			   
		   }
		}catch ( IOException excepcionES ) {
	   			excepcionES.printStackTrace();
		}
	    if(!IndicadorPrueba)
	    	Enviar("Fin_Red");
	    else
	    	areaPantalla.append("\n/***************************************************************************************************************************************************/\n");
	    IndicadorPrueba=false;
   }
	
   
   private void InformacionDiscos(){
	   if(IndicadorPrueba){
			  areaPantalla.append("\n\n/**************************************************************************************************************************************************/\n");
	 		  areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:  Informacion de Discos                                                          */\n");
	 		  areaPantalla.append("/**************************************************************************************************************************************************/\n");
	 		   	 		  
	   }else{
	 		  areaPantalla.append("\n   ---> Obteniendo información de los discos duros...\n\n");

	   }
	   
	   Vector<EspacioDisco> Discos = new Vector<EspacioDisco>();	
	   String[] datos;
	   String TipoSisFich = null;
	   try {
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c for /F \"tokens=2*\" %a in (\'fsutil fsinfo drives\') do @echo %a %b");
		   else{
			   String[] command = {"sh","-c","df -T | grep /dev/ | awk '{print $1,$2,$3,$4,$5,$6,$7}'"};
			   p = Runtime.getRuntime().exec (command);
		   }
		   // Se obtiene el stream de salida del programa 
		   is = p.getInputStream();
		   // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		   br = new BufferedReader (new InputStreamReader (is));
		   // Se lee la primera linea
		   aux = br.readLine();
       
		   // Mientras se haya leido alguna linea
	       while (aux!=null) {
	    	   if(SistemaOperativo.equals("Windows")){
	    		   
	    		   datos = aux.split(" ");
		    	   for(int i=0;i<datos.length;i++){
		    		   p = Runtime.getRuntime().exec ("cmd /c fsutil fsinfo drivetype " + datos[i]);
		    		   // Se obtiene el stream de salida del programa 
		    		   is = p.getInputStream();
		           
		    		   // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		    		   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		           
		    		   // Se lee la primera linea
		    		   aux = br.readLine();
		    		   if(aux.equals(datos[i] + " - Unidad fija")){
		    			   Process p1 =  Runtime.getRuntime().exec ("cmd /c fsutil fsinfo volumeinfo " + datos[i]); 
		    			   // Se obtiene el stream de salida del programa 
		    			    // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		    			   InputStream is1 = p1.getInputStream();
		    			   BufferedReader br1 = new BufferedReader (new InputStreamReader (is1,"Cp850"));
		    			   // Se lee la primera linea
		    			   String aux1 = br1.readLine();
		    	       
		    			   // Mientras se haya leido alguna linea
		    		       while (aux1!=null) {
		    		    	   if(aux1.startsWith("Nombre del")){
		    		    		   String dat[] = aux1.split(":");
		    		    		   TipoSisFich = dat[1];
		    		    	   }
		    		    		   	    	   
		    		    	   aux1 = br1.readLine();
		    		       }
		    		       
		    		       p1 =  Runtime.getRuntime().exec ("cmd /c fsutil volume diskfree " + datos[i]); 
		    			   // Se obtiene el stream de salida del programa 
		    			    // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		    			   is1 = p1.getInputStream();
		    			   br1 = new BufferedReader (new InputStreamReader (is1,"Cp850"));
		    			   // Se lee la primera linea
		    			   aux1 = br1.readLine();
		    			   String d[];
	    		    	   long byteslibres = 0;
	    		    	   long bytestotal = 0;
		    			   // Mientras se haya leido alguna linea
		    		       while (aux1!=null) {
		    		    	   d=aux1.split(":");
		    		    	   if(d[0].trim().endsWith("bytes libres")){
		    		    		   byteslibres = Long.valueOf(d[1].trim().toString());
		    		    	   }
		    		    	   if(d[0].trim().endsWith("bytes")){
		    		    		   bytestotal = Long.valueOf(d[1].trim().toString());
		    		    	   }
		    		    	   aux1 = br1.readLine();
		    		       }
		    		       EspacioDisco aux = new EspacioDisco(datos[i],TipoSisFich,byteslibres,bytestotal);
		    		       Discos.add(aux);
		    		       }	   
		    		   }
	    	   }else{
	    		   // Linux
	    		   datos = aux.split(" ");
	    		   EspacioDisco aux = new EspacioDisco(datos[0],datos[1],Long.valueOf(datos[4])*1024,Long.valueOf(datos[2])*1024);
    		       Discos.add(aux);
	    	   }
	    	   
	           aux = br.readLine();

	    	   }
	    
	       for(int cont=0; cont< Discos.size();cont++){
	    	   areaPantalla.append("\n\tNombre: " + Discos.get(cont).NombreDisco() + "\tTipoSistema: " + Discos.get(cont).TipoSistemaDisco() + "\tTam total: " + String.valueOf(Discos.get(cont).EspacioTotal()) + "\t\tTam libre: " + String.valueOf(Discos.get(cont).EspacioLibre()) + "\n");
	    	   areaPantalla.append("\t\tPorcentaje Libre: " + Discos.get(cont).PorLibre() + "\tPorcentaje Usado: " + Discos.get(cont).PorUsado() + "\n\n");
	    	   if(!IndicadorPrueba){
	    		   Enviar(Discos.get(cont).NombreDisco());
	    		   Enviar(Discos.get(cont).TipoSistemaDisco());
	    		   Enviar(String.valueOf(Discos.get(cont).EspacioTotal()));
	    		   Enviar(String.valueOf(Discos.get(cont).PorUsado()));
	    	   }
	       }
	    	   
	       	 	
	       
	   }catch ( IOException excepcionES ) {
	   		excepcionES.printStackTrace();
	   }
	   
	      
	   if(!IndicadorPrueba)
		   Enviar("Fin_InfDiscos");
	   else
		   areaPantalla.append("\n\n/*************************************************************************************************************************************************/\n");
	   IndicadorPrueba=false;
   }
   
   
   private void ListadoProcesos(){
	   if(IndicadorPrueba){
		  areaPantalla.append("\n\n/***************************************************************************************************************************************************/\n");
 		  areaPantalla.append("/*                                             PRUEBAS DE FUNCIONES:  Listado de Procesos                                                              */\n");
 		  areaPantalla.append("/***************************************************************************************************************************************************/\n");
 		  
	   }else{
	 		  areaPantalla.append("\n   ---> Obteniendo el listado de procesos activos en el sistema...\n");

	   }
	   
	   areaPantalla.append("\n\tPID\tTAM.\tNombre ");
	   areaPantalla.append("\n                  ------------------------------------------------------------------------------------------------- ");       
	   
	   try {
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c for /F \"tokens=1,2,5\" %a in (\'tasklist /NH /FO TABLE ^| sort \') do @echo %b %a %c"); 
		   }else{
			   String[] command = {"sh","-c","ps -A -l --sort cmd | awk '{print $4,$14,$10}'"};
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   is = p.getInputStream();
		   br = new BufferedReader (new InputStreamReader (is));
       
		   // Se lee la primera linea
		   aux = br.readLine();
       
		   // Mientras se haya leido alguna linea
	       while (aux!=null) {
	    	   if(!IndicadorPrueba)
	    		   Enviar(aux);
	    	   String datos[];
	    	   datos = aux.split(" ");
	    	   
	    	   if(!datos[0].contains("Idle"))
	    		   areaPantalla.append("\n\t" + datos[0] + "\t " + datos[2] + "\t"  + datos[1] );
	           aux = br.readLine();
	       }
	       if(!IndicadorPrueba)
	    	   Enviar("fin");
   		}catch ( IOException excepcionES ) {
   			excepcionES.printStackTrace();
   		}
	   if(IndicadorPrueba)
 		  areaPantalla.append("\n\n/***************************************************************************************************************************************************/\n");
	   areaPantalla.append("\n\n");

	   
	   IndicadorPrueba=false;
   }
   
   private void ProcesarTaskKillWindows(String orden){
	   String datos[];
	   datos = orden.split(" ");
	   
	   areaPantalla.append("\n   ---> Eliminando el proceso con PID: " + datos[3]);
  	
       try{
    	   if(SistemaOperativo.equals("Windows")){
    		   p = Runtime.getRuntime().exec("cmd /c " + orden);
    	   }else{
    		   String[] command = {"sh","-c","kill " + datos[3]};
			   p = Runtime.getRuntime().exec (command);
    	   }
    	   salida.flush();
    	   salida.writeObject("fin_elimina_proceso");
       }catch ( IOException excepcionES ) {
            excepcionES.printStackTrace();
       }
   }
   
   private void ProcesarNavegacion() throws IOException{
	   areaPantalla.append( "\n   ---> Navegación por árbol de directorios comenzada....\n");
       
	   try {
		   // Leemos entrada desde Navegador
		   mensaje = (String) entrada.readObject();
		   while(!mensaje.equals("Salir_Navegacion")){
			   if(mensaje.equals("MostrarMiPc")){
				   MostrarMiPc();
			   }
			   if(mensaje.equals("ActualizarGridView")){
				   ActualizarGridView();
				   
			   }
			   
			   if(mensaje.equals("EjecutarFichero")){
				   EjecutarFichero();
				   
			   }
			   
			   if(mensaje.equals("TransferirFichero")){
				   TransferirFichero();
			   }
			   
			   if(mensaje.equals("Copia")){
				   CopiarFichero();
			   }
			   
			   if(mensaje.equals("Corte")){
				   CorteFichero();
			   }
			   
			   if(mensaje.equals("EliminarFichero")){
				   EliminarFichero();
			   }
			   
			   if(mensaje.equals("RenombrarFichero")){
				   RenombrarFichero();
			   }
			   
			   if(mensaje.equals("CrearFichero")){
				   CrearFichero();
			   }
			   
			   
			   mensaje = (String) entrada.readObject();
		   }
		   
		   areaPantalla.append("\n\n   ---> Navegación por árbol de directorios terminada.\n\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			areaPantalla.append("\n   Se ha terminado la conexión...");
			start();
			   
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			areaPantalla.append("\nNavegacion ClassNotFound");
		}
   }
   
   
   private void CrearFichero(){
	   try {
		   mensaje = (String) entrada.readObject();
		   String Ruta=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String Fichero=mensaje;
		   
		   
		   mensaje = (String) entrada.readObject();
		   String Contenido=mensaje;
		   areaPantalla.append("\n\t· CrearFichero \tRuta: " + Ruta + "\tFichero: " + Fichero + "\n\t· Contenido:");
		   
		   FileWriter fichero = null;
	       PrintWriter pw = null;
	       try {
	    	   if(SistemaOperativo.equals("Windows"))
	    		   fichero = new FileWriter(Ruta+Fichero);
	    	   else
	    		   fichero = new FileWriter(Ruta+"/"+Fichero);
	           pw = new PrintWriter(fichero);

	           String data[];
	           data=Contenido.split("\\n");
	           for(int i=0;i<data.length;i++){
	        	   areaPantalla.append("\n\t" + data[i]);
	        	   pw.println(data[i]);
	           }
	           

	        } catch (Exception e) {
	           e.printStackTrace();
	        } finally {
	        	try {
    	    	   // Nuevamente aprovechamos el finally para 
    	           // asegurarnos que se cierra el fichero.
    	           if (null != fichero)
    	              fichero.close();
    	           } catch (Exception e2) {
    	              e2.printStackTrace();
    	           }
	        	}
	       } catch (ClassNotFoundException | IOException e) {
	   				// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
}
   private void RenombrarFichero(){
	   try {
		   mensaje = (String) entrada.readObject();
		   String Ruta=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String Fichero=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String NombreNuevo=mensaje;
		   areaPantalla.append("\n\t· Renombrar Fichero:\tFichero Origen: " + Ruta+Fichero + "\tNombre Nuevo: " + NombreNuevo);
		   
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c " +"move  /Y \"" + Ruta+Fichero + "\"  \"" + Ruta+NombreNuevo + "\"");
		   }else{
			   String[] command = {"sh","-c","mv -f " + "\"" + Ruta+"/"+Fichero + "\""  + " \"" + Ruta+"/"+NombreNuevo + "\""};
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   
		   Enviar("Fichero " + Fichero + " renombrado con exito");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
   }
   
   private void EliminarFichero(){
	   try {
		   mensaje = (String) entrada.readObject();
		   String FicheroOrigen=mensaje;
		   areaPantalla.append("\n\t· Eliminar Fichero:\tFichero Origen: " + FicheroOrigen);
		   
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c " +"del  \"" + FicheroOrigen + "\"");
		   }else{
			   String[] command = {"sh","-c","rm -f " + "\"" + FicheroOrigen + "\""};
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   Enviar("Fichero " + FicheroOrigen + " eliminado con exito");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
   }
   
   private void CopiarFichero(){
	   try {
		   mensaje = (String) entrada.readObject();
		   String FicheroOrigen=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String RutaDestino=mensaje;
		   areaPantalla.append("\n\t· CopiarFichero:\tFichero Origen: " + FicheroOrigen + "\tRuta Destino: " + RutaDestino);
		   
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c " +"copy  /Y \"" + FicheroOrigen + "\"  \"" + RutaDestino + "\"");
		   }else{
			   String[] command = {"sh","-c","cp -f " + "\"" + FicheroOrigen + "\" \"" + RutaDestino  + "/\""};
			   p = Runtime.getRuntime().exec (command);
		   }
		   

		   Enviar("Copia realizada con éxito");
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    
	   
   }
   
   private void CorteFichero(){
	   try {
		   mensaje = (String) entrada.readObject();
		   String FicheroOrigen=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String RutaDestino=mensaje;
		   areaPantalla.append("\n\t· Cortar Fichero:\tFichero Origen: " + FicheroOrigen + "\tRuta Destino: " + RutaDestino);
		   
		   if(SistemaOperativo.equals("Windows")){
			   p = Runtime.getRuntime().exec ("cmd /c " +"move /Y \"" + FicheroOrigen + "\"  \"" + RutaDestino + "\"");
			   
		   }else{
			   String[] command = {"sh","-c","mv -f " + "\"" + FicheroOrigen + "\" \"" + RutaDestino  + "/\""};
			   p = Runtime.getRuntime().exec (command);
		   }
		   
		   Enviar("Pegado realizado con éxito");
		
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   
   
   private void EjecutarFichero(){
	   try {
		   mensaje = (String) entrada.readObject();
		   String Ruta=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String FicheroEjecutable=mensaje;
		   
		   mensaje = (String) entrada.readObject();
		   String argumentos=mensaje;
		   areaPantalla.append("\n\t· Ejecutar Fichero:\tRuta: " + Ruta + "\tEjecutable: " + FicheroEjecutable + "\tArgumentos: " + argumentos);
		   
		   // Compilamos y ejecutamos 
		   String eje[]  = FicheroEjecutable.split("\\.");
		   String Ejecutable = eje[0];
		   String Extension = eje[1];
		   
		   
		   if(SistemaOperativo.equals("Windows")){
			   // Ejecución de Ficheros .java en Windows
			   if(Extension.equals("java")){
				   areaPantalla.append("\n\t ·Se ejecuta: cmd /c cd \"" + Ruta + "\" && " + "javac \"" + Ejecutable + "." + Extension + "\" && java \"" + Ejecutable + "\" " + argumentos);
				   p = Runtime.getRuntime().exec ("cmd /c cd \"" + Ruta + "\" && " + "javac \"" + Ejecutable + "." + Extension + "\" && java \"" + Ejecutable + "\" " + argumentos);
			   }
			   // Ejecución de Ficheros .exe y .bat en Windows
			   if(Extension.equals("exe") || Extension.equals("bat")){
				   areaPantalla.append("\n\t ·Se ejecuta: cmd /c " + "\"" + Ruta + Ejecutable + "." + Extension + "\" " + argumentos);
				   p = Runtime.getRuntime().exec ("cmd /c " + "\"" + Ruta + Ejecutable + "." + Extension + "\" " + argumentos);
			   }
			   // Ejecución de Ficheros .py en Windows
			   if(Extension.equals("py")){
				   areaPantalla.append("\n\t ·Se ejecuta: cmd /c " + "cd \"" + Ruta + "\" && python \"" +  FicheroEjecutable + "\" " + argumentos);
				   p = Runtime.getRuntime().exec ("cmd /c " + "cd \"" + Ruta + "\" && python \"" +  FicheroEjecutable + "\" " + argumentos );
			   }
		   }else{
			   // Ejecución de Ficheros .java en Linux
			   if(Extension.equals("java")){
				   areaPantalla.append("\n\t ·Se ejecuta: javac \"" + Ruta + "/" + Ejecutable + ".java\" && cd \"" + Ruta + "\" && " + "java \"" + Ejecutable + "\" " + argumentos);
				   String[] command = {"sh","-c","javac \"" + Ruta + "/" + Ejecutable + ".java\" && cd \"" + Ruta + "\" && " + "java \"" + Ejecutable + "\" " + argumentos};
				   p = Runtime.getRuntime().exec (command);
			   }
			   // Ejecución de Ficheros .bin, .run y .sh en Linux
			   if(Extension.equals("bin") || Extension.equals("run") || Extension.equals("sh") ){
				   areaPantalla.append("\n\t ·Se ejecuta: cd \"" + Ruta + "\" && ./\"" + FicheroEjecutable + "\" " + argumentos);
				   String[] command = {"sh","-c","cd \"" + Ruta + "\" && ./\"" + FicheroEjecutable + "\" " + argumentos};
				   p = Runtime.getRuntime().exec (command);
			   }
			   // Ejecución de Ficheros .py en Linux
			   if(Extension.equals("py")){
				   areaPantalla.append("\n\t ·Se ejecuta: cd \"" + Ruta + "\" && python \"" + FicheroEjecutable + "\" " + argumentos);
				   String[] command = {"sh","-c","cd \"" + Ruta + "\" && python \"" + FicheroEjecutable + "\" " + argumentos};
				   p = Runtime.getRuntime().exec (command);
			   }
		   } 
		   
		  
	       
	   } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
   }
   
   private void ActualizarGridView(){
	   try {
		   mensaje = (String) entrada.readObject();
		   areaPantalla.append("\n\t· (ActualizarGridView) Directorio: " + mensaje);
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c for /F \"tokens=3*\" %a in (\'dir " + "\"" + mensaje + "\"" + "\') do @echo %a %b");
		   else{
			   String[] command = {"sh","-c","ls -l \"/" + mensaje + "\" | awk '{ for (x=1; x<=NF; x++) { if(x==1 || x>=9){printf  $x \" \";} }; print  \"\" }'"};
			   p = Runtime.getRuntime().exec (command);
		   }
		   // Se obtiene el stream de salida del programa 
		   is = p.getInputStream();
		   // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		   if(SistemaOperativo.equals("Windows"))
			   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		   else{
			   br = new BufferedReader (new InputStreamReader (is));
		   }
		   // Se lee la primera linea
		   aux = br.readLine();
		   // Mientras se haya leido alguna linea
		   if(SistemaOperativo.equals("Windows")){
			   while (aux!=null) {
				   if(aux.endsWith("bytes") || aux.endsWith("bytes libres")){
				   }else{
					   salida.flush();
					   salida.writeObject(aux);
				   }
				   aux = br.readLine();
			   }
		   }else{
			   while (aux!=null) {
				   if(!aux.startsWith("total")){
					   salida.flush();
					   salida.writeObject(aux);
				   }
				   aux = br.readLine();
			   }
			   
		   }
			   
		   salida.flush();
		   salida.writeObject("fin_listado_dir");
				 
		}catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   }
   }
   
   private void MostrarMiPc(){
	// Mostramos MiPC (unidades)

	   String[] datos;
	   try {
		   areaPantalla.append("\n\t· Mostrando MiPc...");
		   if(SistemaOperativo.equals("Windows"))
			   p = Runtime.getRuntime().exec ("cmd /c for /F \"tokens=2*\" %a in (\'fsutil fsinfo drives\') do @echo %a %b");
		   else{
			   String[] command = {"sh","-c","ls -l / | awk '{print $1,$9}'" };
			   p = Runtime.getRuntime().exec (command);
		   }
			   
		   // Se obtiene el stream de salida del programa 
		   is = p.getInputStream();
		   // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		   br = new BufferedReader (new InputStreamReader (is));
		   // Se lee la primera linea
		   aux = br.readLine();
       
		   // Mientras se haya leido alguna linea
	       while (aux!=null) {
	    	   datos = aux.split(" ");
	    	   if(SistemaOperativo.equals("Windows")){
	    		   for(int i=0;i<datos.length;i++){
	    			   p = Runtime.getRuntime().exec ("cmd /c fsutil fsinfo drivetype " + datos[i]);
		    		   // Se obtiene el stream de salida del programa 
		    		   is = p.getInputStream();
		           
		    		   // Se prepara un bufferedReader para poder leer la salida m�s comodamente. 
		    		   br = new BufferedReader (new InputStreamReader (is,"Cp850"));
		           
		    		   // Se lee la primera linea
		    		   aux = br.readLine();
		    		   if(aux.equals(datos[i] + " - Unidad fija")){
		    			   salida.flush();
				           salida.writeObject(datos[i] + " Unidadfija");
		    		   }
		    		   if(aux.equals(datos[i] + " - Unidad de CD-ROM")){
		    			   salida.flush();
				           salida.writeObject(datos[i] + " CD-ROM");
		    		   } 
	    		   }
	    		   
	    	   }else{
	    		   if(!datos[0].startsWith("total")){
	    			   salida.flush();
			           salida.writeObject(datos[1] + " " + datos[0]);
	    		   }
	    	   }
	    	   
	           aux = br.readLine();
	       }
	       
	       Enviar("Fin_MostrarMiPc");
	   }catch ( IOException excepcionES ) {
	   		excepcionES.printStackTrace();
	   }
	  
   }
   
   private class Transferirfichero implements Runnable{
	   private String Ruta;
	   private String Fichero;
	   private int PuertoT;
	   
	   Transferirfichero(String r,String f,int pto){
		   Ruta=r;
		   Fichero=f;
		   PuertoT=pto;
		   try {
			   servidortrans= new ServerSocket(PuertoT);
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   Thread t=new Thread (this);
   		   t.start();
	   }

	   @Override
	   public void run() {
		// TODO Auto-generated method stub
		   areaPantalla.append( "\n\n   Esperando una conexión en el puerto " + String.valueOf(PuertoT) + ".....\n\n");
		   Enviar("ListoTransferencia:" + PuertoT);
		   try{
			   conexiontrans = servidortrans.accept();
			   // Informamos de la Conexión recibida desde el terminal

			   areaPantalla.append( "\n   Conexión trans" + " recibida de: " + conexiontrans.getInetAddress().getHostName() + "\n");
				   
			   areaPantalla.append("\n\t· Transferencia de fichero \tRuta: " + Ruta +  "\tEjecutable: " + Fichero);

				 	            
			   final BufferedOutputStream outStream = new BufferedOutputStream(conexiontrans.getOutputStream());
			   final BufferedInputStream inStream;
			   if(SistemaOperativo.equals("Windows"))
				   inStream = new BufferedInputStream(new FileInputStream(Ruta+Fichero));
			   else
				   inStream = new BufferedInputStream(new FileInputStream(Ruta+"/"+Fichero));
			   
			   final byte[] buffer = new byte[4096];
			   for (int read = inStream.read(buffer); read >= 0; read = inStream.read(buffer))
				   outStream.write(buffer, 0, read);

			   inStream.close();
			   outStream.close();
			   servidortrans.close();

		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		}
   }
   
   private void TransferirFichero(){
	   try {
		   
		   mensaje = (String) entrada.readObject();
		   String Ruta=mensaje;
			 
		   mensaje = (String) entrada.readObject();
		   String Fichero=mensaje;
 			
		   
		   new Transferirfichero(Ruta,Fichero,PuertoTransferencia);
		  
	   }	catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
   }
   
   private void Enviar(String n){
	   try {
		salida.flush();
		salida.writeObject(n);
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
   }
   
   private void InicializaServidor(){
	   CrearCarpetaScripts();

	   areaPantalla.append( "\n   Bienvenido al Servidor RemSys...");
	   areaPantalla.append( "\n   Con este servidor podrá obtener información de este sistema en su móvil en todo momento mediante la red");
	   areaPantalla.append( "\n   El puerto de escucha está configurado en el " + Puerto + " y el de transferencia en el: " + PuertoTransferencia);
	   areaPantalla.append( "\n   Para configurar el puerto de escucha/Transferencia del servidor, seleccione Configuracion.../Configurar puerto");
	   areaPantalla.append( "\n   Recuerde que además debe de configurar el router para redirigir las peticiones entrantes de dicho puerto a la ip local del sistema...");
	   areaPantalla.append( "\n   Para otras consultas, por favor, véase el manual...\n");
	   areaPantalla.setAutoscrolls(true);
   }
   
   public  static void main( String args[] )
   {
	  Servidor aplicacion = new Servidor();
      aplicacion.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      aplicacion.InicializaServidor();
      aplicacion.start();
   }
}  // fin de la clase Servidor