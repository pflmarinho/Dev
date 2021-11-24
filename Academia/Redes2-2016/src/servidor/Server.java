package servidor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import modelos.*;
import telas.Servidor;
import util.RSA;

public class Server extends Thread{

	private int porta;
	private boolean executando;
	private ServerSocket socketServidor;
	private ArrayList<Usuario> usuariosConectados;
	private ArrayList<Sala> salasCriadas;
	private ArrayList<ComunicacaoUsuario> dadosDeComunicacaoComUsuario;	
	private Servidor tela;
	private int contadorUsuarios;
	private RSA rsa;
	
	public Server (Servidor tela, String porta) throws Exception {
		if (tela == null) {
			throw new Exception("Para instanciar o servidor é necessário informar a tela associada.");
		}
		
		try {
			this.porta = Integer.parseInt(porta);
		} catch (Exception e) {
			tela.exibirAlerta("Porta inválida.");
		}	
		
		if (this.porta <=0) {
			tela.exibirAlerta("Porta inválida.");
			return;
		} 
		
		this.tela = tela;
		contadorUsuarios = 0;
		usuariosConectados = new ArrayList<Usuario>();
		salasCriadas = new ArrayList<Sala>();
		dadosDeComunicacaoComUsuario = new ArrayList<ComunicacaoUsuario>();
		rsa = new RSA();
		
	}
	
	@Override
    public void run() {
        try {
        	socketServidor = new ServerSocket(this.porta);
        	executando = true;
        	
        	Sala sala = new Sala("Geral");
        	salasCriadas.add(sala);
        	
        	this.tela.adicionarLog("Servidor iniciado com sucesso.");
        	this.tela.adicionarLog("Endereço: "+socketServidor.getInetAddress().getLocalHost() +":"+socketServidor.getLocalPort());
        	this.tela.colocarTelaEmModoIniciado();
        	
            while (executando) {            
                Socket socketCliente = socketServidor.accept();

                if(socketCliente.isConnected()){
                	tela.adicionarLog("Tentativa de conexão do ip "+ socketCliente.getInetAddress().getLocalHost() +" detectada.");
                    ServerHandler serverHandler = new ServerHandler(this, socketCliente);
                    serverHandler.start();
                }
            }

        } catch (Exception e) {
        	this.tela.adicionarLog(e.getMessage());
        	System.out.println("Exceção no servidor: "+e.getMessage());
        }
    }
	
	public Usuario conectarUsuario(MensagemCliente mensagem, Socket socketCliente) throws Exception {
		Usuario remetente = mensagem.getRemetente(); 
		if (remetente == null || remetente.getNome().isEmpty()) {
			throw new Exception("Para conectar o usuário ao servidor é necessário informar o nome.");
		}
		if (encontrarIndiceDoUsuario(remetente.getNome(), usuariosConectados) != -1) {
			throw new Exception("Já existe um usuário conectado com esse nome. Por gentileza escolha outro nome.");
		}
		
		contadorUsuarios++;
		Usuario usuario = mensagem.getRemetente();		
		usuario.setId(contadorUsuarios);
		usuario.setStatus(StatusUsuario.DISPONIVEL);		
		usuariosConectados.add(usuario);
		
		byte[] chavePublicaDoUsuarioEmBytes = Base64.getDecoder().decode(mensagem.getChavePublica());
		PublicKey chavePublicaDoUsuario = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(chavePublicaDoUsuarioEmBytes));
		
		Sala salaGeral = salasCriadas.get(encontrarIndiceDaSala("Geral"));
		adicionarUsuarioNaSala(salaGeral, usuario);		
		
		ComunicacaoUsuario comunicacaoUsuario = new ComunicacaoUsuario();
		comunicacaoUsuario.setUsuario(usuario);
		comunicacaoUsuario.setSocket(socketCliente);
		comunicacaoUsuario.setChavePublica(chavePublicaDoUsuario);
		
		dadosDeComunicacaoComUsuario.add(comunicacaoUsuario);		
		
		tela.atualizarUsuarios();
		tela.adicionarLog("Usuário " + usuario.getNome() + " conectado com sucesso.");
		
