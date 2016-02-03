import java.io.File;

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
		
		Option stage = Option.builder("S")
			     .required(false)
			     .longOpt("stage")
			     .desc( "runs the particular stage"
			     		+ "\n1.Merge RDF by station"
			     		+ "\n2.Generate Mapping from RDF"
			     		+ "\n3.Generate CSV from RDF" )
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
		
		Option output = Option.builder("O")
				.longOpt("output")
				.required(true)
				.hasArg()
				.desc( "the output folder path" )
				.argName("folder path")
				.build();
		
		options.addOption(stage);
		options.addOption(src);
		options.addOption(output);
		
		//test data
		args = new String[]{ "-I /users/", "-O /users/out" };
		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			
			if( cmd.hasOption( "S" ) ) {
		        System.out.println( cmd.getOptionValue( "S" ) );
		    }
		    
		    String inputPath = cmd.getOptionValue( "I" );
		    String outputPath = cmd.getOptionValue( "O" );
		    
		    //add a separator at the end of the output path if it doesnt already exist
		    outputPath = outputPath.endsWith(File.separator) ? outputPath : outputPath + File.separator;
		    String tempOutputPath = outputPath + "_rdf_merged";
		    
		    MergeRDFByStation.run(inputPath, tempOutputPath);
		    inputPath = tempOutputPath;
		    
		    tempOutputPath = outputPath + "_map";
		    ReverseMapFromN3.run(inputPath, tempOutputPath);
		    inputPath = tempOutputPath;
		    
		    tempOutputPath = outputPath + "_csv";
		    ReverseCSVFromN3.run(inputPath, tempOutputPath);
		    inputPath = tempOutputPath;
			
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "LSDTransform", options, true );
		}
	}

}
