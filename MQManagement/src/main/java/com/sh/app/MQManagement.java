package com.sh.app;

import java.text.MessageFormat;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@ComponentScan(basePackages="com.sh")
public class MQManagement {
	
	@Value("${jms.broker.url}")
	private String broker;
    
	
	@Value("${jms.topic.name}")
    private String topicName;
	
	@Value("${jms.queue.name}")
    private String queueName;
	
	private static Logger LOGGER = LoggerFactory.getLogger(MQManagement.class);


	public static void main(String[] args) {
		SpringApplication.run(MQManagement.class);
	}

	
	 	@Bean
	    public ConnectionFactory connectionFactory(){
	 		LOGGER.debug("<<<<<< Loading connectionFactory");
	        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
	        connectionFactory.setBrokerURL(broker);
	        LOGGER.debug(MessageFormat.format("{0} loaded sucesfully >>>>>>>", broker));
	        return connectionFactory;
	    }
	    /*
	     * Optionally you can use cached connection factory if performance is a big concern.
	     */
	 
	    @Bean
	    public ConnectionFactory cachingConnectionFactory(){
	        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
	        connectionFactory.setTargetConnectionFactory(connectionFactory());
	        connectionFactory.setSessionCacheSize(10);
	        return connectionFactory;
	    }
	     
	    /*
	     * Used for Sending Messages to topic.
	     */
	    
	    @Bean(name = "jmsTemplateTopic")
	    public JmsTemplate jmsTemplateTopic(){
	    	LOGGER.debug("<<<<<< Loading jmsTemplateTopic");
	        JmsTemplate template = new JmsTemplate();
	        template.setConnectionFactory(connectionFactory());
	        template.setDefaultDestinationName(topicName);
	        template.setPubSubDomain(true);
	        LOGGER.debug("jmsTemplateTopic loaded >>>>>>>");
	        
	        return template;
	    }
	    
	    /*
	     * Used for Sending Messages to queue.
	     */
	    
	    @Bean(name = "jmsTemplateQueue")
	    public JmsTemplate jmsTemplateQueue(){
	    	LOGGER.debug("<<<<<< Loading jmsTemplateQueue");
	        JmsTemplate template = new JmsTemplate();
	        template.setConnectionFactory(connectionFactory());
	        template.setDefaultDestinationName(queueName);
	        template.setPubSubDomain(false);
	        LOGGER.debug("jmsTemplateQueue loaded >>>>>>>>");
	        
	        return template;
	    }
	    @Bean
	    public MessageConverter converter(){
	        return new SimpleMessageConverter();
	    }
	    
	    @Bean
	    public ThreadPoolTaskExecutor executor()
	    {
	    	ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
	    	ex.setCorePoolSize(5);
	    	ex.setMaxPoolSize(15);
	    	return ex;
	    }
	     
	
}