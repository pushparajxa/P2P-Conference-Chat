package management;

import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import Conference.Conference;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class mgmtMainRequesterThread extends Thread{
	private ServerSocket skt;
	private ChordImpl chord;
	private boolean keepRunning=true;
	private int tableLength;
	private Node[] nodes;
	private ID localId;
	private int total_regUsers;
	private int msgs_forDelivery;
	private int no_of_Coferences;
	private int no_of_liveUsers;
	private Hashtable<String,Conference> confTable;
	private ArrayList<ID> connectedNodes;
	private int requestCount=0;
	private ArrayList<String> servedRequests;
	private String userId;
	public mgmtMainRequesterThread(ServerSocket skt,ChordImpl chord,Hashtable<String,Conference> confTable,ArrayList<String> servedRequests,String userId){
		this.skt = skt;
		this.chord = chord;
		this.tableLength = chord.getID().getLength();
		this.nodes = chord.getFingerNodes();
		this.localId = chord.getID();
		this.confTable = confTable;
		this.servedRequests = servedRequests;
		this.userId =userId;
	}
	public void run(){
		Hashtable<Integer,childRequest> al ;
		String requestId;
		while(keepRunning){
			if(chord.getPredecessor()!=null ){
				//check if this is the first node after id 0. its predecessor will be having ID more than its.
				if(chord.getID().compareTo(chord.getPredecessor().getNodeID())==-1){
					try {
						//sleep till the all things settle down.
						sleep(10000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					nodes = chord.getFingerNodes();
					requestCount++;
					requestId = this.userId.concat(new Integer(requestCount).toString());
					this.servedRequests.add(requestId);//userID.requestCont;
					System.out.println(chord.printFingerTable());
					System.out.println("\nRequester Started on node "+this.localId+" with request id  "+requestId);
					al = new Hashtable<Integer,childRequest>();
					connectedNodes =  new ArrayList<ID>();
					total_regUsers=1;
					msgs_forDelivery=0;
					no_of_Coferences=0;
					no_of_liveUsers=1;
					int i=0;


					ID prev=null;
					for(i=0;i<tableLength;i++){
						if(nodes[i]!=null && !connectedNodes.contains( nodes[i].getNodeID() ) ){
							if(i==0){
								prev = nodes[i].getNodeID();
								Socket skt = connect(nodes[i].getNodeID());
								if(skt!=null){
									
									connectedNodes.add(nodes[i].getNodeID());
									childRequest chr = new childRequest();
									al.put(i, chr);
									new ChildRequesterThread(skt,chr,this.localId,requestId,this.localId).start();
									System.out.println("Sending Mgmt Request finger "+i+"id "+nodes[i].getNodeID());
								}
							}
							else{
								if(prev.compareTo(nodes[i].getNodeID())!=0){
									Socket skt = connect(nodes[i].getNodeID());
									if(skt!=null){
										prev = nodes[i].getNodeID();
										connectedNodes.add(nodes[i].getNodeID());
										childRequest chr = new childRequest();
										al.put(i, chr);
										new ChildRequesterThread(skt,chr,this.localId,requestId,this.localId).start();
										System.out.println("Sending Mgmt Request finger "+i+"id "+nodes[i].getNodeID());
									}
								}
							}
							
							
							
						
						}
					}

					/*
					ID prev=null;
					for(i=0;i<tableLength;i++){
						if(nodes[i]!=null && !connectedNodes.contains( nodes[i].getNodeID() ) ){
							if(i==0){
								prev = nodes[i].getNodeID();
								Socket skt = connect(nodes[i].getNodeID());
								if(skt!=null){
									connectedNodes.add(nodes[i].getNodeID());
									childRequest chr = new childRequest();
									al.put(i, chr);
									new ChildRequesterThread(skt,chr,null).start();
									System.out.println("Sending Mgmt Request to first finger entry");
								}

							}
							else{
								ID end;
								if(i==tableLength-1)
									end = this.localId;
								else
									end = localId.addPowerOfTwo(i+1);

								if(prev.compareTo(nodes[i].getNodeID())!=0){
									Socket skt = connect(nodes[i].getNodeID());
									if(skt!=null){
										prev = nodes[i].getNodeID();
										connectedNodes.add(nodes[i].getNodeID());
										childRequest chr = new childRequest();
										al.put(i, chr);
										new ChildRequesterThread(skt,chr,end).start();
										System.out.println("Sending Mgmt Request .");
									}
								}


							}

						}
					}
					 */
					boolean flag=true;
					HashSet<String> reg_Users=new HashSet<String>();
					HashSet<String> confIds=new HashSet<String>();
					HashSet<String> offLineMsgs=new HashSet<String>();
					 long startTime=System.currentTimeMillis();
					while(flag==true && ((System.currentTimeMillis() - startTime)<100000) ){
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
									this.no_of_liveUsers = this.no_of_liveUsers+ch.getNo_of_liveUsers();
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
					//live users is added before on line number 54.

					this.msgs_forDelivery = offLineMsgs.size();
					this.no_of_Coferences = confIds.size();
					this.total_regUsers = reg_Users.size();

					System.out.println("Mgmt::MSGS_WAITING_FOR_DELIVERY"+this.msgs_forDelivery);
					System.out.println("Mgmt::NO_OF_RUNNING_CONFERENCES"+this.no_of_Coferences);
					System.out.println("Mgmt::NO_OF_REGISTERED_USERS"+this.total_regUsers);
					System.out.println("Mgmt::NO_OF_LIVE_USERS"+this.no_of_liveUsers);

					//sleep for 15 seconds before starting the next iteration of measurements.
					try {
						sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}
	private boolean isAlreadyConnected(Node node) {
		// TODO Auto-generated method stub
		return false;
	}
	private Socket connect(ID hash) {
		//get the userID
		Set<Serializable> s = this.chord.retrieve(new StringKey(hash.toString()));
		String userId="";
		for(Serializable str:s){
			userId = userId.concat(str.toString());

		}
		//System.out.println("In Connect method. The ID is "+hash.toString());
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
			//System.out.println("Unable to get the management port");
			return null;
		}
		//System.out.println("From Management Requester Thread.The mgmt listening port of the user is "+Port);

		try {
			Socket skt = new Socket(IpAddress,Integer.parseInt(Port));
			return skt;
		} catch (Exception e) {
			//System.out.println("From Management Requester Thread.Unable to connect to the users");
			//e.printStackTrace();
			return null;
		} 


	}

	public boolean isClose() {
		return keepRunning;
	}
	public void setClose(boolean close) {
		this.keepRunning = close;
	}
}
class StringKey implements de.uniba.wiai.lspi.chord.service.Key {



	private String theString;

	public StringKey(String theString) {
		this.theString = theString;
	}

	public byte[] getBytes() {
		return this.theString.getBytes();
	}

	public int hashCode() {
		return this.theString.hashCode();
	}

	public boolean equals(Object o) {
		if (o instanceof StringKey) {
			return ((StringKey) o).theString.equals(this.theString);
		}
		return false;
	}
}

