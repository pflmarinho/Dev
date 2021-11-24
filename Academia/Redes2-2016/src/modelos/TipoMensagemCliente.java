package modelos;

public enum TipoMensagemCliente {
	CONECTAR(1),	
	DESCONECTAR(2),
	ALTERAR_STATUS(3),
	ENVIAR_MENSAGEM_PARA_TODOS_NA_SALA(4);

	private final int valor;
	TipoMensagemCliente(int valorOpcao){
		valor = valorOpcao;
	}
	public int getValor(){
		return valor;
	}
}
