import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;


public class CreateUnioteJsonConfig {
	
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_ike_rdf_merged_nafix/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_ike_uniote_config/";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		
		for(File file:folder.listFiles()) {
			try {				
				Boolean isNewFile = false;
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
//				String[] parts = tempFileName.split("_");
				
				String stationName = tempFileName.replace(".n3.bak", "");
				String filename = "_" + stationName + ".json";
				File newFile = new File(outputPath + filename); 
				if(!newFile.exists()){
					newFile.createNewFile();
					isNewFile = true;
		    	}
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile,true));
				
				JSONObject config = new JSONObject();
				config.put("name", "_"+stationName);
				config.put("uri", "http://www.cwi.nl/SRBench/observations");
				config.put("mapping_src", "/home/pi/uniote_weather_data/mappings/"+stationName+".nt");
				config.put("format_src", "/home/pi/uniote_weather_data/format/_"+stationName+".format");
				JSONObject replay = new JSONObject();
				replay.put("src", "/home/pi/uniote_weather_data/samples/"+stationName+".csv");
				replay.put("time_col", 0);
				replay.put("header", true);
				replay.put("fixed_delay", 10000);
				replay.put("time_format", "yyyy-MM-dd'T'hh:mm:ss");
				config.put("replay", replay);
				
				bw.write(config.toString());
				
				totalCount++;
					
				bw.flush();
				bw.close();
				 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(totalCount);
	}
}
