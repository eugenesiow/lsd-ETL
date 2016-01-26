import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;


public class R2RMLtoS2SMAP {

	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_map_meta";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_r2rml";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		try {
		
			for(File file:folder.listFiles()) {
				
	//				String tempFileName = file.getName();
	//				String[] parts = tempFileName.split("_");
	//				String filename = parts[0] + ".csv";
	//				
	//				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
					
					
					// create an empty model
					 Model model = ModelFactory.createDefaultModel();
					 
					 Map<String,List<String>> triples = new HashMap<String,List<String>>(); 
					 
					 // use the FileManager to find the input file
					 String inputName = file.getPath();
					 if(inputName.endsWith(".nt")) {
						 InputStream in = FileManager.get().open( inputName );
						 
						 if (in == null) {
						    throw new IllegalArgumentException(
						                                 "File: " + inputName + " not found");
						}
		
						// read the RDF/XML file
						model.read(in, null, "N3");
						String queryString = "SELECT * {\n" + 
								"	?s ?p ?o.\n" + 
								"}" ;
						QueryExecution qexec = QueryExecutionFactory.create(queryString,model);
						ResultSet results = qexec.execSelect() ;
						while(results.hasNext()) { //add all triples to a map data structure with the key as the subject
							QuerySolution soln = results.nextSolution();
							List<String> doubles = new ArrayList<String>();
							doubles.add(soln.get("p").toString());
							doubles.add(soln.get("o").toString());
							triples.put(soln.get("s").toString(),doubles);
						}
						
						while(!triples.isEmpty()) {
							
							Iterator<Entry<String, List<String>>> it = triples.entrySet().iterator();
							Entry<String, List<String>> firstEntry = it.next();
							GetPredicateObjectMap(firstEntry.getValue());
							it.remove();
							while(it.hasNext()) {
								Entry<String, List<String>> entry = it.next();
								if(entry.getKey().equals(firstEntry.getKey())) {
									GetPredicateObjectMap(entry.getValue());
									it.remove();
								}
						    }
						}
						
					 }
					 totalCount++;
					 
					 model.close();
				
			}
			
			System.out.println(totalCount);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String GetPredicateObjectMap(List<String> value) {
		
		return "";
	}

}
