package modelos;

public enum TipoMensagemServidor {
	ERRO(1),
	CONEXAO_ACEITA(2),
	SERVIDOR_DESLIGADO(3),
	DESCONECTADO_PELO_SERVIDOR(4),
	NOVO_USUARIO_CONECTADO(5),
	USUARIO_DESCONECTADO(6),
	USUARIO_ALTEROU_STATUS(7),
	ENTREGAR_MENSAGEM(8);
		
	private final int valor;
	TipoMensagemServidor(int valorOpcao){
		valor = valorOpcao;
	}
	public int getValor(){
		return valor;
	}
}
