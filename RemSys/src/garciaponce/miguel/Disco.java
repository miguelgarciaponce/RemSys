package garciaponce.miguel;

public class Disco {
	private String NombreDisco;
	private String TipoSistema;
	private int PorcentajeOcupado;
	private long TamTotal;
	
	Disco(String NombreD,String Tipo, int Ocupado,long Tam){
		NombreDisco = NombreD;
		TipoSistema = Tipo;
		PorcentajeOcupado=Ocupado;
		TamTotal = Tam;
	}
	
	public String NombreDisco(){
		return NombreDisco;
	}

	public int PorUsado(){
		return PorcentajeOcupado;
	}
	

	public long Tamanio(){
		return TamTotal;
	}
	
	public String TipoSistemaDisco(){
		return TipoSistema;
	}
	
}
