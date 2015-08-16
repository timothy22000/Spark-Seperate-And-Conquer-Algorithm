package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;


public class Main {

	private static ArrayList<Rule> bestRuleHolder;
	private static final String FILE_LOCATION = "weather.nominal.arff";
	private static Rule defaultRule;
	
	
	
	public static void main(String[] args){
	
		JavaSparkContext sc = new JavaSparkContext("local", "classFreq");

		//Load data
		JavaRDD<String> arff = sc.textFile(FILE_LOCATION);

		//Filter out header files (anything with @) and then split the lines into words

		JavaRDD<String> data = arff.filter(new Function<String, Boolean>(){
			public Boolean call(String x) {
				if(x.contains("@") || x.isEmpty()){
					return false;
				}

				else {
					return true;
				}
			}

		});

		class SplitLines implements Function<String, List<String>>{
			public List<String> call(String line){
				return Arrays.asList(line.split(","));
			}
		}

		//Test out Rule class
		//final Rule testRule = new Rule("outlook", "sunny", "play", "yes");
		//testRule.addConditionsToRule("temperature", "hot");
		//System.out.println(testRule); */



		//Holder for best rules

		bestRuleHolder = new ArrayList<Rule>();

		//Break the lines into arrays of words. wordsInArray kind of represents Examples.
		JavaRDD<List<String>> wordsInArray = data.map(new SplitLines());
		wordsInArray.persist(StorageLevel.MEMORY_AND_DISK());
		

		//Generate rules using RuleGenerator
		RuleGenerator generator = RuleGenerator.getInstance();
		ArrayList<Rule> rulesGenerated = generator.generateRules(FILE_LOCATION);

		//Get default rule
		defaultRule = ClassFrequency.getDefaultRule(data, FILE_LOCATION);


		Rule bestRule = refinements(rulesGenerated, wordsInArray);
		bestRuleHolder.add(bestRule);



	} 
	
	public static Rule refinements(ArrayList<Rule> rulesGenerated, JavaRDD<List<String>> examples){
		
		double BestEval = 0.0;
		double prevBestEval = 0.0;
		Rule prevBestRule = null;
		
		Rule BestRule = defaultRule;
		ArrayList<Rule> refinedRules = rulesGenerated;
		int b = 0;
		
		//Stop when no refinements can be done.
		while (!refinedRules.isEmpty()) {

			//Extract information required for a rule and run a Spark Job to calculate its accuracy, precision and recall.
			
			/*
			 * If the best evaluation never changes and the best rule remains the same as well, then we have found our best rule and can stop.
			 * Otherwise, the refineBestRule method will just refine the same best rule again.
			 * 
			 * Need to use compareTo when comparing Doubles.
			 * 
			 * Possible improvements: What if a refined rule has the same accuracy as the current best rule? (No problems if refining the refined rule gives a new rule
			 * with higher accuracy. But if it doesn't then we have two rules with the same accuracy.) 
			 */
			/* if(b > 0){
				if((prevBestEval.compareTo(BestEval) == 0) && prevBestRule.equals(BestRule)){
					break;
				}
			} */
			
			if(prevBestEval == BestEval && prevBestRule != null && prevBestRule.equals(BestRule)){
				break;
			} 
			prevBestEval = BestEval;
			prevBestRule = BestRule;
			double MaxEval = Double.NEGATIVE_INFINITY;
			Rule MaxRule = defaultRule;
			
			for (int a = 0; a < refinedRules.size(); a++) {
				Rule testRule1 = refinedRules.get(a);
				RuleEvaluator ruleEvaluator = RuleEvaluator.getInstance();
				ruleEvaluator.evaluateRules(testRule1, examples);

				if (testRule1.getAccuracy() > MaxEval) {
					MaxEval = testRule1.getAccuracy();
					MaxRule = testRule1;
				}

			}
			
			//Store if we have a new best rule.
			if (MaxEval >= BestEval) {
				BestEval = MaxEval;
				BestRule = MaxRule;
			}
			
			//If the evaluation quality falls (accuracy decreases) with refinements, then stop since refining rules with lower accuracy won't improve it as much as the best evaluation seen so far.
			else if (MaxEval < BestEval){
				break;
			} 
			
			refinedRules = refineBestRule(BestRule, refinedRules);
			b++;
		/* //Test whether the statistics have been calculated for all the rule
			for(int a = 0; a < rulesGenerated.size(); a++){
				Rule testRule = rulesGenerated.get(a);
				System.out.println(testRule.getAccuracy());
				System.out.println(testRule.getRecall());
				System.out.println(testRule.getPrecision());
			}*/
		}
		
		System.out.println(BestRule);
		System.out.println(BestEval);
		System.out.println(BestRule.getExamplesCovered());

		
		return BestRule;
	}
	
	public static ArrayList<Rule> refineBestRule(Rule bestRule, ArrayList<Rule> generatedRules){
		/*Create a new list of rule that adding on new conditions(except existing attributes) to the best rule.
		 * Will be used to refine the best rule.
		 */
		ArrayList<Rule> remainingRules = new ArrayList<Rule>();
		Set<String> bestRuleAttr = bestRule.getAttributes();
		
		for(int i = 0; i < generatedRules.size(); i++){
			Rule rule = generatedRules.get(i);
			
			if(bestRuleAttr.containsAll(rule.getAttributes())){
				continue;
			}
			else{
				remainingRules.add(rule);
			}
		}
		
		System.out.println(remainingRules);
		if(remainingRules.isEmpty()){
			return remainingRules;
		}
		
		ArrayList<Rule> refinedRules = new ArrayList<Rule>();
		
		
		for(int i = 0; i < remainingRules.size(); i++){
			Rule rule = remainingRules.get(i);
			//System.out.println(rule);
			Rule bestRuleCopy;
			for(String key: rule.getAttributes()){
				LinkedHashMap<String, String> antecedent = rule.getAntecedent();
				bestRuleCopy = new Rule(bestRule);
				//System.out.println(bestRuleCopy);
				bestRuleCopy.addConditionsToRule(key, antecedent.get(key));
				if(!refinedRules.contains(bestRuleCopy)){
					refinedRules.add(bestRuleCopy);
				}
			}
			
		}
		
		//Check that the best Rule is refined
		/* for(int i = 0; i < refinedRules.size(); i++){
			System.out.println(refinedRules.get(i));
		} */
		return refinedRules;
		
		
		
	}
	

}

	
	
	


