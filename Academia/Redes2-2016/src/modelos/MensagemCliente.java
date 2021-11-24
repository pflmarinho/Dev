package modelos;

import java.util.ArrayList;
import java.util.List;

public class MensagemCliente {
	private String status;
	private TipoMensagemCliente tipo;
	private String texto;
	private ArrayList<Usuario> destinatarios;
	private Usuario remetente;
	private String chavePublica;
	private boolean encriptada;

	public MensagemCliente() {
		encriptada = true;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public TipoMensagemCliente getTipo() {
		return tipo;
	}
	public void setTipo(TipoMensagemCliente tipo) {
		this.tipo = tipo;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public List<Usuario> getDestinatarios() {
		return destinatarios;
	}
	public void setDestinatarios(ArrayList<Usuario> destinatarios) {
		this.destinatarios = destinatarios;
	}
	public Usuario getRemetente() {
		return remetente;
	}
	public void setRemetente(Usuario remetente) {
		this.remetente = remetente;
	}
	public String getChavePublica() {
		return chavePublica;
	}
	public void setChavePublica(String chavePublica) {
		this.chavePublica = chavePublica;
	}
	public boolean isEncriptada() {
		return encriptada;
	}
	public void setEncriptada(boolean encriptada) {
		this.encriptada = encriptada;
	}
}