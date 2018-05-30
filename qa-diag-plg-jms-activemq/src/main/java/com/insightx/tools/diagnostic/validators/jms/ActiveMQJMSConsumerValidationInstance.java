package com.insightx.tools.diagnostic.validators.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;

public class ActiveMQJMSConsumerValidationInstance extends LoadValidationInstanceInterface {

	private String url;
	private String queueName;
	private String message;
	private Boolean dispatchAsync;
	
	private Connection connection;
	private Session session;
	Destination destination;
	private MessageConsumer consumer;

	
	public ActiveMQJMSConsumerValidationInstance(String processName, String url, String queueName, long bytes, Boolean dispatchAsync) {
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
		consumer = session.createConsumer(destination);
	}

	@Override
	public void tearDown() throws Exception {
		if(consumer != null) consumer.close();
		if(session != null)	session.close();
		if(connection != null) connection.close();	
	}

	@Override
	public int validate() throws Exception {
		ActiveMQTextMessage msg = (ActiveMQTextMessage) consumer.receive();
		if ( (msg != null) && (msg.getText().length() > 0) && msg.equals(message)) {
			return SUCCESS;
		} else {
			return FAILED;
		}
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