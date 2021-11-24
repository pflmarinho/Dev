package modelos;

import java.net.Socket;
import java.security.PublicKey;

public class ComunicacaoUsuario {
	private Usuario usuario;
	private Socket socket;
	private PublicKey chavePublica;
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public PublicKey getChavePublica() {
		return chavePublica;
	}
	public void setChavePublica(PublicKey chavePublica) {
		this.chavePublica = chavePublica;
	}
}
