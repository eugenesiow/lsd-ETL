import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GenerateFusekiConfig {
	public static void main(String[] a) {
		int fileCount = 0;
		int totalCount = 0;
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_merged/";
		String outputPath = "/Users/eugene/Downloads/knoesis_results/configs/";
		String listPath = "/Users/eugene/Downloads/knoesis_results/lists/";
		String dbPath = "/home/pi/LSD_TDB_databases/";
		try {
			BufferedWriter bw = null;
			BufferedWriter bwList = null;
			List<String> services = null;
			File folder = new File(folderPath);
			for(File file:folder.listFiles()) {
				if(totalCount%100==0) {
					if(bw!=null) {
//						bw.append("[] rdf:type fuseki:Server ;\n" + 
//								"   # Server-wide context parameters can be given here.\n" + 
//								"   # For example, to set query timeouts: on a server-wide basis:\n" + 
//								"   # Format 1: \"1000\" -- 1 second timeout\n" + 
//								"   # Format 2: \"10000,60000\" -- 10s timeout to first result, then 60s timeout to for rest of query.\n" + 
//								"   # See java doc for ARQ.queryTimeout\n" + 
//								"   # ja:context [ ja:cxtName \"arq:queryTimeout\" ;  ja:cxtValue \"10000\" ] ;\n" + 
//								"\n" + 
//								"   # Load custom code (rarely needed)\n" + 
//								"   # ja:loadClass \"your.code.Class\" ;\n" + 
//								"\n" + 
//								"   # Services available.  Only explicitly listed services are configured.\n" + 
//								"   #  If there is a service description not linked from this list, it is ignored.\n" + 
//								"   fuseki:services (\n");
//						for(String service:services)
//								bw.append("     <#"+service+">\n"); 
//						bw.append("   ) .\n");
						bw.close();
						bwList.close();
					}
					services = new ArrayList<String>();
					bwList = new BufferedWriter(new FileWriter(listPath + "stations" + fileCount + ".txt"));
					bw = new BufferedWriter(new FileWriter(outputPath + "config" + fileCount++ + ".ttl"));
					
					bw.append("@prefix :      <http://base/#> .\n" + 
							"@prefix tdb:   <http://jena.hpl.hp.com/2008/tdb#> .\n" + 
							"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
							"@prefix ja:    <http://jena.hpl.hp.com/2005/11/Assembler#> .\n" + 
							"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
							"@prefix fuseki: <http://jena.apache.org/fuseki#> .\n");
					
					bw.append("# Declaration additional assembler items.\n" + 
							"[] ja:loadClass \"org.apache.jena.tdb.TDB\" .\n" + 
							"\n" + 
							"# TDB\n" + 
							"tdb:DatasetTDB  rdfs:subClassOf  ja:RDFDataset .\n" + 
							"tdb:GraphTDB    rdfs:subClassOf  ja:Model .\n");
				}

				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".n3", "");
				bw.append("<#"+stationName+">  rdf:type fuseki:Service ;\n" + 
						"    fuseki:name              \""+stationName+"\" ;       # http://host:port/tdb\n" + 
						"	 fuseki:serviceQuery           \"query\" , \"sparql\" ;" + 
						"    fuseki:dataset           <#"+stationName+"_dataset> ;\n" + 
						"    .\n");
				bw.append("<#"+stationName+"_dataset> rdf:type      tdb:DatasetTDB ;\n" + 
						"    tdb:location \""+dbPath+stationName+"\" ;\n" + 
						"    # Query timeout on this dataset (1s, 1000 milliseconds)\n" + 
						"    ja:context [ ja:cxtName \"arq:queryTimeout\" ;  ja:cxtValue \"30000\" ] ;\n" + 
						"    # Make the default graph be the union of all named graphs.\n" + 
						"    #tdb:unionDefaultGraph true ;\n" + 
						"     .\n");
				services.add(stationName);
				bwList.append(stationName + "\n");
				
				totalCount++;
			}
//			bw.append("[] rdf:type fuseki:Server ;\n" + 
//					"   # Server-wide context parameters can be given here.\n" + 
//					"   # For example, to set query timeouts: on a server-wide basis:\n" + 
//					"   # Format 1: \"1000\" -- 1 second timeout\n" + 
//					"   # Format 2: \"10000,60000\" -- 10s timeout to first result, then 60s timeout to for rest of query.\n" + 
//					"   # See java doc for ARQ.queryTimeout\n" + 
//					"   # ja:context [ ja:cxtName \"arq:queryTimeout\" ;  ja:cxtValue \"10000\" ] ;\n" + 
//					"\n" + 
//					"   # Load custom code (rarely needed)\n" + 
//					"   # ja:loadClass \"your.code.Class\" ;\n" + 
//					"\n" + 
//					"   # Services available.  Only explicitly listed services are configured.\n" + 
//					"   #  If there is a service description not linked from this list, it is ignored.\n" + 
//					"   fuseki:services (\n");
//			for(String service:services)
//					bw.append("     <#"+service+">\n"); 
//			bw.append("   ) .\n");
			bw.close();
			bwList.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
