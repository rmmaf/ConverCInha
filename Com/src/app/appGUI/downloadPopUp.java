package app.appGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class downloadPopUp extends JFrame {

	private JPanel contentPane;
	public JLabel nomeArquivo = new JLabel("");
	JProgressBar progressBar = new JProgressBar();
	JLabel tempoEstimado = new JLabel("");
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public downloadPopUp(int tamanho) {
		setBounds(100, 100, 450, 136);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(nomeArquivo, BorderLayout.NORTH);
		
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, BorderLayout.CENTER);
		
		progressBar.setMaximum(tamanho);
		
		contentPane.add(tempoEstimado, BorderLayout.SOUTH);
	}
	
	public void setName(String text){
		this.nomeArquivo.setText(text);
	}
	public void setValor(long recebido){
		this.progressBar.setValue((int) recebido);
	}
	
	public void setTempo(String text){
		this.tempoEstimado.setText(text);
	}

}
