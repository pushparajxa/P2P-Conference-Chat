/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * key's maintained
 * userID:OM  offline messages
 * userID:IP node.url
 * userID:P pwd
 * user_list:A,user_list:B......data stored as full_name#userID
 * userID:FL friends list
 * userID:PFR pending friends request (userID+"#"+userFullName)
 * userID:FN user's full_name
 */


import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import management.mgmtMainListenerThread;
import management.mgmtMainRequesterThread;
import Conference.ConfListenerThread;
import Conference.Conference;
import Conference.EndPointContainer;
import Conference.OutSocket;
import Conference.TextMessage;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;


public class Chatp2p1_old {

     Chord chord1;
    String userID;
     String userFullName;
     URL bootstrapURL;
     char comingFromSignin = 'N';
     Hashtable<String, chatMsgHolder> chatTable;
     Hashtable<String,Conference> confTable;
     Hashtable<String,OutSocket> confOutSockets;
     Thread MainChatServerThread;
     ConfListenerThread ConfListenerServerThread;
     ID nodeHash;
     EndPointContainer endPoints;
     ServerSocket ConfChatListenerServerSocket; 
     ServerSocket mgmtSenderSocket;
     ServerSocket mgmtListenerSocket;
     ServerSocket chatServerSocket;
     mgmtMainListenerThread mgmtListenThd;
     mgmtMainRequesterThread mgmtRqstThd;
     ArrayList<String> servedRequests =  new ArrayList<String>();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      
    	Chatp2p1_old chat_app = new Chatp2p1_old();
    	try {
    		chat_app.bootstrapURL =new URL(args[0]);
			chat_app.startExecution(args[1],args[2]);
		} catch ( Exception e) {
			System.out.println("Error in chat application execution"+args[0]);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    
    public void startExecution(String port,String idVal)throws UnknownHostException, MalformedURLException, ServiceException, IOException {
    	
    	  PropertiesLoader.loadPropertyFile();
          String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
          
          URL localURL = null;
         
  //localURL.getProtocol()
         
         
          chord1 = new de.uniba.wiai.lspi.chord.service.impl.ChordImpl();
         ChordImpl cg = (ChordImpl)chord1;
         if(idVal.equalsIgnoreCase("After")){
        	 byte arr[] = new byte[20];
        	 for(int i=0;i<18;i++){
        		 arr[i]=0;
        	 }
        	 arr[19]=120;
        	 chord1.setID(new ID(arr));
         }
         else if(idVal.equalsIgnoreCase("Before")){
        	 byte arr[] = new byte[20];
        	 for(int i=0;i<18;i++){
        		 arr[i]=Byte.MAX_VALUE;
        	 }
        	 
        	 arr[19]=126;
        	 chord1.setID(new ID(arr));
         }
         //else normal
          
  //URL url = new URL("http", host, port + i, "");
          localURL = new URL("ocsocket://"+InetAddress.getLocalHost().getHostAddress()+":"+port+"/");
          try {
              //chord1.create ( localURL );
              chord1.join(localURL, bootstrapURL);
              nodeHash =chord1.getID();
             
              this.endPoints = new EndPointContainer(nodeHash);
              
              System.out.println("node 1 jooined");
              System.out.println("Node Id is "+chord1.getID()+ " and node length is "+chord1.getID().getLength());
          } catch (ServiceException e2) {
              throw new RuntimeException(" Could not join DHT ! ", e2);
          }
          printMainMenu();
    	
    }
    
    public  void printMainMenu() throws IOException, ServiceException {
        while (true) {

            System.out.println("========================================");
            System.out.println("|   Welcome to P2P chat application    |");
            System.out.println("========================================");
            System.out.println("| Options:                             |");
            System.out.println("|        1. Signup                     |");
            System.out.println("|        2. Signin                     |");
            System.out.println("|        3. Exit                       |");
            System.out.println("========================================");
            int swValue = Keyin.inInt(" Select option: ");

            //int choice = printMainMenu();   // make sure it's an int
            switch (swValue) {
                case 1:
                    signup();
                case 2:
                    signin();
                case 3:
                    exit();
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }
    }

    public  void userMenu() throws IOException, ServiceException {
        //char comingFromSig//nin = 'Y';
        if (comingFromSignin == 'Y') {
            //give update to user abt all the options together
            quickalert();
        }
        while (true) {

            System.out.println("=============================================");
            System.out.println("|   Welcome " + userID + "                  |");
            System.out.println("=============================================");
            System.out.println("| Options:                                  |");
            System.out.println("|        1. Search  &  send Friends request |");
            System.out.println("|        2. Retrieve Offline Messages       |");
            System.out.println("|        3. Pending Freinds Request         |");
            System.out.println("|        4. Chat                            |");
            System.out.println("|        5. Conference                      |");
            // System.out.println("|        5.                           |");
            System.out.println("|        6. Logout                          |");
            System.out.println("=============================================");
            int swValue = Keyin.inInt(" Select option: ");

            switch (swValue) {

                case 1:
                    search();
                    break;
                case 2:
                    retrieveOfflineMessages();
                    break;
                case 3:
                    pendingFreindRequest();
                    break;
                case 4:
                    chat();
                    break;
                case 5:
                	conference();
                	break;
                case 6:
                    logout();
                    break;

                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }
    }

    public  void signin() throws IOException, ServiceException {

        System.out.println("Enter your user id: ");
        Scanner sc = new Scanner(System.in);
        Console console = System.console();
        String first_name = sc.next().trim();
        Set<Serializable> retrieve_pwd;
        retrieve_pwd = retrieveData(first_name + ":P");

        if (retrieve_pwd.isEmpty()) {
            System.out.println("**********************************");
            System.out.println("Incorrect User ID!Please Try again");
            System.out.println("**********************************");
            printMainMenu();
        }
       
        String password =  Keyin.inString("Enter your password: ");
        password = "[" + password + "]";

        if (password.compareTo(retrieve_pwd.toString()) == 0) {
            System.out.println("Login succesful");
            userID = first_name;
            Set<Serializable> retrieveData = retrieveData(userID + ":FN");
            userFullName = retrieveData.toString();
           // store(userID + ":IP", chord1.getURL().toString());
            store(userID + ":IP",InetAddress.getLocalHost().getHostAddress() );
            store(this.nodeHash.toString(),userID );
            comingFromSignin = 'Y';
            /*****
             * Code added by Pushparaj Motamari
             */
            // Intiate the chatTable for holding the chat Messages.
           
            // Start the Chat Server which listens request to chat from other users.
           //store(userID+":ID",this.nodeHash);
            try{
            	chatServerSocket = new ServerSocket(0);
            	store(userID+":ChatPort",new Integer(this.chatServerSocket.getLocalPort()).toString());
            	chatTable  = new Hashtable<String, chatMsgHolder>();
            	 MainChatServerThread =  new MainChatServerThread(chatServerSocket,chatTable);
                 MainChatServerThread.start();
            }
            catch(Exception e){
            	System.out.println("Unable to open socket for chat Server which accpets incoming chat requests");
            }
            
            try{
                this.ConfChatListenerServerSocket = new ServerSocket(0);
                store(userID+":ConfPort",new Integer(this.ConfChatListenerServerSocket.getLocalPort()).toString());
                this.confTable =  new Hashtable<String,Conference>();
                this.confOutSockets =  new Hashtable<String,OutSocket>();
                this.ConfListenerServerThread  = new ConfListenerThread(this.ConfChatListenerServerSocket,this.confTable,endPoints,this.nodeHash,this.userID,this.confOutSockets,(ChordImpl)this.chord1);
                this.ConfListenerServerThread.start();
                }
                catch(IOException ie){
                	System.out.println("Unable to open a socket for conference chat for user "+this.userID);
                }
            //put the ID in the chord.
                chord1.insert(new StringKey(this.userID+":ID"),this.chord1.getID());
                
                //start the management listener and sender thread
                try{
                this.mgmtListenerSocket = new ServerSocket(0);
                store(userID+":mgmtPort",new Integer(this.mgmtListenerSocket.getLocalPort()).toString());
                this.mgmtListenThd =  new mgmtMainListenerThread(this.mgmtListenerSocket, (ChordImpl)this.chord1,this.confTable);
                this.mgmtListenThd.start();
                }
                catch(Exception e){
                	System.out.println("Unable to open socket for listening manamgement requests");
                	e.printStackTrace();
                }
                
                try{
                this.mgmtSenderSocket = new ServerSocket(0);
                this.mgmtRqstThd =  new mgmtMainRequesterThread(this.mgmtSenderSocket, (ChordImpl)this.chord1,this.confTable,servedRequests,userID);
               // this.mgmtRqstThd.start();
                }
                catch(Exception e){
                	System.out.println("Unable to open socket for sending mgmt requests ");
                	e.printStackTrace();
                }
                
            userMenu();
         
        } else {
            System.out.println("incorrect password");
            printMainMenu();
        }

    }

    private void store(String keyName, ID nodeHash2) {
    	 try {
             StringKey myKey = new StringKey(keyName);
             chord1.insert(myKey, nodeHash2);
             
         } catch (ServiceException ex) {
             Logger.getLogger(Chatp2p1.class.getName()).log(Level.SEVERE, null, ex);
             ex.printStackTrace();
         }
		
	}


	private  void signup() throws ServiceException, IOException {
        boolean loop = true;

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the full name");
        String full_name = sc.nextLine();
        full_name = full_name.trim();
        while (loop) {
            Scanner sc1 = new Scanner(System.in);
            System.out.println("Enter the requested user id");
            String temp = sc1.next().trim();
            Set<Serializable> retrieve_pwd = retrieveData(temp + ":P");
            if (retrieve_pwd.isEmpty()) {
                loop = false;
                boolean loop1 = true;
                while (loop1) {

                   // Console console = System.console();
                   
                    
                    String passwordString = Keyin.inString("Enter your password");

                    String passwordString1 =Keyin.inString("Enter your password again");;
                    if (passwordString.equals(passwordString1)) {
                        userID = temp;
                        store(userID + ":P", passwordString);
                        loop1 = false;
                        System.out.println("User successflly registered");
                        storeFullName(full_name, userID);
                        userFullName = full_name;
                        store(userID + ":FN", userFullName);
                        store(userID+"UID","REG:"+userID);//store the userId. Which is used in counting number of registered users.
                        store(userID + ":IP", InetAddress.getLocalHost().getHostAddress());
                        store(this.nodeHash.toString(),userID );
                        /*****
                         * Code added by Pushparaj Motamari
                         */
                        // Initiate the chatTable for holding the chat Messages.
                        try{
                        	chatServerSocket = new ServerSocket(0);
                        	store(userID+":ChatPort",new Integer(this.chatServerSocket.getLocalPort()).toString());
                        	chatTable  = new Hashtable<String, chatMsgHolder>();
                        	 MainChatServerThread =  new MainChatServerThread(chatServerSocket,chatTable);
                             MainChatServerThread.start();
                        }
                        catch(Exception e){
                        	System.out.println("Unable to open socket for chat Server which accpets incoming chat requests");
                        }
                        //store the NodeID .which is used in conference.
                        chord1.insert(new StringKey(this.userID+":ID"),this.chord1.getID());
                        
                        try{
                        this.ConfChatListenerServerSocket = new ServerSocket(0);
                        store(userID+":ConfPort",new Integer(this.ConfChatListenerServerSocket.getLocalPort()).toString());
                        this.confTable =  new Hashtable<String,Conference>();
                        this.confOutSockets =  new Hashtable<String,OutSocket>();
                        this.ConfListenerServerThread  = new ConfListenerThread(this.ConfChatListenerServerSocket,this.confTable,endPoints,this.nodeHash,this.userID,this.confOutSockets,(ChordImpl)this.chord1);
                        this.ConfListenerServerThread.start();
                        }
                        catch(IOException ie){
                        	System.out.println("Unable to open a socket for conference chat for user "+this.userID);
                        }
                        
                        
                        //start the management listener and sender thread
                        try{
                        this.mgmtListenerSocket = new ServerSocket(0);
                        store(userID+":mgmtPort",new Integer(this.mgmtListenerSocket.getLocalPort()).toString());
                        this.mgmtListenThd =  new mgmtMainListenerThread(this.mgmtListenerSocket, (ChordImpl)this.chord1,this.confTable);
                        this.mgmtListenThd.start();
                        }
                        catch(Exception e){
                        	System.out.println("Unable to open socket for listening manamgement requests");
                        	e.printStackTrace();
                        }
                        
                        try{
                        this.mgmtSenderSocket = new ServerSocket(0);
                        this.mgmtRqstThd =  new mgmtMainRequesterThread(this.mgmtSenderSocket, (ChordImpl)this.chord1,this.confTable,servedRequests,userID);
                        //this.mgmtRqstThd.start();
                        }
                        catch(Exception e){
                        	System.out.println("Unable to open socket for sending mgmt requests ");
                        	e.printStackTrace();
                        }
                      
                        userMenu();
                    } else {
                        System.out.println("passwrods do not match.Re-enter again");


                    }
                }
            } else {
                System.out.println("requested user id is already exists");
            }
        }
    }

    private  void storeFullName(String full_name, String userID) {
        String list_name;
        System.out.println("full name" + full_name);
        String temp = full_name;
        String names[] = full_name.split(" ");
        for (int i = 0; i < names.length; i++) {
            list_name = "user_list:" + Character.toUpperCase(names[i].charAt(0));
            store(list_name, temp + "#" + userID);
        }
    }

    public void store(String keyName, String data) {
        try {
            StringKey myKey = new StringKey(keyName);
            chord1.insert(myKey, data);
        } catch (ServiceException ex) {
            Logger.getLogger(Chatp2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void remove(String keyName, String data) {
        try {
            StringKey myKey = new StringKey(keyName);
            chord1.remove(myKey, data);
        } catch (ServiceException ex) {
            Logger.getLogger(Chatp2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private  void search() throws ServiceException {
        Scanner search = new Scanner(System.in);
        System.out.println("Enter the name to search");
        String searchName = search.nextLine().trim();
//        String[] temp = searchName.split(" ");
        Set<Serializable> user_list = retrieveData("user_list:" + Character.toUpperCase(searchName.charAt(0)));
        Iterator it = user_list.iterator();
        List<String> myList_names = new ArrayList<String>();
        List<String> myList_ID = new ArrayList<String>();

        while (it.hasNext()) {
            String temp2 = it.next().toString();
            if (temp2.toLowerCase().contains(searchName.toLowerCase())) {
                String[] temp3 = temp2.split("#");
                myList_ID.add(temp3[1]);
                myList_names.add(temp3[0]);
                //System.out.println(temp3[0] + "..." + temp3[1]);
            }
        }

        for (int i = 0; i < myList_ID.size(); i++) {
            System.out.println(i + 1 + "-" + myList_names.get(i) + "(" + myList_ID.get(i) + ")");
        }
        System.out.println("Please type the user_id of the friend you want to send friends request");
        Scanner sc = new Scanner(System.in);
        String temp = sc.next().trim();
        boolean success = true;
        while (success) {
            if (myList_ID.contains(temp.trim())) {
                /* check if the friend is online else send it offline*/
                System.out.println("friend request is sent to" + temp);
                store(temp + ":PFR", userID + "#" + userFullName);
                success = false;
            } else {
                System.out.println("wrong user ID entered.retry");
            }
        }
        //Collection<String> luckyNumbers = new ArrayList<>();
    }

    private Set<Serializable> retrieveData(String string) throws ServiceException {
        StringKey user_key = new StringKey(string);
        Set<Serializable> retrievedData = chord1.retrieve(user_key);
        return retrievedData;
    }

    private  void logout() throws IOException, ServiceException {
            /**
             * Code added by Pushparaj Motamari
             */
    	//Interrupt the MainChatServerThread to stop accepting connections;
    	MainChatServerThread.interrupt();
            remove(userID + ":IP", InetAddress.getLocalHost().getHostAddress());
            remove(userID+":ChatPort",new Integer(this.chatServerSocket.getLocalPort()).toString());
            remove(userID+":ConfPort",new Integer(this.ConfChatListenerServerSocket.getLocalPort()).toString());
            remove(userID+":mgmtPort",new Integer(this.mgmtListenerSocket.getLocalPort()).toString());
            remove(this.nodeHash.toString(),userID );            
            System.out.println(userID + "logged out succesfully");
            userID = null;
            userFullName = null;
            printMainMenu();
            
        
    }

    private  void retrieveOfflineMessages() throws IOException, ServiceException {

        //        Assuming that messages are in format (sender_ID:Messages)

        Set<Serializable> offlineMessages, offlineMessagesTemp;

        try {
            offlineMessages = retrieveData(userID + ":OM");
            offlineMessagesTemp = offlineMessages;
            if (offlineMessages.isEmpty()) {
                System.out.println("No offline Messages for you DUDE");
                userMenu();
            }
            Iterator om = offlineMessages.iterator();
            System.out.println("**Inbox of" + userID + "*******");
            while (om.hasNext()) {
                System.out.println(om.next().toString());
            }
            System.out.println("****************************************");
            System.out.println("| Options:                             |");
            System.out.println("|        1. Delete all Messages        |");
            System.out.println("|        2. Goback to previous menu    |");
            System.out.println("|        3. Logout                     |");
            System.out.println("****************************************");
            int swValue = Keyin.inInt(" Select option: ");
            switch (swValue) {

                case 1:
                    removeMessages(offlineMessagesTemp);
                case 2:
                    userMenu();
                case 3:
                    logout();
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        } catch (ServiceException ex) {
            System.out.println("No offline Messages for you DUDE");
            userMenu();
        }
    }

    private  void removeMessages(Set<Serializable> offlineMessagesTemp) throws IOException, ServiceException {
        Iterator om = offlineMessagesTemp.iterator();
        System.out.println("Deleting Messges");
        while (om.hasNext()) {
            remove(userID + ":OM", om.next().toString());
            System.out.println("---------------------");
        }
        userMenu();
    }

    private void pendingFreindRequest() throws ServiceException, IOException {
        Set<Serializable> PFL = retrieveData(userID + ":PFR");
        if (!(PFL.isEmpty())) {
            Iterator it = PFL.iterator();
            List<String> List_names = new ArrayList<String>();
            List<String> List_ID = new ArrayList<String>();
            System.out.println("You have pending friends request from");
            while (it.hasNext()) {
                String temp2 = it.next().toString();
                //if (temp2.toLowerCase().contains(searchName.toLowerCase())) {
                String[] temp3 = temp2.split("#");
                List_ID.add(temp3[1]);
                List_names.add(temp3[0]);
                System.out.println(temp3[0] + "..." + temp3[1]);
                //}
            }
            System.out.println("plese type 1 to accept all,2 to remove all requests or the userID's to accept the request separated by comma");
            Scanner search = new Scanner(System.in);
            String input = search.nextLine().trim();
            String[] friendID = null;
            if (input.equals("1")) {
                friendID = List_ID.toArray(new String[List_ID.size()]);
            } else if (input.equals("2")) {
                for (int i = 0; i < List_ID.size(); i++) {
                    remove(userID + ":PFR", List_names.get(i) + "#" + List_ID.get(i));
                    System.out.println("Friend request from " + List_names.get(i) + "Removed");

                    userMenu();
                }
            } else {
                friendID = input.split(",");
            }


            for (int i = 0; i < friendID.length; i++) {
                if (List_ID.contains(friendID[i])) {
                    store(friendID[i] + ":FL", userID);
                    store(userID + ":FL", friendID[i]);
                    int id = List_ID.indexOf(friendID[i]);
                    remove(userID + ":PFR", List_names.get(id) + "#" + List_ID.get(id));
                    System.out.println("Friend request from" + friendID[i] + "accepted");
                } else {
                    System.out.println(friendID + "'s Friend request not sent !Incorrect userID entered");
                }


            }
        } else {
            System.out.println("You have NO  pending friends request");
            userMenu();
        }
        userMenu();
    }

    private  void chat() throws IOException, ServiceException {
    	/**
    	 * Code added by Pushparaj Motamari
    	 */
    	
    	while(true){
    		 System.out.println("****************************************");
             System.out.println("| Options:                                  |");
             System.out.println("|        1. Send Message to the User        |");
             System.out.println("|        2. Read messages from the Users.   |");
             System.out.println("|        3. List the friends you are chating with.   |");
             System.out.println("|        4. Goback to previous menu         |");
             System.out.println("****************************************");
             int swValue = Keyin.inInt(" Select option: ");
             
             switch (swValue) {

             case 1:
            	 String user_Id,msg;
            	 Socket s;
            	 user_Id = Keyin.inString("Please enter the user id of the friend you want to chat with");
            	// System.out.println("user Id you want to chat with is "+ user_Id );
             	if(chatTable.get(user_Id)==null){
             		Socket sckt;
             		//String user_Ip="192.168.111.130",user_Port="8098";
             		 Set<Serializable> retrieveData = retrieveData(user_Id + ":IP");
             		String user_Ip=null;
             		 for(Serializable s2: retrieveData){
             			 user_Ip = s2.toString();
             		 }
                     
                     Set<Serializable> retrievePort = retrieveData(user_Id + ":ChatPort");
                     String user_Port = null;
                     for(Serializable s2: retrievePort){
                    	 user_Port = s2.toString();
             		 }
                     
                     System.out.println("The port of the user is "+user_Port+" . The ip address is "+user_Ip);
                     
             		try{
             			sckt = new Socket(user_Ip, Integer.parseInt(user_Port));
             		}
             		catch(Exception e){
             			System.out.println("Error in connecting with the user you want to chat with");
             			while(true){
             				System.out.println("****************************************");
                            System.out.println("| Options:                                  |");
                            System.out.println("|        1. Send Offline-Message to the User    |");
                            System.out.println("|        2. Goback to previous menu.         |");
                            int swValue2 = Keyin.inInt(" Select option: ");
                            if(swValue2 == 1){
                            	String g = Keyin.inString("Please enter the message");
                            	if(g!=null)
                            		store(user_Id+":OM","OM:"+g);
                            		
                            }
                            else{
                            	break;
                            }
                 			
             			}
             			break;
             		}
             		new ClientChatThread(sckt,user_Id,chatTable).start();
             		//send the userID of this user.
             		 OutputStream os = sckt.getOutputStream();
		        	  PrintWriter pw =  new PrintWriter(os, true);
		        	  pw.println(this.userID);
		        	//  pw.close();
		        	 // os.close();
             		
             	}
             	
             	msg=Keyin.inString("Please enter the message\n");
             	
    			if (msg != null) {
    				   
    		           if((s=chatTable.get(user_Id).getSocket())!=null){
    		        	   OutputStream os = s.getOutputStream();
    		        	  PrintWriter pw =  new PrintWriter(os, true);
    		        	  pw.println(msg);
    		        	//  pw.close();
    		        	  //os.close();
    		        	  
    		           }
    		           if(msg.equalsIgnoreCase("Bye")){
    		        	   chatTable.get(user_Id).setEndChat(true);
    		           }
    			}
    			break;
    			
             case 2:
            	 Set<Entry<String, chatMsgHolder>> se =chatTable.entrySet();
            	 for(Map.Entry<String,chatMsgHolder> entry: se){
            		 if(entry.getValue().anyNewMsg()){
            			 for(String msge: entry.getValue().getMsgs()){
            				 System.out.println(entry.getKey()+"$$"+msge);
            			 }
            				 
            		 }
            		 if(entry.getValue().isEndChat()){
            			 //Remove the userId entry from the table;
            			 //Sockets were closed in the corresponding threads.
            			 chatTable.remove(entry.getKey());
            		 }
            	 }
            	 break;
            	 
             case 3:
            	 Set<String> frnd_lst = chatTable.keySet();
            	 for(String userId: frnd_lst){
            		 System.out.println("Friend UserId is$$"+userId);
            	 }
                 break;
             case 4:
            	 return;
         }//switch
             
             
    	}//while
    }

    private  void quickalert() throws IOException, ServiceException {
        Set<Serializable> offlineMessages, pendingFriendRequests;
        offlineMessages = retrieveData(userID + ":OM");
        pendingFriendRequests = retrieveData(userID + ":PFR");
        int count_offMess = offlineMessages.size();
        int count_pendFrndReq = pendingFriendRequests.size();
        System.out.println("You have " + count_offMess + "offline Messages");
        System.out.println("You have " + count_pendFrndReq + "pending friend requests");
        comingFromSignin = 'N';
        userMenu();
    }

    private void conference(){
    	while(true){
    		
    		System.out.println("****************************************");
            System.out.println("| Options:                                  |");
            System.out.println("|        1. Create a new Conference.        |");
            System.out.println("|        2. Select a confernce.             |");
            System.out.println("|        3. List the conferences.           |");
            System.out.println("|        4. Goback to previous menu         |");
            System.out.println("****************************************");
            int swValue = Keyin.inInt(" Select option: ");
            
            switch (swValue) {
            
            case 1:
            	Conference cf = new Conference(this.userID,this.endPoints,this.confOutSockets,this.confTable,this.nodeHash,(ChordImpl)this.chord1);
            	this.confTable.put(cf.getConfId(), cf);
            	break;
            	
            case 2:
            	String cfId = Keyin.inString("Enter the Conference Id");
            	Conference cfr = confTable.get(cfId);
            	if(cfr==null){
            		System.out.println("Entered Conference Id is not Correct.Please enter the correct one.");
            		break;
            	}
            	int sv;boolean cnt=true;
            	while(cnt){
            		System.out.println("****************************************");
                    System.out.println("| Options:                                  |");
                    System.out.println("|        1. Invite a new User to the Conference.|");
                    System.out.println("|        2. Send a message.            |");
                    System.out.println("|        3. Leave the conference.             |");
                    System.out.println("|        4. Read the messages.         |");
                    System.out.println("|        5. Goback to previous menu         |");
                    System.out.println("****************************************");
                    sv = Keyin.inInt(" Select option: ");
                    
                    switch (sv){
                    
                    case 1:
                    	String userToinvite = Keyin.inString("Enter the userId of the friend you want to invite");
                    	if(this.confOutSockets.get(userToinvite)==null){
                    		try{
                        		Set<Serializable> s = this.retrieveData(userToinvite+":IP");
                        		String IpAddress="";
                        		for(Serializable str:s){
                        			IpAddress = IpAddress.concat(str.toString());
                        			
                        		}
                        		System.out.println("The ipaddress of the user is "+IpAddress);
                        		String ConfPort="";
                        		try{
                            		Set<Serializable> pt = this.retrieveData(userToinvite+":ConfPort");
                            		
                            		for(Serializable str:pt){
                            			ConfPort = ConfPort.concat(str.toString());
                            			
                            		}
                        		}
                        		catch(Exception e){
                        			System.out.println("Unable to get the conference port");
                        		}
                            	System.out.println("The ipaddress of the user is "+IpAddress+" and conference port is "+ConfPort);
                        		Socket skt = new Socket(IpAddress,Integer.parseInt(ConfPort));
                        		OutSocket os = new OutSocket(skt,userToinvite);
                        		os.addConfId(cfr.getConfId());
                        		this.confOutSockets.put(userToinvite,os );
                        		Set<Serializable> sw = this.retrieveData(userToinvite+":ID");
                        		Object arr[] =sw.toArray();
                        		cfr.Invite(userToinvite,(ID)arr[0]);
                        	}
                        	catch(Exception e){
                        		System.out.println("Error while retrieving the IP address of the user.");
                        		e.printStackTrace();
                        	}
                    	}
                    	else{
                    		Set<Serializable> sw;
							try {
								sw = this.retrieveData(userToinvite+":ID");
								Object arr[] =sw.toArray();
								this.confOutSockets.get(userToinvite).addConfId(cfr.getConfId());
	                    		cfr.Invite(userToinvite,(ID)arr[0]);
							} catch (ServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                    		
                    	}
                    		
                    	
                    	
                    	break;
                    case 2:
                    	String msgToSend = Keyin.inString("Enter the message to send");
                    	cfr.sendMessage( new TextMessage(cfr.getConfId(), msgToSend ) );
                    	cfr.getMessages().add(msgToSend);
                    	break;
                    case 3:
                    	cfr.leave();
                    	cnt=false;//leave this loop.
                    	break;
                    case 4:
                    	for(String msg :cfr.getMessages()){
                    		System.out.println(cfId+"::"+msg);
                    	}
                    	break;
                    case 5:
                    	cnt =false;
                    	break;
                    }           
                  
            	}
            	break;
            	
            case 3:
            	System.out.println("Printing the List of Conference");
            	int i=0;
            	for(String s:this.confTable.keySet()){
            		System.out.println(i+" "+s);
            		i++;
            	}
            	break;
            	
            case 4:
            	return;
            
            }
    	}
   		 

    }
    private  void exit() {
        try {
            chord1.leave();
        } catch (Exception e) {
            try {
                chord1.leave();
               // Logger.getLogger(Chatp2p1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServiceException ex1) {
                Logger.getLogger(Chatp2p1.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
       
        System.exit(0);
       // throw new UnsupportedOperationException("Not yet implemented");
    }
}
