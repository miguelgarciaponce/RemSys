package garciaponce.miguel;

public class Proceso {
	private String PID;
	private String nomproc;
	private String tamproc;
	
	

	public Proceso(String pid, String nom, String tam){
		PID=pid;
		nomproc=nom;
		tamproc=tam;
	}
	
	public String getPID(){
		return PID;
	}
	
	public String getNom(){
		return nomproc;
	}
	
	public String getTam(){
		return tamproc;
	}
	
	
}
