import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;


public class DataCounterCityPulse {
	static long litCount = 0;
	static long totalCount = 0;
	
	public static void main(String[] args) {
		String[] parkingP = {"http://iot.ee.surrey.ac.uk/citypulse/resources/ontologies/sao.ttlvalue","http://purl.org/NET/c4dm/timeline.owl#at"};
		List<String>parkingPList = Arrays.asList(parkingP);
		
		String[] eventsP = {"http://iot.ee.surrey.ac.uk/citypulse/resources/ontologies/sao.ttlvalue","http://purl.org/NET/c4dm/timeline.owl#at","http://www.insight-centre.org/citytraffic#hasLatitude","http://www.insight-centre.org/citytraffic#hasNodeName","http://www.insight-centre.org/citytraffic#hasLongitude"};
		List<String>eventsPList = Arrays.asList(eventsP);
		
//		run("/Users/eugene/Documents/Programming/data/aarhus_parking.ttl",parkingPList);
//		run("/Users/eugene/Documents/Programming/data/annotated_weather_data_aarhus/aarhus_weather_dewpoint.ttl",parkingPList);
//		runFolder("/Users/eugene/Documents/Programming/data/annotated_weather_data_aarhus/",parkingPList);
//		runFolder("/Users/eugene/Documents/Programming/data/annotated_weather_data_aug_sep_2014/",parkingPList);
//		runFolder("/Users/eugene/Documents/Programming/data/traffic_feb_june/",parkingPList);
//		runFolder("/Users/eugene/Documents/Programming/data/pollution/",parkingPList);
		runFolder("/Users/eugene/Documents/Programming/data/citypulse_events/",eventsPList);
	}
	
	public static void run(String filePath, List<String> list) {
//		System.out.println(filePath);
		try {
			Model model = ModelFactory.createDefaultModel();
			InputStream in = FileManager.get().open( filePath );
			model.read(in, null, "TURTLE");
			StmtIterator stmtIt = model.listStatements();
			 while(stmtIt.hasNext()) {
				 Statement st = stmtIt.next();
				 if(st.getObject().isLiteral()) {
					 if(list.contains(st.getPredicate().getURI().toString()))
						 litCount++;
				 }
				totalCount++;
			 }
			 stmtIt.close();
			 in.close();
			 model.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(totalCount);
		System.out.println(litCount);
	}
	
	public static void runFolder(String folderPath, List<String> list) {

		File folder = new File(folderPath);
		
//		System.out.println(folderPath);
		for(File file:folder.listFiles()) {
			try {		
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				Model model = ModelFactory.createDefaultModel();
				String inputName = file.getPath();
				if(inputName.endsWith(".ttl")) {
					 InputStream in = FileManager.get().open( inputName );
					 if (in == null) {
						 throw new IllegalArgumentException("File: " + inputName + " not found");
					 }
					 run(inputName,list);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
