package Conference;



import java.io.Serializable;
import java.net.Socket;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class FingerTable{
	private int tableLength;
	private int numberOfUsers=0;
	private Vector<FingerEntry> fingerEntryVector =  new Vector<FingerEntry>();
	private ID chordId;
	private String userId;
	private String ConfId;
	private Hashtable<String, OutSocket> confOutSockets;

	public FingerTable(EndPointContainer epc,ID id,String user,String ConfId, Hashtable<String, OutSocket> confOutSockets){
		this.chordId = id;
		this.userId = user;
		this.ConfId = ConfId;
		this.confOutSockets = confOutSockets;
		Vector<EndPoint> eps = epc.getEndPoints();
		for(int i=0;i<eps.size();i++){
			fingerEntryVector.add(new FingerEntry(eps.get(i).getStart(),eps.get(i).getEnd(),i+1));
		}
		this.tableLength = eps.size();
	}
	public Vector<ID> getIDs() {
		Vector<ID> ids = new Vector<ID>();
		ids.add(this.chordId);
		for(FingerEntry fe : fingerEntryVector){
			if(fe.getIDs().size()!=0)
				ids.addAll(fe.getIDs());
		}
		return ids;
	}

	public Hashtable<String,ID>getIdsAndHashes(){
		Hashtable<String,ID> out = new Hashtable<String,ID>();
		out.put(this.userId,this.chordId);
		for(FingerEntry fe : fingerEntryVector){
			if(fe.getIdsAndHashes().size()!=0)
				out.putAll(fe.getIdsAndHashes());
		}
		return out;
	}
	public  synchronized void addUser(String userId, ID id, OutSocket outskt) {

		FingerEntry fe = getFingerEntryfor(id);
		if(fe!=null){
			if(fe.getSocket() == null){
				//finger entry has not been initialised.
				fe.setSocketUser(userId);
				fe.setSocketUserHash(id);
				fe.setSocket(outskt);
				fe.getUserIds().add(userId);
				fe.getIDs().add(id);
				fe.getIdsAndHashes().put(userId, id);
				this.setNumberOfUsers(this.getNumberOfUsers() + 1);
			}
			else{
				//fingerEntry has some users in it
				//check if the already existing OutSocketUserHash is more than that of this user.
				if(fe.getStart().compareTo(fe.getEnd()) == 1){
					/*start 0 end */
					if(fe.getStart().compareTo(fe.getSocketUserHash())==0){
						fe.getUserIds().add(userId);
						fe.getIDs().add(id);
						fe.getIdsAndHashes().put(userId, id);
						this.setNumberOfUsers(this.getNumberOfUsers() + 1);
						return ;
					}
					else if(fe.getStart().compareTo(fe.getSocketUserHash())==-1 ){
						/*  start   <=Socket_owner>  0   end*/
						if(fe.getStart().compareTo(id)==-1 || fe.getStart().compareTo(id)==0){
							/* start {id, Socket_Owner} 0 end */
							if(fe.getSocketUserHash().compareTo(id)==-1){
								/*start socket_owner id 0 end */
								fe.getUserIds().add(userId);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(userId, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return ;
								
							}
							else{
								/*start id socket_owner 0 end */
								//create new socket to id
								fe.getSocket().removeConfId(this.ConfId);
								if(fe.getSocket().getConfIds().size()==0){
									fe.getSocket().close();
									this.confOutSockets.remove(fe.getSocket().getUserId());
								}
								fe.setSocketUser(userId);
								fe.setSocketUserHash(id);
								fe.setSocket(outskt);
								fe.getUserIds().add(userId);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(userId, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return ;
								
							}
							
							
						}
						else{
							/*start socket_owner 0 id end */
							fe.getUserIds().add(userId);
							fe.getIDs().add(id);
							fe.getIdsAndHashes().put(userId, id);
							this.setNumberOfUsers(this.getNumberOfUsers() + 1);
							return ;
							
						}
						
						
						
					}
					else {
						/*start  0<= {socket_owner,id} end  or
						 * start id 0 {socket_owner} end
						 */
						if(fe.getStart().compareTo(id) == -1){
							/* start id 0 socket_owner end */
							fe.getSocket().removeConfId(this.ConfId);
							if(fe.getSocket().getConfIds().size()==0){
								fe.getSocket().close();
								this.confOutSockets.remove(fe.getSocket().getUserId());
							}
							fe.setSocketUser(userId);
							fe.setSocketUserHash(id);
							fe.setSocket(outskt);
							fe.getUserIds().add(userId);
							fe.getIDs().add(id);
							fe.getIdsAndHashes().put(userId, id);
							this.setNumberOfUsers(this.getNumberOfUsers() + 1);
							return ;
							
							
						}
						
						else{
							/*start 0 {id,socket_owner} end */
							if(fe.getSocketUserHash().compareTo(id)==-1){
								/*start  0 <socket_owner> id end */
								fe.getUserIds().add(userId);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(userId, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return;
							}
							else{
								/*start  0 id <socket_owner> end */
								//create new socket to id.
								fe.getSocket().removeConfId(this.ConfId);
								if(fe.getSocket().getConfIds().size()==0){
									fe.getSocket().close();
									this.confOutSockets.remove(fe.getSocket().getUserId());
								}
								fe.setSocketUser(userId);
								fe.setSocketUserHash(id);
								fe.setSocket(outskt);
								fe.getUserIds().add(userId);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(userId, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return ;
							}
							
						}
						
						
						
					}
					
				}
				else{
					if(fe.getSocketUserHash().compareTo(id)==1){ 
						fe.getSocket().removeConfId(this.ConfId);
						if(fe.getSocket().getConfIds().size()==0){
							fe.getSocket().close();
							this.confOutSockets.remove(fe.getSocket().getUserId());
						}
						fe.setSocketUser(userId);
						fe.setSocketUserHash(id);
						fe.setSocket(outskt);
						fe.getUserIds().add(userId);
						fe.getIDs().add(id);
						fe.getIdsAndHashes().put(userId, id);
						this.setNumberOfUsers(this.getNumberOfUsers() + 1);
					}
					else{
						//the given user is not the first in the ring.
						fe.getUserIds().add(userId);
						fe.getIDs().add(id);
						fe.getIdsAndHashes().put(userId, id);
						this.setNumberOfUsers(this.getNumberOfUsers() + 1);
					}
				}
				
			}

		}
		else{
			System.out.println("FingerEntry is null for this ID ="+id.toDecimalString());
		}
	}
	public FingerEntry getFingerEntryfor(ID id){

		for(FingerEntry fe: fingerEntryVector){
			if(fe.isInInterval(id))
				return fe;
		}
		return null;
	}
	public Vector<FingerEntry> getFingerEntryVector() {
		return fingerEntryVector;
	}

	//used by the 
	public synchronized int addUser(String usr, ID id, ChordImpl chord,String confID) {
		FingerEntry fe = getFingerEntryfor(id);

		if(fe!=null){
			if(fe.getIdsAndHashes().containsKey(usr)){
				//user already added;
				return 0;
			}
			this.setNumberOfUsers(this.getNumberOfUsers() + 1);
			if(fe.getSocket() == null){
				//finger entry has not been initialised.
				fe.setSocketUser(usr);
				fe.setSocketUserHash(id);
				OutSocket outskt = connect(chord,usr,confID);
				if(outskt==null)
					return 0;
				
				fe.setSocket(this.confOutSockets.get(usr));
				fe.getUserIds().add(usr);
				fe.getIDs().add(id);
				fe.getIdsAndHashes().put(usr, id);
				return 1;
			}
			else{
				if(fe.getStart().compareTo(fe.getEnd()) == 1){
					/*start 0 end */
					if(fe.getStart().compareTo(fe.getSocketUserHash())==0){
						fe.getUserIds().add(usr);
						fe.getIDs().add(id);
						fe.getIdsAndHashes().put(usr, id);
						this.setNumberOfUsers(this.getNumberOfUsers() + 1);
						return 1;
					}
					else if(fe.getStart().compareTo(fe.getSocketUserHash())==-1 ){
						/*  start   <=Socket_owner>  0   end*/
						if(fe.getStart().compareTo(id)==-1 || fe.getStart().compareTo(id)==0){
							/* start {id, Socket_Owner} 0 end */
							if(fe.getSocketUserHash().compareTo(id)==-1){
								/*start socket_owner id 0 end */
								fe.getUserIds().add(usr);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(usr, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return 1;
								
							}
							else{
								/*start id socket_owner 0 end */
								//create new socket to id
								fe.getSocket().removeConfId(this.ConfId);
								if(fe.getSocket().getConfIds().size()==0){
									fe.getSocket().close();
									this.confOutSockets.remove(fe.getSocket().getUserId());
								}
								fe.setSocketUser(usr);
								fe.setSocketUserHash(id);
								OutSocket outskt = connect(chord,usr,confID);
								if(outskt==null)
									return 0;
								
								fe.setSocket(outskt);
								fe.getUserIds().add(usr);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(usr, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return 1;
								
							}
							
							
						}
						else{
							/*start socket_owner 0 id end */
							fe.getUserIds().add(usr);
							fe.getIDs().add(id);
							fe.getIdsAndHashes().put(usr, id);
							this.setNumberOfUsers(this.getNumberOfUsers() + 1);
							return 1;
							
						}
						
						
						
					}
					else {
						/*start  0<= {socket_owner,id} end  or
						 * start id 0 {socket_owner} end
						 */
						if(fe.getStart().compareTo(id) == -1){
							/* start id 0 socket_owner end */
							fe.getSocket().removeConfId(this.ConfId);
							if(fe.getSocket().getConfIds().size()==0){
								fe.getSocket().close();
								this.confOutSockets.remove(fe.getSocket().getUserId());
							}
							fe.setSocketUser(usr);
							fe.setSocketUserHash(id);
							OutSocket outskt = connect(chord,usr,confID);
							if(outskt==null)
								return 0;
							
							fe.setSocket(outskt);
							fe.getUserIds().add(usr);
							fe.getIDs().add(id);
							fe.getIdsAndHashes().put(usr, id);
							this.setNumberOfUsers(this.getNumberOfUsers() + 1);
							return 1;
							
							
						}
						
						else{
							/*start 0 {id,socket_owner} end */
							if(fe.getSocketUserHash().compareTo(id)==-1){
								/*start  0 <socket_owner> id end */
								fe.getUserIds().add(usr);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(usr, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return 1;
							}
							else{
								/*start  0 id <socket_owner> end */
								//create new socket to id.
								fe.getSocket().removeConfId(this.ConfId);
								if(fe.getSocket().getConfIds().size()==0){
									fe.getSocket().close();
									this.confOutSockets.remove(fe.getSocket().getUserId());
								}
								fe.setSocketUser(usr);
								fe.setSocketUserHash(id);
								OutSocket outskt = connect(chord,usr,confID);
								if(outskt==null)
									return 0;
								
								fe.setSocket(outskt);
								fe.getUserIds().add(usr);
								fe.getIDs().add(id);
								fe.getIdsAndHashes().put(usr, id);
								this.setNumberOfUsers(this.getNumberOfUsers() + 1);
								return 1;
							}
							
						}
						
						
						
					}
					
				}
				else{
					if(fe.getSocketUserHash().compareTo(id)==1){ 
						fe.getSocket().removeConfId(this.ConfId);
						if(fe.getSocket().getConfIds().size()==0){
							fe.getSocket().close();
							this.confOutSockets.remove(fe.getSocket().getUserId());
						}
						fe.setSocketUser(usr);
						fe.setSocketUserHash(id);
						OutSocket outskt = connect(chord,usr,confID);
						if(outskt==null)
							return 0;
						
						fe.setSocket(outskt);
						fe.getUserIds().add(usr);
						fe.getIDs().add(id);
						fe.getIdsAndHashes().put(usr, id);
						this.setNumberOfUsers(this.getNumberOfUsers() + 1);
						return 1;
					}
					else{
						//the given user is not the first in the ring.
						fe.getUserIds().add(usr);
						fe.getIDs().add(id);
						fe.getIdsAndHashes().put(usr, id);
						this.setNumberOfUsers(this.getNumberOfUsers() + 1);
						return 1;
					}
				}
				
				
			}

		}
		else{
			System.out.println("FingerEntry is null for this ID ="+id.toDecimalString());
			return 0;
		}
		//return 1;
	}

	public synchronized void clearTable() {
		for(FingerEntry fe : getFingerEntryVector()){
			if(fe.getSocket()!=null){
				if(fe.getSocket().getConfIds().size()!=0){
					fe.getSocket().removeConfId(this.ConfId);
				}
				if(fe.getSocket().getConfIds().size()==0)
					fe.getSocket().close();
			}

		}

	}
	public int getNumberOfUsers() {
		return numberOfUsers;
	}
	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	//1 for success and -1 for failure ,in removing the given user.
	public  synchronized  int removeUser(String userId,ID userHash,ChordImpl chord){
		FingerEntry fe = getFingerEntryfor(userHash);
		if(fe!=null){

			if(fe.getIdsAndHashes().get(userId)!=null){
				
				fe.getIdsAndHashes().remove(userId);
				fe.getIDs().remove(userHash);
				fe.getUserIds().remove(userId);

				if(fe.getSocketUser().equals(userId)){

					fe.getSocket().getConfIds().remove(this.ConfId);
					if(fe.getSocket().getConfIds().size()==0){
						fe.getSocket().close();
						fe.setSocket(null);
						fe.setSocketUser(null);
					}
						
					
					if(this.confOutSockets.get(userId).getConfIds().size()==0){
						
						this.confOutSockets.remove(userId);
						System.out.println("Removed Socket from the main socket table for the user"+userId);
					}

					if(fe.getIdsAndHashes().size()>1){
						String user=null;
						ID id=null;
						//check if finger interval crosses the 0.Means the interval includes 0 ..in it.
						if(fe.getStart().compareTo(fe.getEnd()) == 1){
							Vector<ID> beforeZero =  new Vector<ID>();
							Vector<ID> afterZero = new Vector<ID>();
							for(ID i: fe.getIDs()){
								if(fe.getStart().compareTo(i)==-1 ){
									/*start i 0 end */
									beforeZero.add(i);
								}
								else{
									/* start 0 i end */
									afterZero.add(i);
								}
							}
							
							if(beforeZero.size()!=0){
								
								Collections.sort(beforeZero);//sort in ascending order
								id = beforeZero.get(0);
								user=null;
								if(fe.getIdsAndHashes().containsValue(id)){
									for(String s : fe.getIdsAndHashes().keySet()){
										if(fe.getIdsAndHashes().get(s).equals(id)){
											user = s;
										}
									}
								}
								else{
									System.out.println("Could not found the ID . Returning");
									return 0;
								}
							}
							else{
								Collections.sort(afterZero);//sort in ascending order.
								id = afterZero.get(0);
								user=null;
								if(fe.getIdsAndHashes().containsValue(id)){
									for(String s : fe.getIdsAndHashes().keySet()){
										if(fe.getIdsAndHashes().get(s).equals(id)){
											user = s;
										}
									}
								}
								else{
									System.out.println("Could not found the ID . Returning");
									return 0;
								}
								
							}
							
//							
//							Collections.sort(fe.getIDs());//sort in ascending order.
//							Collections.reverse(fe.getIDs());//reverse the order to get the descending order.
//							id = fe.getIDs().get(0);
//							user=null;
//							if(fe.getIdsAndHashes().containsValue(id)){
//								for(String s : fe.getIdsAndHashes().keySet()){
//									if(fe.getIdsAndHashes().get(s).equals(id)){
//										user = s;
//									}
//								}
//							}
//							else{
//								System.out.println("Could not found the ID . Returning");
//								return 0;
//							}
						}
						else{
							Collections.sort(fe.getIDs());//sort in ascending order.
							id = fe.getIDs().get(0);
							user=null;
							if(fe.getIdsAndHashes().containsValue(id)){
								for(String s : fe.getIdsAndHashes().keySet()){
									if(fe.getIdsAndHashes().get(s).equals(id)){
										user = s;
									}
								}
							}
							else{
								System.out.println("Could not found the ID . Returning");
								return 0;
							}

						}	
						//create the new connection
						if(this.confOutSockets.get(user)==null){
							try{
								Set<Serializable> s = chord.retrieve(new StringKey(new String(user+":IP")));
								String IpAddress="";
								for(Serializable str:s){
									IpAddress = IpAddress.concat(str.toString());

								}
								System.out.println("The ipaddress of the user is "+IpAddress);
								String ConfPort="";
								try{
									Set<Serializable> pt = chord.retrieve(new StringKey(new String(user+":ConfPort")));

									for(Serializable str:pt){
										ConfPort = ConfPort.concat(str.toString());

									}
								}
								catch(Exception e){
									System.out.println("Unable to get the conference port");
									return 0;
								}
								System.out.println("The ipaddress of the user is "+IpAddress+" and conference port is "+ConfPort);
								Socket skt = new Socket(IpAddress,Integer.parseInt(ConfPort));
								OutSocket os = new OutSocket(skt,user);
								os.addConfId(this.ConfId);
								this.confOutSockets.put(user,os );
								fe.setSocket(this.confOutSockets.get(user));
								fe.setSocketUser(user);
								fe.setSocketUserHash(id);
								return 1;

							}
							catch(Exception e){
								System.out.println("Error while retrieving the IP address of the user.");
								return 0;
							}


						}
						else{
							fe.setSocket(this.confOutSockets.get(user));
							fe.setSocketUser(user);
							fe.setSocketUserHash(id);
							this.confOutSockets.get(user).addConfId(this.ConfId);
							return 1;
						}

						
					}

					else if(fe.getIdsAndHashes().size()==1){

						//create the connection to this user and set it to the finger entry.
						String user=fe.getUserIds().get(0);
						ID id = fe.getIDs().get(0);
						if(this.confOutSockets.get(user)==null){
							try{
								Set<Serializable> s = chord.retrieve(new StringKey(new String(user+":IP")));
								String IpAddress="";
								for(Serializable str:s){
									IpAddress = IpAddress.concat(str.toString());

								}
								System.out.println("The ipaddress of the user is "+IpAddress);
								String ConfPort="";
								try{
									Set<Serializable> pt = chord.retrieve(new StringKey(new String(user+":ConfPort")));

									for(Serializable str:pt){
										ConfPort = ConfPort.concat(str.toString());

									}
								}
								catch(Exception e){
									System.out.println("Unable to get the conference port");
								}
								System.out.println("The ipaddress of the user is "+IpAddress+" and conference port is "+ConfPort);
								Socket skt = new Socket(IpAddress,Integer.parseInt(ConfPort));
								OutSocket os = new OutSocket(skt,user);
								os.addConfId(this.ConfId);
								this.confOutSockets.put(user,os );
								fe.setSocket(this.confOutSockets.get(user));
								fe.setSocketUser(user);
								fe.setSocketUserHash(id);
								return 1;
							}
							catch(Exception e){
								System.out.println("Error while retrieving the IP address of the user.");
								return 0;
							}


						}
						else{
							fe.setSocket(this.confOutSockets.get(user));
							fe.setSocketUser(user);
							fe.setSocketUserHash(id);
							this.confOutSockets.get(user).addConfId(this.ConfId);
							return 1;
						}

						
					}

					else{
						//there are no users,since we already cleared the socket.Just return success.
						fe.setSocket(null);
						fe.setSocketUser(null);
						fe.setSocketUserHash(null);

						return 1;
					}
					//Create a new Socket Connection for this FingerEntry 

				}

				else{
					//removed user in not the socket owner.
					if(this.confOutSockets.get(userId)!=null){
						this.confOutSockets.get(userId).removeConfId(this.ConfId);
						if(this.confOutSockets.get(userId).getConfIds().size()==0){
							this.confOutSockets.get(userId).close();
							this.confOutSockets.remove(userId);
						}
						this.numberOfUsers--;
						return 1;
					}
					else{
						this.numberOfUsers--;
						return 1;
					}
					
				}

			}
			else{
				System.out.println("There is no user in the fingerTable ,while removing"+userId);
				return 0;
			}
		}
		else{
			System.out.println("No finger entry for the given userid,while removing"+userId);
			return 0;
		}
		//this.numberOfUsers--;
		//return 0;
	}
	
	public OutSocket connect(ChordImpl chord,String usr,String confID){

		try{
			Set<Serializable> s = chord.retrieve(new StringKey(new String(usr+":IP")));
			String IpAddress="";
			for(Serializable str:s){
				IpAddress = IpAddress.concat(str.toString());

			}
			System.out.println("The ipaddress of the user is "+IpAddress);
			String ConfPort="";
			try{
				Set<Serializable> pt = chord.retrieve(new StringKey(new String(usr+":ConfPort")));

				for(Serializable str:pt){
					ConfPort = ConfPort.concat(str.toString());

				}
			}
			catch(Exception e){
				System.out.println("Unable to get the conference port");
				return null;
			}
			System.out.println("The ipaddress of the user is "+IpAddress+" and conference port is "+ConfPort);
			Socket skt = new Socket(IpAddress,Integer.parseInt(ConfPort));
			OutSocket os = new OutSocket(skt,usr);
			os.addConfId(confID);
			this.confOutSockets.put(usr,os );
			return os;
		}
		catch(Exception e){
			System.out.println("Error while retrieving the IP address of the user.");
			return null;
		}

	}
	public void printTable() {
		for(FingerEntry fe: this.getFingerEntryVector()){
			System.out.print("--------Printing finger Entry with index="+fe.getIndex()+" start="+fe.getStart()+" end="+fe.getEnd());
			if(fe.getSocketUser()==null){
				System.out.print("\n");
			}
			else{
				System.out.println(" Socket user = "+fe.getSocketUser());
			}
			for(String s: fe.getIdsAndHashes().keySet()){
				System.out.println("UserID= "+s+" ID = "+fe.getIdsAndHashes().get(s).toString());
			}
		}
		
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