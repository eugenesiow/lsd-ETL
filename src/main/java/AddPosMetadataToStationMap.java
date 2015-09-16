import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;


public class AddPosMetadataToStationMap {
	public static void main(String[] args) {
		try {
			String metadataPath = "/Users/eugene/Dropbox/Private/WORK/LinkedSensorData/knoesis_metadata_csv/sensors.csv";
			Map<String,String> metadata = new HashMap<String,String>();
			BufferedReader br = new BufferedReader(new FileReader(metadataPath));
			String line="";
			while((line=br.readLine())!=null) {
				String[] parts = line.split(",",-1);
				if(parts.length>5) 
					metadata.put(parts[0], parts[1]+","+parts[2]+","+parts[3]+","+parts[4]+","+parts[5]);
			}
			br.close();
			String folderPath = "/Users/eugene/Downloads/knoesis_observations_map/";
			String outputPath = "/Users/eugene/Downloads/knoesis_observations_map_meta/";
			File folder = new File(folderPath);
			for(File file:folder.listFiles()) {
				String inputName = file.getPath();
				String fileName = file.getName();
				if(inputName.endsWith(".nt") && !fileName.startsWith(".")) {
					BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath+fileName));
					br = new BufferedReader(new FileReader(inputName));
					line = "";
					while((line=br.readLine())!=null) {
						bw.append(line+"\n");
					}
					br.close();
					String stationName = fileName.replace(".nt", "");
					if(metadata.containsKey(stationName)) {
						String[] parts = metadata.get(stationName).split(",",-1);
						Model outModel = ModelFactory.createDefaultModel();
						Resource sensor = outModel.createResource("http://knoesis.wright.edu/ssw/System_"+stationName);
						Resource point = outModel.createResource("http://knoesis.wright.edu/ssw/point_"+stationName);
						Resource locNear = outModel.createResource("http://knoesis.wright.edu/ssw/LocatedNearRel"+stationName);
						Resource miles = outModel.createResource("http://knoesis.wright.edu/ssw/ont/weather.owl#miles");
						outModel.add(sensor,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#processLocation"),point);
						outModel.add(sensor,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#processLocation"),point);
						outModel.add(point,RDF.type,outModel.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#Point"));
						outModel.add(point,outModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#alt"),outModel.createLiteral(parts[0]));
						outModel.add(point,outModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat"),outModel.createLiteral(parts[1]));
						outModel.add(point,outModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long"),outModel.createLiteral(parts[2]));
						outModel.add(sensor,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#hasLocatedNearRel"),locNear);
						outModel.add(locNear,RDF.type,outModel.createResource("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#LocatedNearRel"));
						outModel.add(locNear,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#distance"),outModel.createLiteral(parts[3]));
						outModel.add(locNear,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#hasLocation"),outModel.createResource(parts[4]));
						outModel.add(locNear,outModel.createProperty("http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom"),miles);
						outModel.write(bw,"N-TRIPLES");
						outModel.close();
					} 
					bw.close();
//					else {
//						System.out.println("missing:"+stationName);
//					}
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
