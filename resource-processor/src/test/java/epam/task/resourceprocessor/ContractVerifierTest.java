package epam.task.resourceprocessor;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessage;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.contract;

//@SpringJUnitConfig
public class ContractVerifierTest {

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private void triggerMessage() {
        // You can mock the behavior here
        Mockito.doNothing().when(rabbitTemplate).convertAndSend(Mockito.anyString());
    }
}