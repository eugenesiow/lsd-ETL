import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;


public class R2RMLtoS2SMAP {

	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_map_meta/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_r2rml/";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		try {
		
			for(File file:folder.listFiles()) {
				
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				
				File newFile = new File(outputPath + tempFileName); 
				
//				System.out.println(newFile.toString());
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));

				// create an empty model
				 Model model = ModelFactory.createDefaultModel();
				 
				 Map<RDFNode,List<List<RDFNode>>> triples = new HashMap<RDFNode,List<List<RDFNode>>>(); 
				 
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
						List<RDFNode> doubles = new ArrayList<RDFNode>();
						doubles.add(soln.get("p"));
						doubles.add(soln.get("o"));
						RDFNode key = soln.get("s");
						List<List<RDFNode>> triple = triples.get(key);
						if(triple==null) {
							triple = new ArrayList<List<RDFNode>>();
						}
						triple.add(doubles);
						triples.put(soln.get("s"),triple);
					}
					
					String r2rmlMapping = "";
					
					int index = 0;
					
					for(Entry<RDFNode,List<List<RDFNode>>> entry:triples.entrySet()) {
						
						String triplesMap = "";
						
						triplesMap += "\trr:subjectMap [    \n" + 
								"\t    a rr:Subject;\n" + 
								"\t    rr:termType rr:IRI;  \n" + 
								"\t    rr:template \"http://data.example.com/"+index+++"/{time}\";\n" + 
								"\t    rr:class <http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#MeasureData>;\n" + 
								"\t  ];\n";
						
						for(List<RDFNode> doubleEntry:entry.getValue()) {
							triplesMap += GetPredicateObjectMap(doubleEntry);
					    }
						
						triplesMap += "\ta rr:TriplesMap;\n" + 
								"\t  rr:logicalTable [ \n" + 
								"\t    rr:tableName  \"_4UT01\" \n" + 
								"\t  ];\n";
						
						triplesMap += ".\n";
						r2rmlMapping += triplesMap;
					}
					
					bw.append(r2rmlMapping);
					bw.close();
				 }
				 totalCount++;
				 
				 model.close();
				
			}
			
			System.out.println(totalCount);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String GetPredicateObjectMap(List<RDFNode> po) {
		List<String> tables = new ArrayList<String>();
		String predicate = "rr:constant <"+po.get(0).toString()+">";
		String object = ""; //TODO: object map
		return "\trr:predicateObjectMap [ \n" + 
				"\t      rr:predicateMap [ "+predicate+" ]; \n" + 
				"\t      rr:objectMap    [ "+object+" ]; \n" + 
				"\t    ];\n";
	}

}
