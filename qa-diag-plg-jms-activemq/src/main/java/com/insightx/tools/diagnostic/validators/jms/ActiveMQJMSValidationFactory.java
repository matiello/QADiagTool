package com.insightx.tools.diagnostic.validators.jms;

import com.insightx.tools.diagnostic.LoadValidationFactoryInterface;
import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;
import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;

public class ActiveMQJMSValidationFactory extends LoadValidationFactoryInterface{

	public ActiveMQJMSValidationFactory() {
		super("ActiveMQJMSDiagnostic", "-jmsActiveMQ", "Execute ActiveMQ JMS load diagnostic");
	}

	@Override
	protected void loadExecutionParameters() {
		addRequiredParamter ("action",new RequiredParameter("action","The jms action [CONSUMER|PRODUCER].", STRING));
		addRequiredParamter ("url", new RequiredParameter("url","The jms url to connect.", STRING));
		addOptionalParameter("queueName", new OptionalParameter("queueName", "The JMS queue name.", "-queueName", STRING, "default_queue"));
		addOptionalParameter("bytes", new OptionalParameter("bytes", "The JMS message size in bytes.", "-bytes", INTEGER, 1));
		addOptionalParameter("dispatchAsync", new OptionalParameter("dispatchAsync", "The indication to dispatch messages asynchronously.", "-dispatchAsync", BOOLEAN, false));
	}

	@Override
	protected LoadValidationInstanceInterface createInstance() {
		LoadValidationInstanceInterface result = null;
		if (getRequiredParameterList().get("action").getValue().equals("CONSUMER")) {
			result = new ActiveMQJMSConsumerValidationInstance(
					this.getProcessName(),
			 		(String) getRequiredParameterList().get("url").getValue(),
			 		(String) getParameter("queueName"),
			 		(Integer) getParameter("bytes"),
			 		(Boolean) getParameter("dispatchAsync")
			);
		} else if (getRequiredParameterList().get("action").getValue().equals("PRODUCER")) {
			result = new ActiveMQJMSProducerValidatorInstance(
					this.getProcessName(),
			 		(String) getRequiredParameterList().get("url").getValue(),
			 		(String) getParameter("queueName"),
			 		(Integer) getParameter("bytes"),
			 		(Boolean) getParameter("dispatchAsync")
			);
		} 
		return result;
	}
}
