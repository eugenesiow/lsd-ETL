import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CreateOntopQueries {
	public static void main(String[] args) {
//		String stationPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		String stationPath = "/Users/eugenesiow/Documents/Programming/knoesis_observations_rdf_fix/";
		if (args.length > 0) {
			stationPath = args[0];
        }
		
		String mappingFolder = "/Users/eugene/Downloads/knoesis_observations_r2rml/";
		if (args.length > 1) {
			mappingFolder = args[1];
        }
		
		String outputPath = "samples/ontop/";
		if (args.length > 2) {
			outputPath = args[2];
        }
		
		try {
			List<String> stationList = new ArrayList<String>();
			File folder = new File(stationPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".n3", "");
				stationList.add(stationName);
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "query_ontop.sh"));
			for(int i=1;i<=10;i++) {
				for(String stationName:stationList) {
					String shellScript = "./ontop query -u eugene -o results/q"+i+"_"+stationName+".out -q examples/q"+i+".sparql -m "+mappingFolder+stationName+".ttl -d org.h2.Driver -l jdbc:h2:tcp://192.168.0.103/~/h2/LSD_h2_databases_user/"+stationName+" -p eugene > output/q"+i+"_"+stationName+".log";
					bw.append(shellScript+"\n");
				}
			}
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
