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
		//System.out.println(wordsInArray.collect());
		final ArrayList<Attribute> attributes = rule.getAntecedent();
		final Class predictedClass = rule.getPredictedClass();
		

		final int noOfMatches = attributes.size();

		// Create Key/Value pairs with word as key and count as value and
		// then sum them up using reduceByKey
		JavaPairRDD<String, Integer> positivesAndNegatives = wordsInArray
				.mapToPair(
						new PairFunction<List<String>, String, Integer>() {

							public Tuple2<String, Integer> call(
									List<String> t) throws Exception {
								// TODO Auto-generated method stub
								
								//Need to test for position in the event of another attribute having the same values.
			
								int matchesSoFar = 0;
								boolean classMatch = false;
								
								if (!t.isEmpty() && t.size() > 1) {
									for (int i = 0; i < attributes.size(); i++) {
										Attribute attribute = attributes.get(i);
										//System.out.println(attribute);
										//System.out.println(t.get(attribute.getPosition()));
										//System.out.println(attribute.getValue());
										if (attribute.getValue().equals(
												t.get(attribute.getPosition()))) {
											matchesSoFar++;
										}
									}
									if (predictedClass.getValue()
											.equals(t.get(predictedClass
													.getPosition()))) {
										classMatch = true;
									}
									if (matchesSoFar == noOfMatches
											&& classMatch == true) {
										return new Tuple2("tp", 1);
									}

									else if (matchesSoFar == noOfMatches
											&& classMatch == false) {
										return new Tuple2("fp", 1);
									}

									else if (matchesSoFar < noOfMatches
											&& classMatch == true) {
										return new Tuple2("fn", 1);
									}

									else if (matchesSoFar < noOfMatches
											&& classMatch == false) {
										return new Tuple2("tn", 1);
									}

									else {
										return new Tuple2("error", 1);
									}
								}
								
								else {
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
		double numerator1 = 0.0;
		double numerator2 = 0.0;
		//Double denomForRecall = 0.0;
		//Double denomForPrecision = 0.0;
		double denomForAccuracy = 0.0;
		boolean isThereTP = false;
		
		for (int i = 0; i < results.size(); i++) {
			Tuple2<String, Integer> tuple = results.get(i);
			
			if (tuple._1.equals("tp")) {
				numerator1 += (double) tuple._2;
				numerator2 += (double) tuple._2;
				//denomForPrecision += (double) tuple._2;
				//denomForRecall += (double) tuple._2;
				denomForAccuracy += (double) tuple._2;
				
				rule.setExamplesCovered(tuple._2);
				isThereTP = true;
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
		
		if(isThereTP == false){
			rule.setExamplesCovered(0);
			numerator2 = 0;
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
