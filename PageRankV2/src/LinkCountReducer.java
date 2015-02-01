import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;


public class LinkCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    public static Counter counter = null;
	
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
    	int sum = 0;
    	for (IntWritable value : values) {
    		sum += value.get();
    	}
		
		counter = context.getCounter("NUMBER_OF_LINKS", "n");
		counter.increment(1);
		context.write(key, new IntWritable(sum));
    }
}
