import java.io.File;


public class S2RDriver {
	public static void main(String[] args) {

		String folderPath = "/Users/eugenesiow/Dropbox/Private/WORK/LinkedSensorData/knoesis_observations_map_meta_test/";
//		String folderPath = "/Users/eugene/Downloads/knoesis_observations_map_snow_meta/";
//		String outputPath = "/Users/eugene/Downloads/knoesis_observations_r2rml/";		
//		String folderPath = "/Users/eugene/Downloads/smarthome_map/";
		if (args.length > 0) {
        	folderPath = args[0];
        }
		
//		String outputPath = "/Users/eugene/Downloads/smarthome_r2rml/";
		String outputPath = "/Users/eugenesiow/Dropbox/Private/WORK/LinkedSensorData/knoesis_observations_r2rml/";
		if (args.length > 1) {
			outputPath = args[1];
        }
		
		File folder = new File(folderPath);
		
		int totalCount = 1;
		try {
		
			for(File file:folder.listFiles()) {
				
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				tempFileName = tempFileName.replace(".nt", ".ttl");
				
				S2SMLtoR2RML.translate(file.getPath(),outputPath + tempFileName);
				totalCount++;
			}
			
			System.out.println(totalCount);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
