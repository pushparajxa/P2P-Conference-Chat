package Conference;

import java.util.Hashtable;
import java.util.Vector;

import de.uniba.wiai.lspi.chord.data.ID;

public class FingerEntry{
	private Vector<String> userIds =  new Vector<String>();
	private Vector<ID> IDs = new Vector<ID>();
	private Hashtable<String,ID>IdsAndHashes = new Hashtable<String,ID>();
	private ID start;
	private ID end;
	private OutSocket socket=null;
	private String socketUser=null;
	private ID socketUserHash =null;
	private int index;
	public FingerEntry(ID start, ID end,int index) {
		this.setStart(start);
		this.setEnd(end);
		this.setIndex(index);
	}
	public Vector<String> getUserIds(){
		return userIds;
	}
	public String getSocketUser() {
		return socketUser;
	}
	public void setSocketUser(String socketUser) {
		this.socketUser = socketUser;
	}
	public OutSocket getSocket() {
		return socket;
	}
	public void setSocket(OutSocket socket) {
		this.socket = socket;
	}
	public ID getStart() {
		return start;
	}
	public void setStart(ID start) {
		this.start = start;
	}
	public ID getEnd() {
		return end;
	}
	public void setEnd(ID end) {
		this.end = end;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Vector<ID> getIDs() {
		return IDs;
	}
	public void setIDs(Vector<ID> iDs) {
		IDs = iDs;
	}
	public boolean isInInterval(ID id) {
		//If id equals to start id and is in between start and end.
		if(start.compareTo(id)==0)
			return true;
		else if(id.isInInterval(start, end))
			return true;
		else
			return false;
	}
	public ID getSocketUserHash() {
		return socketUserHash;
	}
	public void setSocketUserHash(ID socketUserHash) {
		this.socketUserHash = socketUserHash;
	}
	public Hashtable<String,ID> getIdsAndHashes() {
		return IdsAndHashes;
	}
	public void setIdsAndHashes(Hashtable<String,ID> idsAndHashes) {
		//IdsAndHashes = idsAndHashes;
		IdsAndHashes.clear();
		IdsAndHashes.putAll(idsAndHashes);
	}
	
}