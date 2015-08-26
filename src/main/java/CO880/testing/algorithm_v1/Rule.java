package CO880.testing.algorithm_v1;

import java.util.ArrayList;

/**
 * Rule class that combines information from Attribute and Class to form a rule with antecedents and consequents
 * @author Timothy Sum
 *
 */
public class Rule implements java.io.Serializable{
	//Decided to use LinkedHashMap so that the keys are based on insertion order. Originally to simplify the equals method when comparing antecedents. In case order makes them not equal.
	private Class predictedClass ;
	private ArrayList<Attribute> antecedent;
	private double accuracy;
	//private Double precision;
	//private Double recall;
	private Integer examplesCovered = 0;
	
	/**
	 * Rule constructor
	 */
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
	
	/**
	 * Rule constructor
	 * @param attribute name
	 * @param attribute value
	 * @param attribute's position
	 * @param class name 
	 * @param class value
	 * @param class position
	 */
	public Rule(String attribute, String value, int attrPosition, String classPred, String classValue, int classPosition){
		this.predictedClass = new Class(classPred, classValue, classPosition);
		this.antecedent = new ArrayList<Attribute>();
		
		this.antecedent.add(new Attribute(attribute, value, attrPosition));
		

	}
	
	/**
	 * Rule constructor for rules without any antecedent.
	 * @param class name
	 * @param class value
	 * @param class position
	 */
	public Rule(String classPred, String classValue, int position){
	
		this.antecedent = new ArrayList<Attribute>();
		
		this.predictedClass = new Class(classPred, classValue, position);

	}

	/**
	 * Returns the Class of the rule
	 * @return Class 
	 */
	public Class getPredictedClass() {
		return predictedClass;
	}
	
	/**
	 * Sets the information that is required for a Class
	 * @param class name 
	 * @param class value
	 * @param class position
	 */
	public void setPredictedClass(String classPred, String classValue, int position) {
		this.predictedClass.setName(classPred);
		this.predictedClass.setValue(classValue);
		this.predictedClass.setPosition(position);
	}

	/**
	 * Returns the list of Attributes for the rule
	 * @return ArrayList of Attribute
	 */
	public ArrayList<Attribute> getAntecedent() {
		return antecedent;
	}

	
	/**
	 * Adds new conditions to the antecedent of the rule
	 * @param attribute name
	 * @param attribute value
	 * @param position
	 */
	
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
	
	/**
	 * Gets the accuracy of this rule
	 * @return accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}
	
	/**
	 * Sets the accuracy of this rule
	 * @param accuracy
	 */

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
	
	/**
	 * Gets the number of examples covered by this particular rule
	 * @returns int
	 */
	public int getExamplesCovered() {
		return examplesCovered;
	}
	
	/**
	 * Sets the number of examples covered by this rule.
	 * @param examplesCovered
	 */
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
