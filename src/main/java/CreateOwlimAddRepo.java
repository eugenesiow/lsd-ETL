import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CreateOwlimAddRepo {
	public static void main(String[] args) {
		String exampleJson = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/json/sample.json";
		String folderPath = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/srbench/";
		String outputPath = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/json/";
		String scriptPath = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/json/addSRBench_short.sh";
		
		try {
			String sampleConfig = FileUtils.readFileToString(new File(exampleJson));
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptPath));
		
			File folder = new File(folderPath);
			
			int count=0;
			int start=0;
			int end=999;
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				
				if(count>=start && count <=end) {
					String stationName = tempFileName.replace(".ttl", "");
					String configTTL = sampleConfig.replaceAll("StationName", stationName);
					
					
					String shellScript = "curl -X PUT http://192.168.0.102:8080/rest/repositories -d @"+stationName+".json --header \"Content-Type: application/json\"";
					bw.append(shellScript+"\n");
//					shellScript = "curl -X DELETE http://192.168.0.102:8080/rest/repositories/"+stationName;
//					bw.append(shellScript+"\n");
					
					FileUtils.writeStringToFile(new File(outputPath+stationName+".json"), configTTL);
					count++;
				}
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
