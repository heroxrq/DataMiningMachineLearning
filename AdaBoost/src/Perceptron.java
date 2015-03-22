import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Perceptron {
	
	private static final int MAX_ITERATION_NUM = 50;
	private static final double LAMBDA = 0.1;
	
    /**
     * Parse the input file to get the data set and label.
     * @param fileStr The path of the input file.
     * @param dataSet The data set got by parsing the input file. 
     * @param label   The corresponding label got by parsing the input file. 
     */
    public void parseInputFile(String fileStr, ArrayList<ArrayList<Double>> dataSet, ArrayList<Integer> label) {
    	File file = new File(fileStr);
    	BufferedReader reader = null;
    	
    	try {
        	reader = new BufferedReader(new FileReader(file));
        	String line = null;
        	
        	try {
	        	while ((line = reader.readLine()) != null) {
	        		String[] record = line.split(",");
	        		ArrayList<Double> attribute = new ArrayList<Double>();
	        		
	        		attribute.add(1.0); // x0 = 1
	        		for (int i = 0; i < record.length - 1; i++) {
	        			attribute.add(Double.valueOf(record[i]));
	        		}
	        		dataSet.add(attribute);
	        		
	        		if (record[record.length - 1].equals("Iris-versicolor")) {
	        			label.add(0); // Iris-versicolor's label is 0
	        		} else {
	        			label.add(1); // Iris-virginica's label is 1
	        		}
	        	}
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    }
	
	/**
	 * Initialize the weight vector.
	 * @param weight The weight vector.
	 * @param value  The value which is used to initialize the weight vector.
	 */
	private void weightInit(double[] weight, double value) {
		int i;
		
		for (i = 0; i < weight.length; i++) {
			weight[i] = value;
		}
	}
	
	/**
	 * The sign function.
	 */
    private int sign(double i) {
    	return i > 0 ? 1 : 0;
    }
    
    /**
     * Train the perceptron model by using stochastic gradient descent algorithm to get the weight vector.
     * @param dataSet The training data set.
     * @param label   The class label corresponding to each record in the data set.
     * @return        The weight vector.
     */
    public double[] getWeight(ArrayList<ArrayList<Double>> dataSet, ArrayList<Integer> label) {
    	int attributeNum;
    	int recordNum;
    	
    	if (dataSet == null) {
    		return null;
    	} else {
    		recordNum = dataSet.size();
//    		attributeNum = dataSet.get(0).size();
    		attributeNum = 5;
    	}
    	
    	double[] weight = new double[attributeNum];
    	double y;
    	
    	weightInit(weight, 0.0);
    	
    	// stochastic gradient descent
    	for (int m = 0; m < MAX_ITERATION_NUM; m++) {
    		for (int i = 0; i < recordNum; i++) {
    			y = 0;
    			for (int j = 0; j < attributeNum; j++) {
    				y += weight[j] * dataSet.get(i).get(j);
    			}
    			y = sign(y); // predict the label
    			
    			for (int k = 0; k < attributeNum; k++) {
    				weight[k] = weight[k] + LAMBDA * (label.get(i) - y) * dataSet.get(i).get(k);
    			}
    		}
    	}
    	
    	return weight;
    }
    
    /**
     * Classify the data set using the given weight of perceptron model.
     * @param dataSet    The data set to be classified.
     * @param weight     The weight of the perceptron model.
     * @param classLabel THe class label obtained.
     */
    public void classify(ArrayList<ArrayList<Double>> dataSet, double[] weight, ArrayList<Integer> classLabel) {
    	int recordNum = dataSet.size();
    	int attrNum = weight.length;  	
    	double y;
    	
    	for (int i = 0; i < recordNum; i++) {
    		y = 0;
    		for (int j = 0; j < attrNum; j++) {
    			y += weight[j] * dataSet.get(i).get(j);
    		}
    		classLabel.add(sign(y));
    	}
    }
    
    /**
     * Split the file into two parts.
     * @param fileStr The path of the original file;
     */
    public void fileSplit(String fileStr) {
    	File file = new File(fileStr);
    	File file1 = new File(fileStr + "1");
    	File file2 = new File(fileStr + "2");
    	
    	BufferedReader reader = null;
    	BufferedWriter writer1 = null;
    	BufferedWriter writer2 = null;
    	
    	try {
        	reader = new BufferedReader(new FileReader(file));
        	writer1 = new BufferedWriter(new FileWriter(file1));
        	writer2 = new BufferedWriter(new FileWriter(file2));
        	String line = null;
        	
    		line = reader.readLine();
    		for (int i = 1; line != null; i++) {
    			if (i % 2 == 1) {
    				writer1.write(line + "\n"); // odd lines
    			} else {
    				writer2.write(line + "\n"); // even lines
    			}
    			line = reader.readLine();
    		}
    		reader.close();
    		writer1.close();
    		writer2.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}