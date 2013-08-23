import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class InputReader{
	private String inputFileName=null;
	private BufferedReader reader=null;
	public InputReader(String fileName){
		this.setInputFileName(fileName);
		try {
			this.reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in opening the file");
			//e.printStackTrace();
		}

	}
	public String getInputFileName() {
		return inputFileName;
	}
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public String getString(){
		try {
			String s = reader.readLine();
			if(s.contains(":")){
				String[] tokens = s.split(":");
				if(tokens[0].equalsIgnoreCase("Wait")){
					long startTime = System.currentTimeMillis();
					int waitTime = Integer.parseInt(tokens[1])*1000;
					while(System.currentTimeMillis() -startTime < waitTime)
						;
					return getString();
				}
				else
					return null;
			}
			else if(s.contains("#")){
				return getString();
			}
			else{
				return s;
			}
				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in reading from the file");
			e.printStackTrace();
			return null;
		}
	}
	
	public int getInt(){
		
		return Integer.parseInt(getString());
	}
	
	public void close() throws Exception{
		this.reader.close();
	}

}
