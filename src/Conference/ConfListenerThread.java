package Conference;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class ConfListenerThread extends Thread{

	private ServerSocket sckt;
	private Hashtable<String,Conference> confTable ;
	private boolean listening=true;
	private EndPointContainer epc;
	private ID nodeHash;
	private String userId;
	private Hashtable<String,OutSocket> scktTable;
	private ChordImpl chord;
	public ConfListenerThread(ServerSocket sckt,Hashtable<String,Conference> confTable,EndPointContainer epc,ID id,String user,Hashtable<String,OutSocket> scktTable,ChordImpl root){
		this.sckt = sckt;
		this.confTable = confTable;
		this.epc = epc;
		this.userId = user;
		this.scktTable = scktTable;
		this.chord = root;
		this.nodeHash=id;
	}

	public void run(){
		Socket s;

		while(listening){
			try {
				s = sckt.accept();
				new ChildConfListenerThread(s,confTable,epc,this.nodeHash,this.userId,this.scktTable,this.chord).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in accepting conference connection from Main Conferece Thread");
				e.printStackTrace();
			}
		}
	}
	public void setListening(boolean flag){
		this.listening = flag;
	}

}