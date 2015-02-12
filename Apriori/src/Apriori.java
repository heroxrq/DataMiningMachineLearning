import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Apriori {

	private static final int MINSUPCNT = 2; // 最小支持度计数 = 事务个数 * 最小支持度
	private static final double MINCONF = 0.7; // 最小置信度
	private static final String ITEM_SPLIT = ",";
	private static final String RULE_SPLIT = "->";
	
	private static final List<String> transList = new ArrayList<>();
	
	static {
		transList.add("1,2,5");
		transList.add("2,4");
		transList.add("2,3");
		transList.add("1,2,4");
		transList.add("1,3");
		transList.add("2,3");
		transList.add("1,3");
		transList.add("1,2,3,5");
		transList.add("1,2,3");
	}
	
	/**
	 * 函数名： 	getFrequentOneItemset
	 * 返回类型： Map<String, Integer> 函数返回产生的所有频繁1项集
	 * 函数功能： 通过单遍扫描数据集，得到所有的频繁1项集
	 */
	private Map<String, Integer> getFrequentOneItemset() {
		Map<String, Integer> OneItemset = new HashMap<String, Integer>(); // 1项集
		Map<String, Integer> FrequentOneItemset = new HashMap<String, Integer>(); // 频繁1项集
		
		// 单遍扫描数据集，确定每个项的支持度计数
		for (String trans : transList) {
			String[] items = trans.split(ITEM_SPLIT);
			for (String item : items) {
				Integer count = OneItemset.get(item);
				if (count == null) {
					OneItemset.put(item, 1);
				} else {
					OneItemset.put(item, count + 1);
				}
			}
		}
		
		// 通过与最小支持度计数阈值比较，得到频繁1项集
		Set<String> keySet = OneItemset.keySet();
		for (String key : keySet) {
			Integer count = OneItemset.get(key);
			if (count >= MINSUPCNT) {
				FrequentOneItemset.put(key, count);
			}
		}
		
		return FrequentOneItemset;
	}
	
	/**
	 * 函数名： 	candidateItemsetGen
	 * 返回类型： Map<String, Integer> 函数返回由频繁k-1项集产生的候选k项集
	 * 输入参数： Map<String, Integer> frequentItemset 输入频繁k-1项集
	 * 函数功能： 由频繁k-1项集产生候选k项集，并基于支持度计数进行剪枝
	 */
	private Map<String, Integer> candidateItemsetGen(Map<String, Integer> frequentItemset) {
		Map<String, Integer> allCandidateItemset = new HashMap<String, Integer>(); // 所有的候选k项集
		Set<String> freqItemset1 = frequentItemset.keySet(); // 所有的频繁k-1项集
		Set<String> freqItemset2 = frequentItemset.keySet(); // 所有的频繁k-1项集
		
		// 由两个频繁k-1项集产生一个候选k项集
		for (String itemset1 : freqItemset1) {
			for (String itemset2 : freqItemset2) {
				String[] items1 = itemset1.split(ITEM_SPLIT);
				String[] items2 = itemset2.split(ITEM_SPLIT);
				String candidateItemset = ""; // 一个候选k项集
				
				if (items1.length == 1) {
					// 由两个频繁1项集产生一个候选2项集
					if (items1[0].compareTo(items2[0]) < 0) {
						candidateItemset = items1[0] + ITEM_SPLIT + items2[0];
					}
				} else {
					// 由两个频繁k-1项集产生一个候选k项集(k > 2)
					boolean flag = true;				
					// 检查前k-2项是否相同
					for (int i = 0; i < items1.length - 1; i++) {
						if (!items1[i].equals(items2[i])) {
							flag = false;
							break;
						}
					}
					
					// 若前k-2项相同，则检查第k-1项是否不同,若第k-1项不同，则加入候选集
					if (flag && (items1[items1.length - 1].compareTo(items2[items2.length - 1]) < 0)) {
						candidateItemset = itemset1 + ITEM_SPLIT + items2[items2.length - 1];
					}
				}
				
				// 基于支持度计数进行剪枝，删除一些候选k项集
				boolean isSubSetFrequent = true;
				if (!candidateItemset.equals("")) {
					String[] items = candidateItemset.split(ITEM_SPLIT);
					// 得到此候选k项集的所有k-1项真子集，并检查其是否频繁（其k-1项子集若频繁，则一定在频繁k-1项集中能找到）
					for (int i = 0; i < items.length; i++) {
						String subCandidateItemset = "";
						for (int j = 0; j < items.length; j++) {
							if (i != j) {
								if (subCandidateItemset.equals("")) {
									subCandidateItemset = items[j];
								} else {
									subCandidateItemset = subCandidateItemset + ITEM_SPLIT + items[j];
								}
							}
						}
						
						// 若此候选k项集的某个k-1项真子集非频繁，则此候选集一定非频繁，应将其删掉
						if (frequentItemset.get(subCandidateItemset) == null) {
							isSubSetFrequent = false;
							break;
						}
					}
				} else {
					isSubSetFrequent = false;
				}
				
				if (isSubSetFrequent) {
					allCandidateItemset.put(candidateItemset, 0);
				}
			}
		}
		
		return allCandidateItemset;
	}
	
	/**
	 * 函数名： 	frequentItemsetGen
	 * 返回类型： Map<String, Integer> 函数返回产生的所有频繁项集
	 * 函数功能： Apriori算法的频繁项集产生，通过逐层迭代寻找频繁项集。
	 *          1）首先通过单遍扫描数据集，确定每个项的支持度，得到所有的频繁1项集；
	 *          2）然后由上一次迭代发现的频繁k-1项集产生新的候选k项集；
	 *          3）再次扫描一遍数据集，确定包含在每个事务中的所有候选k项集，增加其支持度计数；
	 *          4）删除支持度计数小于最小支持度计数阈值的候选项，得到本轮迭代产生的频繁项集；
	 *          5）当没有新的频繁项集产生时，算法结束。
	 */
	private Map<String, Integer> frequentItemsetGen() {
		Map<String, Integer> allFrequentItemset = new HashMap<String, Integer>(); // 所有的频繁项集		
		allFrequentItemset.putAll(getFrequentOneItemset()); // 插入频繁1项集
		
		Map<String, Integer> frequentItemset = new HashMap<String, Integer>(); // 每轮迭代产生的频繁项集
		frequentItemset.putAll(getFrequentOneItemset());
		
		// 逐层迭代寻找频繁项集
		while ((frequentItemset != null) && (frequentItemset.size() != 0)) {
			Map<String, Integer> candidateItemset = candidateItemsetGen(frequentItemset); // 由频繁k-1项集产生候选k项集
			Set<String> candItemset = candidateItemset.keySet();
			
			// 对每个事务进行扫描，计算每个候选项的支持度计数
			for (String trans : transList) {
				for (String candidate : candItemset) {
					boolean flag = true;
					String[] candidateItems = candidate.split(ITEM_SPLIT);
					
					// 检测候选项集candidate是否在事务trans中
					for (String candidateItem : candidateItems) {
						if (trans.indexOf(candidateItem) == -1) {
							flag = false;
							break;
						}
					}
					
					// 若候选项集candidate在事务trans中，则其支持度加1
					if (flag) {
						Integer count = candidateItemset.get(candidate);
						candidateItemset.put(candidate, count + 1);
					}
				}
			}
			
			// 提取本轮迭代产生的频繁项集
			frequentItemset.clear();
			for (String candidate : candItemset) {
				Integer count = candidateItemset.get(candidate);
				if (count >= MINSUPCNT) {
					frequentItemset.put(candidate, count);
				}
			}
			
			allFrequentItemset.putAll(frequentItemset); // 将本轮迭代产生的频繁项集插入所有的频繁项集中
		}
		
		return allFrequentItemset;
	}
	
	/**
	 * 函数名： 	getSubset
	 * 输入参数： List<String> sourceSet 输入源集合
	 *          List<List<String>> subset 产生源集合的所有非空子集
	 * 函数功能： 通过递归的方式，获取集合的所有非空子集
	 */
	private void getSubset(List<String> sourceSet, List<List<String>> subset) {

		if (sourceSet.size() == 1) {
			// 仅有一个元素时，非空子集为其自身，直接将其添加到subset中
			List<String> set = new ArrayList<String>();
			set.add(sourceSet.get(0));
			subset.add(set);
		} else {
			// 当有n个元素时，递归求出其前n-1个元素的非空子集，存于subset中
			getSubset(sourceSet.subList(0, sourceSet.size() - 1), subset);
			int size = subset.size(); // 求出前n-1个元素的非空子集的长度，便于后面将第n个元素插入到前n-1个元素的非空子集中
			// 将第n个元素加入subset集合中
			List<String> single = new ArrayList<String>();
			single.add(sourceSet.get(sourceSet.size() - 1));
			subset.add(single);
			
			// 将第n个元素插入到前n-1个元素的非空子集中，形成新的子集，并添加于subset中
			for (int i = 0; i < size; i++) {
				List<String> clone = new ArrayList<String>();
				for (String str : subset.get(i)) {
					clone.add(str);
				}			
				clone.add(sourceSet.get(sourceSet.size() - 1));
				subset.add(clone);
			}
		}
	}
	
	/**
	 * 函数名：	associationRuleGen
	 * 返回类型：	Map<String, Double> 函数返回产生的所有关联规则及其置信度
	 * 输入参数： Map<String, Integer> frequentItemset 输入所有的频繁项集
	 * 函数功能： Apriori算法的关联规则产生，从频繁项集中提取关联规则。
	 *          首先由频繁项集产生其所有的非空子集，然后由其中所有的非空真子集构造所有可能的规则，
	 *          最后检查每条规则的置信度，确定强规则。
	 */
	private Map<String, Double> associationRuleGen(Map<String, Integer> frequentItemset) {
		Map<String, Double> allAssociationRule = new HashMap<String, Double>(); // 所有的强规则
		Set<String> freqItemset = frequentItemset.keySet();
		
		// 对频繁项集中的每一个项集，求出其中包含的强规则
		for (String itemset : freqItemset) {
			String[] items = itemset.split(ITEM_SPLIT);
			if (items.length > 1) {
				double countItemset = frequentItemset.get(itemset); // 此频繁项集的支持度计数
				List<String> sourceSet = new ArrayList<String>();
				Collections.addAll(sourceSet, items);
				List<List<String>> subset = new ArrayList<List<String>>();
				
				getSubset(sourceSet, subset); // 求出此频繁项集的所有非空子集
				
				// 对每个非空真子集，产生一条规则，若其置信度大于等于最小置信度阈值，则产生一条强规则
				for (List<String> itemList : subset) {
					if (itemList.size() < sourceSet.size()) {
						// 求出该子集的补集
						List<String> complementarySet = new ArrayList<String>();
						for (String item : sourceSet) {
							if (!itemList.contains(item)) {
								complementarySet.add(item);
							}
						}
						
						String antecedent = ""; // 规则前件
						String consequent = ""; // 规则后件
						
						// 由该子集产生规则前件
						for (String item : itemList) {
							if (antecedent.equals("")) {
								antecedent = item;
							} else {
								antecedent = antecedent + ITEM_SPLIT + item;
							}
						}
						
						// 由补集产生规则后件
						for (String item : complementarySet) {
							if (consequent.equals("")) {
								consequent = item;
							} else {
								consequent = consequent + ITEM_SPLIT + item;
							}
						}
						
						// 判断此规则的置信度是否大于等于最小置信度阈值，若是，则产生一条强规则
						double countAntecedent = frequentItemset.get(antecedent); // 直接从频繁项集中得到规则前件的支持度计数，无需再计算
						double conf = countItemset / countAntecedent;
						if (conf >= MINCONF) {
							String rule = antecedent + RULE_SPLIT + consequent;
							allAssociationRule.put(rule, conf);
						}
					}
				}
			}
		}
		
		return allAssociationRule;
	}
	
	public static void main(String[] args) {
		Apriori apriori = new Apriori();
		Map<String, Integer> frequentItemset = apriori.frequentItemsetGen();
		Set<String> freqItemset = frequentItemset.keySet();
		System.out.println("---------------frequent itemset---------------");
		for (String keySet : freqItemset) {
			System.out.println(keySet + " : " + frequentItemset.get(keySet));
		}
		System.out.println("---------------association rule---------------");
		Map<String, Double> associationRule = apriori.associationRuleGen(frequentItemset);
		Set<String> assoRule = associationRule.keySet();
		for (String keySet : assoRule) {
			System.out.println(keySet + " : " + associationRule.get(keySet));
		}
	}

}
