package cliente;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.google.gson.Gson;

import modelos.*;
import servidor.Server;
import util.RSA;

public class ClientHandler extends Thread {

	private Socket socketComServidor;
	private Client cliente;
	private boolean executando;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Usuario usuario;	
	private RSA rsa;
	
	public ClientHandler(Client cliente, Socket socketServidor) {
		this.cliente = cliente;
		this.socketComServidor = socketServidor;
		this.rsa = cliente.getRSA();
	}
		
	@Override
    public void run() {
        try {        
        	executando = true;
        	inputStream = socketComServidor.getInputStream();
            outputStream = socketComServidor.getOutputStream();           
                            
            while (executando) {
                int tamanhoDaMsgRecebida = inputStream.available();                
                if(tamanhoDaMsgRecebida > 0) {                	
                    byte[] buffer = new byte[tamanhoDaMsgRecebida];
                    inputStream.read(buffer);
                    
                    String conteudoRecebido = new String(buffer);
                    
                    System.out.println("--- Recebida mensagem enviada pelo servidor ---");
                    System.out.println("Mensagem encriptada: "+conteudoRecebido);
                    
                    String conteudoDecriptado = rsa.decrypt(conteudoRecebido, rsa.getKeys().getPrivate());
                    
                    System.out.println("Mensagem decriptada: "+conteudoDecriptado);
                    System.out.println();
                    
                    MensagemServidor mensagem = null;
                    try {
                    	mensagem = new Gson().fromJson(conteudoDecriptado, MensagemServidor.class);
                    } catch (Exception e) {
                    	throw new Exception("Falha ao transformar a mensagem recebida em JSON.");
                    }
                    
                    switch(mensagem.getTipo()) {
                    case ERRO:
                    	exibirErro(mensagem); break;
                    case CONEXAO_ACEITA:
                    	concluirConexao(mensagem); break;
                    case SERVIDOR_DESLIGADO:
                    	desligarConexao(); break;
                    case DESCONECTADO_PELO_SERVIDOR:
                    	forcarDesconexaoPeloServidor(); break;
                    case NOVO_USUARIO_CONECTADO:
                    	atualizarListaDeUsuariosDaSala(mensagem); break;
                    case USUARIO_DESCONECTADO:
                    	atualizarListaDeUsuariosDaSala(mensagem); break;
                    case USUARIO_ALTEROU_STATUS:
                    	atualizarListaDeUsuariosDaSala(mensagem); break;
                    case ENTREGAR_MENSAGEM:
                    	exibirMensagemRecebida(mensagem); break;
                    default:
                    	break;
                    }
                }
            }
            
            socketComServidor.close();

        } catch (Exception e) {
        	this.cliente.getTela().exibirAlerta("Erro: "+e.getMessage());;
        	e.printStackTrace();
        }
    }		
	
	private void exibirErro(MensagemServidor mensagem) {
		this.cliente.getTela().exibirAlerta("Erro: "+mensagem.getTexto());
	}
	
	private void concluirConexao(MensagemServidor mensagem) {
		armazenarChavePublicaDoServidor(mensagem.getChavePublica());
		
		cliente.getTela().colocarTelaEmModoConectado();
		usuario = mensagem.getDestinatario();
		cliente.setSalaAtual(mensagem.getSalaAtual());
		cliente.getTela().limparUsuarios();
		cliente.getTela().limparSalas();
		for (Usuario usuario : mensagem.getSalaAtual().getUsuariosConectados()) {
			cliente.getTela().adicionarUsuario(usuario);
		}		
		for (Sala sala : mensagem.getSalasCriadas()) {
			cliente.getTela().adicionarSala(sala.getNome());
		}
		cliente.getTela().selecionarSala(cliente.getSalaAtual().getNome());
		cliente.getTela().adicionarOpcoesDeStatus();
		cliente.getTela().ativarEventoDoComboStatus();
		cliente.getTela().exibirAlerta(mensagem.getTexto());
	}
	
	private void armazenarChavePublicaDoServidor(String chavePublicaDoServidor) {
		try {
			byte[] chavePublicaDoServidorEmBytes = Base64.getDecoder().decode(chavePublicaDoServidor);
			PublicKey chave = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(chavePublicaDoServidorEmBytes));
			this.cliente.setChavePublicaDoServidor(chave);
		} catch (Exception e) {
			System.out.println("Falha ao armazenar a chave pública do servidor.");
			e.printStackTrace();
		} 
	}
	
	private void desligarConexao() throws IOException {
		this.cliente.getTela().colocarTelaEmModoDeAguardandoConexao();
		this.cliente.getTela().limpar();
		this.cliente.getTela().exibirAlerta("O servidor foi desligado.");		
		this.interrupt();
		this.socketComServidor.close();
	}
	
	private void forcarDesconexaoPeloServidor() throws IOException {
		this.cliente.getTela().colocarTelaEmModoDeAguardandoConexao();
		this.cliente.getTela().limpar();
		this.cliente.getTela().exibirAlerta("Você foi desconectado pelo administrador do servidor.");		
		this.interrupt();
		this.socketComServidor.close();
	}
	
	private void atualizarListaDeUsuariosDaSala(MensagemServidor mensagem) {
		if (!cliente.getSalaAtual().getNome().equals(mensagem.getSalaAtual().getNome())) {
			return;
		}		
		cliente.setSalaAtual(mensagem.getSalaAtual());
		cliente.setSalasCriadas(mensagem.getSalasCriadas());
		cliente.setUsuariosConectados(mensagem.getUsuariosConectados());
		cliente.getTela().limparUsuarios();

		for (Usuario usuario : mensagem.getSalaAtual().getUsuariosConectados()) {
			cliente.getTela().adicionarUsuario(usuario);
		}
	}
	
	private void atualizarStatusUsuario(MensagemServidor mensagem) {
		if (!cliente.getSalaAtual().getNome().equals(mensagem.getSalaAtual().getNome())) {
			return;
		}
		cliente.setSalaAtual(mensagem.getSalaAtual());
		cliente.setSalasCriadas(mensagem.getSalasCriadas());
		cliente.setUsuariosConectados(mensagem.getUsuariosConectados());
		cliente.getTela().atualizarUsuario(mensagem.getUsuario());		
	}
	
	private void exibirMensagemRecebida(MensagemServidor mensagem) {
		cliente.exibirMensagemRecebida(mensagem.getUsuario(), mensagem.getTexto());
	}
}
