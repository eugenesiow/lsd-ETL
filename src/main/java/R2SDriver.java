import java.io.File;


public class R2SDriver {
	public static void main(String[] args) {

		String folderPath = "/Users/eugenesiow/Dropbox/Private/WORK/LinkedSensorData/knoesis_observations_r2rml/";
		if (args.length > 0) {
        	folderPath = args[0];
        }
		
		String outputPath = "/Users/eugenesiow/Dropbox/Private/WORK/LinkedSensorData/knoesis_observations_s2sml/";
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
				tempFileName = tempFileName.replace(".ttl", ".nt");
				
				R2RMLtoS2SML.translate(file.getPath(),outputPath + tempFileName);
				totalCount++;
			}
			
			System.out.println(totalCount);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
