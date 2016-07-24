import java.io.UnsupportedEncodingException;

public class Data {	
	
	// prints stuff/// use built in udp packet length... = length of udp payload... 
	static int getMsgLen(byte[] buffer)	{
		int len = 0; //stop at non printable.. excluding hex A LF

		for(int i=0; i<=1024; i++) // loop throug entire "file stream" buffer
		{
			// if char is printable... letters + ? () ! spaces.. or is 'A' line feed.. count it
			// telnet adds \r\n ... netcat adds \n
			if( ((buffer[i] >= 0x20) && (buffer[i] <= 0x7f)) || (buffer[i] == 0x0a) )
			{
				len = len + 1;
				//printf("Len Count: %d - Char: %c \n", i, buffer[i]);
			}
			else // if bad chars come in the middle of good ones.. BUG!
			{
				//printf("Lenth of stream: %d \n", len);
				break;
			}
		} // end for
		
		return len;
	} // end getMsgLen
	
	// dumps payload.
	static void printBuffer(byte[] buffer, int len) { // default buffer[x] is 8 bits
			
		System.out.println("[-] Payload Length: " + len + "\n");		
		
		for(int i=0; i<len; i++) { // loop throug entire "file stream" buffer	
			
			char c = (char)(buffer[i] & 0xFF);
			byte d = (byte)(buffer[i] & 0xFF);
			String hex = Integer.toHexString(d);			
			
			System.out.println("("+i+")" + "  " + "'"+c+"'" + "  " + "0x" + hex + "  " + d);				
		}		
	} // end printBuff
	
	
	// NETWORKING SPECIFIC MAGIC NUMBERS... NOT SCIENTIFIC
	// regarldess of UDP payload tye... use UDP packet getLenth..
	static int getDNSPacketLength(byte[] buffer) { // default buffer[x] is 8 bits
		
		int len = 0; //stop at non printable.. excluding hex A LF

		for(int i=0; i<=64; i++) // loop throug entire "file stream" buffer
		{			
			//String encodedbytes = Hex.
			// find CO ;;; C = ith byte into payload.. we want total len
			if(buffer[i] == 109 && buffer[i+1] == 0) // = m in decimal... COM<null><0><1><0><1>	
			{
				len = i + 6; // o boy..				
				break;			
			}			
		}
		return len;
		
	} // end getDNSPacketLenfth
	
	static void say(String s) {
		
		System.out.println(s);		
	}
	
	static String byteToString(byte[] b) throws UnsupportedEncodingException {
		
		String str = new String(b, "UTF-8");
		return str;
		//System.out.println("Str: " + str);
	}
	
} // end Data class