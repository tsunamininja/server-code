import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * in this class, it is not expected to need to retreive any instance object
 * class members because it is used in building a response and thus the values
 * are known from the previous query received in addition to our own values inputting...
 * 
 * the important part is that we are constructing a byte buffer to pass to a socket
 * ByteBuffer objects help us align this buffer with data received
 */

public class DnsResponsePacket {
	
	public DnsQueryPacket dqp;
	public byte[] thisBuffer;
	private ByteBuffer bb;	
	
	// hard values
	private final short DNS_RESPONSE_FLAG   		= (short) 0x8100;
	private final short DNS_RESPONSE_QCOUNT 		= (short) 0x0001;
	private final short DNS_RESPONSE_ANSRRS 		= (short) 0x0001;
	private final short DNS_RESPONSE_NSRRS  		= (short) 0x0000; // no SOA records...
	
	private final short DNS_RESPONSE_QNAMEPTR  		= (short) 0xc00c;
	//private final short DNS_RESPONSE_RESPTYPE 	= (short) 0x0001; // A record
	private final short DNS_RESPONSE_ADDITRRS   	= (short) 0x0000;
	
	private final int   DNS_RESPONSE_RESPTTL     	= (int) 0x00000040; // TTL = 64 decimal
	private final short DNS_RESPONSE_RESPDATALEN 	= 4; // 4 bytes ipv4 address
	private final int   DNS_RESPONSE_RESPDATAVAL 	= (int) 0xaabbccdd;
	
	/*********************/
	/* DNS HEADER FIELDS */
	/*********************/
	public short transacID;
	
	public short flags;
	
	public short qCount;
	
	public short answerRRs;
	
	// number of Name Server (NS) resource records (RR) found in
	// authority records section
	public short nsRRs;
	
	// number of RRs in the additional records section
	public short additRRs;
	
	/************************/
	/* DNS QUESTION SECTION */
	/************************/
	public byte[] queryName; 
	//private int qNameLen = 0;
	
	// specifies type of query.
	// 0001 for A record query
	public short qType;
	
	// represents IN for internet.
	public short qClass;
	
	/*********************/
	/* DNS ANSWER FIELDS */	// some field names taken from query, field used in responses	
	/*********************/
	public short qNamePtr;    //= (short)0xc00c; // default setting A record replies
	public short respType;    // = dqp.getQType();
	public short respClass;   //= dqp.getQClass();;
    public int respTTL;       //= (int)0x12345678; // 4 bytes TTL total .. signed int.. 32700 max	
	public short respDataLen;  //= (short)0x0010;	
	public int respAddr;       //= (int)0x99999999; // ipv4 ipaddr answer.. signed int.. 32700 max
	
	/* Constructor 1 -- auto build for auto reply */
	public DnsResponsePacket (DnsQueryPacket dqp) {
		
		this.dqp = dqp;
		thisBuffer = new byte[256];
		bb = ByteBuffer.wrap(thisBuffer).order(ByteOrder.BIG_ENDIAN);  
		
		// start building or instance object private buffer.. wrapped data and push onto it
		
		/*********************/
		/* DNS HEADER FIELDS */
		/*********************/
		setTransacID(dqp.getTransacID());
		setFlags(DNS_RESPONSE_FLAG);
		setQCount(DNS_RESPONSE_QCOUNT);
		setAnsRRs(DNS_RESPONSE_ANSRRS);
		setNsRRs(DNS_RESPONSE_NSRRS);
		setAdditRRs(DNS_RESPONSE_ADDITRRS);
		
		/************************/
		/* DNS QUESTION SECTION */
		/************************/
		setQName(dqp.getQueryName()); // auto filled <<
		setQType(dqp.getQType());
		setQClass(dqp.getQClass());
		
		/*********************/
		/* DNS ANSWER FIELDS */		
		/*********************/
		setQNamePtr(DNS_RESPONSE_QNAMEPTR);
		setRespType(dqp.getQType());
		setRespClass(dqp.getQClass());
		setRespTTL(DNS_RESPONSE_RESPTTL);
		setRespDataLen(DNS_RESPONSE_RESPDATALEN);
		setRespDataValue(DNS_RESPONSE_RESPDATAVAL);
	}
	
