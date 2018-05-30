package com.insightx.tools.diagnostic.parameters;

public class RequiredParameter {
	private String id;
	private String description;
	private int type;
	private Object value;
	private ParameterValidator validator;

	public RequiredParameter (String id, String description, int type, ParameterValidator validator) {
		this.validator = validator;
	}
	
	public RequiredParameter (String id, String description, int type) {
		this.id = id;
		this.description = description;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue (Object value) {
		if ((validator == null) || ( (validator != null) && (value != null) && (validator.validateParameterValue(value)))) {
			this.value = value;
		} else {
			throw new RuntimeException ("Invalid paramter value: " + value + ". " + validator.getParameterValidationRule());
		}
	}
	
	public String getValidationRule() {
		if (validator == null) {
			return "";
		} else {
			return validator.getParameterValidationRule();
		}
	}
}
