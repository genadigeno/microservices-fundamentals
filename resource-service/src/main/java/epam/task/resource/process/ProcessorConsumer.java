package epam.task.resource.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessorConsumer {
    private static final Logger log = LoggerFactory.getLogger(ProcessorConsumer.class);

    @RabbitListener(queues = "file-completion.queue")
    public void process(Message message) {
        try {
            log.info("Message received: {}", message.getBody());
            if ("created".equals(new String(message.getBody()))) {
                log.info("extracting resource id...");
                int resourceId = extractResourceId(message.getMessageProperties().getHeader("resourceId"));
                log.info("calling resource service with id {}", resourceId);

                //
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static int extractResourceId(Object resourceId) {
        if (resourceId instanceof String) {
            return Integer.parseInt((String) resourceId);
        }
        else if (resourceId instanceof Integer) {
            return (Integer) resourceId;
        }
        throw new IllegalArgumentException("ResourceId must be an integer");
    }

}
