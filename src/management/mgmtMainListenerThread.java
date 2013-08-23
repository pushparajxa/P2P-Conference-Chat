package management;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import Conference.Conference;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class mgmtMainListenerThread extends Thread{
	private ServerSocket skt;
	private ChordImpl chord;
	private boolean keepListening = true;
	private Hashtable<String,Conference> confTable;
	private Vector<String> servedRequests;
	public mgmtMainListenerThread(ServerSocket skt,ChordImpl chord,Hashtable<String,Conference> confTable){
		this.skt = skt;
		this.chord = chord;
		this.confTable = confTable;
		this.servedRequests =  new Vector<String>();
	}
	public void run(){
		while(keepListening){
		try {
			Socket 	s = skt.accept();
			new mgmtChildListenerThread(chord,s,this.confTable,servedRequests).start();
		} catch (Exception e) {
			System.out.println("Exception while accepting the sockets in MgmtMainListener Thread");
			e.printStackTrace();
		}
		}
	}
	public boolean isKeepListening() {
		return keepListening;
	}
	public void setKeepListening(boolean keepListening) {
		this.keepListening = keepListening;
	}


}