package epam.task.song.controller;

import epam.task.song.model.Song;
import epam.task.song.reqres.SongDto;
import epam.task.song.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping
    public String createSong(@Valid @RequestBody SongDto data) {
        return songService.create(data);
    }

    @GetMapping("/{id}")
    public Song getSong(@PathVariable int id) {
        return songService.get(id);
    }

    @DeleteMapping
    public List<Integer> deleteSong(@RequestParam("id") String ids) {
        return songService.delete(ids);
    }
}
