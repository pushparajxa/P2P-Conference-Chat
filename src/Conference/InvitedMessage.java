package Conference;

import de.uniba.wiai.lspi.chord.data.ID;



public class InvitedMessage extends Message implements Cloneable{

	private static final long serialVersionUID = -4758575850245358771L;
	private String user;
	private ID userID;
	private String ConfId;
	public InvitedMessage(String InvitedUser, ID InviteduserID,String confId){
		this.setUser(InvitedUser);
		this.setUserID(InviteduserID);
		this.setConfId(confId);
		this.setMessageName("InvitedMessage");
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public ID getUserID() {
		return userID;
	}
	public void setUserID(ID userID) {
		this.userID = userID;
	}
	public Object clone(){
		try{
			InvitedMessage tm =(InvitedMessage)super.clone();
			tm.setUserID(userID);
			return tm;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}
	public String getConfId() {
		return ConfId;
	}
	public void setConfId(String confId) {
		ConfId = confId;
	}

}