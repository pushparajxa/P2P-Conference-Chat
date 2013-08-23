package management;

import de.uniba.wiai.lspi.chord.data.ID;

public class RequestMessage extends MgmtMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2809497624260611236L;
	private ID end;
	private String requestId;
	private ID senderId;
	public RequestMessage(ID end, String requestId, ID senderId){
		this.setMessageName("RequestMessage");
		this.end = end;
		this.requestId =requestId;
		this.setSenderId(senderId);
	}
	public ID getEnd() {
		return end;
	}
	public void setEnd(ID end) {
		this.end = end;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public ID getSenderId() {
		return senderId;
	}
	public void setSenderId(ID senderId) {
		this.senderId = senderId;
	}
	
}