package telas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import cliente.Client;
import modelos.StatusUsuario;
import modelos.Usuario;
import servidor.Server;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import util.*;
import javax.swing.JCheckBox;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.beans.PropertyChangeEvent;

public class Cliente extends JFrame {

	private JPanel contentPane;
	private JTextField txtIp;
	private JTextField txtPorta;
	private JTextField txtNovaSala;
	private JTextField txtNome;
	private JTextField txtMensagem;
	private JButton btnConectar;
	private JButton btnDesconectar;
	private Client cliente;
	private JComboBox cmbStatus;
	private JComboBox cmbSalaAtual;
	private JComboBox cmbUsuarioConvite;	
	private JButton btnCriarNovaSala;
	private JButton btnEnviarMsg;
	private JButton btnRemoverDaSala;
	private JButton btnConvidar;
	private boolean conectado;
	private JCheckBox chkSalaPrivada;
	private DefaultListModel usuariosModel;
	private DefaultListModel mensagensModel;
	private JList lstUsuarios;
	private JList lstMensagens;
	private JScrollPane scpMensagens;
	private boolean deveDispararEventoComboStatus;
	private boolean executandoCarregamentoInicial;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente frame = new Cliente();
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
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	public Cliente() {
		executandoCarregamentoInicial = true;
		setTitle("mIRC 2.0 - Cliente");
		setBounds(100, 100, 750, 620);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JFrame referenciaJanela = this;
		this.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	if (conectado) {
		    		desconectar(true);
		    	} else {
		    		referenciaJanela.dispose();
	    			System.exit(0);
		    	}
		    }
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIp = new JLabel("IP do servidor:");
		lblIp.setBounds(20, 14, 129, 14);
		contentPane.add(lblIp);
		
		txtIp = new JTextField();
		txtIp.setBounds(20, 36, 119, 20);
		contentPane.add(txtIp);
		txtIp.setColumns(10);
		
		txtPorta = new JTextField();
		txtPorta.setBounds(149, 36, 53, 20);
		contentPane.add(txtPorta);
		txtPorta.setColumns(10);
		
