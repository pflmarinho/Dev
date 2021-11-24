package modelos;

public enum StatusUsuario {
	DISPONIVEL(1),
	OCUPADO(2),	
	AUSENTE(3);	 	 	
	
	private final int valor;
	StatusUsuario(int valorOpcao){
		valor = valorOpcao;
	}
	public int getValor(){
		return valor;
	}
	
	public String getNome() {
		String nome = "";
		switch (valor) {
		case 1:
			nome = "Disponível";
			break;
		case 2:
			nome = "Ocupado";		
			break;
		case 3:
			nome = "Ausente";
			break;
		default:
			break;
		}
		return nome;
	}
}
