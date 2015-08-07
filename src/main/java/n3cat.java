import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;


public class n3cat {
	public static void main( String[] args )
    {
		try {
			String folderPath = "/Users/eugene/Downloads/rdf/";
			String outputPath = "/Users/eugene/Downloads/out.nt";
			File folder = new File(folderPath);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
			
			for(File file:folder.listFiles()) {
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
					
					// write it to standard out
					model.write(bw,"N-TRIPLES");
				 }
				 bw.flush();
				 
				 model.close();
			}
			
			
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
