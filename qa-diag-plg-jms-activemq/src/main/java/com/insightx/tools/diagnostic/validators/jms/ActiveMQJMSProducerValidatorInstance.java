package com.insightx.tools.diagnostic.validators.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;

public class ActiveMQJMSProducerValidatorInstance extends LoadValidationInstanceInterface {

	private String url;
	private String queueName;
	private String message;
	private Boolean dispatchAsync;
	
	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;

	
	public ActiveMQJMSProducerValidatorInstance(String processName, String url, String queueName, int bytes, Boolean dispatchAsync) {
		this.url = url;
		this.queueName = queueName;
		this.dispatchAsync = dispatchAsync;

		session = null;
		connection = null;
		destination = null;
		message = "";
		
		for (long i=0; i<bytes; i++){
			message += "X";
		}

	}

	@Override
	public void setup() throws Exception {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		connectionFactory.setDispatchAsync(dispatchAsync);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(queueName);
		producer = session.createProducer(destination);
	}

	@Override
	public void tearDown() throws Exception {
		if(producer != null) producer.close();
		if(session != null)	session.close();
		if(connection != null) connection.close();	
	}

	@Override
	public int validate() throws Exception {
		ActiveMQTextMessage msg = new ActiveMQTextMessage();
		msg.setText(message);
		producer.send(msg);

		return SUCCESS;
	}	

	@Override
	public int preValidate() throws Exception {
		return SUCCESS;
	}

	@Override
	public int postValidate() throws Exception {
		return SUCCESS;
	}
}