package Conference;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import de.uniba.wiai.lspi.chord.data.ID;

public class Message implements Serializable,Cloneable{

	/**
	 * 
	 */
	private String MessageName;
	private Vector<ID> userHases;
	private Vector<String> userIds;
	private Hashtable<String,ID> usersAndHashes;
	private static final long serialVersionUID = 3395025577387258837L;
	public Message(){
		
		this.userHases = new Vector<ID>();
		this.userIds = new Vector<String>();
		usersAndHashes = new Hashtable<String,ID>();
		//this.MessageName="SimpleMessage";
	}
	public Vector<ID> getUserHases() {
		return userHases;
	}
	public void setUserHases(Vector<ID> userHases) {
		this.userHases.removeAllElements();
		this.userHases.addAll(userHases);
	}
	public Vector<String> getUserIds() {
		return userIds;
	}
	public void setUserIds(Vector<String> userIds) {
		this.userIds.removeAllElements();
		this.userIds.addAll(userIds);
	}
	public void addUserHash(ID id){
		this.userHases.add(id);

	}
	public void addUserId(String userId){
		this.userIds.add(userId);
	}
	public void addUserHash(Vector<ID> id){
		this.userHases.addAll(id);

	}
	public void addUserId(Vector<String> userId){
		this.userIds.addAll(userId);
	}
	
	public Object clone(){
		try{
			Message m = (Message)super.clone();
			m.setUserIds(new Vector<String>());
			m.setUserHases(new Vector<ID>());
			m.setUsersAndHashes(new Hashtable<String,ID>());
			return m;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public Hashtable<String,ID> getUsersAndHashes() {
		return usersAndHashes;
	}
	public void setUsersAndHashes(Hashtable<String,ID> usersAndHashes) {
		this.usersAndHashes.clear();
		this.usersAndHashes.putAll(usersAndHashes);
	}
	public String getMessageName() {
		return MessageName;
	}
	public void setMessageName(String messageName) {
		MessageName = messageName;
	}
	
}