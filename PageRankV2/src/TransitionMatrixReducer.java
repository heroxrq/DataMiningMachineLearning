import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class TransitionMatrixReducer extends Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    	String outLinks = "";
    	for (Text value : values) {
    		outLinks = outLinks + value.toString() + "!";
    	}
    	
    	outLinks = outLinks.substring(0, outLinks.length() - 1);
    	context.write(key, new Text(outLinks));
    }
}
