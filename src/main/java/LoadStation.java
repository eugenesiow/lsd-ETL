

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadStation {
//	public static void main(String[] a)
//            throws Exception {
	public static int run(String folderPath, String driver, String jdbc, String outputPath, String user, String pass) {
		int totalCount = 0;
		try {
	//        Class.forName("org.h2.Driver");
			Class.forName(driver);
	        
	//        String folderPath = "/Users/eugene/Downloads/knoesis_observations_ike_csv/";
	//        if(a.length>0) {
	//        	folderPath = a[0];
	//		}
			File folder = new File(folderPath);
			
			
			for(File file:folder.listFiles()) {
				String filename = file.getName();
				if(filename.startsWith(".")) {
					continue;
				}
				String stationName = filename.replace(".csv","");
				
		        Connection conn = DriverManager.getConnection(jdbc + outputPath + stationName, user, pass);
		//        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
		        
		//		System.out.println(a.length);
				
				BufferedReader br = new BufferedReader(new FileReader(folderPath + filename));
				String header = br.readLine();
				if(header!=null) {
					String[] colNames = header.split(",");
					String createCols = "";
					String insertHeaders = "";
					String insertParams = "";
					for(int i=1;i<colNames.length;i++) {
						String[] colHead = colNames[i].split("_");
						if(colHead.length>1) {
							createCols += ", " + colHead[0] + " BOOLEAN\n";
							insertHeaders += ","+colHead[0];
						} else {
							createCols += ", " + colNames[i] + " NUMERIC\n";
							insertHeaders += ","+colNames[i];
						}
						
						insertParams += ",?";
					}
			        
			        Statement stat = conn.createStatement();
			        
					//create table
					stat.execute("CREATE TABLE _"+stationName+"\n" + 
							"(\n" + 
							"  time TIMESTAMP\n" + 
							createCols +
							")\n" + 
							";");
			
					//prepared statement
					PreparedStatement prep = conn.prepareStatement("INSERT INTO _"+stationName+" (time"+insertHeaders+") VALUES (?"+insertParams+")");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					
					conn.setAutoCommit(false);
					String line="";
					while((line=br.readLine())!=null) {
						if(line.endsWith(",")) {
							line += "\n";
						}
						String[] parts = line.split(",");
						try {
						prep.setTimestamp(1, new Timestamp(sdf.parse(parts[0]).getTime()));
						} catch (ParseException pe) {
							prep.setTimestamp(1, new Timestamp((new Date()).getTime()));
						}
						if(parts.length==colNames.length) {
							for(int i=1;i<colNames.length;i++) {
								if(!parts[i].trim().equals("")) {
									if(parts[i].equals("true") || parts[i].equals("false"))
										prep.setBoolean(i+1, Boolean.parseBoolean(parts[i]));
									else
										prep.setFloat(i+1, Float.parseFloat(parts[i]));
								}
							}
							//batch insert
							prep.addBatch();
						} else {
							System.out.println(parts.length + ":" + colNames.length);
							System.out.println(line);
							System.out.println(stationName);
							break;
						}
					}
					prep.executeBatch();
					conn.setAutoCommit(true);
			        
			      stat.executeUpdate("CREATE INDEX _"+stationName+"_date_index ON _"+stationName+"(time)");
			
			
			//        Long startTime = System.currentTimeMillis();
			//		//query to database
			//		try {
			//			ResultSet rs = stat.executeQuery("SELECT max(environment.insideTemp) AS max , min(environment.insideTemp) AS min , day(environment.TimestampUTC) AS day FROM environment WHERE environment.TimestampUTC>'2012-07-01T00:00:00' AND environment.TimestampUTC<'2012-07-30T00:00:00' GROUP BY day(environment.TimestampUTC) ");
			//			//ResultSet rs = stat.executeQuery("SELECT avg(environment.insideTemp) AS sval , HOUR(environment.TimestampUTC) AS hours FROM environment WHERE environment.TimestampUTC>'2012-07-20T00:00:00' AND environment.TimestampUTC<'2012-07-21T00:00:00' GROUP BY HOUR(environment.TimestampUTC)"); 
			////			ResultSet rs = stat.executeQuery("Select HOUR(TimestampUTC) as hours, AVG(insideTemp) from ENVIRONMENT where TimestampUTC >= '2012-07-20' AND TimestampUTC <= '2012-07-21' GROUP BY HOUR(TimestampUTC) ");
			////			ResultSet rs = stat.executeQuery("Select DAY(TimestampUTC), MAX(insideTemp) from ENVIRONMENT where TimestampUTC >= '2012-07-01' AND TimestampUTC <= '2012-07-30' GROUP BY DAY(TimestampUTC)");
			//			while (rs.next()) {
			//				int start = rs.getInt(1);
			//				float temp = rs.getFloat(2);
			//				
			//				
			//				System.out.println("start: " + start);
			//				System.out.println("temp: " + temp);
			//				System.out.println("--------------------------");
			//			}
			//			rs.close();
			//		} catch (SQLException e) {
			//			e.printStackTrace();
			//		}
			        
			      br.close();
			       conn.close();
				}
	//	       System.out.println(totalCount++);
		        
		//        System.out.println((System.currentTimeMillis() - startTime));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalCount;
    }
}
