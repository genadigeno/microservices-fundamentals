package epam.task.resource.service;

import epam.task.resource.exception.FileFormatException;
import epam.task.resource.model.SongResource;
import epam.task.resource.repository.ResourceRepository;
import epam.task.resource.reqres.MetadataInfo;
import epam.task.resource.util.CustomMultipartFile;
import epam.task.resource.util.FormatUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    private final Tika tika = new Tika();

    private final ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;
    private final ResourceParserService resourceParserService;

    @Value("${song.service.url}")
    private String songServiceUrl;

    @Transactional(timeout = 10_000, rollbackFor = Exception.class)
    public Map<String, Integer> create(HttpServletRequest request) throws IOException {

        byte[] fileBytes = StreamUtils.copyToByteArray(request.getInputStream());
        MultipartFile multipartFile = new CustomMultipartFile("song", fileBytes);

        if (!SongResource.RESOURCE_CONTENT_TYPE.equals(tika.detect(fileBytes))) {
            throw new FileFormatException("file is not mp3");
        }
        logger.info("Creating resource");

        SongResource resource = new SongResource();
        resource.setData(fileBytes);
        SongResource saved = resourceRepository.save(resource);

        //Apache Tika - extract metadata from a file
        Map<String, String> metadata = resourceParserService.extractMetadata(multipartFile);

        MetadataInfo info = MetadataInfo.builder()
                .id(String.valueOf(saved.getId()))
                .name(metadata.get("name"))
                .artist(metadata.get("artist"))
                .album(metadata.get("album"))
                .duration(
                        FormatUtils.formatDuration(
                                Double.parseDouble(metadata.get("duration"))
                        )
                )
                .year(metadata.get("year"))
                .build();

        //send to song service
        restTemplate.exchange(songServiceUrl, HttpMethod.POST, new HttpEntity<>(info), String.class);

        return Map.of("id", saved.getId());
    }

    public byte[] get(int id) {
        logger.info("Retrieving resource");
        SongResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("resource not found"));

        return resource.getData();
    }

    @Transactional(timeout = 10_000, rollbackFor = Exception.class)
    public Map<String, List<Integer>> delete(String ids) {
        ///get the ids that exist in resource DB
        List<Integer> idList = resourceRepository.getAllIdaByIds(toIntArray(ids));

        if (!idList.isEmpty()) {
            logger.info("Deleting resource(s)");

            //cascade deletion
            for (Integer id : idList) {
                ///1.   delete from db
                resourceRepository.deleteById(id);
            }

            ///2.   then call song REST API to remove a song from there.
            ///     if it fails we will throw an exception that rolls back our changes
            ids = idList.stream().map(String::valueOf).collect(Collectors.joining(","));
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(songServiceUrl+"?id="+ids, HttpMethod.DELETE,null, String.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(responseEntity.getStatusCode().value() + " " + responseEntity.getBody());
            }

            /* *
             * Note:
             *  It will be better approach to invoke song api at first, because it is external service and our app does
             *  not control it; thus if song api isn't available for some period or has a bug then we load our database
             *  ineffectively (rolling back changes frequently). We had to implement SAGA pattern, but I left it as it
             *  is for a simplicity.
             * */
        }

        return Map.of("ids", idList);
    }

    private static List<Integer> toIntArray(String ids) {
        String[] nums = ids.split(",");
        //validate numeric value
        boolean allNumeric = Arrays.stream(nums).allMatch(id -> id.matches("^\\d+$"));
        if (!allNumeric) {
            throw new IllegalArgumentException("non-numeric id not allowed");
        }

        return Arrays.stream(nums)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
