package Conference;

import java.util.Hashtable;
import java.util.Vector;

import de.uniba.wiai.lspi.chord.data.ID;

public class InviteMessage extends Message{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5901647294961831681L;

	/**
	 * 
	 */
	private Hashtable<String,ID> IDs;
	private String msg;
	private String confId;
	private String Inviter;
	private Vector<String> messages;
	public InviteMessage(String msg,Hashtable<String,ID> Ids,String ConfId,String inviter, Vector<String> messages){
		this.setMsg(msg);
		this.setIDs(Ids);
		this.setConfId(ConfId);
		this.setMessageName("InviteMessage");
		this.Inviter = inviter;
		this.setMessages(new Vector<String>(messages));
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getConfId() {
		return confId;
	}
	public void setConfId(String confId) {
		this.confId = confId;
	}

	public Hashtable<String,ID> getIDs() {
		return IDs;
	}

	public void setIDs(Hashtable<String,ID> iDs) {
		IDs = iDs;
	}

	public String getInviter() {
		return Inviter;
	}

	public void setInviter(String inviter) {
		Inviter = inviter;
	}

	public Vector<String> getMessages() {
		return messages;
	}

	public void setMessages(Vector<String> messages) {
		this.messages = messages;
	}
}