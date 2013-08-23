package management;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import de.uniba.wiai.lspi.chord.data.ID;

public class ChildRequesterThread extends Thread {
	private Socket skt;
	private childRequest chr;
	private ID end;
	private boolean keepRunning=true;
	private String requestId;
	private ID senderId;
	public ChildRequesterThread(Socket skt,childRequest chr,ID end, String requestId, ID senderId){
		this.skt = skt;
		this.chr = chr;
		this.end = end;
		this.requestId = requestId;
		this.senderId = senderId;
	}

	public void run(){

		try {
			InputStream is = skt.getInputStream();
			OutputStream os = skt.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(is); 
			
			//send message to the client
			oos.writeObject(new RequestMessage(end,this.requestId,this.senderId));
			//wait for the response.
			while(true){

				MgmtMessage msg = (MgmtMessage)ois.readObject();
				if(msg instanceof ResponseMessage)
					handleResponseMessage(msg);
				else if(msg instanceof ByeMessage){
					ois.close();
					oos.close();
					is.close();
					os.close();
					skt.close();
					chr.setCompleted(true);
					break;
				}
				else{
					System.out.println("Received message is not of any type. So returning.");
				}
					
			}

		} catch (Exception e) {

			e.printStackTrace();
		}  

	}

	private void handleResponseMessage(MgmtMessage imsg) {
		ResponseMessage msg = (ResponseMessage)imsg;
		chr.setConfIds(msg.getConfIds());
		chr.setOffLineMsgs(msg.getOffLineMsgs());
		chr.setReg_Users(msg.getReg_Users());
		chr.setNo_of_liveUsers(msg.getNo_of_liveUsers());
		//chr.setCompleted(true);
	}

}
