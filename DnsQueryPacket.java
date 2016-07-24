import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DnsQueryPacket { // purpose of extending UDP_Packet?
		
	//private final int UDP_HEADER_SIZE = 8; // length of UDP payload
	private int dnsPcktLen = 0;		// if non standard A record query, ie with
									// additional RR... bigger packet.
	private ByteBuffer bb;	
	
	
	private byte[] tempBuff;
	/*********************/
	/* DNS HEADER FIELDS */
	/*********************/
	public short transacID; // value = 2 bytes
	
	public short flags;
	
	public short qCount; // =1
		
	// total number of resource records found in answer section
	// client will never supply answers on initial query..
	// server query response fills in at least 1.
	public short answerRRs;
	
	// number of Name Server (NS) resource records (RR) found in
	// authority records section
	public short nsRRs; // wireshark: " authority rr's "
	
	// number of RRs in the additional records section
	public short additRRs;
			
	/************************/
	/* DNS QUESTION SECTION */
	/************************/
	public byte[] queryName;
	private int qNameLen = 0;
	
	// specifies type of query.
	// 0001 for A record query
	public short qType;  
	
	// represents IN for internet.
	public short qClass;	
	
	// this constructor is used when building a packet from received data..
	// data buffer contains received message from a socket..
	public DnsQueryPacket(byte[] data, int length){		
		 
		bb = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN); // natural for net recv		
		dnsPcktLen = length; // length of entire UDP payload received... udp dns query	
	
		// bytebuffer get operations on right side of assignment operator..
		// :gets: the first x bytes in the byte array... assigns them to variable on left side..
		// then increments position of buffer index.. walking array " "
		
		// initialize DNS header field values
		transacID = bb.getShort();		
		flags = bb.getShort();		
		qCount = bb.getShort(); // only works because the saved value is 0 or 1
		answerRRs = bb.getShort();
		nsRRs = bb.getShort();
		additRRs = bb.getShort();
		
		// initialize DNS question field values	
		// the entire query name length is 
		// FROM: the current .position of our ByteArray index
		// TO: the entire length of received query -- 4 byte (2-Qtype; 2-Qclass)
		// we need to create a byte array to hold query name.. this is a class value
		// but the length of the array is not determined until here..
		// we need to length to appropriately push forward the ByteArray buffer by qNameLen bytes.
		
		// end of qName = first occurence of 0 after additRRs
		// from bb.pos to dnsPacktLen.. find offset from pos to first 0
		tempBuff = new byte[255];
		// save cur position of bb. right at dns questions section[0]
		int tmpPos = bb.position();
		bb.get(tempBuff, 0, ((dnsPcktLen - 4) - bb.position())); // incase the udp payload has dns extensions. custom size
		
		qNameLen = calcQnameLength(tempBuff)+1;
		System.out.println("qNameLen >- " + qNameLen);
		bb.position(tmpPos); // reset back to orig		
		
		queryName = new byte[qNameLen]; 
		bb.get(queryName, 0, qNameLen); // relative method.. copy to buffer then push index+
						
		qType = bb.getShort();		
		qClass = bb.getShort();		
		
		// what this for again?
		// for all future bb related operations... they will be "put" --- overwritting buffer at idex0
		//bb.rewind();	
		
	} // end custom class constructor 1
	
	
	/*  
	 * These setters act on ByteBuffer and Byte objects.. not class.
	 * Instance class fields are set in constructor for new received packet
	 * these are set for outgoing packets.
	 * */
	public void setTransacID(short c){
		bb.putShort(c);
	}
	
	public void setFlags(short s){
		bb.putShort(s);		
	}
	
	public void setQCount(short s){
		bb.putShort(s);
	}
	
	public void setAnsRRs(short s){
		bb.putShort(s);
	}
	
	// NS rr's == authority RR's
	public void setNsRRs(short s){
		bb.putShort(s);
	}
	
	public void setAdditRRs(short s){
		bb.putShort(s);
	}
	
	public void setQName(byte[] q){
		bb.put(q, 0, q.length);
	}
	
	public void setQType(short s){
		bb.putShort(s);
	}
		
	public void setQClass(short s){
		bb.putShort(s);
	}
		
	public void setQNamePtr(short s){
		bb.putShort(s);
	}
	
	public void setTTL(int i){
		bb.putInt(i);
	}
		
	public void setDataLen(short s){
		bb.putShort(s);
	}
	
	public void setData(int i){
		bb.putInt(i);
	}
		
	/* 
	 * instance getters ( for outgoing messages )
	 */
	public short getTransacID(){
		return transacID;
	}
	
	public short getFlags() {
		return flags;
	}
	
	public short getQCount(){
		return qCount;
	}
	
	public short getAnswerRRs() {
		return answerRRs;
	}
	
	// Authority RRs
	public short getNsRRs() {
		return nsRRs;
	}
	
	public short getAdditRRs() {
		return additRRs;
	}	
	
	public byte[] getQueryName() {
		return queryName;
	}	

	public short getQType() {
		return qType;
	}

	public short getQClass() {
		return qClass;
	}
	
	// Miscellaneous getters
	public int getDnsPcktLen() {
		return dnsPcktLen;
	}
	
	public int getBBIndex() {
		return bb.position();
	}
	
	public int calcQnameLength(byte[] buf)
	{
		int length = 0;
		
		for (int i=0; i<buf.length; i++)
		{
			if(buf[i] == 0)
			{
				break;
			}
			length++;
		}
		
		return length; // null
	}
	
} // end DNS_Packet class