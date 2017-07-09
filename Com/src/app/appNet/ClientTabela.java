package app.appNet;


import javax.swing.JFrame;
import javax.swing.JTextPane;
import tcp.ALTCPSocket;
import tcp.Redirector;
/*A tebela*/
public class ClientTabela extends Thread {
	private int porta; //porta para o servidor se conectar
	JTextPane tabelaMostrar;
	private volatile boolean stop;
	private String perda;
	JFrame frame;
	public ClientTabela(int porta, JTextPane tabelaMostrar, String perda,JFrame frame) {
		this.frame=frame;
		this.porta = porta;
		stop = true;
		this.tabelaMostrar = tabelaMostrar;
		this.perda = perda;
	}

	synchronized public void run(){
		String tabela;
		try {
			Redirector redi = new Redirector(porta);
			ALTCPSocket receberTabela  = redi.accept();
			if(!perda.equals("")){
				redi.setLossChance(Integer.parseInt(perda));
				receberTabela.setForcedLossChance(Integer.parseInt(perda));
			}
			String array[];
			while(stop) {
				tabela = receberTabela.readUTF();//leu string
				array = tabela.split("#");
				for(int cont = 0; cont < array.length; cont++){
					tabelaMostrar.setText(tabelaMostrar.getText() + array[cont] + "\n");
				}
				
			}
			receberTabela.close();
			redi.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//esperando
	}
	synchronized public void pare(){
		stop = false;
	}
}
