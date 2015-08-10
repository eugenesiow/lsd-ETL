import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MergeRDFByStation {
	public static void main(String[] args) {
		String folderPath = "/Users/eugene/Downloads/knoesis_observations_rdf/";
		String outputPath = "/Users/eugene/Downloads/knoesis_observations_rdf_merged/";
		File folder = new File(folderPath);
		
		int totalCount = 1;
		
		for(File file:folder.listFiles()) {
			try {
				Boolean isNewFile = false;
				String tempFileName = file.getName();
				String[] parts = tempFileName.split("_");
				String filename = parts[0] + ".n3";
//				String filename = "ALDM8.csv";
				File newFile = new File(outputPath + filename); 
				if(!newFile.exists()){
					newFile.createNewFile();
					isNewFile = true;
		    	}
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile,true));
				BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
				
				String line = "";
				while((line=br.readLine())!=null) {
					if(!isNewFile) {
						if(!line.trim().startsWith("@prefix")) {
							bw.append(line+"\n");
						}
					} else {
						bw.append(line+"\n");
					}
				}
				
				bw.close();
				br.close();
				totalCount++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(totalCount);
	}
}
