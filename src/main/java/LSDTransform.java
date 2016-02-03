import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class LSDTransform {

	public static void main(String[] args) {
		Options options = new Options();
		
		List<String> stages = new ArrayList<String>();
		stages.add("1.Merge RDF by station");
		stages.add("2.Generate Mapping from RDF");
		stages.add("3.Generate CSV from RDF");
		stages.add("4.Metadata to CSV from RDF");
		stages.add("5.Add Metadata to Mappings");
		stages.add("6.Load RDBMS");
		stages.add("7.Load TDB");
		
		String desc = "runs the particular stage";
		for(String stage:stages) {
			desc += "\n" + stage;
		}
		
		Option stage = Option.builder("S")
			     .required(false)
			     .longOpt("stage")
			     .desc( desc )
			     .hasArg()
			     .argName("stage number")
			     .build();
		
		Option src = Option.builder("I")
				.longOpt("src")
				.required(true)
				.hasArg()
				.desc( "the source folder path" )
				.argName("folder path")
				.build();
		
		Option metaSrc = Option.builder("M")
				.longOpt("metadata_src")
				.required(false)
				.hasArg()
				.desc( "the metadata folder path" )
				.argName("folder path")
				.build();
		
		Option output = Option.builder("O")
				.longOpt("output")
				.required(true)
				.hasArg()
				.desc( "the output folder path" )
				.argName("folder path")
				.build();
		
		Option driver = Option.builder("D")
				.longOpt("jdbc_driver")
				.required(false)
				.hasArg()
				.desc( "the JDBC driver package" )
				.argName("driver")
				.build();
		
		Option jdbcStr = Option.builder("J")
				.longOpt("jdbc_string")
				.required(false)
				.hasArg()
				.desc( "the jdbc path" )
				.argName("the jdbc string")
				.build();
		
		Option userOpt = Option.builder("U")
				.longOpt("user")
				.required(false)
				.hasArg()
				.desc( "the db user" )
				.argName("username")
				.build();
		
		Option passOpt = Option.builder("P")
				.longOpt("password")
				.required(false)
				.hasArg()
				.desc( "the db password" )
				.argName("password")
				.build();
		
		options.addOption(stage);
		options.addOption(src);
		options.addOption(output);
		options.addOption(metaSrc);
		options.addOption(driver);
		options.addOption(jdbcStr);
		options.addOption(userOpt);
		options.addOption(passOpt);
		
		//test data
		args = new String[]{ "-I /users/", "-O /users/out", "-M /users/meta", "-D org.h2.Driver", "-J jdbc:h2:", "-U sa", "-P " };
		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			
			if( cmd.hasOption( "S" ) ) {
		        System.out.println( cmd.getOptionValue( "S" ) );
		    }
		    
		    String inputPath = cmd.getOptionValue( "I" );
		    String metadataPath = null;
		    if(cmd.hasOption("M")) {
		    	metadataPath = cmd.getOptionValue( "M" );
		    }
		    String driverStr = "org.h2.Driver";
		    if(cmd.hasOption("D")) {
		    	driverStr = cmd.getOptionValue( "D" );
		    }
		    String jdbc = "jdbc:h2:";
		    if(cmd.hasOption("J")) {
		    	jdbc = cmd.getOptionValue( "J" );
		    }
		    String user = "sa";
		    if(cmd.hasOption("U")) {
		    	user = cmd.getOptionValue( "U" );
		    }
		    String pass = "";
		    if(cmd.hasOption("P")) {
		    	pass = cmd.getOptionValue( "P" );
		    }
		    String outputPath = cmd.getOptionValue( "O" );
		    
		    //add a separator at the end of the output path if it doesnt already exist
		    outputPath = outputPath.endsWith(File.separator) ? outputPath : outputPath + File.separator;
		    String tempOutputPath = outputPath + "_rdf_merged/";
		    
		    
		    PrintStatus(stages.get(0),MergeRDFByStation.run(inputPath, tempOutputPath));
		    inputPath = tempOutputPath;
		    
		    tempOutputPath = outputPath + "_map/";
		    PrintStatus(stages.get(1),ReverseMapFromN3.run(inputPath, tempOutputPath));
		    inputPath = tempOutputPath;
		    
		    tempOutputPath = outputPath + "_csv/";
		    PrintStatus(stages.get(2),ReverseCSVFromN3.run(inputPath, tempOutputPath));
		    inputPath = tempOutputPath;
		    
		    if(metadataPath!=null) {
		    	PrintStatus(stages.get(3),ReverseMetadata.run(metadataPath, outputPath + "_metadata/"));
		    }
		    
		    tempOutputPath = outputPath + "_map_meta/";
		    PrintStatus(stages.get(4),AddPosMetadataToStationMap.run(inputPath, tempOutputPath, outputPath + "_metadata/"));
		    
		    PrintStatus(stages.get(5),LoadStation.run(inputPath, driverStr, jdbc, outputPath + "_rdbms/", user, pass));
		    
		    tempOutputPath = outputPath + "_tdb/";
		    PrintStatus(stages.get(6),LoadStationTDB.run(inputPath, tempOutputPath));
		    
			
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "LSDTransform", options, true );
		}
	}
	
	public static void PrintStatus(String stage, int count) {
		System.out.println(stage + " was completed on" + count + "stations.");
	}

}
