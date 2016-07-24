import java.util.concurrent.LinkedBlockingQueue;

/* 
 * 
 * client session representing a clients session to include task queue, client specific
 * items such as network info, and posted cmd out?
 * 
 * 12/13/15 -- need to define what a Task object is.
 */
public class ClientSession {	
	
	private int clientID;
	private LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<Task>();
			
	public ClientSession(int cid) {
		this.clientID = cid;
	}
	
	public int getClientID() {
		return clientID;
	}
	
	public void addTask(Task t) {
		taskQueue.add(t);
	}
	
	public Task removeTask() {
		return taskQueue.remove();
	}
	
	public void printTaskQueueDetails() {		
		System.out.println("[-] @ClientSession::printTaskQueueDetails    \n" +
					       "[-] Task queue size: " + taskQueue.size() + "\n" +
				           "[-] Viewing queue contents: " + taskQueue.toString());		
	}
	
	public boolean isTaskQueueEmpty() {
		return taskQueue.isEmpty();
	}
}