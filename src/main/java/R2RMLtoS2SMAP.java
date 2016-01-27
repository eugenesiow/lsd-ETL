import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;


public class R2RMLtoS2SMAP {
	
	private static Map<String,String> subjectReference = new HashMap<String,String>();

	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_map_meta_test/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_r2rml/";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		try {
		
			for(File file:folder.listFiles()) {
				
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				tempFileName = tempFileName.replace(".nt", ".ttl");
				
				File newFile = new File(outputPath + tempFileName); 
				
//				System.out.println(newFile.toString());
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
				bw.append("@prefix rr: <http://www.w3.org/ns/r2rml#> .\n");
				bw.append("@base <http://mappingpedia.org/rdb2rdf/r2rml/tc/> .\n");

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
					
					String lastTable = "";
					
					String r2rmlMapping = "";
					
					int index = 0;
					
					subjectReference.clear();
					
					for(Entry<RDFNode,List<List<RDFNode>>> entry:triples.entrySet()) {
						
						Set<String> tableSet = new HashSet<String>();
						String triplesMap = "";
						
						String subjectClass = "";
						
						RDFNode subject = entry.getKey();
						triplesMap += GetReference(subject.toString()) + "\n";
						
						for(List<RDFNode> doubleEntry:entry.getValue()) {
							List<String> result = GetPredicateObjectMap(doubleEntry);
							String poMap = result.get(0);
							if(poMap.equals("[class]")) {
								subjectClass = "\t    rr:class <"+result.get(1)+">;\n";
							} else {
								triplesMap += poMap;
							}
							if(!result.get(1).equals("") && !result.get(0).equals("[class]")) {
								tableSet.add(result.get(1));
							}
					    }
						
						triplesMap += "\ta rr:TriplesMap;\n"+
										"\trr:logicalTable [ \n";
						if(!tableSet.isEmpty()) {
							for(String table:tableSet) {
								triplesMap += "\t  rr:tableName  \""+table+"\"; \n"; 
								lastTable = table;
							}
						} else {
							triplesMap += "\t  rr:tableName  \"[no_table]\"; \n"; 
						}
						triplesMap += "\t];\n";
						
						String subjectMapLink = "";
						if(subject.isAnon()) {
							subjectMapLink = "\t    rr:constant <"+subject.toString()+">;\n";
						} else {
							String subjectUri = subject.asResource().toString();
							if(subjectUri.contains("{")) {
								subjectMapLink = "\t    rr:termType rr:IRI;  \n" + 
										"\t    rr:template \"http://data.example.com/"+index+++"/{time}\";\n";
							} else {
								subjectMapLink = "\t    rr:constant <"+subjectUri+">;\n";
							}
						}
						
						triplesMap += "\trr:subjectMap [    \n" + 
								"\t    a rr:Subject;\n" + 
								subjectMapLink +
								subjectClass +
								"\t  ];\n";

						triplesMap += ".\n";
						r2rmlMapping += triplesMap;
					}
					
					r2rmlMapping = r2rmlMapping.replaceAll("\\[no_table\\]", lastTable); //this is necessary as R2RML needs a triplemap to have a logicaltable
					
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

	private static String GetReference(String key) {
		String refName = subjectReference.get(key);
		if(refName==null) {
			refName = "<TripleMap" + subjectReference.size() + ">";
			subjectReference.put(key, refName);
		}
		return refName;
	}

	private static List<String> GetPredicateObjectMap(List<RDFNode> po) {
		String table = "";		
		String predicate = "rr:constant <"+po.get(0).toString()+">";
		String object = ""; 
		RDFNode o = po.get(1);
		
		if(po.get(0).toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) { //is a class
			List<String> classResult = new ArrayList<String>();
			classResult.add("[class]");
			if(o.isURIResource())
				classResult.add(o.asResource().getURI());
			return classResult;
		}
		
		if(o.isLiteral()) {
			//TODO: check datatypes properly
			String literal = o.asLiteral().getString();
			String[] parts = literal.split("\\.");
			object = "rr:constant \""+literal+"\"";
			if(literal.contains(".")) {
				if(parts.length>1 && !Character.isDigit(parts[1].charAt(0))) {
					table = parts[0];
					object = "rr:termType rr:Literal; rr:column \""+parts[1]+"\";";
				}
			}
		} else if(o.isURIResource()) {
			String resource = o.asResource().getURI();
			if(resource.contains("{")) {
				object = "rr:termType rr:IRI; rr:template \""+resource+"\";";
			} else {
				object = "rr:constant <"+resource+">";
			}
		} else if(o.isAnon()) {
			String key = o.toString();
			object = "rr:parentTriplesMap "+GetReference(key);
		}
		
		List<String> result = new ArrayList<String>();
		result.add("\trr:predicateObjectMap [ \n" + 
				"\t      rr:predicateMap [ "+predicate+" ]; \n" + 
				"\t      rr:objectMap    [ "+object+" ]; \n" + 
				"\t    ];\n");
		result.add(table);
		return result;
	}

}
