import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.PrefixMapping;


public class rdf2dot {
	public static void main(String[] args) {
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
		model.read("/Users/eugene/Documents/workspace/sparql2sql/mapping/smarthome_environment.nt");
		
		// list the statements in the Model
		StmtIterator iter = model.listStatements();
		
		PrefixMapping pm = PrefixMapping.Factory.create();
		pm.setNsPrefix("ssn", "http://purl.oclc.org/NET/ssnx/ssn#");
		pm.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		pm.setNsPrefix("iot", "http://purl.oclc.org/NET/iot#");
		pm.setNsPrefix("smh", "http://iot.soton.ac.uk/smarthome/sensor#");
		pm.setNsPrefix("time", "http://www.w3.org/2006/time#");

		GraphViz gv = new GraphViz();
		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
//		    Resource  subject   = stmt.getSubject();     // get the subject
//		    Property  predicate = stmt.getPredicate();   // get the predicate
//		    RDFNode   object    = stmt.getObject();      // get the object
		    Triple triple = new Triple(stmt.getSubject().asNode(),stmt.getPredicate().asNode(),stmt.getObject().asNode());

//		    System.out.print(subject.toString());
//		    System.out.print(" " + predicate.toString() + " ");
//		    if (object instanceof Resource) {
//		       System.out.print(object.toString());
//		    } else {
//		        // object is a literal
//		        System.out.print(" \"" + object.toString() + "\"");
//		    }
		    
//		    gv.addEdge(subject.toString(), predicate.toString(), object.toString());
		    gv.addTriple(triple, pm);
		 
//		    System.out.println(" .");
		} 
		
		System.out.println(gv.toString());
	
		model.close();
	}
}
