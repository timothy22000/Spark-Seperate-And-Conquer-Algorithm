package CO880.testing.algorithm_v1;

/**
 * Class to store extracted information regarding predicted class from arff file.
 * @author Timothy Sum
 *
 */
public class Class implements java.io.Serializable{
	private String name;
	private int position;
	private String value;
	
	/**
	 * Class constructor
	 * @param class name
	 * @param class position
	 */
	public Class(String name, int position){
		this.name = name;
		this.position = position;
	}
	
	/**
	 * Class constructor
	 * @param name
	 * @param value
	 * @param position
	 */
	public Class(String name, String value, int position){
		this.name = name;
		this.value = value;
		this.position = position;
	}
	
	public Class(){
		
	}
	
	/**
	 * Returns name of the class
	 * @return class name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the class name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the position in the file for the class
	 * @return position number
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the position of the class in the file.
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * Get the value of the class
	 * @return String classValue
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value of the class
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
		if (!(obj instanceof Class)) {
			return false;
		}
		Class other = (Class) obj;
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
		return "Class [name=" + name + ", position=" + position + ", value="
				+ value + "]";
	}
	
	
}
