package com.sh.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MsgListenerQueue {

	@Autowired
	@Qualifier("jmsTemplateTopic")
	private JmsTemplate jmsTemplateTopic;
	
	private static Logger LOGGER = LoggerFactory.getLogger(MsgListenerQueue.class);
	
	public void onMessage(String msg) 
	{
		
		LOGGER.debug(msg);
		jmsTemplateTopic.send(session->session.createTextMessage(UUID.randomUUID()+" "+ msg));
	}

}
