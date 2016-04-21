import java.io.File;

public class testETL {
	public static void main(String[] args) {
		//Map from RDF
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf_merged/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_map_snow/";
		ReverseMapFromN3.run(folderPath, mkdir(outputPath));
		
		//Add metadata to Map
		String metadataPath = "/Users/eugene/Dropbox/Private/WORK/LinkedSensorData/knoesis_metadata_csv/";
		folderPath = "/Users/eugene/Downloads/knoesis_observations_map_snow/";
		outputPath = "/Users/eugene/Downloads/knoesis_observations_map_snow_meta/";
		AddPosMetadataToStationMap.run(folderPath, mkdir(outputPath), metadataPath);
	}
	
	private static String mkdir(String outputPath) {
		File file = new File(outputPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return outputPath;
	}
}
