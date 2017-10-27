package com.sh.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class MsgListenerTopic {

	private static Logger LOGGER = LoggerFactory.getLogger(MsgListenerTopic.class);
	
	public void onMessage(String msg) 
	{
		LOGGER.debug(msg);
	}
	
}
