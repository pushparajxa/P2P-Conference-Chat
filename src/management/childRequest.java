package management;

import java.util.HashSet;

public class childRequest{
	private boolean completed=false;
	private int regUserCount;
	int msgs_forDelivery;
	int no_of_Coferences;
	int no_of_liveUsers=0;
	private HashSet<String> reg_Users = new HashSet<String>();
	private HashSet<String> confIds= new HashSet<String>();
	private HashSet<String> offLineMsgs= new HashSet<String>();
	
	
	boolean read=false;
	public  boolean isCompleted() {
		return completed;
	}
	public  void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public int getRegUserCount() {
		return regUserCount;
	}
	public void setRegUserCount(int regUserCount) {
		this.regUserCount = regUserCount;
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
	public boolean isread(){
		return read;
	}
	
	public void setRead(boolean val){
		this.read = val;
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