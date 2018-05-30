package com.insightx.tools.diagnostic;

import java.util.LinkedHashMap;
import java.util.Map;

import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;

public abstract class LoadValidationFactoryInterface {
		
	public static final int STRING = 1;
	public static final int INTEGER = 2;
	public static final int FLOAT = 3;
	public static final int BOOLEAN = 4;
	
	private String shellKey;
	private String description;
	private String processName;

	private Map<String,RequiredParameter> requiredParameterList;
	private Map<String,OptionalParameter> optionalParameterList;
	
	public LoadValidationFactoryInterface(String processName, String shellKey, String description) {
		this.processName = processName;
		this.shellKey = shellKey;
		this.description = description;
		requiredParameterList = new LinkedHashMap<String, RequiredParameter>();
		optionalParameterList = new LinkedHashMap<String, OptionalParameter>();
		loadExecutionParameters();
	}
	
	protected abstract void loadExecutionParameters();
	
	protected abstract LoadValidationInstanceInterface createInstance();

	public String getProcessName() {
		return processName;
	}

	public String getShellKey() {
		return shellKey;
	}

	public String getDescription() {
		return description;
	}

	protected void addRequiredParamter (String parameterId, RequiredParameter requiredParameter) {
		requiredParameterList.put(parameterId, requiredParameter);
	}
	
	protected void addOptionalParameter (String parameterId, OptionalParameter optionalParameter) {
		optionalParameterList.put(parameterId, optionalParameter);
	}

	public Object getParameter(String parameterId) {
		Object result = null;
		if (requiredParameterList.containsKey(parameterId)) {
			result = requiredParameterList.get(parameterId).getValue();
		} else if (optionalParameterList.containsKey(parameterId)) {
			if (optionalParameterList.get(parameterId).getValue() != null) {
				result = optionalParameterList.get(parameterId).getValue();
			} else {
				result = optionalParameterList.get(parameterId).getDefaultValue();
			}
		}
		return result;
	}
	
	public Map<String, RequiredParameter> getRequiredParameterList() {
		return this.requiredParameterList;
	}

	public Map<String, OptionalParameter> getOptionalParameterList() {
		return this.optionalParameterList;
	}	
}
