package management;

import java.util.HashSet;
import java.util.Set;

public class ResponseMessage extends MgmtMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4951006467349448553L;
	
	private int total_regUsers;
	private int msgs_forDelivery;
	private int no_of_Coferences;
	private int no_of_liveUsers;
	private HashSet<String> reg_Users;
	private HashSet<String> confIds;
	private HashSet<String> offLineMsgs;
	
	public ResponseMessage(){
		
		this.setMessageName("ResponseMessage");
			total_regUsers=0;
			msgs_forDelivery=0;
			no_of_Coferences=0;
			no_of_liveUsers=0;
	}

	public int getTotal_regUsers() {
		return total_regUsers;
	}

	public void setTotal_regUsers(int total_regUsers) {
		this.total_regUsers = total_regUsers;
	}

	public int getMsgs_forDelivery() {
		return msgs_forDelivery;
	}

	public void setMsgs_forDelivery(int msgs_forDelivery) {
		this.msgs_forDelivery = msgs_forDelivery;
	}

	public int getNo_of_Coferences() {
		return no_of_Coferences;
	}

	public void setNo_of_Coferences(int no_of_Coferences) {
		this.no_of_Coferences = no_of_Coferences;
	}

	public int getNo_of_liveUsers() {
		return no_of_liveUsers;
	}

	public void setNo_of_liveUsers(int no_of_liveUsers) {
		this.no_of_liveUsers = no_of_liveUsers;
	}

	

	public HashSet<String> getReg_Users() {
		return reg_Users;
	}

	public void setReg_Users(HashSet<String> reg_Users) {
		this.reg_Users = reg_Users;
	}

	public HashSet<String> getConfIds() {
		return confIds;
	}

	public void setConfIds(HashSet<String> confIds) {
		this.confIds = confIds;
	}

	public HashSet<String> getOffLineMsgs() {
		return offLineMsgs;
	}

	public void setOffLineMsgs(HashSet<String> offLineMsgs) {
		this.offLineMsgs = offLineMsgs;
	}

	
}