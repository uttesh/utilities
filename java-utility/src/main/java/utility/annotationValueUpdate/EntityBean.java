package utility.annotationValueUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EntityBean {

	String name;
	String value;
	
	@JsonIgnore
	String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
