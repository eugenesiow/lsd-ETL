import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ProcessMorphLog {
	public static void main(String[] args) {
//		String folderPath = "output/";
//		String outputPath = "processed/";
		String folderPath = "/Users/eugene/Documents/Programming/morph/examples/srbench/output/";
		if (args.length > 0) {
        	folderPath = args[0];
        }
		
		String outputPath = "/Users/eugene/Documents/Programming/morph/processed/";
		if (args.length > 1) {
			outputPath = args[1];
        }
		
		try {
			for(int i=1;i<=10;i++) {
				File folder = new File(folderPath+"q"+i);
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "q" + i + ".csv"));
				for(File file:folder.listFiles()) {
					String tempFileName = file.getName();
					if(tempFileName.startsWith("."))
						continue;
					String stationName = tempFileName.replace(".out", "");

					BufferedReader br = new BufferedReader(new FileReader(file));
					String line = "";
					long trans = 0;
					long query = 0;
					while( (line=br.readLine()) != null ) {
						String parts[] = line.split(" ");
						if(line.contains("Query translation time =")) {
							trans = Long.parseLong(parts[parts.length-2]);
						} else if(line.contains("Result generation time =")) {
							query = Long.parseLong(parts[parts.length-2]);
						}
					}
					bw.append(stationName+","+trans+","+query+","+(query+trans)+"\n");
					br.close();
				}
				bw.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
