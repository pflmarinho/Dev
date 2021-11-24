package cliente;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import modelos.MensagemCliente;
import modelos.Sala;
import modelos.StatusUsuario;
import modelos.TipoMensagemCliente;
import modelos.Usuario;
import telas.Cliente;
import telas.Servidor;
import util.RSA;

public class Client {
	private String ip;
	private String nome;
	private Usuario usuario;
	private int porta;
	private boolean executando;
	private ArrayList<Usuario> usuariosConectados;	
	private ArrayList<Sala> salasCriadas;
	private Cliente tela;
	private Socket socketComServidor;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ClientHandler clientHandler;
	private Sala salaAtual;	
	private RSA rsa;
	private PublicKey chavePublicaDoServidor;

	public Client (Cliente tela, String ip, String porta, String nome) throws Exception {
		if (tela == null) {
			throw new Exception("Para instanciar o cliente é necessário informar a tela associada.");
		}
		
		try {
			this.porta = Integer.parseInt(porta);
		} catch (Exception e) {
			tela.exibirAlerta("Porta inválida.");
		}	
		
		if (ip.isEmpty()) {
			tela.exibirAlerta("IP inválido.");
			return;
		} 
		
		if (nome.isEmpty()) {
			tela.exibirAlerta("Nome inválido.");
			return;
		} 
		
		if (this.porta <=0) {
			tela.exibirAlerta("Porta inválida.");
			return;
		} 
			
		this.ip = ip;
		this.nome = nome;
		this.tela = tela;
		usuariosConectados = new ArrayList<Usuario>();
		salasCriadas = new ArrayList<Sala>();
		socketComServidor = new Socket();
		rsa = new RSA();
	}
	
	public void conectar() {
		try {
			socketComServidor.connect(new InetSocketAddress(ip, porta));
			inputStream = socketComServidor.getInputStream();
            outputStream = socketComServidor.getOutputStream();                            
            
            clientHandler = new ClientHandler(this, socketComServidor);
            clientHandler.start();
            
            this.usuario = new Usuario(nome);
            
            String chavePublica = Base64.getEncoder().encodeToString(this.rsa.getEncodedPublicKey()); 
            
            MensagemCliente mensagem = new MensagemCliente();
            mensagem.setTipo(TipoMensagemCliente.CONECTAR);
            mensagem.setRemetente(this.usuario);
            mensagem.setChavePublica(chavePublica);
            mensagem.setEncriptada(false);
            
            enviarMensagemAoServidor(mensagem);
		} catch (IOException e) {
			tela.exibirAlerta("Erro na conexão: "+e.getMessage());			
		}
	}
	
	public void desconectar() {		
		MensagemCliente mensagem = new MensagemCliente();
        mensagem.setTipo(TipoMensagemCliente.DESCONECTAR);
        mensagem.setRemetente(this.usuario);
        
        enviarMensagemAoServidor(mensagem); 
        
        clientHandler.interrupt();
        tela.exibirAlerta("Desconectado do servidor.");
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	private void enviarMensagemAoServidor(MensagemCliente mensagem) {
		System.out.println("--- Enviando mensagem ao servidor ---");
		String json = "";
		String jsonEncriptado = "";
		try {
    		json = new Gson().toJson(mensagem) + "\r\n";
    		jsonEncriptado = (mensagem.isEncriptada() ? this.rsa.encrypt(json, this.chavePublicaDoServidor) : json );
    		outputStream.write(jsonEncriptado.getBytes());
    		outputStream.flush();
		} catch (Exception e) {
			String mensagemDeFalha = "Falha ao enviar mensagem ao servidor.";
			this.tela.exibirAlerta(mensagemDeFalha);
			System.out.println(mensagemDeFalha);
			System.out.println("Erro: "+e.getMessage()); 
		}    
    	System.out.println("JSON original: "+json.replace("\r\n", ""));
		System.out.println("JSON encriptado: "+jsonEncriptado);
		System.out.println("Mensagem encriptada? "+mensagem.isEncriptada());
		System.out.println();
	}
	
	public void alterarStatus(String status) {
		StatusUsuario statusUsuario = null;
		switch (status) {
		case "Disponível":
			statusUsuario = StatusUsuario.DISPONIVEL;
			break;
		case "Ocupado":
			statusUsuario = StatusUsuario.OCUPADO;		
			break;
		case "Ausente":
			statusUsuario = StatusUsuario.AUSENTE;
			break;
		default:
			tela.exibirAlerta("Status inválido");
			break;
		}
		if (statusUsuario == null) {
			return;			
		}
		usuario.setStatus(statusUsuario);
		MensagemCliente mensagem = new MensagemCliente();
		mensagem.setRemetente(usuario);
		mensagem.setTipo(TipoMensagemCliente.ALTERAR_STATUS);
		enviarMensagemAoServidor(mensagem);
	}
	
	public void enviarMensagemParaTodosNaSala(String textoDaMensagem) {
		MensagemCliente mensagem = new MensagemCliente();
		mensagem.setRemetente(usuario);
		mensagem.setTipo(TipoMensagemCliente.ENVIAR_MENSAGEM_PARA_TODOS_NA_SALA);
		mensagem.setTexto(textoDaMensagem);
		enviarMensagemAoServidor(mensagem);
	}
	
	public void exibirMensagemRecebida(Usuario remetente, String mensagem) {
		tela.adicionarMensagem(remetente.getNome() + ": "+mensagem);
	}
	
	public ArrayList<Usuario> getUsuariosConectados() {
		return usuariosConectados;
	}

	public void setUsuariosConectados(ArrayList<Usuario> usuariosConectados) {
		this.usuariosConectados = usuariosConectados;
	}

	public ArrayList<Sala> getSalasCriadas() {
		return salasCriadas;
	}

	public void setSalasCriadas(ArrayList<Sala> salasCriadas) {
		this.salasCriadas = salasCriadas;
	}
	
	public Cliente getTela() {
		return this.tela;
	}
	
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public Sala getSalaAtual() {
		return salaAtual;
	}

	public void setSalaAtual(Sala salaAtual) {
		this.salaAtual = salaAtual;
	}
	
	public PublicKey getChavePublicaDoServidor() {
		return chavePublicaDoServidor;
	}

	public void setChavePublicaDoServidor(PublicKey chave) {
		this.chavePublicaDoServidor = chave;
	}
	
	public RSA getRSA() {
		return this.rsa;
	}
}
