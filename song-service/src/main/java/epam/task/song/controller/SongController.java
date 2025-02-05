package epam.task.song.controller;

import epam.task.song.model.Song;
import epam.task.song.reqres.SongDto;
import epam.task.song.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping
    public Map<String, Integer> createSong(@Valid @RequestBody SongDto data) {
        return songService.create(data);
    }

    @GetMapping("/{id}")
    public Song getSong(@PathVariable int id) {
        return songService.get(id);
    }

    @DeleteMapping
    public Map<String, List<Integer>> deleteSong(@Valid
                                                 @Length(min = 1, max = 100)
                                                 @RequestParam("id") String ids) {
        return songService.delete(ids);
    }
}
