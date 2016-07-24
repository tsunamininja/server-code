import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * 
 *  this class is used to represent a task object
 *  
 *  the client UI will set all the variables of a task object... to be wrapped into a queryname byte[]
 *  
 *  To Do: bytebuffer to wrap fields into a buffer array of cmtType,CmdLen,Command, etc.. 12/13/15
 */
public class Task {	
	
	public static int commandType;
	public static int commandLength;
	public static byte[] command;
	public static int maxSegmentSize; // max amount of data to be sent in one message, overHead+cmdLen
	private static int controlBytes = 2; // cmdType + cmdLength

	public static byte[] buffer;
	private static ByteBuffer bb;	
	
	public static byte[] 
	init(int cmdType, int cmdLen, byte[] cmd) {
		
		buffer = new byte[(controlBytes+cmdLen)];
		bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN); 
		
		commandType = cmdType;
		commandLength = cmdLen;
		command = cmd;
		setCommandType(commandType);
		setCommandLength(commandLength);
		setCommand(command);
		
		return getBuffer();
	}
		
	/* setters are for bb aligning bufers 
	 * relative pushes... incrementing bb.position
	 */
	public static void setCommandType(int cmdType) {		
		bb.put((byte)cmdType);
	}
	
	public static void setCommandLength(int cmdLen) {
		bb.put((byte)cmdLen);
	}
	
	public static void setCommand(byte[] cmd) {
		bb.put(cmd, 0, cmd.length);
	}
	
	// prolly wont need this value.. ?
	public static void setMaxSegmentSize() {
		maxSegmentSize = 2 + getCommandLength(); // 2 bytes of overhead
	}
	
	// getters
	public static int getCommandType() {
		return commandType;
	}
	
	public static int getCommandLength() {
		return commandLength;
	}
	
	public static byte[] getCommand() {
		return command;
	}	
	
	public static int getMaxSegmentSize() {
		return maxSegmentSize;
	}
	
	public static int getControlByteCount() {
		return controlBytes;
	}
	
	public static int getBBIndex() {
		return bb.position();
	}
	
	public static byte[] getBuffer() {		
		return buffer;
	}	
}