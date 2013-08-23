package Conference;

import java.util.Vector;

import de.uniba.wiai.lspi.chord.data.ID;

public class EndPointContainer{
	//EndPoints are like finger entries ..their index start from 1 and ends at ID.length
	private Vector<EndPoint> endPoints = new Vector<EndPoint>();
	public EndPointContainer(ID seed){
		generateEndPoints(seed);
	}
	private void generateEndPoints(ID seed) {
		int len = seed.getLength();
		int i=0;
		ID prev = seed.addPowerOfTwo(0);
		ID next;
		for(i=0;i<len-1;i++){
			next = seed.addPowerOfTwo(i+1);
		  endPoints.add(new EndPoint(prev,next,i+1));
			prev = next;
		}
		if(i==len-1)
			endPoints.add(new EndPoint(prev,seed,i+1));
		
	}
 
	public Vector<EndPoint>getEndPoints(){
		return endPoints;
	}
	 
}