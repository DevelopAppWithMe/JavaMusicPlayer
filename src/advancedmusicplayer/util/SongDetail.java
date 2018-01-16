package advancedmusicplayer.util;

import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SongDetail {

    private String album_title;
    private Image album_art;
    private String album_album;
    private String album_artist;
    private String album_year;
    private String album_composer;
    private String album_genera;

    public String getAlbum_genera() {
        return album_genera;
    }

    public SongDetail(File file) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTag();
        AudioHeader audioHeader = audioFile.getAudioHeader();

        try {
            album_year = tag.getFirst(FieldKey.YEAR);
            album_album = tag.getFirst(FieldKey.ALBUM);
            album_artist = tag.getFirst(FieldKey.ARTIST);
            album_composer = tag.getFirst(FieldKey.COMPOSER);
            album_title = tag.getFirst(FieldKey.TITLE);
            album_genera = tag.getFirst(FieldKey.GENRE);

            byte[] bytes = tag.getFirstArtwork().getBinaryData();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            Image image = new Image(in, 300, 300, true, true);

            if (image.isError()){
                album_art = null;
            }else {
                album_art = image;
            }

        }catch (Exception c){
            System.out.println(c.getMessage());
        }
    }

    public String getAlbum_title() {
        return album_title;
    }

    public Image getAlbum_art() {
        return album_art;
    }

    public String getAlbum_album() {
        return album_album;
    }

    public String getAlbum_artist() {
        return album_artist;
    }

    public String getAlbum_year() {
        return album_year;
    }

    public String getAlbum_composer() {
        return album_composer;
    }
}
