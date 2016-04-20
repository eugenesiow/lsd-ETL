import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CreateOwlimSettings {
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		if (args.length > 0) {
        	folderPath = args[0];
        }
		
		String exampleTTL = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/sample.ttl";
		if (args.length > 1) {
			exampleTTL = args[1];
        }
		
		String outputPath = "/Users/eugene/Documents/graphdb-se-6.6.3/loadrdf/srbench/";
		if (args.length > 2) {
			outputPath = args[2];
        }
		
		String scriptPath = "/Users/eugene/Documents/graphdb-se-6.6.3/bin/loadSRBench2.sh";
		if (args.length > 3) {
			scriptPath = args[3];
        }
		
		String rdfPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		if (args.length > 4) {
			rdfPath = args[4];
        }
		
		int start = 0;
		if (args.length > 5) {
			start = Integer.parseInt(args[5]);
        }
		
		int end = 0;
		if (args.length > 6) {
			end = Integer.parseInt(args[6]);
        }
		
		try {
			String sampleConfig = FileUtils.readFileToString(new File(exampleTTL));
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptPath));
		
			File folder = new File(folderPath);
			
			int count=0;
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				if(count>=start && count<=end) {
					String stationName = tempFileName.replace(".n3", "");
					String configTTL = sampleConfig.replaceAll("StationName", stationName);
					
//					String shellScript = "./loadrdf.sh ../loadrdf/srbench/"+stationName+".ttl serial ../../knoesis_observations_rdf_fix/"+stationName+".n3";
					String shellScript = "./loadrdf.sh "+outputPath+stationName+".ttl serial "+rdfPath+stationName+".n3";
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
