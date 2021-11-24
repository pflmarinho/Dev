package servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

import com.google.gson.Gson;

import modelos.*;
import util.RSA;

public class ServerHandler extends Thread {

	private Socket socketCliente;
	private Server servidor;
	private boolean executando;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Usuario usuario;
	private RSA rsa;
	
	public ServerHandler(Server servidor, Socket socketCliente) {
		this.servidor = servidor;
		this.socketCliente = socketCliente;	
		this.rsa = this.servidor.getRSA();
	}
		
	@Override
    public void run() {
        try {        	
        	inputStream = socketCliente.getInputStream();
            outputStream = socketCliente.getOutputStream();           
                            
            executando = true;
            
            while (executando) {
                int tamanhoDaMsgRecebida = inputStream.available();                
                if(tamanhoDaMsgRecebida > 0) {                	
                    byte[] buffer = new byte[tamanhoDaMsgRecebida];
                    inputStream.read(buffer);
                    
                    MensagemCliente mensagem = null;
                    String conteudoDecriptado = "";
                    String conteudoRecebido = new String(buffer);
                    try {
                    	conteudoDecriptado = rsa.decrypt(conteudoRecebido, rsa.getKeys().getPrivate());
                    	mensagem = new Gson().fromJson(conteudoDecriptado, MensagemCliente.class);
                    } catch (Exception e) {
                    	mensagem = new Gson().fromJson(conteudoRecebido, MensagemCliente.class);
                    }
                    
                    System.out.println("--- Recebida mensagem enviada por cliente ---");
                    System.out.println("Mensagem encriptada: "+(mensagem.isEncriptada() ? conteudoRecebido : conteudoRecebido.replace("\r\n", "") ));
                    System.out.println("Mensagem decriptada: "+conteudoDecriptado);
                    System.out.println("Mensagem encriptada? "+mensagem.isEncriptada());
                    System.out.println();
                    
                    try {
                    	switch(mensagem.getTipo()) {
		                    case CONECTAR:
		                    	conectar(mensagem); break;
		                    case DESCONECTAR:
		                    	desconectar(mensagem); break;
		                    case ALTERAR_STATUS:
		                    	alterarStatus(mensagem); break;
		                    case ENVIAR_MENSAGEM_PARA_TODOS_NA_SALA:
		                    	enviarMensagemParaTodosNaSala(mensagem); break;
		                    default:
		                    	break;
	                    }
                    } catch(Exception e) {
                    	this.servidor.getTela().adicionarLog("Erro: "+e.getMessage());
                    	MensagemServidor retorno = new MensagemServidor();
                    	retorno.setTipo(TipoMensagemServidor.ERRO);
                    	retorno.setTexto(e.getMessage());
                    	this.servidor.enviarMensagemAoCliente(retorno, usuario);
                    }
                }
            }
            
            socketCliente.close();

        } catch (Exception e) {
        	this.servidor.getTela().adicionarLog("Erro: "+e.getMessage());
        	
        }
    }		
	
	private void conectar(MensagemCliente mensagem) throws Exception {			
		Usuario usuarioConectado = null;
		if (mensagem.getRemetente().getNome().isEmpty()) {
			throw new Exception("Para se conectar ao servidor é necessário informar seu nome ou apelido.");
		} else {
			usuarioConectado = servidor.conectarUsuario(mensagem, socketCliente);
			this.usuario = usuarioConectado;
		}
		String chavePublica = Base64.getEncoder().encodeToString(this.servidor.getRSA().getEncodedPublicKey());
		
		MensagemServidor retorno = new MensagemServidor();
		retorno.setTipo(TipoMensagemServidor.CONEXAO_ACEITA);		
		retorno.setDestinatario(usuarioConectado);
		retorno.setSalasCriadas(this.servidor.getSalasCriadas());
		retorno.setTexto("Conexão realizada com sucesso. Seja bem-vindo ao chat.");	
		retorno.setSalaAtual(servidor.identificarSala("Geral"));
		retorno.setChavePublica(chavePublica);
		
		this.servidor.enviarMensagemAoCliente(retorno,usuarioConectado);
		this.servidor.avisarTodosSobreNovoUsuario(usuarioConectado);
	}	
	
	private void desconectar(MensagemCliente mensagem) throws Exception {
		this.servidor.desconectarUsuario(mensagem.getRemetente(), true);
		this.executando = false;
	}		
	
	private void alterarStatus(MensagemCliente mensagem) {
		usuario = mensagem.getRemetente();
		servidor.avisarTodosSobreMudancaDeStatusDeUsuario(usuario);		
	}
	
	private void enviarMensagemParaTodosNaSala(MensagemCliente mensagem) {
		servidor.enviarMensagemParaTodosNaSala(mensagem.getRemetente(), mensagem.getTexto());
	}
}
