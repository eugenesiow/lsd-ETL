

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadStationMonetDB {
	static String station = "";
	
	public static void main(String[] a) {
		run("/Users/eugene/Downloads/knoesis_observations_csv/","nl.cwi.monetdb.jdbc.MonetDriver","jdbc:monetdb://localhost/","monetdb","monetdb");
	}
	
	public static int run(String folderPath, String driver, String jdbc, String user, String pass) {
		int totalCount = 0;
		try {
			Class.forName(driver);
	        
			File folder = new File(folderPath);
			
			for(File file:folder.listFiles()) {
				String filename = file.getName();
				if(filename.startsWith(".")) {
					continue;
				}
				String stationName = filename.replace(".csv","");
				station = stationName;
				
		        Connection conn = DriverManager.getConnection(jdbc + stationName, user, pass);
				
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
								} else {
									prep.setNull(i+1, java.sql.Types.NULL);
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
			      
//			      stat.executeUpdate("CREATE INDEX _"+stationName+"_date_index ON _"+stationName+"(time)");
//			      
//			      stat.execute("CREATE USER eugene PASSWORD 'eugene' ADMIN");
			        
			      br.close();
			       conn.close();
				}
				totalCount++;
	//	       System.out.println(totalCount++);
		        
		//        System.out.println((System.currentTimeMillis() - startTime));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(station);
		}
		return totalCount;
    }
}
