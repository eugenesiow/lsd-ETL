
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.jdbc.remote.RemoteEndpointDriver;

public class TestTDBQuery {
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
//		String folderPath = "/Users/eugene/Downloads/knoesis_results/lists/";
		String queryPath = "/Users/eugene/Dropbox/Private/WORK/LinkedSensorData/queries/";
		String outputPath = "/Users/eugene/Downloads/knoesis_results/";
		
//		String listNumber = "5";
		for(int run = 1;run<=3;run++) {
			System.out.println("run "+run);
	
			try {
				String queryName = "q5";
				String queryStr = FileUtils.readFileToString(new File(queryPath + queryName + ".sparql"));
				
//				BufferedReader br = new BufferedReader(new FileReader(folderPath + "stations"+listNumber+".txt"));
				
//				int totalCount = 0;
//				BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "results_tdb_"+queryName+"_"+listNumber+"_run"+run+".csv"));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "results_tdb_"+queryName+"_run"+run+".csv"));
				
				RemoteEndpointDriver.register();
				File folder = new File(folderPath);
//				String line = "";
//				while((line=br.readLine())!=null) {
				for(File file:folder.listFiles()) {
					String tempFileName = file.getName();
					if(tempFileName.startsWith("."))
						continue;
					String stationName = tempFileName.replace(".n3", "");
//					String stationName = line.trim();
					BufferedWriter bwResults = new BufferedWriter(new FileWriter(outputPath + "/results/tdb_"+queryName+"_run"+run+"_"+stationName+".csv"));
					
					long startTime = System.currentTimeMillis();
//					Connection conn = DriverManager.getConnection("jdbc:jena:remote:query=http://192.168.0.103:3030/"+stationName+"/sparql");
					Connection conn = DriverManager.getConnection("jdbc:jena:remote:query=http://192.168.0.103:8080/"+stationName);
					
					// Need a statement
					Statement stmt = conn.createStatement();
		
					try {
					  // Make a query
					  ResultSet rs = stmt.executeQuery(queryStr);
		
					  // Iterate over results
//					  while (rs.next()) {
////						  System.out.println(rs.getString(1));
//						  totalCount++;
//					  }
					  
					  CSVPrinter printer = new CSVPrinter(bwResults,CSVFormat.DEFAULT);
					  printer.printRecords(rs);
		
					  // Clean up
					  printer.close();
					  rs.close();
					  bwResults.close();
					} catch (SQLException e) {
					  System.err.println("SQL Error - " + e.getMessage());
//					  e.printStackTrace();
					} finally {
					  stmt.close();
					}
					conn.close();
					long executionTime = System.currentTimeMillis() - startTime;
					bw.append(stationName + "," + executionTime + "\n");
					bw.flush();
				}
//				System.out.println(totalCount);
				
//				br.close();
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
}