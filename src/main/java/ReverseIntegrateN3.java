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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;


public class ReverseIntegrateN3 {

	public static void main(String[] args) {
//		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_merged/";
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_ike_rdf_merged/";
//		String outputPath = "/Users/eugene/Downloads/knoesis_observations_map/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_ike_map/";
		File folder = new File(folderPath);
		
		List<String> doneStations = new ArrayList<String>();
		
		int totalCount = 0;
		for(File file:folder.listFiles()) {
			try {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
//				String[] parts = tempFileName.split("_");
				String stationName = tempFileName.replace(".n3", "");
				if(!doneStations.contains(stationName)) {
					doneStations.add(stationName);
					String filename = stationName + ".nt";
					BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + filename));
					
					// create an empty model
					 Model model = ModelFactory.createDefaultModel();
					 Model outModel = ModelFactory.createDefaultModel();
		
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
						String queryString = "PREFIX om-owl:<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> SELECT DISTINCT ?class ?prop ?uom where {?obs om-owl:procedure ?sensor;a ?class; om-owl:observedProperty ?prop; om-owl:result [om-owl:uom ?uom].}" ;
						QueryExecution qexec = QueryExecutionFactory.create(queryString,model);
						ResultSet results = qexec.execSelect() ;
						Resource sensor = outModel.createResource("http://knoesis.wright.edu/ssw/System_"+stationName);
						Resource instant = outModel.createResource();
						while(results.hasNext()) {
							QuerySolution soln = results.nextSolution() ;
							Resource obs = outModel.createResource();
							Resource result = outModel.createResource();
							String propName = soln.getResource("prop").toString().replace("http://knoesis.wright.edu/ssw/ont/weather.owl#_", "");
							
							//add ssn structure
							outModel.add(obs,RDF.type,soln.getResource("class"));
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result"),result);
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#procedure"),sensor);
							outModel.add(sensor,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#generatedObservation"),obs);
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#observedProperty"),soln.getResource("prop"));
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#samplingTime"),instant);
							outModel.add(result,RDF.type,outModel.createResource("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#MeasureData"));
							outModel.add(result,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue"),outModel.createLiteral("_"+stationName+"."+propName));
							outModel.add(result,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom"),soln.get("uom"));
							outModel.add(instant,RDF.type,outModel.createResource("http://www.w3.org/2006/time#Instant"));
							outModel.add(instant,outModel.createProperty("http://www.w3.org/2006/time#inXSDDateTime"),outModel.createLiteral("_"+stationName+".time"));
						}
						outModel.write(bw,"N-TRIPLES");
						
						
						 bw.flush();
						 bw.close();
						 
						 model.close();
						 outModel.close();
					 }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
