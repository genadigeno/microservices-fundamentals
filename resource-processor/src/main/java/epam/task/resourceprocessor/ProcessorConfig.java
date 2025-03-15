package epam.task.resourceprocessor;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProcessorConfig {

    /*@Bean
    public SimpleRabbitListenerContainerFactory myFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(new MessageConverter() {
            @Override
            public Message toMessage(Object obj, MessageProperties properties) throws MessageConversionException {
                return null;
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                return null;
            }
        });
        return factory;
    }*/

    @Bean
    public Queue queue() {
        return new Queue("resources.queue", false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("resources.exchange");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("*");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
