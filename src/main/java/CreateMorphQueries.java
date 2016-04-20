import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CreateMorphQueries {
	public static void main(String[] args) {
		String folderPath = "samples/srbench_queries/";
		if (args.length > 0) {
        	folderPath = args[0];
        }
		
		String settingsPath = "samples/morph_settings/sample.r2rml.properties";
		if (args.length > 1) {
			settingsPath = args[1];
        }
		
		String outputPath = "/Users/eugene/Documents/Programming/morph/examples/srbench/";
		if (args.length > 2) {
			outputPath = args[2];
        }
		
		String stationPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		if (args.length > 3) {
			stationPath = args[3];
        }
		
		String logPath = "examples/srbench/output/";
		if (args.length > 4) {
			logPath = args[4];
        }
		
		try {
			File folder = new File(folderPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String queryIn = FileUtils.readFileToString(file);
				
				FileUtils.writeStringToFile(new File(outputPath+tempFileName), queryIn);
			}
			
			String settings = FileUtils.readFileToString(new File(settingsPath));
			
			List<String> stationList = new ArrayList<String>();
			folder = new File(stationPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".n3", "");
				stationList.add(stationName);
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "run_srbench.sh"));
				bw.append("cd ../../\n");
				for(int i=1;i<=10;i++) {
					for(String stationName:stationList) {
						String settingsStr = settings.replaceAll("db_name",stationName).replaceAll("query_number", "q"+i);
						String outputFile = stationName+"_q"+i+".r2rml.properties";
						FileUtils.writeStringToFile(new File(outputPath+outputFile), settingsStr);
						String shellScript = "java -cp .:morph.jar:lib/* es.upm.fi.dia.oeg.morph.r2rml.rdb.engine.MorphRDBRunner "+outputPath+" "+outputFile+" > "+logPath+"q"+i+"/"+stationName+".out";
						bw.append(shellScript + "\n");
					}
				}
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
