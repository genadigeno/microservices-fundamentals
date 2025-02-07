package epam.task.song.controller;

import epam.task.song.model.Song;
import epam.task.song.reqres.SongDto;
import epam.task.song.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createSong(@Valid @RequestBody SongDto data) {
        return ResponseEntity.ok(songService.create(data));//assertion in postman tests requires 200 status, not 201
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSong(@PathVariable int id) {
        return ResponseEntity.ok(songService.get(id));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteSong(@Valid @Length(min = 1, max = 100) @RequestParam("id") String ids) {
        return ResponseEntity.ok(songService.delete(ids));//assertion in postman tests requires 200 status, not 409
    }
}
