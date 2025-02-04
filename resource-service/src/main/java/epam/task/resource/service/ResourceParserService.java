package epam.task.resource.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceParserService {

    public Map<String, String> extractMetadata(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            Metadata metadata = new Metadata();
            ContentHandler handler = new BodyContentHandler();
            Mp3Parser parser = new Mp3Parser();
            ParseContext context = new ParseContext();

            parser.parse(input, handler, metadata, context);
            HashMap<String, String> map = new HashMap<>();
            map.put("name", metadata.get("dc:title"));
            map.put("artist", metadata.get("xmpDM:artist"));
            map.put("album", metadata.get("xmpDM:album"));
            map.put("year", metadata.get("xmpDM:releaseDate"));
            map.put("duration", metadata.get("xmpDM:duration"));
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing MP3 file", e);
        }
    }
}
