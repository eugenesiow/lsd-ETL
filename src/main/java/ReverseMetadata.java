import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;


public class ReverseMetadata {

	public static int run(String folderPath, String outputPath) {
//		String folderPath = "D:\\Documents\\Programming\\knoesis_metadata";
//		String outputPath = "D:\\Documents\\Programming\\knoesis_metadata_csv\\";
		File folder = new File(folderPath);
		Map<String,List<String>> geonames = new HashMap<String,List<String>>(); 
		
		int totalCount = 1;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "sensors.csv"));
			
			for(File file:folder.listFiles()) {
				
	//				String tempFileName = file.getName();
	//				String[] parts = tempFileName.split("_");
	//				String filename = parts[0] + ".csv";
	//				
	//				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
					
					
					// create an empty model
					 Model model = ModelFactory.createDefaultModel();
		
					 // use the FileManager to find the input file
					 String inputName = file.getPath();
					 if(inputName.endsWith(".n3")) {
						 InputStream in = FileManager.get().open( inputName );
						 
						 if (in == null) {
						    throw new IllegalArgumentException(
						                                 "File: " + inputName + " not found");
						}
		
						// read the RDF/XML file
						model.read(in, null, "N3");
		//				String queryString = "PREFIX om-owl:<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> SELECT (group_concat(?prop) as ?propall) where {?obs om-owl:procedure ?sensor;a ?class; om-owl:observedProperty ?prop; om-owl:samplingTime ?instant.} GROUP BY ?instant" ;				
						String queryString = "PREFIX om-owl:<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#>\n PREFIX wgs84:<http://www.w3.org/2003/01/geo/wgs84_pos#> SELECT DISTINCT ?id ?alt ?lat ?lon ?distance ?location where {?sensor a om-owl:System; om-owl:ID ?id; om-owl:processLocation [wgs84:alt ?alt;wgs84:lat ?lat;wgs84:long ?lon]; om-owl:hasLocatedNearRel [om-owl:distance ?distance;om-owl:hasLocation ?location].}" ;
						QueryExecution qexec = QueryExecutionFactory.create(queryString,model);
						ResultSet results = qexec.execSelect() ;
						while(results.hasNext()) {
							QuerySolution soln = results.nextSolution() ;
							String id = soln.get("id").toString();
							String alt = "";
							String lat = "";
							String lon = "";
							String distance = "";
							String location = "";
							try {
								alt = soln.getLiteral("alt").getValue().toString();
								lat = soln.getLiteral("lat").getValue().toString();
								lon = soln.getLiteral("lon").getValue().toString();
								distance = soln.getLiteral("distance").getValue().toString();
								location = soln.getResource("location").getURI();
							} catch(DatatypeFormatException de) {
								
							}
							
	//						?id ?alt ?lat ?lon ?distance ?location
							bw.append(id + "," + alt + "," + lat + "," + lon + "," + distance + "," + location + "\n");
							bw.flush();
//							System.out.println(totalCount);
							List<String> stationList = null;
							if(geonames.containsKey(location)) {
								stationList = geonames.get(location);
								
							} else {
								stationList = new ArrayList<String>();
							}
							stationList.add(id);
							geonames.put(location, stationList);
						}
						
					 }
					 totalCount++;
					 
					 model.close();
				
			}
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(outputPath + "geonames_stations.csv"));
			for(Entry<String,List<String>> geoname:geonames.entrySet()) {
				bw.append(geoname.getKey());
				for(String station:geoname.getValue())
					bw.append(","+station);
				bw.append("\n");
			}
			bw.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return totalCount;
	}

}
