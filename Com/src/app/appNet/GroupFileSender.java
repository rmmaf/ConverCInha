package app.appNet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import app.appGUI.GUI;
import app.appGUI.uploadPopUp;
import tcp.ALTCPSocket;
import tcp.Redirector;

public class GroupFileSender extends Thread {
	private String endereco;
	private File file;
	int porta;
	private Redirector red;
	private String user;
	public GroupFileSender (Redirector red, int porta, File file, String endereco, String user){
		this.endereco = endereco;
		this.file = file;
		this.porta = porta;
		this.red = red;
		this.user = user;
	}

	public void run(){
		try {
		
			ALTCPSocket enviarDados = red.connect(endereco, porta);	//red ï¿½ um redirector
			enviarDados.writeUTF("$+" + user);
			long tamanho = 0;
			tamanho = file.length();
			uploadPopUp popup= new uploadPopUp((int) tamanho);
			popup.setVisible(true);
			popup.setValor(0);
			
			enviarDados.writeLong(tamanho);
			String nome = file.getName();
			enviarDados.writeUTF(nome);

			FileInputStream leitura = new FileInputStream(file);//sistema de leitura do arquivo
			
			int cont = 0;
			int read;

			long timer = System.currentTimeMillis() + 1000;
			long EWMA = 0;
			long lastCont = 0;
			boolean firstRun = true;

			byte[] buffer = new byte[1420];

			while (cont < tamanho) {
				cont += (read = leitura.read(buffer));
				enviarDados.write(Arrays.copyOf(buffer, read));
				popup.setValor(cont);

				long b = System.currentTimeMillis();
				if (b > timer) {
					popup.setPackets(String.valueOf(enviarDados.packetsLost()));
					if (firstRun) {
						EWMA = cont - lastCont;
						firstRun = false;
					} else {
						EWMA = 7*(EWMA>>3) + ((cont - lastCont)>>3);
					}
					lastCont = cont;
					timer = b + 1000;
					if (EWMA != 0) {
						long l = (tamanho - cont)/(EWMA);
						if (l == 0) {
							popup.setTempo("0s Aguardando confirmação...");
						} else {
							popup.setTempo(String.valueOf((tamanho - cont)/(EWMA)) + "s");
						}
					}
				}
			}
			popup.setTempo("0s Aguardando confirmação...");
			enviarDados.waitForAck();
			enviarDados.slowClose();
			leitura.close();
			popup.setTempo("0s Confirmado");
			//popup.dispose();
		}catch (IOException | InterruptedException e) {
			e.printStackTrace();

		}
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}