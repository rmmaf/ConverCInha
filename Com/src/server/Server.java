package server;

import java.io.IOException;
import java.net.InetAddress;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import tcp.ALTCPSocket;
import tcp.Broadcaster;
import tcp.Redirector;

public class Server extends Thread{
	int portIndex=2; //Indice na tabela para a porta
	int ipIndex=0; //Indice na tabela para o ip
	int usernameIndex=1; //Indice na tabela para o username
	int length=0; //tamanho da tabela
	int port; //porta do servidor
	String ip; //nome do ip do servidor
	String contactList="";  //string que ser� enviada para os usu�rios
	int lost;
	//Partes da GUI que ser�o modificadas
	JFrame it;
	JTextField field; 
	JTextField field2;
	JLabel pacotes;
	int taxa; //taxa de perda de pacotes;
	JButton start;
	//
	InetAddress address; //endere�o do servidor
	JTable jtab;
	String[] columns;
	
	public Server(JFrame it,int port, String ip, JTextField field,JButton start,JTable jtab,String[] columns,JTextField perda, int taxa,JLabel pacotes){
		this.it=it;
		this.port=port;
		this.ip=ip;
		this.field=field;
		this.start=start;
		this.jtab=jtab;
		this.columns=columns;
		this.taxa=taxa;
		this.pacotes=pacotes;
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Não foi possível identificar o IP","", JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("deprecation")
	public void run(){
		try{
			(new setLost()).start();
			Redirector redi = new Redirector(port);
			new Broadcaster(redi, port);
			redi.setLossChance(taxa);
			System.out.println("Esperando");
			ALTCPSocket socket = null;
			while(true){
				String newContact="";
				try {
					socket = redi.accept();
					System.out.println("Recebi");
					newContact = socket.readUTF();
				} catch (InterruptedException e) {
					JOptionPane.showMessageDialog(null,"Desconectado","", JOptionPane.ERROR_MESSAGE);
				}
				addContact(newContact, ""+socket.getIP(),socket.getPort());
				if(!contactList.equals("")){
					contactList+=("#"+socket.getIP()+" "+socket.getPort()+" "+newContact);
				}else{
					contactList=socket.getIP()+" "+socket.getPort()+" "+newContact;
				}

				(new sendContactList(""+socket.getIP(),newContact, socket,redi)).start();
			}
		}catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Porta Ocupada","", JOptionPane.ERROR_MESSAGE);
			field.setEnabled(true);
			field.setEditable(true);
			field2.setEnabled(true);
			field2.setEditable(true);
			start.setEnabled(true);
			for(int i=0;i<length;i++){
				for(int j=0;j<5;j++){
					jtab.setValueAt("", i, j);
				}
			}
			this.stop();
		}
	}
	
	//metodo de envio de arquivo
	synchronized public void addContact(String newContact, String address,int port){
		int index=length;
		int samename=1;
		for(int i=0;i<length;i++){
			if(jtab.getValueAt(i, ipIndex).equals(address)){
				if(jtab.getValueAt(i, 4).equals("Não")){
					if(index==length){
						index=i;
					}else{
						for(int j=i+1;j<length;j++){
							jtab.setValueAt(jtab.getValueAt(j-1,0),j, 0);
							jtab.setValueAt(jtab.getValueAt(j-1,1),j, 1);
							jtab.setValueAt(jtab.getValueAt(j-1,2),j, 2);
							jtab.setValueAt(jtab.getValueAt(j-1,3),j, 3);
							jtab.setValueAt(jtab.getValueAt(j-1,4),j, 4);
						}
						length--;
					}
				}
				if(jtab.getValueAt(i,usernameIndex)==newContact){
					newContact=newContact+"("+samename+")";
				}
			}
		}
		jtab.setValueAt(newContact,index, usernameIndex);
		jtab.setValueAt(address,index, ipIndex);
		jtab.setValueAt(port,index, portIndex);

		Date data= new Date(System.currentTimeMillis()-10000000);
		DateFormat df= DateFormat.getInstance();
		jtab.setValueAt((""+df.format(data)),index, 3);

		jtab.setValueAt("Sim",index, 4);
		if(index==length)
			length++;
	}
	
	//envio da lista de contatos
		public class sendContactList extends Thread{
			String username;
			String ad;
			InetAddress address;
			Redirector r;
			ALTCPSocket soquete;
			public sendContactList(Object address, Object username, ALTCPSocket socket, Redirector r){
				try {
					this.username=(String) username;
					this.soquete=socket;
					this.ad=(String) address;
					String [] array= ad.split("/");
					this.address=InetAddress.getByName(array[array.length-1]);
					this.r=r;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			public void run(){
			int packets=0;
			try {
				while(true){
						Thread.sleep(2000);
						//JOptionPane.showMessageDialog(null, "Enviando Lista de Contatos","",JOptionPane.ERROR_MESSAGE);
						System.out.println(contactList);
						soquete.writeUTF(contactList);//envia array de bytes
						System.out.println("Esperando o ACK");
						soquete.waitForAck();
						System.out.println("Passou do ACK");
						lost+=soquete.packetsLost()-packets;
						packets=soquete.packetsLost();
				}
			} catch (IOException e) {
				System.out.println("REMOVER");
				JOptionPane.showMessageDialog(it, "Usuário " +username+" Removido","",JOptionPane.ERROR_MESSAGE);
				removeContact(ad,username);
			} catch(InterruptedException e){
				
			}
			}
		}
		synchronized public void removeContact(String address,String username){
			Boolean remover=false;
			int index=-1;
			for(int i=0; i<length;i++){
				/*if(remover&&i!=length-1){
					table[i]=table[i+1];
				}
				else if(remover&&i==length-1){
					table[i]=new Object[5];
				}
				else*/ if(jtab.getValueAt(i, 0).equals(address)&&jtab.getValueAt(i, 1).equals(username)){
					jtab.setValueAt("N�o",i, 4);
					remover=true;
					index=i;
				}
			}
			if(index!=-1){
				//setTable(table,contactList);
				//length--;
				setContactList(contactList);
			}
		}
		synchronized public void setContactList(String contactList){
			String newie="";
			for(int i=0;i<length;i++){
				if(jtab.getValueAt(i, 4).equals("Sim")){
					newie+=jtab.getValueAt(i, ipIndex)+" "+jtab.getValueAt(i, portIndex)+" "+jtab.getValueAt(i, usernameIndex);
				if(i!=(length-1)){
					newie+="#";
				}
				}
			}
			this.contactList=newie;
			System.out.println(newie);
		}
		synchronized public void setTable(Object[][] contacts, String contactList){
			for(int i=0;i<jtab.getRowCount();i++){
					for(int j=0;j<5;j++){
						if(i<length){
							jtab.setValueAt(contacts[i][j], i, j);
						}else{
							jtab.setValueAt("", i, j);
						}
					}
				}
		}
		public class setLost extends Thread{
			public void run(){
				while(true){
					pacotes.setText(""+lost);
				}
			}
		}
}