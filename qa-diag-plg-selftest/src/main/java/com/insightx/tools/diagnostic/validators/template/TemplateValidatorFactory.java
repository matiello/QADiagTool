package com.insightx.tools.diagnostic.validators.template;

import com.insightx.tools.diagnostic.LoadValidationFactoryInterface;
import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;

/* Attention: Any changes to the classe name should be applied on 
 * the src/main/resources/pt.ptinovacao.acm.tools.diagnostic.LoadValidationFactoryInterface file
 */
public class TemplateValidatorFactory extends LoadValidationFactoryInterface{

	public TemplateValidatorFactory() {
		/* Fill the plugin main definitions */
		super("Self Test Diagnostic", "-selftest", "Execute internal timers validations");
	}

	@Override
	protected void loadExecutionParameters() {
		/* Add the plugin required paramters */
		addRequiredParamter ("sleep",new RequiredParameter("sleep","Process sleep time in ms", INTEGER));
	}

	@Override
	protected LoadValidationInstanceInterface createInstance() {
		LoadValidationInstanceInterface result = null;
		/* Pass the required plugin paramters to the validation instance to be created */
		result = new TemplateValidatorInstance(
				this.getProcessName(),
		 		(Integer) getParameter("sleep")
		);
		return result;
	}

}
