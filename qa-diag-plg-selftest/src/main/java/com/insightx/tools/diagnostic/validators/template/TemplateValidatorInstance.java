package com.insightx.tools.diagnostic.validators.template;

import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;

public class TemplateValidatorInstance extends LoadValidationInstanceInterface {

	Integer sleepTime;

	public TemplateValidatorInstance(String processName, Integer sleepTime) {
		/* Paramteres should be stored here */
		this.sleepTime = sleepTime;
	}

	@Override
	public void setup() throws Exception {
		/* Setup code should be placed here. It is executed only once by the main application per thread. */
		/* It's execution time is not considered by the main application. */
	}

	@Override
	public void tearDown() throws Exception {
		/* Release resources code should be placed here. It is executed only once by the main application per thread. */
		/* It's execution time is not considered by the main application. */
	}

	@Override
	public int validate() throws Exception {
		/* Validation code goes here. */
		/* Here goes the code whose execution time will be measured. */
		Thread.sleep(sleepTime);
		return SUCCESS;
	}

	@Override
	public int postValidate() throws Exception {
		return SUCCESS;
	}

	@Override
	public int preValidate() throws Exception {
		return SUCCESS;
	}

}
