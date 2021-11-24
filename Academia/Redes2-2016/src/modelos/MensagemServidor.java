package modelos;

import java.util.ArrayList;

public class MensagemServidor {
	private Usuario destinatario;	
	private ArrayList<Sala> salasCriadas;
	private ArrayList<Usuario> usuariosConectados;
	private String texto;
	private TipoMensagemServidor tipo;
	private Sala salaAtual;
	private Usuario usuario;	
	private String chavePublica;
	
	public ArrayList<Usuario> getUsuariosConectados() {
		return usuariosConectados;
	}
	public void setUsuariosConectados(ArrayList<Usuario> usuariosConectados) {
		this.usuariosConectados = usuariosConectados;
	}

	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Sala getSalaAtual() {
		return salaAtual;
	}
	public void setSalaAtual(Sala salaAtual) {
		this.salaAtual = salaAtual;
	}
	public Usuario getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(Usuario destinatario) {
		this.destinatario = destinatario;
	}
	public ArrayList<Sala> getSalasCriadas() {
		return salasCriadas;
	}
	public void setSalasCriadas(ArrayList<Sala> salasCriadas) {
		this.salasCriadas = salasCriadas;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public TipoMensagemServidor getTipo() {
		return tipo;
	}
	public void setTipo(TipoMensagemServidor tipo) {
		this.tipo = tipo;
	}
	public String getChavePublica() {
		return chavePublica;
	}
	public void setChavePublica(String chavePublica) {
		this.chavePublica = chavePublica;
	}
}
