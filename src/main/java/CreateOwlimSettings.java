import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CreateOwlimSettings {
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		String exampleTTL = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/sample.ttl";
		String outputPath = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/srbench/";
		String scriptPath = "/Users/eugene/Documents/graphdb-se-6.6.3/bin/loadSRBench.sh";
		String rdfPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		
		try {
			String sampleConfig = FileUtils.readFileToString(new File(exampleTTL));
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptPath));
		
			File folder = new File(folderPath);
			
			int count=0;
			int start=4000;
			int end=4999;
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				if(count>=start && count<=end) {
					String stationName = tempFileName.replace(".n3", "");
					String configTTL = sampleConfig.replaceAll("StationName", stationName);
					
					String shellScript = "./loadrdf.sh ../loadrdf/srbench/"+stationName+".ttl serial ../../knoesis_observations_rdf_fix/"+stationName+".n3";
//					String shellScript = "./loadrdf.sh "+outputPath+stationName+".ttl serial "+rdfPath+stationName+".n3";
					bw.append(shellScript+"\n");
					
					FileUtils.writeStringToFile(new File(outputPath+stationName+".ttl"), configTTL);
				}
				count++;
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
