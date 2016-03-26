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
		String stationPath = "/Users/eugenesiow/Downloads/graphdb-se-6.6.3/loadrdf/srbench/";
		
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
				String stationName = tempFileName.replace(".ttl", "");
				stationList.add(stationName);
			}
			
			int count = 0;
			BufferedWriter bw = null;
			for(String stationName:stationList) {
				if(count%100==0) {
					if(bw!=null)
						bw.close();
					bw = new BufferedWriter(new FileWriter(outputPath + "query"+(count/100)+".sh"));
				}
				for(int i=1;i<=10;i++) {
					String request = "curl -X POST http://localhost:8080/repositories/"+stationName+" -d @q"+i+".sparql -w \"%{time_}\\n\" -o /dev/null -s >> results"+(count/100)+".out";
					bw.append(request + "\n");
				}
				
				count++;
			}
			if(bw!=null)
				bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}