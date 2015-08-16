package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;


public class ClassFrequency {

	
	private static String majorityClassValue;
	private ClassFrequency instance = new ClassFrequency();
	
	public ClassFrequency(){
		
	}
	
	public ClassFrequency getInstance(){
		return instance;
	}
	
	/* public static void main(String[] args){
	
	//Test out Rule class
	Rule testRule = new Rule("outlook", "sunny", "play", "yes");
	testRule.addConditionsToRule("temperature", "hot");
	System.out.println(testRule);
	
	//Holder for best rules
	ArrayList<Rule> ruleHolder = new ArrayList<Rule>();
	
	JavaSparkContext sc = new JavaSparkContext("local", "classFreq");
	
	//Load data
	JavaRDD<String> arff = sc.textFile("weather.nominal.arff");
	
	//Filter out header files (anything with @) and then split the lines into words

	JavaRDD<String> data = arff.filter(new Function<String, Boolean>(){
		public Boolean call(String x) {
			if(x.contains("@")){
				return false;
			}
			
			else {
				return true;
			}
		}
		
	}).flatMap(new FlatMapFunction<String, String>(){

		public Iterable<String> call(String line) throws Exception {
			// TODO Auto-generated method stub
			return Arrays.asList(line.split(","));
		}
		
	});
	
	//Create Key/Value pairs with word as key and count as value and then sum them up using reduceByKey
	JavaPairRDD<String,Integer> classCounts = data.mapToPair(
			new PairFunction<String, String, Integer>(){

				public Tuple2<String, Integer> call(String x)
						throws Exception {
					// TODO Auto-generated method stub
					return new Tuple2(x, 1);
				}
				
			}).reduceByKey(new Function2<Integer, Integer, Integer>(){
				public Integer call(Integer x, Integer y){
					return x + y;
				}
			});
			
			
	//Confirm Output
	for(Tuple2<String, Integer> tuple1 : classCounts.take((int) classCounts.count())){
		if(tuple1._1.equals("no") || tuple1._1.equals("yes")){
			System.out.println("Class: " + tuple1._1 + ", Frequency: " + tuple1._2);
		}
		
	
	}*/
	
	/* Debugging and Troubleshooting */
	/* for(String line : data.take(10)){
		System.out.println(line);
	}
	//System.out.println(data);
	

	} */
	
	// Returns a default rule after identifying the majority class
	public static Rule getDefaultRule(JavaRDD<String> data, String filePath){

	
		
		JavaRDD<String> flattenedLines = data.flatMap(new FlatMapFunction<String, String>(){

			public Iterable<String> call(String line) throws Exception {
				// TODO Auto-generated method stub
				return Arrays.asList(line.split(","));
			}

		});
		
		//Create Key/Value pairs with word as key and count as value and then sum them up using reduceByKey
		JavaPairRDD<String,Integer> classCounts = flattenedLines.mapToPair(
				new PairFunction<String, String, Integer>(){

					public Tuple2<String, Integer> call(String x)
							throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2(x, 1);
					}
					
				}).reduceByKey(new Function2<Integer, Integer, Integer>(){
					public Integer call(Integer x, Integer y){
						return x + y;
					}
				});
				
				
		/*Confirm Output */
		ArrayList<Tuple2<String, Integer>> holderForClasses = new ArrayList<Tuple2<String, Integer>>();
		for(Tuple2<String, Integer> tuple1 : classCounts.take((int) classCounts.count())){
			if(tuple1._1.equals("no") || tuple1._1.equals("yes")){
				//System.out.println("Class: " + tuple1._1 + ", Frequency: " + tuple1._2);
				holderForClasses.add(tuple1);
			}
		}
		
		Integer largestCount = 0;
		for(int i = 0; i < holderForClasses.size(); i++){
			Tuple2<String, Integer> tuple1 = holderForClasses.get(i);
			
			/*
			 *  Important: Does not handle situations where there are more than one majority class yet (Last one to be majority class is selected for now).
			 * Check which class has the largest count and that class will be the majority class.
			 */
			if(tuple1._2 >= largestCount){
				largestCount = tuple1._2;
				majorityClassValue = tuple1._1;
			}
		}
		
		/* Debugging and Troubleshooting */
		/* for(String line : data.take(10)){
			System.out.println(line);
		}*/
		//System.out.println(data);
		
		RuleGenerator generator = RuleGenerator.getInstance();
		generator.findClassName(filePath);
		String className = RuleGenerator.getClassName();
		System.out.println(className);
		System.out.println(majorityClassValue);
		return new Rule(className,majorityClassValue);
		
	}

}

	
	
	


