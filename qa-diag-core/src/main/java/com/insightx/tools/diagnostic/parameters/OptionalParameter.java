package com.insightx.tools.diagnostic.parameters;

public class OptionalParameter {
	private String id;
	private String description;
	private String shellKey;
	private int type;
	private Object value;
	private Object defaultValue;
	
	public OptionalParameter(String id, String description, String shellKey, int type, Object defaultValue) {
		this.id = id;
		this.description = description;
		this.shellKey = shellKey;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	public String getShellKey() {
		return shellKey;
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

	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public void setValue (Object value){
		this.value = value;
	}
}
