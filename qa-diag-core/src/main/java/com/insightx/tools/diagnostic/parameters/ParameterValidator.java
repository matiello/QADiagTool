package com.insightx.tools.diagnostic.parameters;

public interface ParameterValidator {
	public abstract boolean validateParameterValue(Object param);
	
	public abstract String getParameterValidationRule();
}
