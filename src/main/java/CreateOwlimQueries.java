import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CreateOwlimQueries {
	public static void main(String[] args) {
		String folderPath = "samples/srbench_queries/";
		String outputPath = "samples/owlim_queries/";
		String stationPath = "/Users/eugene/Downloads/knoesis_observations_rdf_fix/";
		
		try {
			File folder = new File(folderPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String queryIn = FileUtils.readFileToString(file);
				String queryOut = "query="+URLEncoder.encode(queryIn)+"&infer=true"; 
				
				FileUtils.writeStringToFile(new File(outputPath+tempFileName), queryOut);
			}
			
			List<String> stationList = new ArrayList<String>();
			folder = new File(stationPath);
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".n3", "");
				stationList.add(stationName);
			}
			
			int count = 0;
			int start = 0;
			int end = 999;
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "queryNow.sh"));
//			for(int i=1;i<=10;i++) {
//				count=0;
				for(String stationName:stationList) {
					//have to add and remove repo because owlim runs out of memory trying to add too many repos
					if(count>=start && count <=end) {
						String shellScript = "curl -X PUT http://192.168.0.102:8080/rest/repositories -d @"+stationName+".json --header \"Content-Type: application/json\"";
						bw.append(shellScript+"\n");
						for(int i=1;i<=10;i++) {
							String request = "curl -X POST http://192.168.0.102:8080/repositories/"+stationName+" -d @q"+i+".sparql -w \"%{time_total}\\n\" -o /dev/null -s >> results_q"+(i)+".out";
							bw.append(request + "\n");
						}
						shellScript = "curl -X DELETE http://192.168.0.102:8080/rest/repositories/"+stationName;
						bw.append(shellScript+"\n");
					}
					count++;
				}
//			}
//			BufferedWriter bw = null;
//			for(String stationName:stationList) {
//				if(count%100==0) {
//					if(bw!=null)
//						bw.close();
//					bw = new BufferedWriter(new FileWriter(outputPath + "query"+(count/100)+".sh"));
//				}
//				for(int i=1;i<=10;i++) {
//					String request = "curl -X POST http://localhost:8080/repositories/"+stationName+" -d @q"+i+".sparql -w \"%{time_}\\n\" -o /dev/null -s >> results"+(count/100)+".out";
//					bw.append(request + "\n");
//				}
//				
//				count++;
//			}
//			if(bw!=null)
//				bw.close();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
