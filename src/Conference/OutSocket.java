package Conference;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class OutSocket{
	private Socket sckt;	
	private String userId;
	private ObjectOutputStream oos ;
	private ArrayList<String>ConfIds = new ArrayList<String>();
	public OutSocket(Socket sckt,String user){
		this.sckt =sckt;
		this.userId =user;
		try {
			this.oos =  new ObjectOutputStream(sckt.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList<String> getConfIds() {
		return ConfIds;
	}
	public void addConfId(String confId) {
		ConfIds.add(confId);
	}
	
	public  synchronized  void sendMsg(Message msg){
		try {
			oos.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void removeConfId(String confId) {
		this.ConfIds.remove(confId);
		
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void close() {
		try {
			oos.close();
			sckt.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public boolean isAlive() {
		// TODO Auto-generated method stub
		try {
			oos.writeObject(new Message());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}