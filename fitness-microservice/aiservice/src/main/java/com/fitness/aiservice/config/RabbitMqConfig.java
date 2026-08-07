package com.fitness.aiservice.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


@Configuration
public class RabbitMqConfig
{
    @Value("${spring.rabbitmq.queue.name}")
    private String queue;


    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;


    @Bean
    public Queue activityQueue()
    {
        return new Queue(queue, true);
    }// this bean declares a queue named activity.queue in rabbitMq and the durable is true means even if the rabbitMq restarts the message will last.


    //getting the exchange and binding it to the routing key and queue so that the message can be sent to the queue via exchange and routing key.

    @Bean
    public DirectExchange activityExchange()
    {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange)
    {
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter()
    {
        return new JacksonJsonMessageConverter();
    }//converts java objects into json before sending it to rabbitMq and if we dont have this then by default the messages will be sent in raw byte arrays or instead we can also manually serialize and de-serialize it

}
