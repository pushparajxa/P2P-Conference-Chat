import java.net.Socket;
import java.util.Vector;

public class chatMsgHolder{
	private Socket sckt;
	private Vector<String> sessionMsgs = new Vector<String>(10,10);//initial capacity 10. Capacity increment is 10.
	private Vector<String> msgs = new Vector<String>(10,10);
	private boolean endChat=false;
	public chatMsgHolder(Socket s){
		this.sckt=s;
	}
	
	public boolean anyNewMsg(){
		return msgs.size() > 0;//return if there are messages which are unread;
	}
	public synchronized Vector<String>  getMsgs(){
		//add all the messages to the session Messages list.
		sessionMsgs.addAll(msgs);
		 Vector<String> msg = new Vector<String>(msgs);
		 //clear the messages in the msgs Vector to add new ones later.
		 msgs.clear();
		return msg;
		
	}
	public synchronized void putMsg(String msg){
		msgs.add(msg);
	}
	
	public Socket getSocket(){
		return sckt;
	}
	public void setScoket(Socket s){
		this.sckt = s;
	}
	public boolean isEndChat() {
		return endChat;
	}
	public void setEndChat(boolean endChat) {
		this.endChat = endChat;
	}
	
}