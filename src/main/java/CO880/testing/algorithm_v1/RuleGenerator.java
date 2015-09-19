package CO880.testing.algorithm_v1;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
/**
 * Class that makes the first pass through the file to generate list of possible rules.
 * @author Timothy Sum
 *
 */
public class RuleGenerator implements java.io.Serializable{
	private static RuleGenerator instance = new RuleGenerator();
	//private static ArrayList<Rule> generatedRules;
	private static LinkedHashMap<String, ArrayList<String>> attrHolder;
	private static LinkedHashMap<String, ArrayList<String>> classHolder;
	private static List<List<String>> filteredLines = new ArrayList<List<String>>();
	
	//Using this for finding majority class in Class Frequency since I would need to know what Classes are there, their positions and possible values.
	private static String className;
	private static int classPos;
	private static ArrayList<ArrayList<String>> classValues;
	
	private RuleGenerator(){
		
	}
	
	/**
	 * Returns a singleton instance of RuleGenerator
	 * @return RuleGenerator
	 */
	public static RuleGenerator getInstance(){
		return instance;
	}
	
	//Keep for testing purposes
/* public static void main(String args[]){
	JavaSparkContext sc = new JavaSparkContext("local", "classFreq");
	
	//Load data
	JavaRDD<String> arff = sc.textFile("weather.nominal.arff");
	
	//Filter out header files (anything with @) and then split the lines into words

	JavaRDD<String> data = arff.filter(new Function<String, Boolean>(){
		public Boolean call(String x) {
			if(x.contains("@attribute")){
				
				return true;
			}
			
			else {
				return false;
			}
		}
		
	});
	
	//split spaces into words
	class SplitSpaces implements Function<String, List<String>>{
		public List<String> call(String line){
			List<String> newList =Arrays.asList(line.split("[ {},]"));
			
			return newList;
		}
	}
	
	//Break the lines into arrays of words.
	JavaRDD<List<String>> wordsInArray = data.map(new SplitSpaces());
	
	List<List<String>> extractedListFromFile =  wordsInArray.collect();

	ArrayList<Rule> generatedRules = processingForRules(extractedListFromFile); 
	
	List<List<String>> filteredLines = new ArrayList<List<String>>();
	Path file = Paths.get("weather.nominal.arff");
	try(InputStream in = Files.newInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
		String line = null;
		while((line = reader.readLine()) != null){
			if(line.contains("@attribute")){
				List<String> newList = Arrays.asList(line.split("[ {},]"));
				filteredLines.add(newList);
			}
			
		}
	} catch(IOException x){
		System.err.println(x);
	} 

	

	

	//ArrayList<Rule> generatedRules = processingForRules(filteredLines);
	} */
	

	
/*	public ArrayList<Rule> generateRules(JavaRDD<String> arff) {
		

		

		// Filter out header files (anything with @) and then split the lines
		// into words
		class attributeFilter implements Function<String, Boolean>{

			public Boolean call(String x) throws Exception {
				// TODO Auto-generated method stub
				if (x.contains("@attribute")) {

					return true;
				}

				else {
					return false;
				}
			}
			
		}
		JavaRDD<String> data = arff.filter(new attributeFilter());

		// split spaces into words
		class SplitSpaces implements Function<String, List<String>> {
			public List<String> call(String line) {
				List<String> newList = Arrays.asList(line.split("[ {},]"));

				return newList;
			}
		}

		// Break the lines into arrays of words.
		JavaRDD<List<String>> wordsInArray = data.map(new SplitSpaces());

		List<List<String>> extractedListFromFile = wordsInArray.collect();

		ArrayList<Rule> generatedRules = processingForRules(extractedListFromFile);
		
		return generatedRules;

	} */
	
	/**
	 * File I/O method to open the file and close the buffer once done.
	 * @param filePath
	 */
	public static void openFile(String filePath) {
		try{
			//The path is not java.io or java.nio's Path, it is hadoop's Path implementation to handle hdfs
			Path file = new Path(filePath);
			filteredLines = new ArrayList<List<String>>();
			FileSystem fs = FileSystem.get(new Configuration());
			BufferedReader reader=new BufferedReader(new InputStreamReader(fs.open(file)));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("@attribute")) {
					List<String> newList = Arrays.asList(line.split("[ {},]"));
					filteredLines.add(newList);
				}

			}
			
