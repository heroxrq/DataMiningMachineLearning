import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class PageRankReducer extends Reducer<Text, Text, Text, Text> {	
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {	
    	Text outLinks = new Text();
    	String linkPr;
    	double pr = 0;
        
    	for (Text value : values) {
    		char firstChar = value.toString().charAt(0);
    		if (('0' <= firstChar) && (firstChar <= '9') ) {
    			pr += Double.valueOf(value.toString());
    		} else {
    			outLinks.set(value);
    		}
    	}
    	
    	pr = Parameter.a * pr + (1 - Parameter.a) / Parameter.n; // compute the new pr for each link
    	linkPr = key.toString() + "," + Double.toString(pr);
    	context.write(new Text(linkPr), outLinks);
    }
}
