import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class UserInterface extends Thread {
	
	private int commandOption;
	Scanner scanner = new Scanner(System.in);
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	// for now.. only allowing user to issue shell commands
	private final int TASK_CMD_TYPE_SHELL_CMD 	   = 1;
	private final int TASK_CMD_TYPE_NEW_SLEEP_TIME = 2;
	
	private final int TEMP_ONLY_CLIENT_ID = 0;
	
	byte[] cmdString;
	Task t;
	
	public UserInterface() {
		
		start(); // calls run
	}
	
	
	public void run() {
		
		displayUserPrompt();
		
		// interact with Listener worker/consumer/socket manager threads
		while (true) {			
			
			// retrieve a valid integer
		   System.out.print("Selection: ");
		   
		   while(!scanner.hasNextInt()) {
			    System.out.println("Enter a number >= 0 only ");
			    System.out.print("Choice: ");
			    scanner.next();
			}	 		   
		   commandOption = scanner.nextInt();	
		   
		   switch (commandOption) {  
		   		 				   					   		
		   		case 1:
				    cmdString = getCmdString();				   			    
				    
			   		t = new Task(TASK_CMD_TYPE_SHELL_CMD, cmdString.length, cmdString);		   		
			   				   		
					ClientManager.addTask(TEMP_ONLY_CLIENT_ID, t);
			   		System.out.println("[-] task added");							
		   			System.out.print("\n"); 
		   			break;
		   		/****************************************/	
		   			
		   		case 2:	   			
		   			cmdString = getSleepString();	
					   
		   			t = new Task(TASK_CMD_TYPE_NEW_SLEEP_TIME, cmdString.length, cmdString);   		
			   				   					   		
					ClientManager.addTask(TEMP_ONLY_CLIENT_ID, t);
					System.out.println("[-] task added");		   			
		   			System.out.print("\n"); 
		   			break;
		   		/****************************************/	
		   			
		   		default:
		   			break;			   	
		   } // end switch
		   
		   displayUserPrompt();
		   
		} // end while
	} // end run, thread returns..
	
	public byte[] getCmdString() {
   		
   		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   		String strCmd = null;
   		byte[] byteCmd = null;   		
   		
   		System.out.print("bash$ ");
   		try {
			strCmd = br.readLine();	
			byteCmd = new byte[ strCmd.length() ];  
			byteCmd = strCmd.getBytes("UTF-8");			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("caught exception.. ");
		}
   		   		   		
   		return byteCmd;   	
   	} // end function
	
	/*
	 * going to need to convert an integer to a 4 byte char array
	 */
	public byte[] getSleepString() {
   		
   		ByteBuffer bb = ByteBuffer.allocate(4);   		
		
		System.out.print("Sleep time in seconds: ");
		int sleepTime = 60;		
		   
	    while(!scanner.hasNextInt()) {
	    	System.out.println("Enter a number >= 0 only ");
	    	System.out.print("Sleep time in seconds: ");
		    scanner.next();
		 }
	    
	    sleepTime = scanner.nextInt();
   		bb.putInt(sleepTime);
   		
	    return bb.array();   	
   	} // end function
	
	
	public void displayUserPrompt() {
		System.out.println("[+] Welcome to the UserInterface	\n" +
						   "[-] Available Commands so far    	\n" +
						   " ==============================  	\n" +						
						   " 1 => Run shell command on a client \n" +
						   " 2 => Adjust sleep time on a client <NOT WORKING> \n" +
						   " ==============================  	\n");
	}
	
	public int getClientID() {
		
		System.out.print("Client ID: ");
		int clientID = -1;
		   
	    while(!scanner.hasNextInt()) {
	    	System.out.println("Enter a number >= 0 only ");
		    System.out.print("Client ID: ");
		    scanner.next();
		 }
	    
	    clientID = scanner.nextInt();
	
	    return clientID;	
	} // end function
	
} // end class