			reader.close();
		} catch (IOException x) {
			System.err.println(x);
		}

	}
	
	/**
	 * Generating rules by extracting attributes from files without making it a Spark job. Just using BufferedReader.
	 * @param filePath
	 * @return ArrayList of Rule
	 */
	 
	public ArrayList<Rule> generateRules(String filePath) {

		openFile(filePath);

		// System.out.println(filteredLines);

		ArrayList<Rule> generatedRules = processingForRules(filteredLines);

		return generatedRules;

	}
	
	/**
	 * Handles the processing required to extract only the relevant information to form the list of rules.
	 * @param extractedListFromFile
	 * @return ArrayList of Rule
	 */
	private static ArrayList<Rule> processingForRules(List<List<String>> extractedListFromFile){
		ArrayList<String> attributes = new ArrayList<String>();
		ArrayList<String> exampleClass = new ArrayList<String>();
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> localClassValues = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> attrPos = new ArrayList<Integer>();
		ArrayList<Integer> classPos = new ArrayList<Integer>();
		
		/*Split the information in the extracted list into two list (one for attributes and one for values)
		 * 
		 * Output should look like:
		 * [outlook, temperature, humidity, windy, play]
		 * [[sunny, overcast, rainy], [hot, mild, cool], [high, normal], [TRUE, FALSE], [yes, no]]
		 */
		for(int j = 0; j < extractedListFromFile.size(); j++){
			
			if(j == (extractedListFromFile.size() - 1)){
				List<String> item = extractedListFromFile.get(j);
				ArrayList<String> temp = new ArrayList<String>();
				exampleClass.add(item.get(1));
				classPos.add(j);
				for (int i = 2; i < item.size(); i++){
					temp.add(item.get(i));
				}
				temp.removeAll(Arrays.asList(null,""));
				localClassValues.add(temp);
			}
			
			else {
				List<String> item = extractedListFromFile.get(j);
				ArrayList<String> temp = new ArrayList<String>();
				attributes.add(item.get(1));
				attrPos.add(j);
				for (int i = 2; i < item.size(); i++){
					temp.add(item.get(i));
				}
				temp.removeAll(Arrays.asList(null,""));
				values.add(temp);
			}
			
		}
		
		
		
		//Transfer data from two list into hashmap as the final preparation step for rule creation
		attrHolder = new LinkedHashMap<String, ArrayList<String>>();
		classHolder = new LinkedHashMap<String, ArrayList<String>>();
		
		for(int i = 0; i < attributes.size(); i++){
			ArrayList<String> valuesForAttribute = values.get(i);
			attrHolder.put(attributes.get(i), valuesForAttribute);
		}
		
		for(int i = 0; i < exampleClass.size(); i++){
			ArrayList<String> valuesForClass = localClassValues.get(i);
			classHolder.put(exampleClass.get(i), valuesForClass);
			className = exampleClass.get(i);
		}
		
		
		ArrayList<Rule> generatedRules = new ArrayList<Rule>();
		
		int a = -1;
		
		for(String key1: attrHolder.keySet()){
			ArrayList<String> attrValues = attrHolder.get(key1);
			a++;
			for(int i = 0; i < attrValues.size(); i++){
				String value = attrValues.get(i);
			
				for (String key2: classHolder.keySet()){
					ArrayList<String> classValues2 = classHolder.get(key2);
					for(int j = 0; j < classValues2.size(); j++){
						String classValue = classValues2.get(j);
						Rule ruleGen = new Rule(key1, value, attrPos.get(a), key2, classValue,  classPos.get(0));
						
						if(ruleGen != null ){
							System.out.println(ruleGen);
							
							ArrayList<Attribute> testing = ruleGen.getAntecedent();
							
							for(int k = 0; k < testing.size(); k++){
								Attribute attribute = testing.get(k);
								//System.out.println("AttrPos is " + attribute.getPosition());
							}
							
							//System.out.println(ruleGen.getPredictedClass().getPosition());
							
							generatedRules.add(ruleGen);
						}
					}
					
				}
			}
		}
		
		/* System.out.println(extractedListFromFile);
		System.out.println(attributes);
		System.out.println(values);
		System.out.println(exampleClass);
		System.out.println(classValues);
		System.out.println(attrHolder);
		System.out.println(classHolder);
		System.out.println(generatedRules); */
		
		return generatedRules;
		
	}

	//Discover what class does the file have. More lightweight compared to generateRules (Ignoring the values for the class) 
	public void findClassName(String filePath){
		openFile(filePath);
		ArrayList<String> exampleClass = new ArrayList<String>();
		List<List<String>> extractedListFromFile = filteredLines;
		classValues = new ArrayList<ArrayList<String>>();
		
		/*Split the information in the extracted list into two list (one for attributes and one for values)
		 * 
		 * Output should look like:
		 * [outlook, temperature, humidity, windy, play]
		 * [[sunny, overcast, rainy], [hot, mild, cool], [high, normal], [TRUE, FALSE], [yes, no]]
		 */
		for(int j = 0; j < extractedListFromFile.size(); j++){
			
			
			if(j == (extractedListFromFile.size() - 1)){
				List<String> item = extractedListFromFile.get(j);
				ArrayList<String> temp = new ArrayList<String>();
				exampleClass.add(item.get(1));
				classPos = j;
				for (int i = 2; i < item.size(); i++){
					temp.add(item.get(i));
				}
				temp.removeAll(Arrays.asList(null,""));
				classValues.add(temp);
			}
			

		}
		
		
		for(int i = 0; i < exampleClass.size(); i++){
			className = exampleClass.get(i);
		}
		
	
		
		
		
	}

	public static LinkedHashMap<String, ArrayList<String>> getAttrHolder() {
		return attrHolder;
	}


	public static LinkedHashMap<String, ArrayList<String>> getClassHolder() {
		return classHolder;
	}


	public static String getClassName() {
		return className;
	}


	public static void setClassName(String className) {
		RuleGenerator.className = className;
	}


	public static int getClassPos() {
		return classPos;
	}


	public static void setClassPos(int classPos) {
		RuleGenerator.classPos = classPos;
	}


	public static ArrayList<ArrayList<String>> getClassValues() {
		return classValues;
	}


	public static void setClassValues(ArrayList<ArrayList<String>> classValues) {
		RuleGenerator.classValues = classValues;
	}


}
