package Conference;

import de.uniba.wiai.lspi.chord.data.ID;


public class ByeMessage extends Message implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5245311858216767653L;

	private String ConfId;
	private String userId;
	private ID userHash;
	public ByeMessage(String confId,String userId,ID userHash){
		this.ConfId =confId;
		this.userId = userId;
		this.userHash = userHash;
	}
	public String getConfId() {
		return ConfId;
	}
	public void setConfId(String confId) {
		ConfId = confId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public ID getUserHash() {
		return userHash;
	}
	public void setUserHash(ID userHash) {
		this.userHash = userHash;
	}
}