import java.util.ArrayList;


public class AdaBoost {
	private static final int k = 20; // 基分类器的个数
	
	// 初始化每个样本的权重因子
	private void initWeight(double[] weight) {
		for (int j = 0; j < weight.length; j++) {
			weight[j] = 1.0 / weight.length;
		}
	}
	
	// 从训练数据集中按照权值采样同样大小的数据集
	private void sample(ArrayList<ArrayList<Double>> dataSet, ArrayList<Integer> label, double[] weightRecord, 
			            ArrayList<ArrayList<Double>> sampleSet, ArrayList<Integer> sampleSabel) {
		int length = dataSet.size();
		double rand, count;
		int i, j;
		for (i = 0; i < length; i++) {
			rand = Math.random();
			count = 0;
			for (j = 0; j < length; j++) {
				count += weightRecord[j];
				if (rand <= count) {
					sampleSet.add(dataSet.get(j));
					sampleSabel.add(label.get(j));
					break;
				}
			}
		}
	}
	
	// 构建k个弱分类器
	public double[] getWeakClassifier(ArrayList<ArrayList<Double>> dataSet, ArrayList<Integer> label,
			                          ArrayList<double[]> weakClassifier) {
		Perceptron perceptron = new Perceptron();
		
		ArrayList<ArrayList<Double>> sampleSet = new ArrayList<ArrayList<Double>>(); // 保存每次采样得到的训练集
		ArrayList<Integer> sampleLabel = new ArrayList<Integer>(); // 保存每次采样得到的训练集的类标号
		ArrayList<Integer> classLabel = new ArrayList<Integer>(); // 保存每次利用弱分类器对全部数据集分类得到的类标号
		
		int recordNum = dataSet.size();
		double[] weightRecord = new double[recordNum]; // 保存每条记录的权值
		double[] weight; // 保存每个弱分类器的内部权重向量
		double[] alpha = new double[recordNum]; // 保存每个基分类器的重要性因子
		double errorRate; // 保存加权误差
		
		initWeight(weightRecord); // 初始化样本的权值
		
		for (int i = 0; i < k; i++) {
			sampleSet.clear();
			sampleLabel.clear();
			sample(dataSet, label, weightRecord, sampleSet, sampleLabel); // 根据每个样本的权重得到采样数据集及其类标号
			
			weight = perceptron.getWeight(sampleSet, sampleLabel); // 在采样数据集上训练感知器，得到感知器的权值向量，即构建了一个弱分类器
			
			classLabel.clear();
			perceptron.classify(dataSet, weight, classLabel); // 利用得到的弱分类器对全部数据集分类从而得到类标号
			
			// 计算加权误差
			errorRate = 0;
			for (int j = 0; j < recordNum; j++) {
				if (classLabel.get(j) != label.get(j)) {
					errorRate += weightRecord[j];
				}
			}
			errorRate = errorRate / recordNum;
			
			if (errorRate > 0.5) {
				initWeight(weightRecord); // 初始化样本的权值
				i--;
			} else {
				weakClassifier.add(weight);
				alpha[i] = 0.5 * Math.log((1 - errorRate) / errorRate);
				
				// 更新权值
				double z = 0;
				for (int k = 0; k < recordNum; k++) {
					if (classLabel.get(k) != label.get(k)) {
						weightRecord[k] = weightRecord[k] * Math.exp(alpha[k]);
						z += weightRecord[k];
					} else {
						weightRecord[k] = weightRecord[k] * Math.exp(-alpha[k]);
						z += weightRecord[k];
					}
				}
				
				for (int m = 0; m < recordNum; m++) {
					weightRecord[m] = weightRecord[m] / z;
				}
			}
		}
		
		return alpha;
	}
	
	// 利用k个弱分类器构建一个强分类器
    public void classify(ArrayList<ArrayList<Double>> dataSet, ArrayList<Integer> label, 
    		             ArrayList<ArrayList<Double>> testDataSet, ArrayList<Integer> testLabel, ArrayList<Integer> classLabel) {
    	Perceptron perceptron = new Perceptron();
    	int recordNum = testDataSet.size();
    	double[] alpha; // 保存每个基分类器的重要性因子
    	ArrayList<Integer> tmpLabel = new ArrayList<Integer>(); // 保存每次调用基分类器分类一条记录得到的类标号
    	ArrayList<double[]> weakClassifier = new ArrayList<double[]>(); // 保存全部弱分类器的内部权重向量
    	ArrayList<ArrayList<Double>> record = new ArrayList<ArrayList<Double>>(); // 保存一条记录
    	double[] classWeight = new double[2];
    	
    	alpha = getWeakClassifier(dataSet, label, weakClassifier); // 构造k个弱分类器
    	
    	// 对每条记录，调用基本分类器
    	for (int i = 0; i < recordNum; i++) {
    		record.clear();
    		classWeight[0] = 0;
    		classWeight[1] = 0;
    		record.add(testDataSet.get(i));

			for (int j = 0; j < k; j++) {
				tmpLabel.clear();
				perceptron.classify(record, weakClassifier.get(j), tmpLabel); // 利用得到的弱分类器对每条记录分类从而得到类标号
				if (tmpLabel.get(0) == testLabel.get(i)) {
					if (tmpLabel.get(0) == 0)
						classWeight[0] += alpha[j];
					else
						classWeight[1] += alpha[j];
				}
			}
			if (classWeight[0] < classWeight[1]) {
				classLabel.add(1);
				System.out.println(1);
			} else {
				classLabel.add(0);
				System.out.println(0);
			}
		}
    }
    
    // 交叉验证
    public double crossValidation(String fileStr) {
    	double accuracy1, accuracy2, accuracy;
    	int count;
    	Perceptron perceptron = new Perceptron();
    	ArrayList<ArrayList<Double>> dataSet1 = new ArrayList<ArrayList<Double>>();
    	ArrayList<Integer> label1 = new ArrayList<Integer>();
    	ArrayList<ArrayList<Double>> dataSet2 = new ArrayList<ArrayList<Double>>();
    	ArrayList<Integer> label2 = new ArrayList<Integer>();
    	ArrayList<Integer> classLabel = new ArrayList<Integer>();
    	
    	perceptron.fileSplit(fileStr);
    	perceptron.parseInputFile(fileStr + "1", dataSet1, label1);
    	perceptron.parseInputFile(fileStr + "2", dataSet2, label2);
    	
    	classify(dataSet1, label1, dataSet2, label2, classLabel);
    	count = 0;
    	for (int j = 0; j < dataSet2.size(); j++) {
    		if (classLabel.get(j) == label2.get(j)) {
    			count++;
    		}
    	}
    	accuracy1 = (double)count / dataSet2.size();
    	
    	classLabel.clear();
    	classify(dataSet2, label2, dataSet1, label1, classLabel);
    	count = 0;
    	for (int k = 0; k < dataSet1.size(); k++) {
    		if (classLabel.get(k) == label1.get(k)) {
    			count++;
    		}
    	}
    	accuracy2 = (double)count / dataSet1.size();
    	
    	accuracy = (accuracy1 + accuracy2) / 2;
    	System.out.println("accuracy: " + accuracy1 + " " + accuracy2 + " " + accuracy);
    	
    	return accuracy;
    }
    
    public static void main(String[] args) {
    	AdaBoost adaboost = new AdaBoost();
    	adaboost.crossValidation(args[0]);
    }
}
