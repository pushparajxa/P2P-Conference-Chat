import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class MainChatServerThread extends Thread{
	private Hashtable<String, chatMsgHolder> ht;
	private boolean listening = true;
	private ServerSocket serverSocket;
	public MainChatServerThread(ServerSocket chatServerSocket, Hashtable<String, chatMsgHolder> ht){
		this.serverSocket = chatServerSocket;
		this.ht = ht;
	}
	public void run(){
		
			    
	    Socket s;
	     
	    //new responderThread(ht).start();
	    try{
	    while (listening){
	    	try {
				s = serverSocket.accept();
				new ChildChatServerThread(s,ht).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in accepting chat connections");
				e.printStackTrace();
			}
	    	
		   Thread.sleep(1000);     
	    }
	    }
		catch(InterruptedException e){
			listening = false;
		}
	       

	    try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in closing the server socket");
			e.printStackTrace();
		}

		
	}
	
}