package garciaponce.miguel;

public class Host {
	private String nomhost;
	private String IP;
	private String puertohost;
	private String dirmac;
	
	public Host(String nom, String i, String puert,String mac){
		IP=i;
		nomhost=nom;
		puertohost=puert;
		dirmac=mac;
	}
	
	public String getIP(){
		return IP;
	}
	
	public String getNom(){
		return nomhost;
	}
	
	public String getPuerto(){
		return puertohost;
	}
	
	public String getMac(){
		return dirmac;
	}
	
	
}
