import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;


public class R2RMLtoS2SML {
	
	private final static String rr = "http://www.w3.org/ns/r2rml#";
	private final static String a = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static Model s2sml = ModelFactory.createDefaultModel();
	private static Model r2rml = ModelFactory.createDefaultModel();
	
	private static Map<String,String> tpMapRef = new HashMap<String,String>();

	public static void translate(String inputFile,String outputFile) {
		try {
			// use the FileManager to find the input file
			if(inputFile.endsWith(".ttl")) {
				InputStream in = FileManager.get().open( inputFile );
				if (in == null) {
					throw new IllegalArgumentException("File: " + inputFile + " not found");
				}

				// read the file
				r2rml.read(in, null, "TURTLE");
				String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#>"
						+ "SELECT * {\n" + 
						"	?s a rr:TriplesMap;\n" +
						"		rr:subjectMap ?o.\n" + 
						"}" ;
				QueryExecution qexec = QueryExecutionFactory.create(queryString,r2rml);
				ResultSet results = qexec.execSelect() ;
				while(results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					String tpMap = soln.get("s").asResource().getURI();
					Resource subjectMap = soln.get("o").asResource();
					String tableName = "";
					
					//get logical table
					String tableString = "PREFIX rr: <http://www.w3.org/ns/r2rml#>"
							+ "SELECT * {\n" + 
							"	<"+tpMap+"> rr:logicalTable ?table.\n" +
							"	?table rr:tableName ?tableName.\n" + 
							"}" ;
					ResultSet tableResults = QueryExecutionFactory.create(tableString,r2rml).execSelect() ;
					while(tableResults.hasNext()) {
						QuerySolution tableSoln = tableResults.nextSolution();
						tableName = tableSoln.get("tableName").toString();
					}
					
					//create subject
					Resource subject = ProcessMap(tableName, subjectMap).asResource();
					tpMapRef.put(tpMap,subject.getURI());
				}
				
				QueryExecution qexec2 = QueryExecutionFactory.create(queryString,r2rml);
				ResultSet results2 = qexec2.execSelect() ;
				while(results2.hasNext()) {
					QuerySolution soln = results2.nextSolution();
					String tpMap = soln.get("s").asResource().getURI();
					Resource subjectMap = soln.get("o").asResource();
					String tableName = "";
					
					//get logical table
					String tableString = "PREFIX rr: <http://www.w3.org/ns/r2rml#>"
							+ "SELECT * {\n" + 
							"	<"+tpMap+"> rr:logicalTable ?table.\n" +
							"	?table rr:tableName ?tableName.\n" + 
							"}" ;
					ResultSet tableResults = QueryExecutionFactory.create(tableString,r2rml).execSelect() ;
					while(tableResults.hasNext()) {
						QuerySolution tableSoln = tableResults.nextSolution();
						tableName = tableSoln.get("tableName").toString();
					}
					
					//create subject
					Resource subject = ProcessMap(tableName, subjectMap).asResource();
				
					//get class if exists
					StmtIterator stmts = r2rml.listStatements(subjectMap, ResourceFactory.createProperty(rr+"class"), (RDFNode) null);
					while ( stmts.hasNext() ) {
						Statement stmt = stmts.next();
						s2sml.add(subject, ResourceFactory.createProperty(a), stmt.getObject().asResource());
			        }
					
					//get predicate object maps
					String poString = "PREFIX rr: <http://www.w3.org/ns/r2rml#>"
							+ "SELECT * {\n" + 
							"	<"+tpMap+"> rr:predicateObjectMap ?po.\n" +
							"	?po rr:predicateMap ?p;\n" +
							"		rr:objectMap ?o.\n" +
							"}" ;
					ResultSet poResults = QueryExecutionFactory.create(poString,r2rml).execSelect() ;
					while(poResults.hasNext()) {
						QuerySolution poSoln = poResults.nextSolution();
						Resource predicate = ProcessMap(tableName,poSoln.get("p").asResource()).asResource();
						RDFNode object = ProcessMap(tableName,poSoln.get("o").asResource());
						s2sml.add(subject, ResourceFactory.createProperty(predicate.getURI()), object);
					}
				}
				
				r2rml.close();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				RDFDataMgr.write(baos, s2sml, RDFFormat.TURTLE_PRETTY) ;
				System.out.println(baos.toString());
				s2sml.close();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static RDFNode ProcessMap(String tableName, Resource node) {
		RDFNode res = ResourceFactory.createResource();
		if(r2rml.contains(node, ResourceFactory.createProperty(rr+"termType"), ResourceFactory.createResource(rr+"Literal"))) {
			StmtIterator stmts = r2rml.listStatements(node, ResourceFactory.createProperty(rr+"column"), (RDFNode) null);
			while ( stmts.hasNext() ) {
				Statement stmt = stmts.next();
				res = ResourceFactory.createPlainLiteral(tableName.concat("."+stmt.getObject().asLiteral().getString()));
	        }
		} else if(r2rml.contains(node, ResourceFactory.createProperty(rr+"termType"), ResourceFactory.createResource(rr+"IRI"))) {
			StmtIterator stmts = r2rml.listStatements(node, ResourceFactory.createProperty(rr+"template"), (RDFNode) null);
			while ( stmts.hasNext() ) {
				Statement stmt = stmts.next();
				res = ResourceFactory.createResource(stmt.getObject().asLiteral().getString().replace("{", "{"+tableName+"."));
	        }
		} else if(r2rml.contains(node, ResourceFactory.createProperty(rr+"constant"))) {
			StmtIterator stmts = r2rml.listStatements(node, ResourceFactory.createProperty(rr+"constant"), (RDFNode) null);
			while ( stmts.hasNext() ) {
				Statement stmt = stmts.next();
				RDFNode obj = stmt.getObject();
				if(obj.isResource()) 
					res = ResourceFactory.createResource(obj.asResource().getURI());
				else if(obj.isLiteral())
					res = ResourceFactory.createPlainLiteral(obj.asLiteral().getString());
	        }
		} else if(r2rml.contains(node, ResourceFactory.createProperty(rr+"parentTriplesMap"))) {
			StmtIterator stmts = r2rml.listStatements(node, ResourceFactory.createProperty(rr+"parentTriplesMap"), (RDFNode) null);
			while ( stmts.hasNext() ) {
				Statement stmt = stmts.next();
//				res = stmt.getObject().asResource();
				String sIRI = tpMapRef.get(stmt.getObject().asResource().getURI());
				res = ResourceFactory.createResource(sIRI);
	        }
		}
		return res;
	}
}
