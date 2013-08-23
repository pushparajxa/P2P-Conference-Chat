package management;

import java.io.Serializable;

public class MgmtMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 493155774364311709L;
	private String MessageName;
	public MgmtMessage(){
		
	}
	public String getMessageName() {
		return MessageName;
	}
	public void setMessageName(String messageName) {
		MessageName = messageName;
	}
}