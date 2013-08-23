import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

class responderThread extends Thread{
	private Hashtable<String,chatMsgHolder> ht;
	public responderThread(Hashtable<String,chatMsgHolder> ht){
		this.ht = ht;
	}
	
	public void run() {
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer;String user;Socket s;StringTokenizer strt;
		while(true){
		try {
			
			fromServer = stdIn.readLine();
			if (fromServer != null) {
				   strt = new StringTokenizer(fromServer,":");
		           user = strt.nextToken();
		           if((s=ht.get(user).getSocket())!=null){
		        	   new PrintWriter(s.getOutputStream(), true).println(strt.nextToken());
		           }
		    	
		    	
		       
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
}