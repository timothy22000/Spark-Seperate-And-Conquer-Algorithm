package CO880.testing.algorithm_v1;

import java.util.ArrayList;



import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class ruleGeneratorTest extends TestCase {
	private static String filePath;
	private static ArrayList<Rule> rulesTestFixture;
	
	public ruleGeneratorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		filePath = "weather.nominaltest.arff";
		rulesTestFixture = new ArrayList<Rule>();
		
		Rule rule1 = new Rule("humidity", "high", 0,  "play", "yes", 2);
		Rule rule2 = new Rule("humidity", "high", 0, "play", "no", 2);
		Rule rule3 = new Rule("humidity", "normal", 0, "play", "yes", 2);
		Rule rule4 = new Rule("humidity", "normal", 0, "play", "no", 2);
		Rule rule5 = new Rule("windy", "TRUE", 1, "play", "yes", 2);
		Rule rule6 = new Rule("windy", "TRUE", 1,  "play", "no", 2);
		Rule rule7 = new Rule("windy", "FALSE", 1,  "play", "yes", 2);
		Rule rule8 = new Rule("windy", "FALSE", 1, "play", "no", 2);
		
		
		rulesTestFixture.add(rule1);
		rulesTestFixture.add(rule2);
		rulesTestFixture.add(rule3);
		rulesTestFixture.add(rule4);
		rulesTestFixture.add(rule5);
		rulesTestFixture.add(rule6);
		rulesTestFixture.add(rule7);
		rulesTestFixture.add(rule8);
		
	}

	protected void tearDown() throws Exception {
		rulesTestFixture = null;
	}

	@Test
	public void testGenerateRules() {
		
		RuleGenerator ruleGen = RuleGenerator.getInstance();
		ArrayList<Rule> generatedRules = ruleGen.generateRules(filePath);
		System.out.println(generatedRules);
		System.out.println(rulesTestFixture); 
		Assert.assertNotNull(generatedRules); 
		assertEquals(generatedRules, rulesTestFixture);
		//assertThat(generatedRules, is(rulesTestFixture));

	}
	
	public void runTest(){
		testGenerateRules();
	}

}
