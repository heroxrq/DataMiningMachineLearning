import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class TransitionMatrixMapper extends Mapper<LongWritable, Text, Text, Text> {
    public void map(LongWritable unusedKey, Text keyValue, Context context) throws IOException, InterruptedException {
    	// split the keyValue to key and value due to the KeyValueInputFormat can't be used in this version (0.20.2)
    	String[] splitKeyValue = keyValue.toString().split("\\s+");
    	String key = splitKeyValue[0];
    	String value = splitKeyValue[1];
    	context.write(new Text(key), new Text(value));
    }
}
