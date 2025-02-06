package epam.task.resource.controller;

import epam.task.resource.service.ResourceService;
import epam.task.resource.util.PositiveNumber;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private static final Logger logger = LogManager.getLogger(ResourceController.class);

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> createResource(HttpServletRequest request) throws IOException {
        logger.info("Creating a new Resource");
        //assertion in postman tests requires 200 status, not 201
        return ResponseEntity.ok(resourceService.create(request));
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<?> getResource(@Valid @PositiveNumber @PathVariable int id){
        logger.info("Getting Resource with id {}", id);
        return ResponseEntity.ok(resourceService.get(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteResource(@Valid @Length(min = 1, max = 100) @RequestParam String id){
        logger.info("Deleting resource with id {}", id);
        //assertion in postman tests requires 200 status, not 409
        return ResponseEntity.ok(resourceService.delete(id));
    }
}
