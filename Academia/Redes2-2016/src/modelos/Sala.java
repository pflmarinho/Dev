package modelos;

import java.util.ArrayList;
import java.util.List;

public class Sala {
	private String nome;
	private Usuario administrador;
	private boolean privada;
	private ArrayList<Usuario> usuariosConectados;
	
	public Sala() {
		nome = null;
		administrador = null;
		privada = false;
		usuariosConectados = new ArrayList<Usuario>();
	}
	
	public Sala(String nome) {
		this();
		this.nome = nome;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Usuario getAdministrador() {
		return administrador;
	}
	public void setAdministrador(Usuario administrador) {
		this.administrador = administrador;
	}
	public boolean isPrivada() {
		return privada;
	}
	public void setPrivada(boolean privada) {
		this.privada = privada;
	}
	public ArrayList<Usuario> getUsuariosConectados() {
		return usuariosConectados;
	}
	public void setUsuariosConectados(ArrayList<Usuario> usuariosConectados) {
		this.usuariosConectados = usuariosConectados;
	}
}
