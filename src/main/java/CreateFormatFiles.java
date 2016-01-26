import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class CreateFormatFiles {

	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_ike_csv/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_ike_format/";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		
		for(File file:folder.listFiles()) {
			try {				
				Boolean isNewFile = false;
				String tempFileName = file.getName();
				if(tempFileName.startsWith("."))
					continue;
//				String[] parts = tempFileName.split("_");
				
				String filename = "_" + tempFileName.replace(".csv", "") + ".format";
				File newFile = new File(outputPath + filename); 
				if(!newFile.exists()){
					newFile.createNewFile();
					isNewFile = true;
		    	}
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile,true));
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				String header = br.readLine();
				String[] headerParts = header.split(",");
				
				for(String part:headerParts) {
					String type = "float";
					if(part.trim().toLowerCase().equals("time")) {
						type = "time";
					}
					bw.append(part+","+type+"\n");
				}
				
				br.close();
				
				totalCount++;
					
				bw.flush();
				bw.close();
				 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(totalCount);
	}

}
