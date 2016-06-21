

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CreateDbsPG {
	public static void main(String[] a) {
		run("/Users/eugene/Downloads/knoesis_observations_csv/","/Users/eugene/Downloads/knoesis_pg_load/");
	}
	
	public static int run(String folderPath, String outputPath) {
		int totalCount = 0;
		try {
			File folder = new File(folderPath);

			for(File file:folder.listFiles()) {
				String filename = file.getName();
				if(filename.startsWith(".")) {
					continue;
				}
				String stationName = filename.replace(".csv","");
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + stationName + ".sql"));
				
				BufferedReader br = new BufferedReader(new FileReader(folderPath + filename));
				String header = br.readLine();
				if(header!=null) {
					String[] colNames = header.split(",");
					String createCols = "";
//					String insertHeaders = "";
//					String insertParams = "";
					for(int i=1;i<colNames.length;i++) {
						String[] colHead = colNames[i].split("_");
						if(colHead.length>1) {
							createCols += ", " + colHead[0] + " BOOLEAN\n";
//							insertHeaders += ","+colHead[0];
						} else {
							createCols += ", " + colNames[i] + " NUMERIC\n";
//							insertHeaders += ","+colNames[i];
						}
						
//						insertParams += ",?";
					}
					
					String preSQL = "CREATE EXTENSION cstore_fdw;\n" + 
							"CREATE SERVER cstore_server FOREIGN DATA WRAPPER cstore_fdw;\n";
			        		        
					//create table
					String tableSQL = "CREATE FOREIGN TABLE _"+stationName+"\n" + 
							"(\n" + 
							"  time TIMESTAMP\n" + 
							createCols +
							") SERVER cstore_server\n" + 
							"OPTIONS(compression 'pglz')\n" + 
							";\n";
					
					String postSQL = "\\COPY _"+stationName+" FROM '"+folderPath+filename+"' WITH CSV HEADER;\n"; 
			
					bw.append(preSQL);
					bw.append(tableSQL);
					bw.append(postSQL);
			        
					br.close();
				}
				totalCount++;
				bw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalCount;
    }
}
