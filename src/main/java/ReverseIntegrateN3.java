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
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_map/";
		File folder = new File(folderPath);
		
		List<String> doneStations = new ArrayList<String>();
		
		int totalCount = 0;
		for(File file:folder.listFiles()) {
			try {
				String tempFileName = file.getName();
				String[] parts = tempFileName.split("_");
				if(!doneStations.contains(parts[0])) {
					doneStations.add(parts[0]);
					String filename = parts[0] + ".nt";
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
						Resource sensor = outModel.createResource("http://knoesis.wright.edu/ssw/System_"+parts[0]);
						while(results.hasNext()) {
							QuerySolution soln = results.nextSolution() ;
							Resource obs = outModel.createResource();
							Resource result = outModel.createResource();
							Resource instant = outModel.createResource();
							String propName = soln.getResource("prop").toString().replace("http://knoesis.wright.edu/ssw/ont/weather.owl#_", "");
							
							//add ssn structure
							outModel.add(obs,RDF.type,soln.getResource("class"));
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result"),result);
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#procedure"),sensor);
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#observedProperty"),soln.getResource("prop"));
							outModel.add(obs,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#samplingTime"),instant);
							outModel.add(result,RDF.type,outModel.createResource("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#MeasureData"));
							outModel.add(result,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue"),outModel.createLiteral(parts[0]+"."+propName));
							outModel.add(result,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom"),soln.get("uom"));
							outModel.add(instant,RDF.type,outModel.createResource("http://www.w3.org/2006/time#Instant"));
							outModel.add(instant,outModel.createProperty("http://www.w3.org/2006/time#inXSDDateTime"),outModel.createLiteral(parts[0]+".time"));
						}
						outModel.write(bw,"N-TRIPLES");
						
//						@prefix om-owl:  <http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#> .
//							@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
//							@prefix sens-obs:  <http://knoesis.wright.edu/ssw/> .
//							@prefix owl-time:  <http://www.w3.org/2006/time#> .
//							@prefix owl:     <http://www.w3.org/2002/07/owl#> .
//							@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
//							@prefix weather:  <http://knoesis.wright.edu/ssw/ont/weather.owl#> .
//							@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
//						
//						sens-obs:MeasureData_WindDirection_4UT01_2003_3_31_15_55_00
//					      a       om-owl:MeasureData ;
//					      om-owl:floatValue "112.0"^^xsd:float ;
//					      om-owl:uom weather:degrees .
//
//					sens-obs:Observation_WindDirection_4UT01_2003_3_31_4_50_00
//					      a       weather:WindDirectionObservation ;
//					      om-owl:observedProperty
//					              weather:_WindDirection ;
//					      om-owl:procedure sens-obs:System_4UT01 ;
//					      om-owl:result sens-obs:MeasureData_WindDirection_4UT01_2003_3_31_4_50_00 ;
//					      om-owl:samplingTime sens-obs:Instant_2003_3_31_4_50_00 .
						
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
