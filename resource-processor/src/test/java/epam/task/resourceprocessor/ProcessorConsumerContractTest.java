package epam.task.resourceprocessor;

import epam.task.resourceprocessor.process.ProcessorConsumer;
import epam.task.resourceprocessor.reqres.MetadataInfo;
import epam.task.resourceprocessor.services.ResourceAPIService;
import epam.task.resourceprocessor.services.SongAPIService;
import epam.task.resourceprocessor.utils.ResourceParserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@SpringBootTest
@Disabled
@AutoConfigureStubRunner(ids = "epam.task:resource-processor:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ExtendWith(SpringExtension.class)
public class ProcessorConsumerContractTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProcessorConsumer processorConsumer;

    @MockitoBean
    private ResourceAPIService resourceAPIService;

    @MockitoBean
    private SongAPIService songAPIService;

    @MockitoBean
    private ResourceParserService resourceParserService;

    @Test
    public void shouldProcessCreatedMessage() {
        int resourceId = 123;
        byte[] resourceData = "mocked resource data".getBytes();
        ResponseEntity<byte[]> mockedResponse = new ResponseEntity<>(resourceData, HttpStatus.OK);

        Map<String, String> metadata = Map.of(
                "name", "Song Name",
                "artist", "Artist Name",
                "album", "Album Name",
                "duration", "200",
                "year", "2021"
        );

        MetadataInfo metadataInfo = MetadataInfo.builder()
                .id(String.valueOf(resourceId))
                .name("Song Name")
                .artist("Artist Name")
                .album("Album Name")
                .duration("3:20")
                .year("2021")
                .build();

        ResponseEntity<Map<String, Integer>> songServiceResponse =
                new ResponseEntity<>(Map.of("songId", 456), HttpStatus.OK);

        Mockito.when(resourceAPIService.getResource(resourceId)).thenReturn(mockedResponse);
        Mockito.when(resourceParserService.extractMetadata(Mockito.any())).thenReturn(metadata);
        Mockito.when(songAPIService.createSong(Mockito.any())).thenReturn(songServiceResponse);

        // Simulate message reception
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("resourceId", "123");
        Message message = new Message("created".getBytes(), messageProperties);

        processorConsumer.process(message);

        // Verify external service calls
        Mockito.verify(resourceAPIService).getResource(resourceId);
        Mockito.verify(resourceParserService).extractMetadata(Mockito.any());
        Mockito.verify(songAPIService).createSong(Mockito.any());
    }
}

