package com.sh.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SenderCtrl {
	
	
	@Autowired
	@Qualifier("jmsTemplateTopic")
	private JmsTemplate jmsTemplateTopic;
	
	@Autowired
	@Qualifier("jmsTemplateQueue")
	private JmsTemplate jmsTemplateQueue;
	
	@Autowired
	private ThreadPoolTaskExecutor executor;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SenderCtrl.class);
	
	
	@RequestMapping(value="/sendTopic",method=RequestMethod.POST)
	public String sendTopic(@RequestBody String msg)
	{		
		try 
		{
			jmsTemplateTopic.send(session->session.createTextMessage(msg));
			return "MESSAGE WAS SENT";
		}
		catch (JmsException e) 
		{
			LOGGER.debug("Error: ",e);
			return e.getMessage();
		}
	}
	
	@RequestMapping(value="/sendQueue",method=RequestMethod.POST)
	public String sendQueue(@RequestBody String msg)
	{		
		try 
		{
			jmsTemplateQueue.send(session->session.createTextMessage(msg));
			return "MESSAGE WAS SENT";
		}
		catch (JmsException e) 
		{
			LOGGER.debug("Error: ",e);
			return e.getMessage();
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/sendFromFIle",method=RequestMethod.GET)
	public String sendFromFile()
	{
		try 
		{
			InputStream iStr = getClass().getClassLoader().getResourceAsStream("messages.txt");
			List<String> lsRes =  IOUtils.readLines(iStr);
			executor.execute(createThread(lsRes));
			return "PROCESS LAUNCHED";
			
		} 
		catch (IOException e) {
			
			LOGGER.debug("Error: ",e);
			return e.getMessage();
		}
		
		
	}
	
	private Thread createThread(List<String> ls)
	{
		Runnable hilo = new Runnable() 
		{	
			@Override
			public void run() 
			{
				
				for(String s : ls)
				{
					jmsTemplateQueue.send(session->session.createTextMessage(s));
				}
			
			}
		};
		
		Thread tarea = new Thread(hilo);
		return tarea;
	}

}
