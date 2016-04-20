import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ProcessOntopLog {
	public static void main(String[] args) {
//		String folderPath = "output/";
//		String outputPath = "processed/";
		String folderPath = "/Users/eugene/Downloads/ontop-distribution-1.16.1/output_old/";
		String outputPath = "/Users/eugene/Downloads/ontop-distribution-1.16.1/processed/";
		Map<String,List<String>> results = new HashMap<String,List<String>>();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		
		try {
			File folder = new File(folderPath);
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				tempFileName = tempFileName.replace(".log", "");
				String[] parts = tempFileName.split("_");
				if(parts.length>1) {
					String query = parts[0];
					String station = parts[1];
					List<String> stationResultsList = results.get(query);
					if(stationResultsList==null) {
						stationResultsList = new ArrayList<String>();
					}
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line = "";
					Boolean first = true;
					String start = "";
					String endTrans = "";
					String endQuery = "";
					while( (line=br.readLine()) != null ) {
						String[] lineParts = line.split("\\|");
						if(lineParts.length>1) {
							String message = lineParts[1].trim();
							if(message.equals("-DEBUG in i.u.k.o.o.core.QuestStatement - Executing the SQL query and get the result...")) {
								endTrans = lineParts[0].trim();
							}
							else if(message.equals("-DEBUG in i.u.k.o.o.core.QuestStatement - Execution finished.")) {
								endQuery = lineParts[0].trim();
							}
//							else if(message.equals("-DEBUG in i.u.k.obda.model.impl.OBDAModelImpl - OBDA model is initialized!") && first) {
							else if(message.equals("-DEBUG in i.u.k.obda.owlrefplatform.core.Quest - Initializing Quest...")&&first) {
								start = lineParts[0].trim();
								first = false;
							}
							
						}
					}
					br.close();
					
					if(start.trim().equals("") || endTrans.trim().equals("") || endQuery.trim().equals("")) {
						System.out.println(tempFileName);
						stationResultsList.add(station+",,,,0,0,0");
					} else {
						Date startT = format.parse(start);
						Date endTransT = format.parse(endTrans);
						Date endQueryT = format.parse(endQuery);
						long transTime = endTransT.getTime() - startT.getTime(); 
						long queryTime = endQueryT.getTime() - endTransT.getTime(); 
						long totalTime = endQueryT.getTime() - startT.getTime(); 
						stationResultsList.add(station+","+start+","+endTrans+","+endQuery+","+transTime+","+queryTime+","+totalTime);
					}
					
					results.put(query, stationResultsList);
				}
			}
			
			for(Entry<String,List<String>> result:results.entrySet()) {
				System.out.println(outputPath + result.getKey() + ".csv");
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + result.getKey() + ".csv"));
				for(String row:result.getValue()) {
					bw.append(row+"\n");
				}
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
