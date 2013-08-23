package management;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import Conference.Conference;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class mgmtChildListenerThread extends Thread {
	
	private ChordImpl chord;
	private Socket skt;
	private Hashtable<String,Conference> confTable;
	private Vector<String> servedRequests;
	public mgmtChildListenerThread(ChordImpl chord, Socket skt,Hashtable<String,Conference> confTable, Vector<String> servedRequests){
		System.out.println("ChildMgmtListener strated on "+chord.getID());
		this.chord = chord;
		this.skt = skt;
		this.confTable = confTable;
		this.servedRequests = servedRequests;
	}
	public void run(){
		try {
			InputStream is = skt.getInputStream();
			OutputStream os = skt.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(is); 
			System.out.println("Before running 2");
			
			boolean keepRunning = true;
			
			while(keepRunning){
			
				MgmtMessage msg = (MgmtMessage)ois.readObject();
				System.out.println("Receive Message is "+msg.getMessageName());
				if(msg instanceof RequestMessage){
					System.out.println("Received Message"+msg.getMessageName());
					handleRequestMessage(msg,oos);//send the response message.Then send the bye message then close all the sockets.
					oos.writeObject(new management.ByeMessage());
					keepRunning=false;
					sleep(1000);
					ois.close();
					oos.close();
					//is.close();
					//os.close();
					skt.close();
					
				}
					
				
			}
					
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	private void handleRequestMessage(MgmtMessage imsg,ObjectOutputStream oos) throws Exception {
		System.out.println("Handling request message from "+((RequestMessage)imsg).getSenderId());
		RequestMessage msg = (RequestMessage)imsg;
		ID end = msg.getEnd();
		if(this.servedRequests.contains(msg.getRequestId())){
			System.out.println("ChildListener: Request Id found");
			ResponseMessage rm = new ResponseMessage();
			rm.setReg_Users(new HashSet<String>());
			rm.setOffLineMsgs(new HashSet<String>());
			rm.setConfIds(new HashSet<String>());			
			rm.setNo_of_liveUsers(0);
			oos.writeObject(rm);
		}
		else{
			System.out.println("ChildListener: Request Id not found");
			this.servedRequests.add(msg.getRequestId());
			ResponseMessage rm = new ResponseMessage();
			int i=0;
			int tableLength = this.chord.getID().getLength();
			Node[] nodes = this.chord.getFingerNodes();
			System.out.println(chord.printFingerTable());
			Hashtable<Integer,childRequest> al = new Hashtable<Integer,childRequest>();
			ArrayList<ID> connectedNodes =  new ArrayList<ID>();
			ID prev=null;
			System.out.println("Entering finger table");
			for(i=0;i<tableLength;i++){
				
				if(nodes[i]!=null  && !connectedNodes.contains( nodes[i].getNodeID() )){
					//System.out.println("Entered the for loop . ID is "+nodes[i].getNodeID());
					if(i==0){
						//System.out.println("Entered i==0");
						prev = nodes[i].getNodeID();
						if(nodes[i].getNodeID().compareTo(msg.getSenderId())!=0){
							if(nodes[i].getNodeID().compareTo(end)==0)//The node id should be less than end id.
								;//do nothing
							else{
								//System.out.println("ChildListener::i==0::before connecting socket");
								Socket skt = connect(nodes[i].getNodeID());
								//System.out.println("ChildListener::i==0::After connecting socket");
								if(skt!=null){
									
									connectedNodes.add(nodes[i].getNodeID());
									childRequest chr = new childRequest();
									al.put(i, chr);
									new ChildRequesterThread(skt,chr,end,msg.getRequestId(),this.chord.getID()).start();
									System.out.println("Sent request message to "+nodes[i].getNodeID());
								}
							}
						}
						
						
					}
					else{
						if(nodes[i].getNodeID().compareTo(msg.getSenderId())!=0){
							if(nodes[i].getNodeID().compareTo(end)==0)//The node id should be less than end id.
								;//do nothing
							else{
								if(prev.compareTo(nodes[i].getNodeID())!=0){
									Socket skt = connect(nodes[i].getNodeID());
									if(skt!=null){
										prev = nodes[i].getNodeID();
										connectedNodes.add(nodes[i].getNodeID());
										childRequest chr = new childRequest();
										al.put(i, chr);
										new ChildRequesterThread(skt,chr,end,msg.getRequestId(),this.chord.getID()).start();
										System.out.println("Sent request message to "+nodes[i].getNodeID());
									}
								}
								
								
							}
						}
						
						
					}
						
							
					}
					
				}
			
			
			
			boolean flag=true;
			 HashSet<String> reg_Users=new HashSet<String>();
			 HashSet<String> confIds=new HashSet<String>();
			 HashSet<String> offLineMsgs=new HashSet<String>();
			 int no_of_liveUsers=0;
			 long startTime=System.currentTimeMillis();
			while(flag==true && ((System.currentTimeMillis() - startTime)<5000) ){
				if(al.keySet().size()==0)
					flag=false;
				
				for(Integer itg : al.keySet()){
					if(al.get(itg).isCompleted()){
						childRequest ch = al.get(itg);
						if(ch.isread()==false){
							ch.setRead(true);
							reg_Users.addAll(ch.getReg_Users());//Taking the unions..
							confIds.addAll(ch.getConfIds());
							offLineMsgs.addAll(ch.getOffLineMsgs());
							no_of_liveUsers = no_of_liveUsers + ch.getNo_of_liveUsers();
						}
						flag = false;
					}
					else{
						flag = true;
					}
						
				}
				
			}
			
			//compute the offline messages and no_of_conferences and total registered users at this node.
			
			Map<ID,Set<Entry>> emap = new HashMap<ID,Set<Entry>>(chord.getEntries());
			HashSet<String> Me_reg_Users = new HashSet<String>();
			HashSet<String> Me_confIds = new HashSet<String>();
			HashSet<String> Me_offLineMsgs = new HashSet<String>();
			String temp;
			for(ID id : emap.keySet()){
				for(Entry e: emap.get(id)){
					temp = e.getValue().toString();
					if(temp.contains("REG:")){
						Me_reg_Users.add(temp);
					}
					if(temp.contains("OM:")){
						Me_offLineMsgs.add(temp);
					}
						
				}
				
			}
			
			for(String s:this.confTable.keySet()){
				Me_confIds.add(s);
			}
			
			//add above computed metrics to global metrics.
			offLineMsgs.addAll(Me_offLineMsgs);
			confIds.addAll(Me_confIds);
			reg_Users.addAll(Me_reg_Users);
			
			rm.setReg_Users(reg_Users);
			rm.setOffLineMsgs(offLineMsgs);
			rm.setConfIds(confIds);
			rm.setNo_of_liveUsers(no_of_liveUsers+1);//1 for him self ..at here.
			
			oos.writeObject(rm);
		}
		
		
		
		
		
	/*	
		
		ID end = msg.getEnd();
		if(end == null){//this is the node of the first finger entry.
			ResponseMessage rm = new ResponseMessage();
			System.out.println("Response Message for first finger entry");
			System.out.println(chord.printFingerTable());
			rm.setNo_of_liveUsers(1);//set yourself as live user.
			Map<ID,Set<Entry>> emap = new HashMap<ID,Set<Entry>>(chord.getEntries());
			HashSet<String> reg_Users = new HashSet<String>();
			HashSet<String> confIds = new HashSet<String>();
			HashSet<String> offLineMsgs = new HashSet<String>();
			String temp;
			for(ID id : emap.keySet()){
				for(Entry e: emap.get(id)){
					temp = e.getValue().toString();
					if(temp.contains("REG:")){
						reg_Users.add(temp);
					}
					if(temp.contains("OM:")){
						offLineMsgs.add(temp);
					}
						
				}
				
			}
			
			rm.setReg_Users(reg_Users);
			rm.setOffLineMsgs(offLineMsgs);
			
			for(String s:this.confTable.keySet()){
				confIds.add(s);
			}
			rm.setConfIds(confIds);
			
			rm.setNo_of_liveUsers(1);
			
			oos.writeObject(rm);
		}
		else{
			ResponseMessage rm = new ResponseMessage();
			int i=0;
			int tableLength = this.chord.getID().getLength();
			Node[] nodes = this.chord.getFingerNodes();
			System.out.println(chord.printFingerTable());
			Hashtable<Integer,childRequest> al = new Hashtable<Integer,childRequest>();
			ArrayList<ID> connectedNodes =  new ArrayList<ID>();
			ID prev=null;
			boolean stop = false;
			for(i=0;i<tableLength;i++){
				if(nodes[i]!=null && stop==false && !connectedNodes.contains( nodes[i].getNodeID() )){
					if(i==0){
						prev = nodes[i].getNodeID();
						if(nodes[0].getNodeID().compareTo(end)==1 || nodes[0].getNodeID().compareTo(end)==0)//the first finger table is greater than the end id so ..break the loop.
							break;
						else{
							Socket skt = connect(nodes[i].getNodeID());
							if(skt!=null){
								connectedNodes.add(nodes[i].getNodeID());
								childRequest chr = new childRequest();
								al.put(i, chr);
								new ChildRequesterThread(skt,chr,null).start();
							}
							
						}
							
					}
					else{
						
						ID ih;
						if(i==tableLength-1){
							ih = end;
						}
						else{
							if(end.compareTo(this.chord.getID().addPowerOfTwo(i+1)) == -1){
								ih = end;
								stop=true;//already reached end..stop now.
							}
							else{
								ih = this.chord.getID().addPowerOfTwo(i+1);
								
							}
							
							if(prev.compareTo(nodes[i].getNodeID())!=0){
								Socket skt = connect(nodes[i].getNodeID());
								if(skt!=null){
									prev = nodes[i].getNodeID();
									connectedNodes.add(nodes[i].getNodeID());
									childRequest chr = new childRequest();
									al.put(i, chr);
									new ChildRequesterThread(skt,chr,ih).start();
								}
							}
							
							
							
						}
							
					}
				}
			}
			
			
			boolean flag=true;
			 HashSet<String> reg_Users=new HashSet<String>();
			 HashSet<String> confIds=new HashSet<String>();
			 HashSet<String> offLineMsgs=new HashSet<String>();
			 int no_of_liveUsers=0;
			while(flag){
				for(Integer itg : al.keySet()){
					if(al.get(itg).isCompleted()){
						childRequest ch = al.get(itg);
						if(ch.isread()==false){
							ch.setRead(true);
							reg_Users.addAll(ch.getReg_Users());//Taking the unions..
							confIds.addAll(ch.getConfIds());
							offLineMsgs.addAll(ch.getOffLineMsgs());
							no_of_liveUsers =+ ch.getNo_of_liveUsers();
						}
						flag = false;
					}
					else{
						flag = true;
					}
						
				}
			}
			
			//compute the offline messages and no_of_conferences and total registered users at this node.
			
			Map<ID,Set<Entry>> emap = new HashMap<ID,Set<Entry>>(chord.getEntries());
			HashSet<String> Me_reg_Users = new HashSet<String>();
			HashSet<String> Me_confIds = new HashSet<String>();
			HashSet<String> Me_offLineMsgs = new HashSet<String>();
			String temp;
			for(ID id : emap.keySet()){
				for(Entry e: emap.get(id)){
					temp = e.getValue().toString();
					if(temp.contains("REG:")){
						Me_reg_Users.add(temp);
					}
					if(temp.contains("OM:")){
						Me_offLineMsgs.add(temp);
					}
						
				}
				
			}
			
			for(String s:this.confTable.keySet()){
				Me_confIds.add(s);
			}
			
			//add above computed metrics to global metrics.
			offLineMsgs.addAll(Me_offLineMsgs);
			confIds.addAll(Me_confIds);
			reg_Users.addAll(Me_reg_Users);
			
			rm.setReg_Users(reg_Users);
			rm.setOffLineMsgs(offLineMsgs);
			rm.setConfIds(confIds);
			rm.setNo_of_liveUsers(no_of_liveUsers+1);//1 for him self ..at here.
			
			oos.writeObject(rm);
		}
		*/
	}
	
	private Socket connect(ID hash) {
		//get the userID
		Set<Serializable> s = this.chord.retrieve(new StringKey(hash.toString()));
		String userId="";
		for(Serializable str:s){
			userId = userId.concat(str.toString());

		}
		//System.out.println("From Management Requester Thread.UserId is  "+userId);

		//get the user Ip Address
		Set<Serializable> sIp = this.chord.retrieve(new StringKey(userId+":IP"));
		String IpAddress="";
		for(Serializable str:sIp){
			IpAddress = IpAddress.concat(str.toString());

		}
		//System.out.println("From Management Requester Thread.The ipaddress of the user is "+IpAddress);

		//get the Management listening port.
		String Port="";
		try{
			Set<Serializable> pt = chord.retrieve(new StringKey(new String(userId+":mgmtPort")));

			for(Serializable str:pt){
				Port = Port.concat(str.toString());

			}
		}
		catch(Exception e){
			System.out.println("Unable to get the management port");
			return null;
		}
	//	System.out.println("From Management Requester Thread.The mgmt listening port of the user is "+Port);

		try {
			Socket skt = new Socket(IpAddress,Integer.parseInt(Port));
			return skt;
		} catch (Exception e) {
			System.out.println("From Management Requester Thread.Unable to connect to the userId="+userId);
			//e.printStackTrace();
			return null;
		} 


	}
}
