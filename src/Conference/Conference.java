package Conference;

import java.util.Hashtable;
import java.util.Vector;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Conference{
	private static int ConfCount=0;
	private String ConfId;
	private FingerTable fingerTable;
	private Hashtable<String,Conference> confTable;
	private Hashtable<String,OutSocket> outSckts;
	private ID chordID;
	//private int numberOfUsersInConf=0;
	private String userId;
	private Vector<String> messages;
	private ChordImpl chord;
	public  Conference(String userId,EndPointContainer epc, Hashtable<String, OutSocket> confOutSockets, Hashtable<String, Conference> confTable,ID id,ChordImpl chord){
		synchronized(this.getClass()){
			Conference.ConfCount++;
		}
		ConfId = "Conf".concat(userId).concat(new Integer(Conference.ConfCount).toString());
		fingerTable = new FingerTable(epc,id,userId,ConfId,confOutSockets);
		this.confTable = confTable;
		this.outSckts = confOutSockets;
		this.chordID = id;
		this.userId = userId;
		this.messages = new Vector<String>();
		this.chord = chord;
	}
	public Conference(String ConfId,EndPointContainer epc,boolean newConference,ID id,String User,Hashtable<String,OutSocket> outSckts, Hashtable<String, Conference> confTable,ChordImpl chord){
		this.ConfId = ConfId;
		fingerTable =  new FingerTable(epc,id,User,ConfId,outSckts);
		this.userId = User;
		this.chordID = id;
		this.outSckts = outSckts;
		this.messages = new Vector<String>();
		this.confTable = confTable;
		this.chord =chord;
	}
	
	public synchronized  Vector<String>getMessages(){
		return messages;
	}
	
	public void putMessages(Vector<String> msgs){
		messages.addAll(msgs);
	}
	public synchronized void putMessage(String msg){
		messages.add(msg);
	}
	public String getConfId(){
		return ConfId;
	}
	public  synchronized void  sendMessage(Message msg){
		System.out.println("Printing the fingerTable for User= "+this.userId);
		fingerTable.printTable();
		for(FingerEntry fe : fingerTable.getFingerEntryVector()){
			if(fe.getSocket()!=null){
				/*start of new code */
				if(fe.getSocket().isAlive()==false){
					int result =fingerTable.removeUser(fe.getSocketUser(), fe.getSocketUserHash(),this.chord);
					if(result==0 || fe.getSocket()==null){
						//do nothing.
					}
					else{
						Message msgc = ((Message)msg.clone());
						if(fe.getIdsAndHashes().size()!=0){
							//fe.getIdsAndHashes().remove(fe.getSocket());
							msgc.setUsersAndHashes(fe.getIdsAndHashes());
							fe.getSocket().sendMsg(msgc);
							System.out.println("Sending message on FingerEntry index= "+fe.getIndex() +" with size= "+fe.getIdsAndHashes().size()+" msg_Size= "+msgc.getUsersAndHashes().size()+" with scoket user= "+fe.getSocketUser());
							for(String t: msgc.getUsersAndHashes().keySet()){
								System.out.println("Sending Message :: User="+t);
							}
							System.out.println("Sending message is complete");
						}
					}
				}
				
				/* End of new Code */
				else{
					Message msgc = ((Message)msg.clone());
					if(fe.getIdsAndHashes().size()!=0){
						msgc.setUsersAndHashes(fe.getIdsAndHashes());
						fe.getSocket().sendMsg(msgc);
						System.out.println("Sending message on FingerEntry index= "+fe.getIndex() +" with size= "+fe.getIdsAndHashes().size()+" msg_Size= "+msgc.getUsersAndHashes().size()+" with scoket user= "+fe.getSocketUser());
						for(String t: msgc.getUsersAndHashes().keySet()){
							System.out.println("Sending Message :: User="+t);
						}
						System.out.println("Sending message is complete");
					}
				}
				
			}
		}
		System.out.println("Completed Sending messages");
	}
	public synchronized void  Invite(String userId,ID id){
		/*
		 * 1. Send the Invite message consisting of INVITE:CONF_ID:INVITER:USER'IDS_IN_CONFERENCE
		 * 2. Send message to all existing users in conference about the invite of this node.
		 */
		OutSocket outskt = outSckts.get(userId);
		String msg = "INVITE:".concat(ConfId);
		//this.numberOfUsersInConf++;
		outskt.sendMsg(new InviteMessage(msg,fingerTable.getIdsAndHashes(),this.ConfId,this.userId,this.messages));
		
		//send message to all the nodes in the conference
		sendMessage(new InvitedMessage(userId,id,this.ConfId));
		fingerTable.addUser(userId,id,outskt);
		System.out.println("Invitation Message sent");
	}
	public synchronized void  leave() {
		try{
			ByeMessage bye = new ByeMessage(this.ConfId,this.userId,this.chordID);
			sendMessage(bye);
			this.fingerTable.clearTable();
			this.confTable.remove(this.ConfId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public FingerTable getFingerTable() {
		return fingerTable;
	}
	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}
	
	public int getNumberOfUsers(){
		return this.fingerTable.getNumberOfUsers();
	}
}