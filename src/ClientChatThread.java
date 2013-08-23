import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

public class ClientChatThread extends Thread{
	private Hashtable<String,chatMsgHolder> ht;
	private chatMsgHolder msgHolder;
	private Socket sckt;
	private String user_Id;
	PrintWriter out = null;
    BufferedReader in = null;
	public ClientChatThread(Socket sckt, String user_Id,Hashtable<String,chatMsgHolder> ht){
		this.sckt =sckt;
		this.user_Id = user_Id;
		this.ht = ht;
	}
	public void run(){
		String fromServer;
		try {
			in = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException while opening steams with the user "+user_Id);
			e.printStackTrace();
		}
      
		msgHolder = new chatMsgHolder(sckt);
		ht.put(user_Id,msgHolder);
		
		try {
			while ((fromServer = in.readLine()) != null) {
				if(fromServer.equalsIgnoreCase("Bye")){
					  msgHolder.putMsg(fromServer);
					  msgHolder.setEndChat(true);
					  sckt.close();
					  break;
				}
				else
			    msgHolder.putMsg(fromServer);
			  
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error while reading from the sokcet with user "+user_Id);
			e.printStackTrace();
		}
	}
}