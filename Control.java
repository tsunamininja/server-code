import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * 12/15/15
 * 
 * This class represents a data structure used to represent
 * various control self constructed flags associated with each DNS query.
 * 
 * These control flags affect the management and control aspects
 * of incoming client queries and their individual "sessions" 
 * with the ClientManager class 'ui'
 * class mainly used for controlling the handling of received messages and session mgmnt,.
 */
public class Control {
		
	private ByteBuffer bb;
	
	/* the below values are parsed from the $dnsQueryName portion of a dns request */
	private int hostLabelLength;
	private int clientID; // = queryName[	]..
	private int messageType;	
	private short dataSegmentLength; // if has data - print, else no printing
	private byte[] dataOutput; // stdout "chunk per mesg".. file... config... from client	
		
	/* these final values are for byte buffer's absolute get Methods operating on byte bufs */	
	// 1 = index 1.. "second byte into buffer"
	private final int DNS_QUERY_NAME_LABEL_LENGTH_INDEX 	   = 0;
	private final int DNS_QUERY_NAME_CLIENT_ID_INDEX 		   = 1;
	private final int DNS_QUERY_NAME_MESSAGE_TYPE_INDEX		   = 2;	
	private final int DNS_QUERY_NAME_DATA_SEGMENT_LENGTH_INDEX = 3; 	
	
	public Control(DnsQueryPacket dqp) {
		
		// byte buffer time		
		bb = ByteBuffer.wrap(dqp.queryName).order(ByteOrder.LITTLE_ENDIAN); 				
		hostLabelLength 	= setHostLabelLength();	//Data.say("hostLabelLength: "    + hostLabelLength);	
		clientID 			= setClientID();		//Data.say("clientID: " 			+ clientID);	
		messageType 		= setMessageType();		//Data.say("messageType : " 		+ messageType);
		dataSegmentLength 	= setSegmentLength();	
		
		// if packet is not sending us data, do not try to save "data" to a buffer..
		if (messageType == 81) {
			setDataOutput();
		}
	}	
	
	private int setHostLabelLength() {		
		return bb.get(DNS_QUERY_NAME_LABEL_LENGTH_INDEX);
	}

	/*
	 * since we are creating control fields based on data from the field in another packet.
	 * this will be all buffer operations operating on from start -> end and count slicing..
	 */
	private int setClientID() {			
		return bb.get(DNS_QUERY_NAME_CLIENT_ID_INDEX);		
	}
	
	private int setMessageType() {			
		return bb.get(DNS_QUERY_NAME_MESSAGE_TYPE_INDEX);		
	}	
	
	// 2 bytes (signed java short)
	private short setSegmentLength() {			
		return bb.getShort(DNS_QUERY_NAME_DATA_SEGMENT_LENGTH_INDEX);		
	}
	
	// public ByteBuffer get(byte[] dst, int offset, int length)
	
	
	private void setDataOutput() {					
		
		// make sure has data.. although these fields should never be zero
		// if messageType = 0x51..
		///if (getTotalDataLength() > 0 && getDataSegmentLength() > 0) {
			// -1 for label length - 1 control byte
			dataOutput = new byte[ (getDataSegmentLength()-1) ];			
			
			// copy amount of Segment length data into dataOutput buffer starting
			// at index 0 of dataOutput and with a src buffer location of bb.pos
			// which would be 0 if we have been using absolute function calls
			//bb.position(DNS_QUERY_NAME_DATA_SEGMENT_START_INDEX);
			// bb.pos should be already ok
			
			// catch byte buffer get from crashing
			if (getDataSegmentLength() <= bb.remaining()) {
				bb.get(dataOutput, 0, getDataSegmentLength()-1 );	
			}
			else {
				Data.say("caught buffer underflow... not enough bytes to fill dataOutput buffer");
			}														
		///}		
	}
			
		
	/*
	 *  getters	   
	 */
	public int gethostLabelLength() {
		return hostLabelLength;
	}
	
	public int getClientID() {		
		return clientID;
	}
	
	public int getMessageType() {
		return messageType;
	}
	
	///public int getTotalDataLength() {
	///	return totalDataLength;
	///}
	
	public int getDataSegmentLength() {
		return dataSegmentLength;
	}
	
	public byte[] getDataOutput() {
		return dataOutput;
	}
}