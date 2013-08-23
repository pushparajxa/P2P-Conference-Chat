package Conference;


public class TextMessage extends Message implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3545691346588089045L;
	
		private String Msg;
		private String ConfId;
		public TextMessage(String ConfId,String msg){
			this.setMsg(msg);
			this.setMessageName("TextMessage");
			this.ConfId = ConfId;
		}
		public String getMsg() {
			return Msg;
		}
		public void setMsg(String msg) {
			Msg = msg;
		}
		public Object clone(){
			try{
			TextMessage tm =(TextMessage)super.clone();
			tm.setMsg(new String(Msg));
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