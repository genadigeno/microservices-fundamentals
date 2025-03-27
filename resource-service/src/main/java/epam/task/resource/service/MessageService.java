package epam.task.resource.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final RabbitTemplate rabbitTemplate;

    /**
     * test whether retry mechanism works well.
     * */
    private final ThreadLocal<Integer> retryCounter = ThreadLocal.withInitial(() -> 1);

    public MessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    @Retryable(maxAttempts = 2, retryFor = AmqpException.class)
    public void sendMessage(int resourceId) throws AmqpException {
        /*System.out.println(Thread.currentThread().getName());
        int retryCount = retryCounter.get();
        logger.info("= = = = = = = = = = RETRY NUMBER {} = = = = = = = = = =", retryCounter.get());

        if (retryCounter.get() < 2) {
            logger.error("caused a manual fail");
            retryCounter.set(retryCount + 1);
            throw new AmqpException("manual fail");
        }*/

        logger.info("sending a message...");
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeaders(Map.of("resourceId", resourceId));
        Message message = new Message("created".getBytes(StandardCharsets.UTF_8), messageProperties);
        rabbitTemplate.send("resources.exchange","*", message);
        logger.info("the message sent");

        /*retryCounter.remove();
        logger.info("* * * * * * * * * * * RETRY FINISHED * * * * * * * * * * *");*/
    }

    @Recover
    public void recover(AmqpException ex, int resourceId) {
        logger.error("Final failure after retries.");
        retryCounter.remove();
    }
}
