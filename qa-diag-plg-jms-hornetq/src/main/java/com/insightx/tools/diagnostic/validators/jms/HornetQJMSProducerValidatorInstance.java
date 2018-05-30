package com.insightx.tools.diagnostic.validators.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;

import com.insightx.tools.diagnostic.LoadValidationInstanceInterface;

public class HornetQJMSProducerValidatorInstance extends LoadValidationInstanceInterface {

	private String hostname;
	private int port;
	private String queueName;
	private String message;
	
	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	private Queue queue;

	public HornetQJMSProducerValidatorInstance(String processName, String hostname, int port, String queueName, int bytes) {
		this.hostname = hostname;
		this.port = port;
		this.queueName = queueName;

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
	    Map<String, Object> connectionParams = new HashMap<String, Object>();  
	    connectionParams.put("host", hostname);
	    connectionParams.put("port", port);
	    TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(),connectionParams); 
	       
	    queue = HornetQJMSClient.createQueue(queueName); 
	    ConnectionFactory connectionFactory = HornetQJMSClient.createConnectionFactory(transportConfiguration);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = queue;
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
		TextMessage msg = session.createTextMessage(); 
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
