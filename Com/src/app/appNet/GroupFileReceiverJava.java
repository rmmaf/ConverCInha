package app.appNet;

import tcp.ALTCPSocket;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import app.appGUI.GUI;
import app.appGUI.downloadPopUp;

public class GroupFileReceiverJava extends Thread {
	private double tempoEstimado;
	private String local;
	private String nomeArquivo;
	private long tamanho;// esta aqui por causa da barra de progresso, que
	// precisa ter um maximo setado
	downloadPopUp popUp;
	private ALTCPSocket receberDados;
	private String user;
	public GroupFileReceiverJava(ALTCPSocket receberDados, GUI gui, String user) {
		this.receberDados = receberDados;
		this.local = gui.localGrupo.getText();
		this.user = user;
	}

	synchronized public void run() {
		byte[] buffer = new byte[1420];
		try {
			tamanho = receberDados.readLong();
			popUp= new downloadPopUp((int) tamanho);
			popUp.setVisible(true);
			nomeArquivo = receberDados.readUTF();

			popUp.setName("Baixando: " + nomeArquivo);
			FileOutputStream armazenar = new FileOutputStream(local + "\\" + nomeArquivo);
			int cont = 0, read = 0;
			long timer = System.currentTimeMillis() + 1000;
			long EWMA = 0;
			long lastCont = 0;
			boolean firstRun = true;
			try {
				while (tamanho > cont) {
					read = receberDados.read(buffer);
					cont += read;
					armazenar.write(buffer, 0, read);
					long b = System.currentTimeMillis();
					if (b > timer) {
						if (firstRun) {
							EWMA = cont - lastCont;
							firstRun = false;
						} else {
							EWMA = 7 * (EWMA >> 3) + ((cont - lastCont) >> 3);
						}
						lastCont = cont;
						timer = b + 1000;
						if (EWMA != 0) {
							long l = (tamanho - cont) / (EWMA);
							if (l == 0) {
								popUp.setTempo("0s");
							} else {
								popUp.setTempo(String.valueOf((tamanho - cont) / (EWMA)) + "s");
							}
						}
					}
					popUp.setValor(cont);
				}
				popUp.setValor(tamanho);
				popUp.setTempo("0s");
				armazenar.close();
				receberDados.close();
				//popUp.dispose();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pare() {
		try {
			receberDados.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fechado");
	}

	public double getTempoEstimado() {
		return tempoEstimado;
	}

	public void setTempoEstimado(double tempoEstimado) {
		this.tempoEstimado = tempoEstimado;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public long getTamanho() {
		return tamanho;
	}

	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}

}
