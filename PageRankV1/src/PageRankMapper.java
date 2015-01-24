import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> { 
	public void map(LongWritable unusedKey, Text keyValue, Context context) throws IOException, InterruptedException {
    	
		// split the keyValue to key and value due to the KeyValueInputFormat can't be used in this version (0.20.2)
		String[] splitKeyValue = keyValue.toString().split("\t");
		String key = splitKeyValue[0];
		String value = splitKeyValue[1];
		
		String[] linkPr = key.split(",");
    	String[] outLinks = value.split(",");
    	String link = linkPr[0];
    	double pr;
    	
    	if (linkPr.length == 1) {
    		pr = 1.0 / Parameter.n; // each pr is equal to 1/n in the first iteration
    	} else {
    		pr = Double.valueOf(linkPr[1]);
    	}
    	
    	pr = pr / outLinks.length;
    	
    	for (String outLink : outLinks) {
    		// write the (outLink, pr) pair
    		context.write(new Text(outLink), new Text(Double.toString(pr)));
    	}
    	
    	// deliver the transition matrix in order to use it in the next round of MapReduce iteration
    	context.write(new Text(link), new Text(value));
    }
}
