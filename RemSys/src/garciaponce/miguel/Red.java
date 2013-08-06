package garciaponce.miguel;

public class Red {
	private String Adaptador;
	private String DirFis;
	private String DirIP;
	private String Descripcion;
	private String Mascara;
	private String Estado;
	private String DHCP;

	
	Red(String a,String dirf,String dirip,String des,String mask,String estado,String dhcp){
		Adaptador=a;
		DirFis=dirf;
		DirIP=dirip;
		Descripcion=des; 
		Mascara=mask;
		Estado=estado;
		DHCP=dhcp; 
	}
	  
	public String Adaptador(){ 
		return Adaptador;  
	}
	
		
	public String DirFis(){
		return DirFis;
	}   
	
	public String DirIP(){
		return DirIP;
	}
	
		
	public String Descripcion(){
		return Descripcion;
	}
	
	public String Mascara(){
		return Mascara;
	}
	
		
	public String Estado(){
		return Estado;
	}
	
	public String DHCP(){
		return DHCP;
	}
	
	public boolean equals(Red a){
		if(Adaptador.equals(a.Adaptador()) &&  DirFis.equals(a.DirFis()) && DirIP.equals(a.DirIP()) && Descripcion.equals(a.Descripcion()) && Mascara.equals(a.Mascara()) && Estado.equals(a.Estado()) && DHCP.equals(a.DHCP()))
			return true;
		else
			return false;
		
	}
	
	
}
