package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class Rule {
	//Decided to use LinkedHashMap so that the keys are based on insertion order. Originally to simplify the equals method when comparing antecedents. In case order makes them not equal.
	private LinkedHashMap<String, String> predictedClass ;
	private LinkedHashMap<String, String> antecedent;
	private double accuracy;
	//private Double precision;
	//private Double recall;
	private Integer examplesCovered;

	
	public Rule(){
		this.predictedClass = new LinkedHashMap<String, String>();
		this.antecedent = new LinkedHashMap<String, String>();
	
	}
	
	//Clone constructor
	public Rule(Rule another){
		this.predictedClass = (LinkedHashMap) another.predictedClass.clone();
		this.antecedent = (LinkedHashMap) another.antecedent.clone();
		this.accuracy = another.accuracy;
		this.examplesCovered = another.examplesCovered;
	}
	
	public Rule(String attribute, String value, String classPred, String classValue){
		this.predictedClass = new LinkedHashMap<String, String>();
		this.antecedent = new LinkedHashMap<String, String>();
		this.antecedent.put(attribute, value);
		this.predictedClass.put(classPred, classValue);

	}
	
	public Rule(String classPred, String classValue){
		this.predictedClass = new LinkedHashMap<String, String>();
		this.antecedent = new LinkedHashMap<String, String>();
		this.predictedClass.put(classPred, classValue);

	}

	public LinkedHashMap<String, String> getPredictedClass() {
		return predictedClass;
	}

	public void setPredictedClass(String classPred, String classValue) {
		predictedClass.remove(classPred);
		predictedClass.put(classPred, classValue);
	}

	public LinkedHashMap<String, String> getAntecedent() {
		return antecedent;
	}

	
	public Set<String> getAttributes(){
		return antecedent.keySet();
	}
	
	public void addConditionsToRule(String attribute, String value){
		antecedent.put(attribute, value);
	}
	
	public String toString(){
		String text = "IF ";
		ArrayList<String> tempHolder = new ArrayList<String>();
		
		for (String attribute : getAttributes()){
			tempHolder.add(attribute);
		}
		
		for(int i = 0; i < tempHolder.size(); i++){
			
			String attribute = tempHolder.get(i);
			if(i == tempHolder.size() - 1){
				
				text +=  attribute + " = " + antecedent.get(attribute);

			}
			else {
				text += attribute + " = " + antecedent.get(attribute) + " AND ";
			}
			
		}
		
		for (String key : predictedClass.keySet()){
			tempHolder = new ArrayList<String>();
			tempHolder.add(key);
		}
		text += " THEN " + tempHolder.get(0) + " = " + predictedClass.get(tempHolder.get(0));
		
		return text;
	}
	
	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	/* public Double getPrecision() {
		return precision;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public Double getRecall() {
		return recall;
	}

	public void setRecall(Double recall) {
		this.recall = recall;
	} */

	public Integer getExamplesCovered() {
		return examplesCovered;
	}

	public void setExamplesCovered(Integer examplesCovered) {
		this.examplesCovered = examplesCovered;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((antecedent == null) ? 0 : antecedent.hashCode());
		result = prime * result
				+ ((predictedClass == null) ? 0 : predictedClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Rule)) {
			return false;
		}
		Rule other = (Rule) obj;
		if (antecedent == null) {
			if (other.antecedent != null) {
				return false;
			}
		} else if (!antecedent.equals(other.antecedent)) {
			return false;
		}
		if (predictedClass == null) {
			if (other.predictedClass != null) {
				return false;
			}
		} else if (!predictedClass.equals(other.predictedClass)) {
			return false;
		}
		return true;
	}
	
	public boolean isEmptyConditions(){
		if(this.antecedent.isEmpty() ){
			return true;
		}
		
		return false;
	}
}
