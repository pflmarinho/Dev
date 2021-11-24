package telas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import modelos.ComunicacaoUsuario;
import modelos.Usuario;
import servidor.Server;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class Servidor extends JFrame {

	private JPanel contentPane;
	private JTextField portaTxt;
	private DefaultListModel logsModel;
	private JList logList;
	private DefaultListModel usuariosModel;
	private Server servidor;
	private JScrollPane logScrollPane;
	private JScrollPane usuariosScrollPane;
	private JButton btnIniciar;
	private JButton btnDesligar;
	private JButton btnDesconectarUsuario;
	private ArrayList<Object[]> indicesDosUsuariosNaLista;
	private JList usuariosList;
	private boolean iniciado;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servidor frame = new Servidor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Servidor() {
		setTitle("mIRC 2.0 - Servidor");
		usuariosModel = new DefaultListModel();
		logsModel = new DefaultListModel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 620);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		this.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	if (iniciado) {
		    		desligar();
		    	}		    	
		    }
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPorta.setBounds(10, 24, 46, 20);
		contentPane.add(lblPorta);
		
		portaTxt = new JTextField();
		portaTxt.setBounds(66, 11, 55, 51);
		contentPane.add(portaTxt);
		portaTxt.setColumns(10);
		
		btnIniciar = new JButton("Iniciar");
		btnIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				iniciar();
			}
		});
		btnIniciar.setBounds(126, 11, 144, 51);
		contentPane.add(btnIniciar);
		
		btnDesligar = new JButton("Desligar");
		btnDesligar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				desligar();
			}
		});
		btnDesligar.setBounds(280, 11, 144, 51);
		contentPane.add(btnDesligar);
		
		logScrollPane = new JScrollPane();
		logScrollPane.setBounds(10, 81, 414, 490);
		contentPane.add(logScrollPane);
		
		logList = new JList();
		logScrollPane.setViewportView(logList);
		
		usuariosScrollPane = new JScrollPane();
		usuariosScrollPane.setBounds(460, 81, 264, 490);
		contentPane.add(usuariosScrollPane);
		
		usuariosList = new JList();
		usuariosScrollPane.setViewportView(usuariosList);
		
		JLabel lblLogs = new JLabel("Logs:");
		lblLogs.setBounds(10, 66, 46, 14);
		contentPane.add(lblLogs);
		
		JLabel lblNewLabel = new JLabel("Usu\u00E1rios conectados:");
		lblNewLabel.setBounds(460, 66, 160, 14);
		contentPane.add(lblNewLabel);
		
		logList.setModel(logsModel);
		usuariosList.setModel(usuariosModel);			
		
		btnDesconectarUsuario = new JButton("Desconectar usu\u00E1rio");
		btnDesconectarUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				desconectarUsuario();
			}
		});
		btnDesconectarUsuario.setBounds(460, 11, 264, 51);
		contentPane.add(btnDesconectarUsuario);
		
		colocarTelaEmModoAguardandoInicio();		
		indicesDosUsuariosNaLista = new ArrayList<Object[]>();
		usuariosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
	public void colocarTelaEmModoAguardandoInicio() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	portaTxt.setEnabled(true);
	    		btnIniciar.setEnabled(true);
	    		btnDesligar.setEnabled(false);
	    		btnDesconectarUsuario.setEnabled(false);
	    		iniciado = false;
	        }
	    });
	}
	
	public void colocarTelaEmModoIniciado() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	portaTxt.setEnabled(false);
	    		btnIniciar.setEnabled(false);
	    		btnDesligar.setEnabled(true);
	    		btnDesconectarUsuario.setEnabled(true);
	    		iniciado = true;
	        }
	    });
	}
	
	public void adicionarLog(String mensagem) {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
				Date date = new Date(); 		
				logsModel.addElement("("+dateFormat.format(date) + ") " + mensagem);
				logScrollPane.repaint();
				logList.repaint();
	        }
	    });
	}
	
	public void adicionarUsuario(String texto, Usuario usuario) {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	Object[] indice = new Object[2];
	    		indice[0] = indicesDosUsuariosNaLista.size();
	    		indice[1] = usuario;
	    		indicesDosUsuariosNaLista.add(indice);
	    		usuariosModel.addElement(texto);
	        }
	    });
	}
		
	public void atualizarUsuarios() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	usuariosModel.removeAllElements();
	    		indicesDosUsuariosNaLista = new ArrayList<Object[]>();
	    		ArrayList<ComunicacaoUsuario> comunicacaoUsuarios = servidor.getDadosDeComunicacaoComUsuario();
	    		for (ComunicacaoUsuario comunicacaoUsuario : comunicacaoUsuarios) {
	    			String texto = comunicacaoUsuario.getUsuario().getNome() + " IP: "+comunicacaoUsuario.getSocket().getInetAddress().getHostAddress();
	    		    adicionarUsuario(texto, comunicacaoUsuario.getUsuario());	
				}
	        }
	    });
	}
	
	public void exibirAlerta(String mensagem) {
		Servidor referenciaTela = this;
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	JOptionPane.showMessageDialog(referenciaTela, mensagem);
	        }
	    });
	}
	
	private void iniciar() {
		Servidor referenciaTela = this;
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	logsModel.removeAllElements();
	    		usuariosModel.removeAllElements();
	    		
	    		try {
	    			servidor = new Server(referenciaTela, portaTxt.getText());
	    			servidor.start();
	    		} catch (Exception e) {
	    			exibirAlerta("Erro ao iniciar: "+e.getMessage());
	    			e.printStackTrace();
	    		}
	        }
	    });
	}
	
	private void desligar() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	servidor.desligar();
	    		usuariosModel.removeAllElements();
	    		adicionarLog("Servidor desligado.");
	        }
	    });
	}
	
	private void desconectarUsuario() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	int[] usuariosSelecionados = usuariosList.getSelectedIndices();
	    		if (usuariosSelecionados.length == 0) {
	    			exibirAlerta("É necessário selecionar pelo menos um usuário a ser desconectado do servidor.");
	    			return;
	    		}
	    		ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
	    		for (int i = 0; i < usuariosSelecionados.length; i++) {
	    			for (Object[] indiceUsuario : indicesDosUsuariosNaLista) {
	    				int indice = (int)indiceUsuario[0];
	    				Usuario usuario = (Usuario)indiceUsuario[1];
	    				if (usuariosSelecionados[i] == indice) {
	    					usuarios.add(usuario);
	    				}
	    			}
	    		}
	    		
	    		servidor.desconectarUsuarios(usuarios);
	        }
	    });
	}
}
