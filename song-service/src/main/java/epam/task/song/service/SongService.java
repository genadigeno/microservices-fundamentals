package epam.task.song.service;

import epam.task.song.model.Song;
import epam.task.song.repository.SongRepository;
import epam.task.song.reqres.SongDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SongService.class);

    private final SongRepository songRepository;

    @Transactional(timeout = 10_000, rollbackFor = Exception.class)
    public List<Integer> delete(String ids) {
        List<Integer> idList = songRepository.getAllIdsByIds(toIntArray(ids));
        LOGGER.info("Deleting resource(s)");

        //cascade deletion
        for (Integer id : idList) {
            ///1.   delete from db
            songRepository.deleteById(id);
        }

        return idList;
    }

    public String create(SongDto data) {
        Song song = new Song();
        song.setId(Integer.parseInt(data.getId()));
        song.setName(data.getName());
        song.setArtist(data.getArtist());
        song.setAlbum(data.getAlbum());
        song.setDuration(data.getDuration());
        song.setYear(data.getYear());
        Song saved = songRepository.save(song);
        return String.valueOf(saved.getId());
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

    public Song get(int id) {
        return songRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
