import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class FixDateTime {
	public static void main(String[] a) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_ike_rdf_merged/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_ike_rdf_fix/";
		
		File folder = new File(folderPath);
		
		for(File file:folder.listFiles()) {
			String tempFileName = file.getName();
			if(tempFileName.startsWith("."))
				continue;
			String stationName = tempFileName.replace(".n3", "");
			try {
				BufferedReader br = new BufferedReader(new FileReader(folderPath + stationName + ".n3"));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + stationName + ".n3"));
				String line = "";
				while((line=br.readLine())!=null) {
					if(line.length()<300) {
						Matcher m = Pattern.compile("(.*?)\"(.*?)\"\\^\\^xsd:dateTime(.*)").matcher(line);
						while(m.find()) {
							DateTime dt = new DateTime(m.group(2),DateTimeZone.UTC);
							DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
							line = m.group(1) + "\"" + dt.toString(dtf) + "\"^^xsd:dateTime"+m.group(3); 
						}
					}
					bw.append(line + "\n");
				}
				bw.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
