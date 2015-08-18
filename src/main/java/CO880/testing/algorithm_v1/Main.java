package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;


public class Main {

	private static ArrayList<Rule> bestRuleHolder;
	private static final String FILE_LOCATION = "mushroom.arff";
	private static Rule defaultRule;
	
	
	
	public static void main(String[] args){
	
		JavaSparkContext sc = new JavaSparkContext("local", "classFreq");

		//Load data
		JavaRDD<String> arff = sc.textFile(FILE_LOCATION);

		//Filter out header files (anything with @) and then split the lines into words

		JavaRDD<String> data = arff.filter(new Function<String, Boolean>(){
			public Boolean call(String x) {
				if(x.contains("@") || x.contains("%") || x.isEmpty()){
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
		//final Rule testRule = new Rule("outlook", "sunny", 0, "play", "yes", 4);
		//testRule.addConditionsToRule("temperature", "hot", 2);
		//System.out.println(testRule); 


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
		
		
		//Rule bestRule = refinements(rulesGenerated, wordsInArray);
		//bestRuleHolder.add(bestRule);

		while(!wordsInArray.isEmpty() && wordsInArray.collect().size() > 0){
			Rule bestRule = refinements(rulesGenerated, wordsInArray);
			
			bestRuleHolder.add(bestRule);
			
			final ArrayList<Attribute> attributes = bestRule.getAntecedent();
			final Class predictedClass = bestRule.getPredictedClass();
			

			final int noOfMatches = attributes.size();
		
			wordsInArray = wordsInArray.filter(new Function<List<String>, Boolean>(){

				@Override
				public Boolean call(List<String> t) throws Exception {
					// TODO Auto-generated method stub
					
					int matchesSoFar = 0;
					boolean classMatch = false;
					
					
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
							.equals(t.get(predictedClass.getPosition()))) {
						classMatch = true;
					}
					if (matchesSoFar == noOfMatches
							&& classMatch == true) {
						return false;
					}


					else {
						return true;
					}
				}

				
			});
			
			System.out.println("What's Left inside: " + wordsInArray.collect());
			System.out.println("Examples Left: " + wordsInArray.collect().size());
		} 
		
		bestRuleHolder.add(defaultRule);
		
		
		
		/**
		 * If the file was mushroom.arff then its output:
		 * 
		 * 	@output List of best rules: IF 'odor' = 'n' AND 'veil-type' = 'p' THEN 'class' = 'e'
					List of best rules: IF 'stalk-root' = 'c' AND 'bruises?' = 't' AND 'gill-attachment' = 'f' AND 'gill-spacing' = 'c' AND 'gill-size' = 'b' AND 'stalk-shape' = 'e' AND 'stalk-surface-above-ring' = 's' AND 'stalk-surface-below-ring' = 's' AND 'stalk-color-above-ring' = 'w' AND 'stalk-color-below-ring' = 'w' AND 'veil-type' = 'p' AND 'veil-color' = 'w' AND 'ring-number' = 'o' AND 'ring-type' = 'p' THEN 'class' = 'e'
					List of best rules: IF 'stalk-root' = 'r' AND 'cap-surface' = 'y' AND 'bruises?' = 't' AND 'gill-attachment' = 'f' AND 'gill-spacing' = 'c' AND 'gill-size' = 'b' AND 'stalk-shape' = 'e' AND 'stalk-surface-above-ring' = 's' AND 'stalk-surface-below-ring' = 'y' AND 'stalk-color-above-ring' = 'w' AND 'stalk-color-below-ring' = 'w' AND 'veil-type' = 'p' AND 'veil-color' = 'w' AND 'ring-number' = 'o' AND 'ring-type' = 'p' THEN 'class' = 'e'
					List of best rules: IF 'odor' = 'a' AND 'bruises?' = 't' AND 'gill-attachment' = 'f' AND 'gill-spacing' = 'w' AND 'gill-size' = 'n' AND 'stalk-shape' = 't' AND 'stalk-root' = 'b' AND 'stalk-surface-above-ring' = 's' AND 'stalk-surface-below-ring' = 's' AND 'stalk-color-above-ring' = 'w' AND 'stalk-color-below-ring' = 'w' AND 'veil-type' = 'p' AND 'veil-color' = 'w' AND 'ring-number' = 'o' AND 'ring-type' = 'p' AND 'population' = 'v' AND 'habitat' = 'd' THEN 'class' = 'e'
					List of best rules: IF 'odor' = 'l' AND 'bruises?' = 't' AND 'gill-attachment' = 'f' AND 'gill-spacing' = 'w' AND 'gill-size' = 'n' AND 'stalk-shape' = 't' AND 'stalk-root' = 'b' AND 'stalk-surface-above-ring' = 's' AND 'stalk-surface-below-ring' = 's' AND 'stalk-color-above-ring' = 'w' AND 'stalk-color-below-ring' = 'w' AND 'veil-type' = 'p' AND 'veil-color' = 'w' AND 'ring-number' = 'o' AND 'ring-type' = 'p' AND 'population' = 'v' AND 'habitat' = 'd' THEN 'class' = 'e'
					List of best rules: IF 'veil-type' = 'p' THEN 'class' = 'p'
					List of best rules: IF  THEN 'class' = null
			*/
		 
		for(int i = 0; i < bestRuleHolder.size(); i++){
			Rule bestRules = bestRuleHolder.get(i);
			System.out.println("List of best rules: " + bestRules);
		}

	} 
	
	public static Rule refinements(ArrayList<Rule> rulesGenerated, JavaRDD<List<String>> examples){
		
		double BestEval = 0.0;
		double prevBestEval = 0.0;
		Rule prevBestRule = null;
		
		Rule BestRule = defaultRule;
		ArrayList<Rule> refinedRules = rulesGenerated;
		
		//Stop when no refinements can be done.
		while (!refinedRules.isEmpty()) {

			//Extract information required for a rule and run a Spark Job to calculate its accuracy, precision and recall.
			
			
			 /* If the best evaluation never changes and the best rule remains the same as well, then we have found our best rule and can stop.
			 * Otherwise, the refineBestRule method will just refine the same best rule again.
			 * 
			 * Need to use compareTo when comparing Doubles.
			 * 
			 * Possible improvements: What if a refined rule has the same accuracy as the current best rule? (No problems if refining the refined rule gives a new rule
			 * with higher accuracy. But if it doesn't then we have two rules with the same accuracy.) */
			 
			/*if(b > 0){
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
			
			//Evaluate each rule using rule evaluator and store their best results in MaxEval and MaxRule
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
			
			//Possible Improvements: Instead of feeding the whole rulesGenerated every time, try to save the generatedRules that collide with the bestRule so that we can feed in a smaller input.
			refinedRules = refineBestRule(BestRule, rulesGenerated);
			
		 //Test whether the statistics have been calculated for all the rule
			/*for(int a = 0; a < rulesGenerated.size(); a++){
				Rule testRule = rulesGenerated.get(a);
				//System.out.println(testRule.getAccuracy());
				
			}*/
		}
		
		/* System.out.println("Best Rule: " + BestRule);
		System.out.println(BestEval);
		System.out.println(BestRule.getExamplesCovered()); */

		
		return BestRule;
	}
	
	public static ArrayList<Rule> refineBestRule(Rule bestRule, ArrayList<Rule> generatedRules){
		/*Create a new list of rule that adding on new conditions(except existing attributes) to the best rule.
		 Will be used to refine the best rule. */
		 
		ArrayList<Rule> remainingRules = new ArrayList<Rule>();
		ArrayList<Attribute> bestRuleAttr = bestRule.getAntecedent();
		ArrayList<Rule> generatedRulesCopy = (ArrayList<Rule>) generatedRules.clone();
		Iterator<Rule> genRuleIterator = generatedRulesCopy.iterator();
		ArrayList<Rule> rulesToRemove = new ArrayList<Rule>();
		
		while(genRuleIterator.hasNext()){
			Rule rule = genRuleIterator.next();
			ArrayList<Attribute> attributes = rule.getAntecedent();
			
			//Looping through attributes of the best rule
			
			for(int j = 0; j < bestRuleAttr.size(); j++){
				Attribute bestRuleAttribute = bestRuleAttr.get(j);
				
				//Looping through attributes of each generated rule
				for(int k = 0; k < attributes.size(); k++){
					Attribute attribute = attributes.get(k);
					
					
					if(bestRuleAttribute.getName() == attribute.getName() ){
						rulesToRemove.add(rule);
					}
					
				}
			}
			
			
			
		}
		
		generatedRulesCopy.removeAll(rulesToRemove);
		System.out.println(rulesToRemove);
		System.out.println(generatedRulesCopy);
		if(generatedRulesCopy.isEmpty()){
			return generatedRulesCopy;
		}
		
		ArrayList<Rule> refinedRules = new ArrayList<Rule>();
		
		
		for(int i = 0; i < generatedRulesCopy.size(); i++){
			Rule rule = generatedRulesCopy.get(i);
			//System.out.println(rule);
			Rule bestRuleCopy;
			
			ArrayList<Attribute> antecedent = rule.getAntecedent();
			for(int j = 0; j < antecedent.size(); j++){
				Attribute attribute = antecedent.get(j);
				bestRuleCopy = new Rule(bestRule);
				bestRuleCopy.addConditionsToRule(attribute.getName(), attribute.getValue(), attribute.getPosition());
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

	
	
	


