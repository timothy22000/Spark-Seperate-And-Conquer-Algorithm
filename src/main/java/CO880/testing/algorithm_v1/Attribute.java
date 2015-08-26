package CO880.testing.algorithm_v1;

/**
 * Attribute class to store related information regarding attributes in an arff file
 * @author Timothy Sum
 *
 */
public class Attribute implements java.io.Serializable{
	private String name;
	private int position;
	private String value;
	
	/**
	 * Attribute constructor
	 * @param name
	 * @param position
	 */
	public Attribute(String name, int position){
		this.name = name;
		this.position = position;
	}
	
	/**
	 * Attribute constructor
	 * @param name
	 * @param value
	 * @param position
	 */
	public Attribute(String name, String value, int position){
		this.name = name;
		this.value = value;
		this.position = position;
	}
	public Attribute(){
		
	}

	/**
	 * Returns name of attribute
	 * @return name of attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of attribute
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get attribute's position in the arff file
	 * @return attribute's position
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Set attribute's position
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * Get attribute's value
	 * @return attribute's value
	 */

	public String getValue() {
		return value;
	}
	
	/**
	 * Set attribute's value
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + position;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (!(obj instanceof Attribute)) {
			return false;
		}
		Attribute other = (Attribute) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (position != other.position) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Attribute [name=" + name + ", position=" + position
				+ ", value=" + value + "]";
	}
}
