package app.appNet;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import app.appGUI.GUI;
import tcp.ALTCPSocket;
import tcp.Redirector;
/* Esta Thread � a Thread de in�cio para estabelecer uma conex�o com o servido (informando o nome de usuario, IP e porta)
 */
public class ClienteServer extends Thread{
	private String ip;
	private String username;
	private int porta;
	private String clientIP;
	private JTextPane tabelaMostrar;
	private String perda;
	private boolean stop;
	private Redirector redi;
	private GUI gui;
	
	public ClienteServer(Redirector redi, String ip, String clientIP, String username, int porta, JTextPane tabelaMostrar, String perda, GUI gui){//IP do servidor e porta do servidor (o cliente e servidor funcionar�o na mesma porta)
		this.redi =redi;
		this.ip = ip;
		this.username = username;
		this.porta = porta;
		this.clientIP = clientIP;
		this.tabelaMostrar = tabelaMostrar;
		this.perda = perda;
		this.gui = gui;
		stop = true;
	}

	public void run(){
		String tabela;
		String envioDados = username;
		try {
			ALTCPSocket controle = redi.connect(ip, porta);
			controle.writeUTF(envioDados);
			
			if(!perda.equals("")){
				redi.setLossChance(Integer.parseInt(perda));
				controle.setForcedLossChance(Integer.parseInt(perda));
			}
			String array[];
			while(stop) {
				tabela = controle.readUTF().replace("127.0.0.1", controle.getIP().getHostAddress());//leu string
				array = tabela.split("#");
				tabelaMostrar.setText("");
				String[] array2;
				synchronized(gui.contactList){
					gui.contactList = new String[array.length][2];
					for (int cont = 0; cont < array.length; cont++) {
						tabelaMostrar.setText(tabelaMostrar.getText() + array[cont] + "\n");
						array2 = array[cont].split(" ");
						gui.contactList[cont][0] = array2[0] + " " + array2[1];
						gui.contactList[cont][1] = array2[2];
					}
				}
			}
			/*controle.slowClose();
			redi.close();*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Servidor Inválido ou bateu as botas", "", JOptionPane.ERROR_MESSAGE);
		}

	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
}
