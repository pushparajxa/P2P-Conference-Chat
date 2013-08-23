package Conference;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class ChildConfListenerThread extends Thread{
	private Hashtable<String, Conference> confTable;
	private Socket sckt;
	private EndPointContainer epc;
	private ID nodeHash;
	private String userId;
	private Hashtable<String,OutSocket> scktTable;
	private ChordImpl chord;
	private String threadInitiator;
	public ChildConfListenerThread(Socket s, Hashtable<String, Conference> confTable,EndPointContainer epc,ID id,String user,Hashtable<String,OutSocket> scktTable,ChordImpl chord){
		this.sckt = s;
		this.confTable = confTable;
		this.epc = epc;
		this.nodeHash = id;
		this.userId = user;
		this.scktTable  = scktTable;
		this.chord = chord;
		System.out.println("Conference connection received");
	}
	public void run(){
		
		try {
			InputStream is = sckt.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			while(true){
				Message msg = (Message)ois.readObject();
				if(msg instanceof InviteMessage){
					handleInviteMessgae(msg);
				}
				else if(msg instanceof InvitedMessage){
					handleInvitedMessage(msg);
				}
				else if(msg instanceof TextMessage){
					handleTextMessage(msg);
				}
				else if(msg instanceof ByeMessage){
					handleByeMessage(msg);
					//break;
				}
				else{
					System.out.println("The received message is not of any interested types.The message is "+msg.getMessageName());
				}
			}
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exiting childConfListenerThread initiated by "+this.threadInitiator);
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exiting childConfListenerThread initiated by "+this.threadInitiator);
			return;
		} 
		
	}
	private void handleByeMessage(Message Imsg) {
		ByeMessage msg = (ByeMessage)Imsg;
		System.out.println("Handling Bye Message");
		if(this.confTable.get(msg.getConfId())==null){
			System.out.println("Received a bye message for conference to which I have not joined.I am returning");
			return;
		}
		else{
			this.confTable.get(msg.getConfId()).putMessage(msg.getUserId() + " is leaving the conference");
			int result =this.confTable.get(msg.getConfId()).getFingerTable().removeUser(msg.getUserId(), msg.getUserHash(),this.chord);
			if(result==0){
				System.out.println("Removal of user is not successful. Returning.");
				return;//remove is not successfull;
			}
				
			
		}
		
		Hashtable<FingerEntry,Hashtable<String,ID>> fht = new Hashtable<FingerEntry,Hashtable<String,ID>>();
		for(String usr : msg.getUsersAndHashes().keySet()){
			System.out.println("Handling ByeMessage ..Processing user "+usr);
			if(!usr.equals(this.userId)){
				FingerEntry fe = confTable.get(msg.getConfId()).getFingerTable().getFingerEntryfor(msg.getUsersAndHashes().get(usr));
				if(fe!=null){
					System.out.println("Finger entry for user "+usr+" is index= "+fe.getIndex()+" and socket user is "+fe.getSocketUser());
					if(fht.get(fe)==null){
						fht.put(fe, new Hashtable<String,ID>());
						fht.get(fe).put(usr, msg.getUsersAndHashes().get(usr));
					}
					else{
						fht.get(fe).put(usr, msg.getUsersAndHashes().get(usr));
					}
				}
				
				
			}
			
		}
		
		//Send messages to the fingerEntries.
		for(FingerEntry fe: fht.keySet()){
			if(fht.get(fe).size()!=0){
				if(fe.getSocket()!=null){
					ByeMessage img  = (ByeMessage)msg.clone();
					img.setUsersAndHashes(fht.get(fe));
					fe.getSocket().sendMsg(img);
					System.out.println("Handling  Bye Message::Sent Message on Finger with index= "+fe.getIndex());
				}
			}
				
			
		}
		
	}
	private void handleTextMessage(Message Imsg) {
		System.out.println("Handling Text Message");
		TextMessage msg = (TextMessage)Imsg;
		System.out.println("Handling Text Message::Count="+msg.getUsersAndHashes().size());
		
		//put the message in the respective conference.
		if(this.confTable.get(msg.getConfId())==null){
			//this.confTable.get(msg.getConfId()).putMessage(msg.getMsg());
			//System.out.println("Could not found the coference for the given confId in the receive text message. Returning ");
			return;
		}
		this.confTable.get(msg.getConfId()).putMessage(msg.getMsg());
		Hashtable<FingerEntry,Hashtable<String,ID>> fht = new Hashtable<FingerEntry,Hashtable<String,ID>>();
		for(String usr : msg.getUsersAndHashes().keySet()){
			System.out.println("Handling TextMessage ..Processing user "+usr);
			if(!usr.equals(this.userId)){
				FingerEntry fe = confTable.get(msg.getConfId()).getFingerTable().getFingerEntryfor(msg.getUsersAndHashes().get(usr));
				if(fe!=null){
					if(fe.getIdsAndHashes().containsKey(usr)){
						System.out.println("Finger entry for user "+usr+" is index= "+fe.getIndex()+" and socket user is "+fe.getSocketUser());
						if(fht.get(fe)==null){
							fht.put(fe, new Hashtable<String,ID>());
							fht.get(fe).put(usr, msg.getUsersAndHashes().get(usr));
						}
						else{
							fht.get(fe).put(usr, msg.getUsersAndHashes().get(usr));
						}
					}
					
				}
				
				
			}
			
		}
		
		//Send messages to the fingerEntries.
		for(FingerEntry fe: fht.keySet()){
			if(fe!=null){
				if(fht.get(fe).size()!=0){
					if(fe.getSocket()!=null){
						TextMessage img  = (TextMessage)msg.clone();
						img.setUsersAndHashes(fht.get(fe));
						if(fe.getSocket().isAlive()==false){
							img.getUsersAndHashes().remove(fe.getSocketUser());
							int result = confTable.get(img.getConfId()).getFingerTable().removeUser(fe.getSocketUser(), fe.getSocketUserHash(), this.chord);
							if(result==0 || fe.getSocket()==null){
								//do nothing.
							}
							else{
								//img.getUserHases().remove(fe.getIdsAndHashes().get(fe.getSocketUser()));
								//img.getUsersAndHashes().remove(fe.getSocketUser());
								//img.getUserIds().remove(fe.getSocketUser());
								fe.getSocket().sendMsg(img);
								System.out.println("Handling Text Message::Sent Message on Finger with index= "+fe.getIndex());
							}
							
						}
						else{
							fe.getSocket().sendMsg(img);
							System.out.println("Handling Text Message::Sent Message on Finger with index= "+fe.getIndex());
						}
						
					}
				}
			}
			
				
			
		}
	}
	private void handleInvitedMessage(Message Imsg) {
		try{
			//You receive this message when someone in the conference invites a new user.
			System.out.println("Handling Invited Message");
			InvitedMessage msg =(InvitedMessage)Imsg;
			//add the newly added user in the conference to the fingerTable.
			int result = confTable.get(msg.getConfId()).getFingerTable().addUser(msg.getUser(), msg.getUserID(), this.chord, msg.getConfId());
			//Put the message in the conference Messages list if it was not added previously.
			if(result!=0)
			confTable.get(msg.getConfId()).putMessage("User with id "+msg.getUser()+" has been added to the Conference");
			
			System.out.println("Handling Invited Message:: The result of adding invited user="+msg.getUser()+" is ="+result);
			//Gather the messages to send.
			Hashtable<FingerEntry,Hashtable<String,ID>> fht = new Hashtable<FingerEntry,Hashtable<String,ID>>();
			for(String usr : msg.getUsersAndHashes().keySet()){
				//Do not add your entry in forwarding message.
				if(!usr.equals(this.userId)){
					FingerEntry fe = confTable.get(msg.getConfId()).getFingerTable().getFingerEntryfor(msg.getUsersAndHashes().get(usr));
					if(fe!=null){
						if(fht.get(fe)==null){
							fht.put(fe, new Hashtable<String,ID>());
							fht.get(fe).put(usr, msg.getUsersAndHashes().get(usr));
						}
						else{
							fht.get(fe).put(usr, msg.getUsersAndHashes().get(usr));
						}
						
					}
					
				}
				
			}
			
			//Send messages to the fingerEntries.
			for(FingerEntry fe: fht.keySet()){
				if(fht.get(fe).size()!=0){
					if(fe.getSocket()!=null){
						InvitedMessage img  = (InvitedMessage)msg.clone();
						img.setUsersAndHashes(fht.get(fe));
						fe.getSocket().sendMsg(img);
					}
				}
					
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	
	}
	private void handleInviteMessgae(Message Imsg) {
		System.out.println("Handling Invite Message");
		
		InviteMessage msg = (InviteMessage)Imsg;
		this.threadInitiator = msg.getInviter();
		if(this.confTable.get(msg.getConfId())==null){
			confTable.put(msg.getConfId(), new Conference(msg.getConfId(),epc,false,this.nodeHash,this.userId,this.scktTable,this.confTable,this.chord));
			confTable.get(msg.getConfId()).putMessages(msg.getMessages());//put the previous messages in the conference,in this users's conference object
			for(String usr: msg.getIDs().keySet()){
				System.out.println("Adding the user "+usr);
				confTable.get(msg.getConfId()).getFingerTable().addUser(usr, msg.getIDs().get(usr),this.chord,msg.getConfId());
			}
			
		}
	
	}
	
	
}