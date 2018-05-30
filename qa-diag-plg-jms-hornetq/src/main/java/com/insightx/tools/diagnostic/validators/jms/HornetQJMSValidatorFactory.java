package com.insightx.tools.diagnostic.validators.jms;

import com.insightx.tools.diagnostic.LoadValidationFactoryInterface;
import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;
import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.parameters.RequiredParameter;

public class HornetQJMSValidatorFactory extends LoadValidationFactoryInterface{

	public HornetQJMSValidatorFactory() {
		super("HornetQJMSDiagnostic", "-jmsHornetQ", "Execute HornetQ JMS load diagnostic");
	}

	@Override
	protected void loadExecutionParameters() {
		addRequiredParamter ("action",new RequiredParameter("action","The jms action [CONSUMER|PRODUCER].", STRING));
		addRequiredParamter ("hostname", new RequiredParameter("hostname","The jms hostname to connect.", STRING));
		addRequiredParamter ("port", new RequiredParameter("port","The jms port to connect.", INTEGER));
		addOptionalParameter("queueName", new OptionalParameter("queueName", "The JMS queue name.", "-queueName", STRING, "default_queue"));
		addOptionalParameter("bytes", new OptionalParameter("bytes", "The JMS message size in bytes.", "-bytes", INTEGER, 1));
	}

	@Override
	protected LoadValidationInstanceInterface createInstance() {
		LoadValidationInstanceInterface result = null;
		if (getRequiredParameterList().get("action").getValue().equals("CONSUMER")) {
			result = new HornetQJMSConsumerValidatorInstance(
					this.getProcessName(),
			 		(String) getParameter("hostname"),
			 		(Integer) getParameter("port"),
			 		(String) getParameter("queueName"),
			 		(Integer) getParameter("bytes")
			);
		} else if (getRequiredParameterList().get("action").getValue().equals("PRODUCER")) {
			result = new HornetQJMSProducerValidatorInstance(
					this.getProcessName(),
			 		(String) getParameter("hostname"),
			 		(Integer) getParameter("port"),
			 		(String) getParameter("queueName"),
			 		(Integer) getParameter("bytes")
			);
		} 
		return result;
	}

}
