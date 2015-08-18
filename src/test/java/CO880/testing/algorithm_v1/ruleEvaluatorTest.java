package CO880.testing.algorithm_v1;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ruleEvaluatorTest implements java.io.Serializable{
	private static Rule ruleFixture;

	public ruleEvaluatorTest() {
	}

	@Before
	public void setUp() throws Exception {
		ruleFixture = new Rule("outlook", "sunny", 0, "play", "yes", 4);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEvaluateRules() {
		RuleEvaluator evaluator = RuleEvaluator.getInstance();
		
		
		//Need the spark job to get the examples for evaluating the rule fixture
		JavaSparkContext sc = new JavaSparkContext("local", "classFreq");
		
		//Load data
		JavaRDD<String> arff = sc.textFile("weather.nominal.arff");
		
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
		
		//Break the lines into arrays of words. wordsInArray kind of represents Examples.
		JavaRDD<List<String>> wordsInArray = data.map(new SplitLines());
	
		
		evaluator.evaluateRules(ruleFixture, wordsInArray);
		Assert.assertEquals(0.2857142857142857, ruleFixture.getAccuracy());
		//Assert.assertEquals(0.2222222222222222, ruleFixture.getRecall());
		//Assert.assertEquals(0.4, ruleFixture.getPrecision());
	}

}
