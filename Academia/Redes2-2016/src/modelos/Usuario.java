package modelos;

public class Usuario {
	private int id;
	private String nome;
	private StatusUsuario status;
	
	public Usuario() {
		
	}
	
	public Usuario(int id) {
		this.id = id;
	}
	
	public Usuario(String nome) {
		this.nome = nome;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public StatusUsuario getStatus() {
		return status;
	}
	public void setStatus(StatusUsuario status) {
		this.status = status;
	}	
}
