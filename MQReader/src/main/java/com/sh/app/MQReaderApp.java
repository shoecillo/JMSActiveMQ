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
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import com.sh.listener.MsgListenerQueue;
import com.sh.listener.MsgListenerTopic;

@SpringBootApplication
@ComponentScan(basePackages="com.sh")
public class MQReaderApp 
{
	
	@Value("${jms.broker.url}")
	private String broker;
    
	@Value("${jms.topic.name}")
    private String topicName;
	
	@Value("${jms.queue.name}")
    private String queueName;
	
	private static Logger LOGGER = LoggerFactory.getLogger(MQReaderApp.class);

	public static void main(String[] args) 
	{
		SpringApplication.run(MQReaderApp.class, args);
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
     * Topic listener container, used for invoking messageReceiver.onMessage on message reception.
     */
    @Bean(name = "jmsTopic")
    public SimpleMessageListenerContainer getTopic(MessageListenerAdapter adapterTopic){
    	LOGGER.debug("<<<<<< Loading Listener topic");
    	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setDestinationName(topicName);
        container.setMessageListener(adapterTopic);
        container.setPubSubDomain(true);
        LOGGER.debug("Listener topic loaded >>>>>>>>>");
        
        return container;
    }
    
    /*
     * Queue listener container, used for invoking messageReceiver.onMessage on message reception.
     */
    @Bean(name = "jmsQueue")
    public SimpleMessageListenerContainer getQueue(MessageListenerAdapter adapterQueue){
    	LOGGER.debug("<<<<<< Loading Listener Queue");
    	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setDestinationName(queueName);
        container.setMessageListener(adapterQueue);
        container.setPubSubDomain(false);
        LOGGER.debug("Listener Queue loaded >>>>>>>");
        
        return container;
    }
    
    @Bean(name = "adapterTopic")
    public MessageListenerAdapter adapterTopic(MsgListenerTopic topic)
    {
    	return new MessageListenerAdapter(topic) 
    	{{
    		setDefaultListenerMethod("onMessage");
    		setMessageConverter(new SimpleMessageConverter());
    	}};
    }
    
    @Bean(name = "adapterQueue")
    public MessageListenerAdapter adapterQueue(MsgListenerQueue queue)
    {
    	return new MessageListenerAdapter(queue) 
    	{{
    		setDefaultListenerMethod("onMessage");
    		setMessageConverter(new SimpleMessageConverter());
    	}};
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
    
     
    @Bean
    public MessageConverter converter(){
        return new SimpleMessageConverter();
    }

}
