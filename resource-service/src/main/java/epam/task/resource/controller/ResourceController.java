package epam.task.resource.controller;

import epam.task.resource.service.ResourceService;
import epam.task.resource.util.PositiveNumber;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private static final Logger logger = LogManager.getLogger(ResourceController.class);

    private final ResourceService resourceService;

    @PostMapping
    public Map<String, Integer> createResource(HttpServletRequest request) throws IOException {
        logger.info("Creating a new Resource");
        return resourceService.create(request);//validate and save to DB
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public byte[] getResource(@Valid
                          @PositiveNumber
                          @PathVariable int id){

        logger.info("Getting Resource with id {}", id);
        return resourceService.get(id);
    }

    @DeleteMapping
    public Map<String, List<Integer>> deleteResource(@Valid
                                                     @Length(min = 1, max = 100)
                                                     @RequestParam String id){
        logger.info("Deleting resource with id {}", id);
        return resourceService.delete(id);
    }
}
