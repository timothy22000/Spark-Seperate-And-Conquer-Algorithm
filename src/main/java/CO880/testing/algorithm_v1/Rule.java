package CO880.testing.algorithm_v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class Rule {
	//Decided to use LinkedHashMap so that the keys are based on insertion order. Originally to simplify the equals method when comparing antecedents. In case order makes them not equal.
	private Class predictedClass ;
	private ArrayList<Attribute> antecedent;
	private double accuracy;
	//private Double precision;
	//private Double recall;
	private Integer examplesCovered = 0;

	
	public Rule(){
		this.predictedClass = new Class();
		this.antecedent = new ArrayList<Attribute>();
	
	}
	
	//Clone constructor
	public Rule(Rule another){
		this.predictedClass = (Class) another.predictedClass;
		this.antecedent = (ArrayList) another.antecedent.clone();
		this.accuracy = another.accuracy;
		this.examplesCovered = another.examplesCovered;
	}
	
	public Rule(String attribute, String value, int attrPosition, String classPred, String classValue, int classPosition){
		this.predictedClass = new Class(classPred, classValue, classPosition);
		this.antecedent = new ArrayList<Attribute>();
		
		this.antecedent.add(new Attribute(attribute, value, attrPosition));
		

	}
	
	public Rule(String classPred, String classValue, int position){
	
		this.antecedent = new ArrayList<Attribute>();
		
		this.predictedClass = new Class(classPred, classValue, position);

	}

	public Class getPredictedClass() {
		return predictedClass;
	}

	public void setPredictedClass(String classPred, String classValue, int position) {
		this.predictedClass.setName(classPred);
		this.predictedClass.setValue(classValue);
		this.predictedClass.setPosition(position);
	}

	public ArrayList<Attribute> getAntecedent() {
		return antecedent;
	}

	

	
	public void addConditionsToRule(String attribute, String value, int position){
		antecedent.add(new Attribute(attribute, value, position));
	}
	
	public String toString(){
		String text = "IF ";
		ArrayList<Attribute> tempHolder = getAntecedent();
		

		
		for(int i = 0; i < tempHolder.size(); i++){
			
			Attribute attribute = tempHolder.get(i);
			String attrName = attribute.getName();
			String attrValue = attribute.getValue();
			if(i == tempHolder.size() - 1){
				
				text +=  attrName + " = " + attrValue;

			}
			else {
				text += attrName + " = " + attrValue + " AND ";
			}
			
		}
		
	
		text += " THEN " + predictedClass.getName() + " = " + predictedClass.getValue();
		
		return text;
	}
	
	public double getAccuracy() {
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

	public int getExamplesCovered() {
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
