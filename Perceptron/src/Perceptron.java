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
	        		
	        		if (record[record.length - 1].equals("Iris-setosa")) {
	        			label.add(0); // Iris-setosa's label is 0
	        		} else {
	        			label.add(1); // Iris-versicolor's label is 1
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
    		attributeNum = dataSet.get(0).size();
    	}
    	
    	double[] weight = new double[attributeNum];
    	double y;
    	
    	weightInit(weight, 0.0);
    	
    	// stochastic gradient descent
    	for (int i = 0; i < MAX_ITERATION_NUM; i++) {
    		for (int m = 0; m < recordNum; m++) {
    			y = 0;
    			for (int j = 0; j < attributeNum; j++) {
    				y += weight[j] * dataSet.get(m).get(j);
    			}
    			y = sign(y); // predict the label
    			
    			for (int k = 0; k < attributeNum; k++) {
    				weight[k] = weight[k] + LAMBDA * (label.get(m) - y) * dataSet.get(m).get(k);
    			}
    		}
    	}
    	
    	return weight;
    }
    
    /**
     * Classify the test data set.
     * @param trainingFile The file which contains training data set.
     * @param testFile     The file which contains test data set.
     * @return             The accuracy of the model on the test data set.
     */
    public double classify(String trainingFile, String testFile) {
    	ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
    	ArrayList<Integer> label = new ArrayList<Integer>();
    	double[] weight = new double[5];
    	
    	// training the model
    	parseInputFile(trainingFile, dataSet, label);
    	weight = getWeight(dataSet, label);
    	dataSet.clear();
    	label.clear();
    	
    	// classify the test data set
    	parseInputFile(testFile, dataSet, label);
    	int recordNum = dataSet.size();
    	double y;
    	ArrayList<Integer> classLabel = new ArrayList<Integer>();
    	for (int i = 0; i < recordNum; i++) {
    		y = 0;
    		for (int j = 0; j < 5; j++) {
    			y += weight[j] * dataSet.get(i).get(j);
    		}
    		classLabel.add(sign(y));
    	}
    	
    	// compute the accuracy
    	int cnt = 0;
    	for (int k = 0; k < recordNum; k++) {
    		System.out.println(classLabel.get(k));
    	    if (classLabel.get(k) == label.get(k)) {
    	    	cnt++;
    	    }
    	}
    	
    	return (double)cnt / recordNum;
    }
    
    /**
     * Validate the performance of the perceptron.
     * @param fileStr The input file which is separated into two parts to use the cross validation method.
     * @return        The accuracy.
     */
    public double crossValidation(String fileStr) {
    	File file = new File(fileStr);
    	File file1 = new File(fileStr + "1");
    	File file2 = new File(fileStr + "2");
    	double accuracy1, accuracy2, accuracy;
    	
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
    	
    	accuracy1 = classify(fileStr + "1", fileStr + "2");
    	accuracy2 = classify(fileStr + "2", fileStr + "1");
    	accuracy = (accuracy1 + accuracy2) / 2;
    	System.out.println("accuracy: " + accuracy1 + " " + accuracy2 + " " + accuracy);
    	
    	return accuracy;
    }
    
    public static void main(String[] args) {
    	Perceptron perceptron = new Perceptron();
		perceptron.crossValidation(args[0]);
    }
}
