package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

/**
 * This class discovers the majority class of the instances in the file.
 * @author Timothy Sum
 *
 */

public class ClassFrequency implements java.io.Serializable {

	
	private static String majorityClassValue;
	private ClassFrequency instance = new ClassFrequency();
	
	public ClassFrequency(){
		
	}
	
	/**
	 * Creates a singleton instance of ClassFrequency
	 * @return ClassFrequency object
	 */
	public ClassFrequency getInstance(){
		return instance;
	}
	
	
	/**
	 * Important method that starts a Spark job to analyze through the file to discover the most common/majority class value.
	 * 
	 * @param a filtered RDD, data
	 * @param filePath
	 * @return Default Rule
	 */
	public static Rule getDefaultRule(JavaRDD<String> data, String filePath){

		RuleGenerator generator = RuleGenerator.getInstance();
		generator.findClassName(filePath);
		String className = RuleGenerator.getClassName();
		int classPos = RuleGenerator.getClassPos();
		ArrayList<ArrayList<String>> classValues = RuleGenerator.getClassValues();
		
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
			for(int i = 0; i < classValues.size(); i++){
				ArrayList<String> values = classValues.get(i);
				for(String value : values){
					if(tuple1._1.equals(value)){
						holderForClasses.add(tuple1);
					}
				}
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
		

		/* System.out.println(className);
		System.out.println(majorityClassValue);
		System.out.println(classPos); */
		return new Rule(className ,majorityClassValue, classPos);
		
	}

}

	
	
	


