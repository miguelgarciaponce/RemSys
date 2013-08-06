
public class EspacioDisco {
	private long EspacioLibre;
	private long EspacioTotal;
	private String NombreDisco;
	private String TipoSistema;
	private int PorcentajeUsado;
	private int PorcentajeLibre;
	
	EspacioDisco(String NombreD,String Tipo, long byteslibres, long bytestotal){
		NombreDisco = NombreD;
		TipoSistema = Tipo;
		EspacioLibre = byteslibres;
		EspacioTotal = bytestotal; // /1073741824
		
		RealizarEstadisticas();
	}
	
	private void RealizarEstadisticas(){
		PorcentajeLibre= (int) ((EspacioLibre*100)/ EspacioTotal);
		PorcentajeUsado  = 100 - PorcentajeLibre;
	}
	
	public String NombreDisco(){
		return NombreDisco;
	}
	
	public int PorLibre(){
		return PorcentajeLibre;
	}
	
	public int PorUsado(){
		return PorcentajeUsado;
	}
	
	public String TipoSistemaDisco(){
		return TipoSistema;
	}
	
	public long EspacioLibre(){
		return EspacioLibre;
	}
	
	public long EspacioTotal(){
		return EspacioTotal;
	}
	
	
	

}
