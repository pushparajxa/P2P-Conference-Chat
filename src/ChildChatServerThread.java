import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

public class ChildChatServerThread extends Thread {
	private Socket socket = null;
	private Hashtable<String,chatMsgHolder> ht;
	private chatMsgHolder msgHolder;
    private String userId;
    public ChildChatServerThread(Socket socket,Hashtable<String,chatMsgHolder> ht) {
		this.socket = socket;
		this.ht = ht;
		System.out.println("Server Thread stared and my ID is "+this.getId());
    }

    public void run() {
    	
    	
	try {
	   
	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine;
	  
          int i=0;
	    while ((inputLine = in.readLine()) != null) {
		   	if(i==0){
	    		msgHolder = new chatMsgHolder(this.socket);
	    		ht.put(inputLine,msgHolder);
	    		i++;
	    		System.out.println(this.getId() + " User Name is"+ inputLine);
	    		this.userId=inputLine;
	    		msgHolder.putMsg(inputLine);
	    	}
	    	else //Put the message in the msgHolder corresponding to this user.
	    		msgHolder.putMsg(inputLine);
	    	
	    	if (inputLine.equalsIgnoreCase("Bye")){
			    ht.get(userId).setEndChat(true);
	    		break;
	    	}
		
		
	    }
	    
	    
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
