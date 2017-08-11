import java.io.File;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;


public class DeviceCounter {
	public static void main(String[] args) {
		run("/Users/eugene/Downloads/knoesis_linkedsensordata_rdf/");
	}
	
	public static void run(String folderPath) {

		File folder = new File(folderPath);
		
		long totalCount = 0;
		long fileCount = 0;
		
//		System.out.println(folderPath);
		for(File file:folder.listFiles()) {
			try {		
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				Model model = ModelFactory.createDefaultModel();
				String inputName = file.getPath();
				if(inputName.endsWith(".n3")) {
					 InputStream in = FileManager.get().open( inputName );
					 if (in == null) {
						 throw new IllegalArgumentException("File: " + inputName + " not found");
					 }
					 model.read(in, null, "N3");
					 StmtIterator stmtIt = model.listStatements();
					 while(stmtIt.hasNext()) {
						 Statement st = stmtIt.next();
						totalCount++;
					 }
					 fileCount++;
				}
				model.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(fileCount%500==0)
				System.out.println(fileCount);
		}
		System.out.println(totalCount);
	}
}
