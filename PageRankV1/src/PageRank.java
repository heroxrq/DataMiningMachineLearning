import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class PageRank {

	public static boolean deleteIntermediateOutput(String path, Configuration conf) throws IOException, URISyntaxException {
	    FileSystem fs = FileSystem.get(new URI(path), conf);
	    return fs.delete(new Path(path), true); 
	}
	
	public static void main(String[] args) throws Exception {
		int maxIteration = 50;
        int i = 0;
        String inputPath = args[0];
        String output = "output";
        String outputPath;

        // transform the input file to the transition matrix
		System.out.println("----------" + "TransitionMatrix" + "----------");
		Configuration conf0 = new Configuration();
		Job job0 = new Job(conf0, "TransitionMatrix");
		job0.setJarByClass(PageRank.class);
		job0.setInputFormatClass(TextInputFormat.class);;
		job0.setMapperClass(TransitionMatrixMapper.class);
		job0.setReducerClass(TransitionMatrixReducer.class);
		job0.setMapOutputKeyClass(Text.class);
		job0.setMapOutputValueClass(Text.class);
		job0.setOutputKeyClass(Text.class);
		job0.setOutputValueClass(Text.class);
		job0.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job0, new Path(inputPath));
		FileOutputFormat.setOutputPath(job0, new Path(output));
		job0.waitForCompletion(true);

		
		inputPath = output;
        Parameter.n = (int)TransitionMatrixReducer.counter.getValue();
        System.out.println("----------" + "n=" + Parameter.n + "----------");
        
        
		while (i++ < maxIteration) {
			System.out.println("----------" + "PageRank" + i + "----------");
			Configuration conf = new Configuration();
			Job job = new Job(conf, "PageRank" + i);
			job.setJarByClass(PageRank.class);
			job.setInputFormatClass(TextInputFormat.class);;
			job.setMapperClass(PageRankMapper.class);
			job.setReducerClass(PageRankReducer.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			FileInputFormat.addInputPath(job, new Path(inputPath));
			outputPath = output + i; // generate a new output path
			inputPath = outputPath; // set the next input path as the current output path
			FileOutputFormat.setOutputPath(job, new Path(outputPath));
			
			job.waitForCompletion(true);
			
			if (i > 1) {
			    deleteIntermediateOutput(output + (i - 1), conf);				
			}
		}
	}

}
