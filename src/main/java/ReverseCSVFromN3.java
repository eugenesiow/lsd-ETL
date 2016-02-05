import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class ReverseCSVFromN3 {

	public static int run(String folderPath, String outputPath) {
//	public static void main(String[] args) {
//		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_merged/";
//		String folderPath = "/Users/eugene/Downloads/knoesis_observations_ike_rdf_merged_nafix/";
//		String outputPath = "/Users/eugene/Downloads/knoesis_observations_ike_csv/";
//		String outputPath = "/Users/eugene/Downloads/knoesis_observations_csv/";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		
//		System.out.println(folderPath);
		for(File file:folder.listFiles()) {
			try {				
				Boolean isNewFile = false;
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
//				String[] parts = tempFileName.split("_");
				
//				String filename = tempFileName.replace(".n3.bak", "") + ".csv";
				String filename = tempFileName.replace(".n3", "") + ".csv";
//				String filename = "ALDM8.csv";
				File newFile = new File(outputPath + filename); 
				if(!newFile.exists()){
					newFile.createNewFile();
					isNewFile = true;
		    	}
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile,true));
				
				// create an empty model
				 Model model = ModelFactory.createDefaultModel();
	
				 // use the FileManager to find the input file
				 String inputName = file.getPath();
//				 String inputName = folderPath + "ALDM8_2003_4_1.n3";
//				 if(inputName.endsWith(".n3.bak")) {
				 if(inputName.endsWith(".n3")) {
					 InputStream in = FileManager.get().open( inputName );
					 
					 if (in == null) {
					    throw new IllegalArgumentException(
					                                 "File: " + inputName + " not found");
					}
	
					// read the RDF/XML file
					model.read(in, null, "N3");
	//				String queryString = "PREFIX om-owl:<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> SELECT (group_concat(?prop) as ?propall) where {?obs om-owl:procedure ?sensor;a ?class; om-owl:observedProperty ?prop; om-owl:samplingTime ?instant.} GROUP BY ?instant" ;				
					String queryString = "PREFIX om-owl:<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> SELECT DISTINCT ?class ?prop ?resultClass where {?obs om-owl:procedure ?sensor;a ?class; om-owl:observedProperty ?prop; om-owl:result [a ?resultClass].}" ;
					QueryExecution qexec = QueryExecutionFactory.create(queryString,model);
					ResultSet results = qexec.execSelect() ;
					List<String> propNames = new ArrayList<String>();
					int count = 0;
					String whereExtra = "\n";
					String selectExtra = " ";
					while(results.hasNext()) {
						QuerySolution soln = results.nextSolution() ;
	//					observations.add(soln.get("class").toString());
						String prop = soln.get("prop").toString();
						String resultClass = soln.get("resultClass").toString().replace("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#","");
						if(resultClass.equals("MeasureData")) {
							propNames.add(prop.replace("http://knoesis.wright.edu/ssw/ont/weather.owl#_", ""));
							selectExtra += "?val"+count+" ?uom"+count+" ";
							whereExtra += "OPTIONAL { ?obs"+count+" om-owl:samplingTime ?instant; a <"+soln.get("class").toString()+">; om-owl:observedProperty <"+prop+">; om-owl:result ?result"+count+". ?result"+count+" om-owl:floatValue ?val"+count+"; om-owl:uom ?uom"+count+".}\n";
						} else if(resultClass.equals("TruthData")) {
							propNames.add(prop.replace("http://knoesis.wright.edu/ssw/ont/weather.owl#_", "")+"_bool");
							selectExtra += "?val"+count+" ";
							whereExtra += "OPTIONAL { ?obs"+count+" om-owl:samplingTime ?instant; a <"+soln.get("class").toString()+">; om-owl:observedProperty <"+prop+">; om-owl:result ?result"+count+". ?result"+count+" om-owl:booleanValue ?val"+count+".}\n";
						}
//						System.out.println(soln.get("resultClass"));
						count++;
					}
					queryString = "PREFIX om-owl:<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#>\n"
							+ "PREFIX owl-time:<http://www.w3.org/2006/time#> \n"
							+ "SELECT ?time "+selectExtra+" where {?instant a owl-time:Instant; owl-time:inXSDDateTime ?time. "+whereExtra+"}" ;
//					System.out.println(queryString);
					qexec = QueryExecutionFactory.create(queryString,model);
					results = qexec.execSelect() ;
					if(isNewFile) {
						bw.append("time");
						for(int i=0;i<count;i++) {
							bw.append("," + propNames.get(i));						
						}
						bw.append("\n");
					}
					while(results.hasNext()) {
						QuerySolution soln = results.nextSolution() ;
//						System.out.println(soln);
						String time = soln.getLiteral("time").getValue().toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", ""); //theres a mistake storing this as a string instead of datetime in the LSD data
//						bw.append(time);
						DateTime dt = new DateTime(time,DateTimeZone.UTC);
						DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
						bw.append(dt.toString(dtf)); 
						for(int i=0;i<count;i++) {
							if(soln.contains("val"+i)) {
								Literal val = soln.getLiteral("val"+i);
								if(val.getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#float"))
									bw.append(","+val.getFloat());
								else
									bw.append(","+val.getString().replace("^^http://www.w3.org/2001/XMLSchema#boolean",""));
							} else {
								bw.append(",");
							}
						}
						bw.append("\n");
					}
					totalCount++;
					
				 }
				 bw.flush();
				 bw.close();
				 
				 model.close();
				 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		System.out.println(totalCount);
		return totalCount;
	}

}
