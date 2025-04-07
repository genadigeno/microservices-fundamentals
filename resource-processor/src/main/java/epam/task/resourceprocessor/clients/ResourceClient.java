package epam.task.resourceprocessor.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${resource.service.name}")
public interface ResourceClient {

    @GetMapping("/resources/{id}")
    ResponseEntity<byte[]> getResource(@PathVariable int id);
}
