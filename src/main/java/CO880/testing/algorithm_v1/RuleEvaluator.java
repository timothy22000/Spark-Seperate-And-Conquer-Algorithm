package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class RuleEvaluator implements java.io.Serializable {
	//Serializable is needed since the Driver needs to send the class that contains the method across to the nodes by serializing it and then sending it while the nodes will deserialize it.
	private static RuleEvaluator instance = new RuleEvaluator();
	
	private RuleEvaluator(){
		
	}
	
	
	public static RuleEvaluator getInstance(){
		return instance;
	}
	
	public void evaluateRules(Rule rule, JavaRDD<List<String>> wordsInArray ){
		System.out.println(wordsInArray.collect());
		Set<String> attributes = rule.getAttributes();
		LinkedHashMap<String, String> predictedClass = rule
				.getPredictedClass();
		LinkedHashMap<String, String> antecedent = rule.getAntecedent();
		final ArrayList<String> attrValues = new ArrayList<String>();
		final ArrayList<String> classValues = new ArrayList<String>();

		Iterator<String> attrIterator = attributes.iterator();
		while (attrIterator.hasNext()) {
			String attribute = attrIterator.next();

			attrValues.add(antecedent.get(attribute));
		}

		for (String key : predictedClass.keySet()) {
			classValues.add(predictedClass.get(key));
		}

		// Create Key/Value pairs with word as key and count as value and
		// then sum them up using reduceByKey
		JavaPairRDD<String, Integer> positivesAndNegatives = wordsInArray
				.mapToPair(
						new PairFunction<List<String>, String, Integer>() {

							public Tuple2<String, Integer> call(
									List<String> t) throws Exception {
								// TODO Auto-generated method stub
								
								//NEed to test for position in the event of another attribute having the same values.
								if (!t.isEmpty() && t.size() > 1) {
									if (t.containsAll(attrValues)
											&& t.containsAll(classValues)) {
										return new Tuple2("tp", 1);
									}

									else if (t.containsAll(attrValues)
											&& !t.containsAll(classValues)) {
										return new Tuple2("fp", 1);
									}

									else if (!t.containsAll(attrValues)
											&& t.containsAll(classValues)) {
										return new Tuple2("fn", 1);
									}

									else if (!t.containsAll(attrValues)
											&& !t.containsAll(classValues)) {
										return new Tuple2("tn", 1);
									}

									else {
										return new Tuple2("error", 1);
									}
								} else {
									return new Tuple2("error", 1);
								}
							}

						}).reduceByKey(
						new Function2<Integer, Integer, Integer>() {
							public Integer call(Integer x, Integer y) {
								return x + y;
							}
						});

		List<Tuple2<String, Integer>> results = positivesAndNegatives
				.collect();
		
		/*
		 * Collect all the required values to calculate accuracy, precision and recall.
		 */
		Double numerator1 = 0.0;
		Double numerator2 = 0.0;
		//Double denomForRecall = 0.0;
		//Double denomForPrecision = 0.0;
		Double denomForAccuracy = 0.0;

		for (int i = 0; i < results.size(); i++) {
			Tuple2<String, Integer> tuple = results.get(i);
			System.out.println(tuple);
			if (tuple._1.equals("tp")) {
				numerator1 += (double) tuple._2;
				numerator2 += (double) tuple._2;
				//denomForPrecision += (double) tuple._2;
				//denomForRecall += (double) tuple._2;
				denomForAccuracy += (double) tuple._2;
				rule.setExamplesCovered(tuple._2);
			}

			if (tuple._1.equals("tn")) {
				numerator2 += (double) tuple._2;
				denomForAccuracy += (double) tuple._2;
			}

			if (tuple._1.equals("fp")) {
				//denomForPrecision += (double) tuple._2;
				denomForAccuracy += (double) tuple._2;
			}

			if (tuple._1.equals("fn")) {
				//denomForRecall += (double) tuple._2;
				denomForAccuracy += (double) tuple._2;
			}
		}
		// System.out.println(numerator2);
		rule.setAccuracy((double) (numerator2 / denomForAccuracy));
		System.out.println(rule.getAccuracy());
		System.out.println(rule.getExamplesCovered());
		//rule.setRecall((double) (numerator1 / denomForRecall));
		// System.out.println(testRule.getRecall());
		//rule.setPrecision((double) (numerator1 / denomForPrecision));
		// System.out.println(testRule.getPrecision());
		/*
		 * Confirm Output for(Tuple2<String, Integer> tuple1 :
		 * classCounts.take((int) classCounts.count())){
		 * if(tuple1._1.equals("no") || tuple1._1.equals("yes")){
		 * System.out.println("Class: " + tuple1._1 + ", Frequency: " +
		 * tuple1._2); }
		 * 
		 * 
		 * }
		 */

		/* Debugging and Troubleshooting */
		/*
		 * for(String line : data.take(10)){ System.out.println(line); }
		 */
		// System.out.println(wordsInArray.collect());
		// System.out.println(positivesAndNegatives.collect());
		// System.out.println(data);

	}
}