		return usuario;
	}
	
	public Sala identificarSala(String sala) {
		return salasCriadas.get(encontrarIndiceDaSala(sala));
	}
	
	public void desconectarUsuario(Usuario usuario, boolean deveAvisarDesconexao) throws Exception {
		if (usuario == null || usuario.getNome().isEmpty()) {
			throw new Exception("Para desconectar o usuário ao servidor é necessário informar seu nome.");
		}
		int indiceDoUsuario = encontrarIndiceDoUsuario(usuario.getNome(), usuariosConectados);
		if (indiceDoUsuario == -1) {
			throw new Exception("Usuário "+usuario.getNome()+" não se encontra conectado");
		}
		
		Sala salaDoUsuario = encontrarSalaAtualDoUsuario(usuario);		
		
		usuariosConectados.remove(indiceDoUsuario);
		for (Sala sala : salasCriadas) {
			ArrayList<Usuario> usuariosNaSala = sala.getUsuariosConectados();
						
			int size = usuariosNaSala.size();
			boolean continuaBusca = true;
			for (int i = 0; i < size && continuaBusca; i++) {
				Usuario usuarioNaSala = usuariosNaSala.get(i); 
				if (usuarioNaSala.getNome().equals(usuario.getNome())) {
					usuariosNaSala.remove(i);
					continuaBusca = false;
				}
			}
		}
		
		boolean continuaBusca = true;
		
		for (int i = 0; i < dadosDeComunicacaoComUsuario.size() && continuaBusca; i++) {
			ComunicacaoUsuario comunicacaoUsuario = dadosDeComunicacaoComUsuario.get(i); 
			Usuario usuarioDaComunicao = comunicacaoUsuario.getUsuario();
			if (usuarioDaComunicao.getNome().equals(usuario.getNome())) {
				dadosDeComunicacaoComUsuario.remove(i);
				continuaBusca = false;
			}
		}
				
		tela.adicionarLog("Usuário " + usuario.getNome() + " desconectado.");
		
		if (deveAvisarDesconexao) {
			tela.atualizarUsuarios();
			avisarTodosSobreDesconexaoDeUsuario(usuario, salaDoUsuario);
		}
	}
	
	public void adicionarUsuarioNaSala(Sala sala, Usuario usuario) throws Exception {
		int indiceDaSala = encontrarIndiceDaSala(sala.getNome());
		if (indiceDaSala == -1) {
			throw new Exception("Sala inválida");
		}
		sala = salasCriadas.get(indiceDaSala);
		ArrayList<Usuario> usuariosNaSala = sala.getUsuariosConectados();	
		int indiceDoUsuarioNoServidor = encontrarIndiceDoUsuario(usuario.getNome(), usuariosConectados);
		if (indiceDoUsuarioNoServidor == -1) {
			throw new Exception("Usuário não está conectado ao servidor.");
		}
		int indiceDoUsuarioNaSala = encontrarIndiceDoUsuario(usuario.getNome(), usuariosNaSala);
		if (indiceDoUsuarioNaSala != -1) {
			throw new Exception("Usuário já se encontra nesta sala.");
		}		
		usuario = usuariosConectados.get(indiceDoUsuarioNoServidor);
		usuariosNaSala.add(usuario);
	}
	
	private int encontrarIndiceDaSala(String nome) {
		int indiceDaSala = -1;
		int size = salasCriadas.size();
		boolean continuaBusca = true;
		for (int i = 0; i < size && continuaBusca; i++) {
			Sala sala = salasCriadas.get(i); 
			if (sala.getNome().equals(nome)) {
				indiceDaSala = i;
				continuaBusca = false;
			}
		}
		return indiceDaSala;
	}
	
	private int encontrarIndiceDoUsuario(String nome, ArrayList<Usuario> usuarios) {
		int indiceDoUsuario = -1;
		int size = usuarios.size();
		boolean continuaBusca = true;
		for (int i = 0; i < size && continuaBusca; i++) {
			Usuario usuario = usuarios.get(i); 
			if (usuario.getNome().equals(nome)) {
				indiceDoUsuario = i;
				continuaBusca = false;
			}
		}
		return indiceDoUsuario;
	}
	
	private Sala encontrarSalaAtualDoUsuario(Usuario usuario) {
		Sala sala = null;
		for (Sala salaCriada : salasCriadas) {
			for (Usuario usuarioNaSala : salaCriada.getUsuariosConectados()) {
				if (usuarioNaSala.getNome().equals(usuario.getNome())) {
					sala = salaCriada;					
				}
			}
		}
		return sala;
	}
	
	public void desligar() {
		try {
			this.executando = false;
			enviarMensagensDeServidorDesligado();
			socketServidor.close();		
			this.tela.colocarTelaEmModoAguardandoInicio();
			this.interrupt();			
		} catch (IOException e) {
			this.tela.exibirAlerta("Erro: "+e.getMessage());
		}
	}
	
	private void enviarMensagensDeServidorDesligado() {
		MensagemServidor mensagem = new MensagemServidor();
		mensagem.setTipo(TipoMensagemServidor.SERVIDOR_DESLIGADO);
		
		for (ComunicacaoUsuario comunicacaoUsuario : dadosDeComunicacaoComUsuario) {
			enviarMensagemAoCliente(mensagem, comunicacaoUsuario.getUsuario());		
		}				    	
	}
	
	private ComunicacaoUsuario identificarComunicaoUsuario(Usuario usuario) {
		ComunicacaoUsuario retorno = null;
		for (ComunicacaoUsuario comunicacaoUsuario : dadosDeComunicacaoComUsuario) {
			if (comunicacaoUsuario.getUsuario().getNome().equals(usuario.getNome())) {
				retorno = comunicacaoUsuario;
			}
		}
		return retorno;
	}
	
	public void desconectarUsuarios(ArrayList<Usuario> usuarios) {
		int qtdUsuariosSelecionados = usuarios.size();
		int qtdUsuariosDesconectados = 0;
		for (Usuario usuario : usuarios) {
			try {				
				MensagemServidor mensagem = new MensagemServidor();
				mensagem.setTipo(TipoMensagemServidor.DESCONECTADO_PELO_SERVIDOR);				
				enviarMensagemAoCliente(mensagem, usuario);
				qtdUsuariosDesconectados++;
				boolean deveAvisarTodos = qtdUsuariosDesconectados == qtdUsuariosSelecionados;
				desconectarUsuario(usuario, deveAvisarTodos);
			} catch (Exception e) {
				this.tela.exibirAlerta("Erro ao desconectar usuário: "+e.getMessage());
			}
		}
	}
	
	public void enviarMensagemAoCliente(MensagemServidor mensagem, Usuario usuario) {
		System.out.println("--- Enviando mensagem ao cliente ---");
		ComunicacaoUsuario comunicacaoUsuario = identificarComunicaoUsuario(usuario);
		String json = "";
		String jsonEncriptado = "";
    	try {
    		json = new Gson().toJson(mensagem) + "\r\n";
    		jsonEncriptado = this.rsa.encrypt(json, comunicacaoUsuario.getChavePublica());
    		OutputStream out = comunicacaoUsuario.getSocket().getOutputStream();
    		out.write(jsonEncriptado.getBytes());
    		out.flush();
		} catch (Exception e) {
			String mensagemDeFalha = "Falha ao enviar retorno ao usuário \""+usuario.getNome()+"\".";
			tela.adicionarLog(mensagemDeFalha);
			System.out.println(mensagemDeFalha);
			System.out.println("Erro: "+e.getMessage()); 
		}    
    	System.out.println("JSON original: "+json.replace("\r\n", ""));
		System.out.println("JSON encriptado: "+jsonEncriptado);
		System.out.println();
	}
	
	public void avisarTodosSobreNovoUsuario(Usuario novoUsuario) {				
		for (ComunicacaoUsuario comunicacaoComUsuario : dadosDeComunicacaoComUsuario) {			
			Usuario usuario = comunicacaoComUsuario.getUsuario();
			if (!usuario.getNome().equals(novoUsuario.getNome())) {
				MensagemServidor mensagem = new MensagemServidor();
				mensagem.setTipo(TipoMensagemServidor.NOVO_USUARIO_CONECTADO);		
				mensagem.setUsuario(usuario);
				mensagem.setSalasCriadas(salasCriadas);
				mensagem.setUsuariosConectados(usuariosConectados);
				mensagem.setSalaAtual(identificarSala("Geral"));
				enviarMensagemAoCliente(mensagem, usuario);
			}
		}	
	}
	
	public void avisarTodosSobreDesconexaoDeUsuario(Usuario usuario, Sala salaDoUsuario) {		
		for (ComunicacaoUsuario comunicacaoComUsuario : dadosDeComunicacaoComUsuario) {			
			Usuario destinatario = comunicacaoComUsuario.getUsuario();			
			if (!usuario.getNome().equals(destinatario.getNome())) {
				MensagemServidor mensagem = new MensagemServidor();
				mensagem.setTipo(TipoMensagemServidor.USUARIO_DESCONECTADO);		
				mensagem.setUsuario(usuario);
				mensagem.setSalasCriadas(salasCriadas);
				mensagem.setUsuariosConectados(usuariosConectados);
				mensagem.setSalaAtual(salaDoUsuario);
				enviarMensagemAoCliente(mensagem, destinatario);	
			}			
		}	
	}
	
	private void atualizarEstruturasDeUsuario(Usuario usuario) {		
		boolean continuaBusca = true;
		for (int i = 0; i < usuariosConectados.size() && continuaBusca; i++) {
			Usuario usuarioConectado = usuariosConectados.get(i);
			if (usuarioConectado.getNome().equals(usuario.getNome())) {
				usuariosConectados.set(i, usuario);
				continuaBusca = false;
			}
		}
		
		continuaBusca = true;
		for (Sala sala : salasCriadas) {
			ArrayList<Usuario> usuariosNaSala = sala.getUsuariosConectados();
			for (int i = 0; i < usuariosNaSala.size() && continuaBusca; i++) {
				Usuario usuarioConectado = usuariosNaSala.get(i);
				if (usuarioConectado.getNome().equals(usuario.getNome())) {
					usuariosNaSala.set(i, usuario);
					sala.setUsuariosConectados(usuariosNaSala);
					continuaBusca = false;
				}
			}
		}
		
		continuaBusca = true;
		for (int i = 0; i < dadosDeComunicacaoComUsuario.size() && continuaBusca; i++) {	
			ComunicacaoUsuario comunicacaoUsuario = dadosDeComunicacaoComUsuario.get(i);
			if (usuario.getNome().equals(comunicacaoUsuario.getUsuario().getNome())) {
				comunicacaoUsuario.setUsuario(usuario);
				dadosDeComunicacaoComUsuario.set(i, comunicacaoUsuario);
				continuaBusca = false;
			}
		}
	}
	
	public void avisarTodosSobreMudancaDeStatusDeUsuario(Usuario usuario) {	
		atualizarEstruturasDeUsuario(usuario);
		Sala sala = encontrarSalaAtualDoUsuario(usuario);
		
		for (Usuario destinatario : sala.getUsuariosConectados()) {				
			MensagemServidor mensagem = new MensagemServidor();
			mensagem.setTipo(TipoMensagemServidor.USUARIO_ALTEROU_STATUS);		
			mensagem.setUsuario(usuario);
			mensagem.setSalasCriadas(salasCriadas);
			mensagem.setUsuariosConectados(usuariosConectados);
			mensagem.setSalaAtual(sala);
			enviarMensagemAoCliente(mensagem, destinatario);	
		}	
	}
	
	public void enviarMensagemParaTodosNaSala(Usuario remetente, String textoDaMensagem) {
		Sala sala = encontrarSalaAtualDoUsuario(remetente);
		for (Usuario destinatario : sala.getUsuariosConectados()) {
			MensagemServidor mensagem = new MensagemServidor();
			mensagem.setTipo(TipoMensagemServidor.ENTREGAR_MENSAGEM);		
			mensagem.setUsuario(remetente);
			mensagem.setSalaAtual(sala);
			mensagem.setTexto(textoDaMensagem);
			enviarMensagemAoCliente(mensagem, destinatario);	
		}
	}
	
	public ArrayList<Sala> getSalasCriadas() {
		return salasCriadas;
	}
	
	public Servidor getTela() {
		return this.tela;
	}
	
	public RSA getRSA() {
		return this.rsa;
	}
	
	public ArrayList<ComunicacaoUsuario> getDadosDeComunicacaoComUsuario() {
		return this.dadosDeComunicacaoComUsuario;
	}
}
	