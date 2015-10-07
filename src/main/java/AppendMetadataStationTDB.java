import java.io.File;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.sys.SystemTDB;


public class AppendMetadataStationTDB {

	public static void main(String[] args) {		
		String folderPath = "/Users/eugene/Downloads/knoesis_linkedsensordata_rdf/";
		String dbPath = "/Users/eugene/LSD_TDB_databases_meta/";
        if(args.length>0) {
        	folderPath = args[0];
		}
		File folder = new File(folderPath);
		int totalCount = 1;
		
		for(File file:folder.listFiles()) {
			String tempFileName = file.getName();
			if(tempFileName.startsWith("."))
				continue;
			String stationFileName = tempFileName.replace(".n3", "");
			String[] parts = stationFileName.split("_");
			String stationName = parts[0];
			
			File f = new File(dbPath+stationName);
			if(f.exists()) { 
				SystemTDB.setFileMode(FileMode.direct);
				Dataset ds=TDBFactory.createDataset(dbPath+stationName);
				TDBLoader.loadModel(ds.getDefaultModel(), file.getPath(), false);
	//			StmtIterator it = ds.getDefaultModel().listStatements();
	//			while(it.hasNext()) {
	//				Statement st = it.next();
	//	//			System.out.println(st.getPredicate());
	//			}
		//		ds.commit();
				ds.close();
				totalCount++;
			}
		}
		System.out.println(totalCount);
	}

}