	/* Constructor 2 -- Auto build except dns query name */
	
	// just thought of something... Response packet object can be built or auto built based on
	// what field we are customizing.. and everything else auto.
	public DnsResponsePacket(DnsQueryPacket dqp, byte[] fakeQName) {
		
		this.dqp = dqp;
		thisBuffer = new byte[256];
		bb = ByteBuffer.wrap(thisBuffer).order(ByteOrder.BIG_ENDIAN); 
		
		/*********************/
		/* DNS HEADER FIELDS */
		/*********************/
		setTransacID(dqp.getTransacID());
		setFlags(DNS_RESPONSE_FLAG);
		setQCount(DNS_RESPONSE_QCOUNT);
		setAnsRRs(DNS_RESPONSE_ANSRRS);
		setNsRRs(DNS_RESPONSE_NSRRS);
		setAdditRRs(DNS_RESPONSE_ADDITRRS);
		
		/************************/
		/* DNS QUESTION SECTION */
		/************************/
		setQName(fakeQName);
		setQType(dqp.getQType());
		setQClass(dqp.getQClass());
		
		/*********************/
		/* DNS ANSWER FIELDS */		
		/*********************/
		setQNamePtr(DNS_RESPONSE_QNAMEPTR);
		setRespType(dqp.getQType());
		setRespClass(dqp.getQClass());
		setRespTTL(DNS_RESPONSE_RESPTTL);
		setRespDataLen(DNS_RESPONSE_RESPDATALEN);
		setRespDataValue(DNS_RESPONSE_RESPDATAVAL);		
	} // end constructor 2
		
	
	// getts and setts
	/*  
	 * These setters act on ByteBuffer and Byte objects.. not class.
	 * Instance class fields are set in constructor for new received packet
	 * these are set for outgoing packets.
	 * */
	public void setTransacID(short c) {
		bb.putShort(c);
	}
	
	public void setFlags(short s) {
		bb.putShort(s);		
	}
	
	public void setQCount(short s) {
		bb.putShort(s);
	}
	
	public void setAnsRRs(short s) {
		bb.putShort(s);
	}
	
	// NS rrs == authority RR's
	public void setNsRRs(short s) {
		bb.putShort(s);
	}
	
	public void setAdditRRs(short s) {
		bb.putShort(s);
	}
	
	// we would like to over-write portions of qname with our fake qname
	public void setQName(byte[] q) {
				
		// if the qname we are setting is shorter than orig query name recv... pad
		//if (q.length < dqp.getQueryName().length) {
			
			// determine how much to pad
			//int padBytesNeeded = dqp.getQueryName().length - q.length;
			//bb.put(q, 0, q.length);			
			// create junk buffer.. to do...			
		//}		
		//Data.printBuffer(q, q.length);
		bb.put(q, 0, q.length);
	}
	
	public void setQType(short s) {
		bb.putShort(s);
	}
		
	public void setQClass(short s) {
		bb.putShort(s);
	}
		
	// response answer section
	public void setQNamePtr(short s) {
		bb.putShort(s);
	}
	
	public void setRespType(short s) {
		bb.putShort(s);
	}
	
	public void setRespClass(short s) {
		bb.putShort(s);
	}
	
	public void setRespTTL(int i) {
		bb.putInt(i);
	}
		
	public void setRespDataLen(short s) {
		bb.putShort(s);
	}
	
	public void setRespDataValue(int i) {
		bb.putInt(i);
	}
	
	/*
	 * bb related operations... how how bytes have we "pushed" onto the wrapped data buffer
	 * 
	 */
	public int getBBIndex() {
		return bb.position();
	}
}