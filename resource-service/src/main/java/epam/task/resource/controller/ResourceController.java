package epam.task.resource.controller;

import epam.task.resource.reqres.*;
import epam.task.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private static final Logger logger = LogManager.getLogger(ResourceController.class);

    private final ResourceService resourceService;

    @PostMapping
    public CreationInfo createSong(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("Creating a new song");
        int id = resourceService.create(file);//validate and save to DB
        return CreationInfo.builder()
                .id(id)
                .build();
    }

    @GetMapping("/{id}")
    public ResourceData getSong(@PathVariable int id){
        logger.info("Getting song with id {}", id);
        return resourceService.get(id);
    }

    @DeleteMapping
    public List<Integer> deleteResource(@RequestParam String id){
        logger.info("Deleting resource with id {}", id);
        return resourceService.delete(id);
    }
}
