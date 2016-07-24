import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

class Database {

    static String url = "jdbc:mysql://localhost/testDB";
    static String user = "root";
    static String password = "password";
    static Connection conn;
    static java.sql.Statement stmt;
    
    // use .execute for all queries???!!
    
    /********************************************************************/  
    
    // query such as SELECT.. not INSERT
	public static ResultSet executeQuery(String sqlQry) throws SQLException {	
		
		// object containing results of query
		ResultSet rs = null;
		
		// connect to database
		if(init()) {
			rs = stmt.executeQuery(sqlQry);			
		}
		else {
			System.out.println("init failed returned 0");
		}
		
		return rs;
	}
	
	public static boolean insertData(String sqlQry) throws SQLException {	
		
		// object containing results of query will need to be fetched later.		
		boolean result = false;
		
		// connect to database
		if(init()) {
			//rs = stmt.executeQuery(sqlQry);	
			result = stmt.execute(sqlQry);
		}
		else {
			System.out.println("init failed returned 0");
		}
		
		return result;
	}
	
	
	/********************************************************************/ 
	
	// kind of like constructor but since all static, no objects being made
	// this function connects to the database
	public static boolean init() {

		boolean flag = true;
		
    	try {      
    		
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("[+] Connected to localhost/testDB \n"); 
            
            stmt = conn.createStatement();            
    	}
    	catch (SQLException e) {
    		// problem !
    		System.out.println(e.getMessage());
    		flag = false;
    	} 
    	
    	return flag;
	}
} // end class