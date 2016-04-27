import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class CreateDbsMonetDB {
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_csv/";
		if (args.length > 0) {
        	folderPath = args[0];
		}
		
		String outputPath = "/Users/eugene/";
		if (args.length > 1) {
			outputPath = args[1];
        }
		
		try {
			File folder = new File(folderPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "create_db.sh"));
			
			for(File file:folder.listFiles()) {
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
				String stationName = tempFileName.replace(".csv", "");
				bw.append("./monetdb create " + stationName + "\n");
				bw.append("./monetdb release " + stationName + "\n");
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
