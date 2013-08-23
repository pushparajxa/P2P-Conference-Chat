package Conference;

import de.uniba.wiai.lspi.chord.data.ID;

public class EndPoint{
	private int index;//EndPoints are like finger entries ..their index start from 1 and ends at ID.length
	private ID start;
	private ID end;
	public EndPoint(ID start ,ID end,int index){
		this.start = start;
		this.end = end;
		this.index = index;
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
}
