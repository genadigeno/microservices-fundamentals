package epam.task.resourceprocessor.clients;

import epam.task.resourceprocessor.reqres.MetadataInfo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${song.service.name}")
public interface SongClient {
    @PostMapping("/songs")
    ResponseEntity<?> saveSong(@RequestBody MetadataInfo body);

    @DeleteMapping("/songs")
    ResponseEntity<?> deleteSong(@RequestParam("id") String ids);
}