		JLabel lblPortaDoServidor = new JLabel("Porta:");
		lblPortaDoServidor.setBounds(149, 14, 53, 14);
		contentPane.add(lblPortaDoServidor);
		
		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				conectar();
			}
		});
		btnConectar.setBounds(20, 64, 182, 43);
		contentPane.add(btnConectar);
		
		btnDesconectar = new JButton("Desconectar");
		btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				desconectar();
			}
		});
		btnDesconectar.setBounds(212, 64, 248, 43);
		contentPane.add(btnDesconectar);
		
		JLabel lblSalaAtual = new JLabel("Sala atual:");
		lblSalaAtual.setBounds(489, 17, 64, 14);
		contentPane.add(lblSalaAtual);
		
		cmbSalaAtual = new JComboBox();
		cmbSalaAtual.setBounds(563, 14, 148, 20);
		contentPane.add(cmbSalaAtual);
		
		JLabel lblNovaSala = new JLabel("Nova sala:");
		lblNovaSala.setBounds(489, 42, 64, 14);
		contentPane.add(lblNovaSala);
		
		txtNovaSala = new JTextField();
		txtNovaSala.setBounds(563, 39, 148, 20);
		contentPane.add(txtNovaSala);
		txtNovaSala.setColumns(10);
		
		btnCriarNovaSala = new JButton("Criar nova sala");
		btnCriarNovaSala.setBounds(592, 64, 119, 43);
		btnCriarNovaSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exibirAlerta("Funcionalidade não implementada :(");
			}
		});
		contentPane.add(btnCriarNovaSala);
		
		scpMensagens = new JScrollPane();
		scpMensagens.setBounds(20, 145, 501, 319);
		contentPane.add(scpMensagens);
		
		lstMensagens = new JList();
		scpMensagens.setViewportView(lstMensagens);
		
		JScrollPane scpUsuarios = new JScrollPane();
		scpUsuarios.setBounds(545, 145, 166, 319);
		contentPane.add(scpUsuarios);
		
		lstUsuarios = new JList();
		lstUsuarios.setCellRenderer(new RenderizadorUsuario());
		scpUsuarios.setViewportView(lstUsuarios);
		
		JLabel lblMensagens = new JLabel("Mensagens:");
		lblMensagens.setBounds(20, 129, 501, 14);
		contentPane.add(lblMensagens);
		
		JLabel lblUsuarios = new JLabel("Usu\u00E1rios na sala:");
		lblUsuarios.setBounds(542, 129, 169, 14);
		contentPane.add(lblUsuarios);
		
		btnEnviarMsg = new JButton("Enviar msg");
		btnEnviarMsg.setBounds(412, 477, 109, 40);
		btnEnviarMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enviarMensagem();
			}
		});
		contentPane.add(btnEnviarMsg);
		
		btnRemoverDaSala = new JButton("Remover da sala");
		btnRemoverDaSala.setBounds(545, 475, 166, 42);
		btnRemoverDaSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exibirAlerta("Funcionalidade não implementada :(");
			}
		});
		contentPane.add(btnRemoverDaSala);
		
		cmbUsuarioConvite = new JComboBox();
		cmbUsuarioConvite.setBounds(351, 536, 170, 20);
		contentPane.add(cmbUsuarioConvite);
		
		btnConvidar = new JButton("Convidar");
		btnConvidar.setBounds(545, 536, 166, 23);
		btnConvidar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exibirAlerta("Funcionalidade não implementada :(");
			}
		});
		contentPane.add(btnConvidar);
		
		JLabel lblConvite = new JLabel("Selecione um usu\u00E1rio para convidar para a sala atual:");
		lblConvite.setBounds(20, 539, 321, 14);
		contentPane.add(lblConvite);
		
		JLabel lblNome = new JLabel("Nome:");
		lblNome.setBounds(212, 14, 98, 14);
		contentPane.add(lblNome);
		
		txtNome = new JTextField();
		txtNome.setBounds(212, 36, 119, 20);
		contentPane.add(txtNome);
		txtNome.setColumns(10);
		
		txtMensagem = new JTextField();
		txtMensagem.setBounds(20, 477, 382, 40);
		txtMensagem.addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					enviarMensagem();
				}
			}
		});
		contentPane.add(txtMensagem);
		txtMensagem.setColumns(10);		
		
		TextPrompt phMsg = new TextPrompt("Digite aqui a mensagem que deseja enviar...", txtMensagem);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(341, 14, 119, 14);
		contentPane.add(lblStatus);
		
		cmbStatus = new JComboBox();
		cmbStatus.setBounds(341, 36, 119, 20);
		contentPane.add(cmbStatus);
		
		chkSalaPrivada = new JCheckBox("Sala privada");
		chkSalaPrivada.setBounds(489, 74, 97, 23);
		contentPane.add(chkSalaPrivada);
		phMsg.setForeground( Color.GRAY );
		phMsg.changeAlpha(0.5f);
		phMsg.changeStyle(Font.BOLD + Font.ITALIC);
		//phMsg.setIcon( ... )
		
		usuariosModel = new DefaultListModel();
		mensagensModel = new DefaultListModel();
		lstUsuarios.setModel(usuariosModel);
		lstMensagens.setModel(mensagensModel);			
		
		deveDispararEventoComboStatus = false;
		
		colocarTelaEmModoDeAguardandoConexao();		
	}
	
	public void colocarTelaEmModoDeAguardandoConexao() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	txtIp.setEnabled(true);
	    		txtIp.setText("");
	    		txtPorta.setEnabled(true);
	    		txtPorta.setText("");
	    		txtNome.setEnabled(true);
	    		txtNome.setText("");
	    		
	    		btnConectar.setEnabled(true);
	    		btnDesconectar.setEnabled(false);
	    		
	    		cmbSalaAtual.removeAllItems();
	    		cmbSalaAtual.setEnabled(false);
	    		
	    		cmbUsuarioConvite.removeAllItems();
	    		cmbUsuarioConvite.setEnabled(false);
	    		
	    		txtNovaSala.setText("");
	    		txtNovaSala.setEnabled(false);
	    		
	    		btnCriarNovaSala.setEnabled(false);
	    		
	    		txtMensagem.setText("");
	    		txtMensagem.setEnabled(false);
	    			
	    		btnEnviarMsg.setEnabled(false);
	    		btnRemoverDaSala.setEnabled(false);
	    		btnConvidar.setEnabled(false);
	    		
	    		chkSalaPrivada.setEnabled(false);	
	    		
	    		limparUsuarios();
	    		mensagensModel.removeAllElements();
	    		
	    		cmbStatus.setEnabled(false);
	    		conectado = false;
	        }
	    });
	}
	
	public void colocarTelaEmModoConectado() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	txtIp.setEnabled(false);		
	    		txtPorta.setEnabled(false);	
	    		txtNome.setEnabled(false);		
	    		
	    		btnConectar.setEnabled(false);
	    		btnDesconectar.setEnabled(true);
	    				
	    		cmbStatus.setEnabled(true);			
	    		cmbSalaAtual.setEnabled(true);			
	    		cmbUsuarioConvite.setEnabled(true);
	    				
	    		txtNovaSala.setEnabled(true);		
	    		btnCriarNovaSala.setEnabled(true);			
	    		txtMensagem.setEnabled(true);
	    			
	    		btnEnviarMsg.setEnabled(true);
	    		btnRemoverDaSala.setEnabled(true);
	    		btnConvidar.setEnabled(true);
	    		
	    		chkSalaPrivada.setEnabled(true);
	    		cmbStatus.setEnabled(true);
	    		
	    		conectado = true;
	        }
	    });
	}
	
	private void conectar() {
		try {
			if (txtIp.getText().isEmpty() || txtPorta.getText().isEmpty() || txtNome.getText().isEmpty()) {
				exibirAlerta("Para conectar é necessário informar ip/porta do servidor e seu nome.");
				return;
			}
			this.cliente = new Client(this, txtIp.getText(), txtPorta.getText(), txtNome.getText());
			this.cliente.conectar();
		} catch (Exception e) {
			exibirAlerta("Erro ao iniciar: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void desconectar() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	cliente.desconectar();
	    		colocarTelaEmModoDeAguardandoConexao();
	        }
	    });
	}
	
	public void desconectar(boolean fechaCliente) {
		JFrame referencia = this;
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	    		cliente.desconectar();
	    		colocarTelaEmModoDeAguardandoConexao();
	    		if (fechaCliente) {
	    			referencia.dispose();
	    			System.exit(0);
	    		}
		    }
	    });
	}
	
	public void exibirAlerta(String mensagem) {
		JOptionPane.showMessageDialog(this, mensagem);
	}
	
	public void limparUsuarios(){
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	usuariosModel.removeAllElements();
	        	cmbUsuarioConvite.removeAllItems();
	        	cmbUsuarioConvite.addItem(" ");
	        }
	    });
	}
	
	public void repaintUsuarios() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	lstUsuarios.repaint();
	        }
	    });
	}
	
	public void adicionarUsuario(Usuario usuario){
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	usuariosModel.addElement(new ElementoListaUsuario(usuario.getNome(), usuario.getStatus()));
	        	cmbUsuarioConvite.addItem(usuario.getNome());
	        }
	    });
	}
	
	public void adicionarMensagem(String mensagem){
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	//DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
	        	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				Date date = new Date(); 		
				mensagensModel.addElement("("+dateFormat.format(date) + ") " + mensagem);
				scpMensagens.repaint();
				lstMensagens.repaint();
	        }
	    });
	}
	
	public void limparSalas(){
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	cmbSalaAtual.removeAllItems();
	        }
	    });
	}
	
	public void adicionarSala(String sala){
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	cmbSalaAtual.addItem(sala);
	        }
	    });
	}
	
	public void selecionarSala(String sala) {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	cmbSalaAtual.setSelectedItem(sala);
	        }
	    });
	}
	
	public void selecionarStatus(String status) {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	deveDispararEventoComboStatus = false;
	    		cmbStatus.setSelectedItem(status);
	    		deveDispararEventoComboStatus = true;
	        }
	    });
	}
	
	public void limparMensagens() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	mensagensModel.removeAllElements();
	        }
	    });
	}
	
	public void limparConvidados() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	cmbUsuarioConvite.removeAllItems();
	        }
	    });
	}
	
	public void limparDigitacao() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	txtMensagem.setText("");
	        }
	    });
	}	
	
	public void limpar() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	limparMensagens();
	    		limparSalas();
	    		limparUsuarios();
	    		limparConvidados();
	    		limparDigitacao();
	        }
	    });
	}
	
	private void alterarStatus() {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	if (conectado && deveDispararEventoComboStatus) {
	    			cliente.alterarStatus(cmbStatus.getSelectedItem().toString());
	    		}
	        }
	    });
	}
	
	public void atualizarUsuario(Usuario usuario) {
		SwingUtilities.invokeLater(new Runnable()
	    {
	        public void run()
	        {
	        	boolean continuaBusca = true;
	    		for (int i = 0; i < usuariosModel.size() && continuaBusca; i++) {
	    			ElementoListaUsuario elemento = (ElementoListaUsuario) usuariosModel.get(i);
	    			if (elemento.getNome().equals(usuario)) {				
	    				usuariosModel.set(i, new ElementoListaUsuario(usuario.getNome(), usuario.getStatus()));
	    				continuaBusca = false;
	    			}
	    		}
	        }
	    });			
	}
	
	public void ativarEventoDoComboStatus() {
		if (!executandoCarregamentoInicial) return;
		
		cmbStatus.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	alterarStatus();
		    }
		});
		
		deveDispararEventoComboStatus = true;
		executandoCarregamentoInicial = false;
	}
	
	public void adicionarOpcoesDeStatus() {
		if (!executandoCarregamentoInicial) return;
		
		cmbStatus.addItem("Disponível");
		cmbStatus.addItem("Ocupado");
		cmbStatus.addItem("Ausente");
		cmbStatus.setSelectedItem("Disponível");
	}
	
	public void enviarMensagem() {
		cliente.enviarMensagemParaTodosNaSala(txtMensagem.getText());
		txtMensagem.setText("");
	}
	
	class ElementoListaUsuario
	{
	   private String nome;
	   private File icone;
	   private String tooltip;
	  
	   public ElementoListaUsuario(String nome, StatusUsuario status) {
	      this.nome = nome;
	      switch (status) {
			case DISPONIVEL:
				icone = new File("icones/disponivel.ico");
				tooltip = nome + " está disponível";
				break;
			case OCUPADO:
				icone = new File("icones/ocupado.ico");
				tooltip = nome + " está ocupado";
				break;
			case AUSENTE:
				icone = new File("icones/ausente.ico");
				tooltip = nome + " está ausente";
				break;
			default:
				break;
			}

	   }	  
	   public String getNome() {
	      return nome;
	   }	  
	   public File getIcone() {
	      return icone;
	   }	  
	   public String toString() {
	      return nome;
	   }
	   public String getTooltip() {
		   return tooltip;
	   }	   
	}
	  
	class RenderizadorUsuario extends DefaultListCellRenderer {
	    private static final long serialVersionUID = -7799441088157759804L;
	    private JLabel label;
	    private FileSystemView fileSystem;
	    private Color textSelectionColor = Color.WHITE;
	    private Color backgroundSelectionColor = Color.BLUE;
	    private Color textNonSelectionColor = Color.BLACK;
	    private Color backgroundNonSelectionColor = Color.WHITE;
	    RenderizadorUsuario() {
	        label = new JLabel();
	        label.setOpaque(true);
	        fileSystem = FileSystemView.getFileSystemView();
	    }

	    @Override
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean selected,
	            boolean expanded) {	    	
	        ElementoListaUsuario usuario = (ElementoListaUsuario)value;
	        label.setIcon(fileSystem.getSystemIcon(usuario.getIcone()));
	        label.setText(usuario.getNome());
	        label.setToolTipText(usuario.getTooltip());
	        if (selected) {
	            label.setBackground(backgroundSelectionColor);
	            label.setForeground(textSelectionColor);
	        } else {
	            label.setBackground(backgroundNonSelectionColor);
	            label.setForeground(textNonSelectionColor);
	        }

	        return label;
	    }
	}	
}
