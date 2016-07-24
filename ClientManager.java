import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;


/*
 * This class is responsible for interacting with the database to include adding clients
 * and querying the database for client related jobs.
 * 
 * when add a job, make sure to add the clientID (if exists) to jobqueue table..
 * 
 */


public class ClientManager {
		
	public static boolean 
	addClient(int clientId) throws SQLException {
		
		System.out.println("==addClient()=== ");
		System.out.println("Arg clientId -> " + clientId);
		boolean result = false;
		
		if (doesClientExist(clientId)) {
			
			// client exists.. do nothing
			System.out.println("client does exist");
		}
		else { // client not exist, add to db			
			
			String SQL_QUERY_ADD_CLIENT = "INSERT INTO `testDB`.`clientinfo` (`clientid`) VALUES ("+Integer.toString(clientId)+")";
						
			System.out.println("Executing this query: " + SQL_QUERY_ADD_CLIENT);		
			
			// check is client exists in column value
			result = Database.insertData(SQL_QUERY_ADD_CLIENT);				
		}
		
		return result;	
	}
	
	/*
	 * This function queries the database to see if the jobqueue table has a column value of
	 * id matching column key=clientid
	 */
	public static boolean 
	doesClientExist(int id) throws SQLException {
		
		System.out.println("==doesClientExist()=== ");
		
		String SQL_QUERY_DOES_CLIENT_EXIST = "SELECT clientid FROM clientinfo WHERE clientid="+Integer.toString(id);
		boolean clientExists = false;
		
		System.out.println("Executing this query: " + SQL_QUERY_DOES_CLIENT_EXIST);		
		
		// check is client exists in column value
		ResultSet rs = Database.executeQuery(SQL_QUERY_DOES_CLIENT_EXIST);
		
		if(rs==null) {
			System.out.println("[!] result set object is empty... executeQuery failed !! \n");
		}		
		
		while( rs.next() ) {
		    // ResultSet processing here
			clientExists = true;
		}

		if( !clientExists ) {
		    // Empty result set
			System.out.println("[-] ClientID: " + id + " does not exist in database yet..\n");
		}
		
		return clientExists;
	}
	
	public static byte[]
	getJobBuffer(int id) throws SQLException, UnsupportedEncodingException {	
		
		System.out.println(" ===getJobDetails()=== ");
						
		String SQL_QUERY_GET_OLDEST_JOB = 
				"SELECT command,type from jobqueue WHERE clientid="+Integer.toString(id) + 
				" and completed=0 ORDER BY id ASC LIMIT 1";	
		
		int cmdType = -1;
		int cmdLength = -1;
		String cmd = null;
		
		if(jobsPending(id)) {

			System.out.println("Executing this query: " + SQL_QUERY_GET_OLDEST_JOB);	
			
			// if jobsPending is true.. has jobs... get oldest job added
			
			// 1.) query jobqueue database looking at oldest job added based on column clientID
			// 2.) get the job, query it-- use results
			// 3.) mark the row returned as now completed=1
			
			ResultSet rs = Database.executeQuery(SQL_QUERY_GET_OLDEST_JOB);
			
			if(rs==null) 
				System.out.println("[!] result set object is empty... executeQuery failed !! ");
				
			// only loops once - contains 1 result / "row", because LIMIT 1 in query
			
			// retrieve the first row of result, if has at least 1 result
			if (rs.next()) {
				
				cmdType = rs.getInt("type");								
				cmdLength = rs.getString("command").length();				
				cmd = rs.getString("command");
				
				//System.out.println("Command Type " + cmdType);				
				//System.out.println("Command Length: " + cmdLength);
				//System.out.println("Command: " + rs.getString("command"));							
			}			
		} // end if	85
		
		System.out.println("===/END getJobDetails()=== ");
		return Task.init(cmdType, cmdLength, cmd.getBytes("UTF-8"));		
	}
	
	public static boolean 
	jobsPending(int id) throws SQLException {
		
		System.out.println("==jobsPending()=== ");
		
		// before we get job details, see if we have any jobs avail, if yes, get latest one
		String SQL_QUERY_HAS_JOBS_PENDING = 
				"SELECT clientid,completed FROM jobqueue WHERE clientid="+Integer.toString(id) + " AND "
						+ "completed=0";
		
		boolean jobsPending = false;		
		
		System.out.println("Executing this query: " + SQL_QUERY_HAS_JOBS_PENDING);		
		
		// check is client exists in column value
		ResultSet rs = Database.executeQuery(SQL_QUERY_HAS_JOBS_PENDING);
		
		if(rs==null) {
			System.out.println("[!] result set object is empty... executeQuery failed !! ");
		}		
		
		while( rs.next() ) { // jobs pending is true because at least 1 record returned
			
		    // ResultSet processing here.. rs not empty
			System.out.println(
			"clientid -> " + rs.getString("clientid") + "\n" +
			"completed -> " + rs.getInt("completed"));
			
			jobsPending = true;
		}
				
		System.out.println("===/END jobsPending()=== ");
		
		return jobsPending;		
	}
	
	public static void 
	printJobQueueTableQuery(ResultSet r) throws SQLException {
		
		System.out.println("==printJobQueueTableQuery==");		
		
		System.out.println("id -> " + r.getString("id") +
				"clientid -> " + r.getString("clientid") +
				"command -> "  + r.getString("command") +
				"type -> " + r.getString("type") +
				"completed -> " + r.getInt("completed"));		
	}	
	
} // end class






















