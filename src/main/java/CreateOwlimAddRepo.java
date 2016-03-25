import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CreateOwlimAddRepo {
	public static void main(String[] args) {
		String exampleJson = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/json/sample.json";
		String folderPath = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/srbench/";
		String outputPath = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/json/";
		String scriptPath = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/json/addSRBench.sh";
		
		try {
			String sampleConfig = FileUtils.readFileToString(new File(exampleJson));
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptPath));
		
			File folder = new File(folderPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".ttl", "");
				String configTTL = sampleConfig.replaceAll("StationName", stationName);
				
				
				String shellScript = "curl -X PUT http://localhost:8080/rest/repositories -d @"+stationName+".json --header \"Content-Type: application/json\"";
				bw.append(shellScript+"\n");
				
				FileUtils.writeStringToFile(new File(outputPath+stationName+".json"), configTTL);
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
