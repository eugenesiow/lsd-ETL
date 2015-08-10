
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.apache.jena.jdbc.tdb.TDBDriver;

public class TestTDBQuery {
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_map/";
		String dbPath = "/Users/eugene/LSD_TDB_databases/";
		String queryPath = "/Users/eugene/Dropbox/Private/WORK/LinkedSensorData/queries/";
		String outputPath = "/Users/eugene/Downloads/knoesis_results/";
		File folder = new File(folderPath);
	
		try {
			String queryName = "q1";
			String queryStr = FileUtils.readFileToString(new File(queryPath + queryName + ".sparql"));
			
	
			int totalCount = 0;
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "results_tdb_"+queryName+".csv"));
			
			TDBDriver.register();
					
			for(File file:folder.listFiles()) {		
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".nt", "");
	
				Connection conn = DriverManager.getConnection("jdbc:jena:tdb:location="+dbPath+stationName);
				
				long startTime = System.currentTimeMillis();
				// Need a statement
				Statement stmt = conn.createStatement();
	
				try {
				  // Make a query
				  ResultSet rs = stmt.executeQuery(queryStr);
	
				  // Iterate over results
				  while (rs.next()) {
					  System.out.println(rs.getString(1));
				    totalCount++;
				  }
	
				  // Clean up
				  rs.close();
				} catch (SQLException e) {
				  System.err.println("SQL Error - " + e.getMessage());
				} finally {
				  stmt.close();
				}
				conn.close();
				long executionTime = System.currentTimeMillis() - startTime;
				bw.append(stationName + "," + executionTime + "\n");
				bw.flush();
				
			}
			System.out.println(totalCount);
			
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
