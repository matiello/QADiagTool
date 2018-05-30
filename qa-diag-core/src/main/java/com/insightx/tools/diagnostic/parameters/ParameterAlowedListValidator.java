package com.insightx.tools.diagnostic.parameters;

import java.util.List;

public class ParameterAlowedListValidator implements ParameterValidator {

	private List<Object> alowedValuesList;
	
	public ParameterAlowedListValidator(List<Object> alowedValuesList) {
		this.alowedValuesList = alowedValuesList;
	}
	
	@Override
	public String getParameterValidationRule() {
		String rule = "Alowed values: [";
		if (alowedValuesList != null) {
			for (int i=0; i<alowedValuesList.size()-1; i++) {
				rule += alowedValuesList.get(i).toString() + ",";
			}
			rule += alowedValuesList.get(alowedValuesList.size()-1).toString();
		}
		rule += "]";
		return rule;
	}

	@Override
	public boolean validateParameterValue(Object param) {
		boolean result = false;
		if (alowedValuesList != null) {
			for (Object obj : alowedValuesList) {
				if (param.equals(obj)) {
					result = true;
				}
			}
		}
		return result;
	}

}
