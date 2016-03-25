import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CreateOwlimSettings {
	public static void main(String[] args) {
		String folderPath = "/Users/eugenesiow/Documents/Programming/knoesis_observations_rdf_fix/";
		String exampleTTL = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/sample.ttl";
		String outputPath = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/srbench/";
		String scriptPath = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/bin/loadSRBench.sh";
		String rdfPath = "/Users/eugenesiow/Documents/Programming/knoesis_observations_rdf_fix/";
		
		try {
			String sampleConfig = FileUtils.readFileToString(new File(exampleTTL));
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptPath));
		
			File folder = new File(folderPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".n3", "");
				String configTTL = sampleConfig.replaceAll("StationName", stationName);
				
				String shellScript = "./loadrdf.sh "+outputPath+stationName+".ttl serial "+rdfPath+stationName+".n3";
				bw.append(shellScript+"\n");
				
				FileUtils.writeStringToFile(new File(outputPath+stationName+".ttl"), configTTL);
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
