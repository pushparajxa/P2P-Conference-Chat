package Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class logger{
	private static String outPutFilePath;
	private static PrintWriter file=null;
	public static String getOutPutFile() {
		
		return outPutFilePath;
	}

	public static void setOutPutFile(String outPutFilePath)  {
		logger.outPutFilePath = outPutFilePath;
		if(file == null){
			try {
				file = new PrintWriter(new BufferedWriter(new FileWriter(logger.outPutFilePath,true)));
				file.flush();
				System.out.println("Successfully opened the logoutput file.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void  log(String s){
		try {
			//file.write(s+"\n");
			file.println(s);
			//System.out.println("Logging string "+s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to write to the file");
			
		}
	}
	
	public static void log(Exception e){
		e.printStackTrace(file);
	}
	public static void close(){
		file.close();
	}
}