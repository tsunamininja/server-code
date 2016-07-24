// Java Listener -- Listens and Accepts connections
// Using port 50123
// local IPv4 Eth Adaptor

/* NOTES:
 * perhaps dns packet classes should also have a buffer there..
 * basically keep all packet related data in classes..
 * use the listener classes just for the functionality of the listen/receive/send
 * functions of sockets. 
 * 
 * Issue 1 - how to send correct number of bytes back to dns client..?
 * Sol 1 - Add 16 bytes to length of dns query packet... kinda ugly.
 * 
 * Pos sol 2 - figure out position of bytebuffer buffer thing... after all the bb.get()s
 * that position will be the total length of the "push'd" data..
 * --- how to get tha value at run time allocated ?
 * 
 * perhaps copy send byte buffer array into new byte array with size known as BB.position()
 * 
 * 6-5-15: stopping at successful client dns query (dig) and response from Java server. (working)
 * 
 * TO DO: Test with multiple client types....
 * TO DO: Make the code less ugly... perhaps work out classes + functionality ?? uml..?
 * TO DO: Figure out GIT... how to push changes.
 * 
 * TIPS: When casting a hex value(s)... make sure to cast to correct function return value...
 * setInt()..... (int) 0x12.....
 * 
 * NOTES: bigendian --- how it looks 0x1234.. is how it is sent on hire.... ethernet|udp|1234
 * 
 * address contents     int a = 0x06070809... 09 = LSB.. little endian store LSB in the lowest address
 * ----------------
 * 3		06
 * 2		07
 * 1		08
 * 0		09		<= address of a = 0x00000000..
 * 
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.*;

public class Listener { 
	
	private final int dnsPort = 53;	
	private DatagramSocket s; 
	private DatagramPacket p;	
	
	private byte[] socketBuffer = new byte[512];	
		
	// constructor
   	public Listener() throws IOException { 

		s = new DatagramSocket(dnsPort);
		
		System.out.println(
				"[-] Java DNS Server \n" +
				"[-] Listen IP: "   + s.getLocalAddress().getHostAddress()+"\n"+
				"[-] Listen Port: " + s.getLocalPort()  + "\n");  
		
		createSession();
   	}   	
   	
   	// main thread?.. spins off threads
   	public void createSession() throws IOException {     		
   		
   		while (true) {
   			
   		    p = new DatagramPacket(socketBuffer, socketBuffer.length);  
   			s.receive(p);
   			
	   		// new thread for datagramPacket.. starts a new "session" per thread?				
			new Session(s, p); // session(s,p, clientID)? ..				
   		}
    } // end void run()
   	
   	
   	public static void main(String[] args) throws IOException {  
   					   		
   		// handles socket communication and per client session task dispatching etc..   		
		new Listener();	// right now listener has no stdout or println active statements..			
    }    	 
    
} // end class Listener