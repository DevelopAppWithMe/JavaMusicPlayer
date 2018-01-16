package advancedmusicplayer;

import advancedmusicplayer.util.CustomSliderSkin;
import advancedmusicplayer.util.SongDetail;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author Jay Prakash Pathak
 */
public class Controller implements Initializable {
    private double initX;
    private double initY;
    private String default_dictionary = "";
    private boolean isVolVisible = false;
    private boolean isSongPlaying = false;
    private boolean  is_opened = false;
    private MediaPlayer mediaPlayer;
    private File song_file;
    @FXML private AnchorPane main_pane;
    @FXML private ImageView back_img;
    @FXML private ImageView album_art;
    @FXML private ImageView play_pause;
    @FXML private Text m_title;
    @FXML private Text media_artist;
    @FXML private Text media_year;
    @FXML private Slider time_slider;
    @FXML private Rectangle vol_rect;
    @FXML private Slider vol_slider;
    @FXML private Text media_album;
    @FXML private Text media_genera;
    @FXML private Text media_composer;
    @FXML void close_window(MouseEvent event) {
        System.exit(0);
    }
    @FXML void volumeControl(MouseEvent event) {
        if (isVolVisible){
            makeVolInvisible();
        }else {
            makeVolVisible();
        }
    }
    @FXML void minimiseThisWindow(MouseEvent event) {
        Stage current_stage = (Stage)main_pane.getScene().getWindow();
        current_stage.setIconified(true);

    }
    @FXML void mouseDragged(MouseEvent event) {
        main_pane.getScene().getWindow().setX(event.getScreenX() - initX);
        main_pane.getScene().getWindow().setY(event.getScreenY() - initY);
    }
    @FXML void mousePresses(MouseEvent event) {
        initX = event.getScreenX() - main_pane.getScene().getWindow().getX();
        initY = event.getScreenY() - main_pane.getScene().getWindow().getY();

    }
    @FXML void open_file(MouseEvent event) {
        handleFileSelect();
    }
    @FXML void play_pause_btn(MouseEvent event) {
        if (is_opened){
            if(!isSongPlaying){
                Image play = new Image(Controller.class.getResourceAsStream("res/btn-pause_video.png"));
                play_pause.setImage(play);
                mediaPlayer.play();
                isSongPlaying = true;
            }else{
                Image play = new Image(Controller.class.getResourceAsStream("res/video-play-icon.png"));
                play_pause.setImage(play);
                mediaPlayer.pause();
                isSongPlaying = false;
            }
        }else {
           handleFileSelect();
        }
        is_opened = true;
    }
    @FXML void releaseBox(MouseEvent event) {
        makeVolInvisible();
    }
    @Override public void initialize(URL location, ResourceBundle resources) {
        //Hiding the volume back rectangle and slider
        makeVolInvisible();

        CustomSliderSkin skin = new CustomSliderSkin(time_slider);
        time_slider.setSkin(skin);

        main_pane.setOnDragDropped(this::handleMusicDrop);

        main_pane.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() || db.hasUrl()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

    }
    private void handleFileSelect(){
        FileChooser fileChooser = new FileChooser();
        setFileLocationAsDefault(fileChooser);
        fileChooser.setTitle("Choose a song");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Music","*.mp3");
        fileChooser.getExtensionFilters().add(filter);
        song_file = fileChooser.showOpenDialog(main_pane.getScene().getWindow());

        if (song_file != null){

            default_dictionary = song_file.getParent();

            Media media = new Media(song_file.toURI().toString());
            stopPrevisionsSong();
            isSongPlaying = true;
            playNewSong(media);

        }else {
            System.out.println("No song selected");
        }

        is_opened = true;

    }
    private void handleMusicDrop(DragEvent event) {
        System.out.println("Drag event detected");

        Dragboard db = event.getDragboard();
        String url = null;
        if (db.hasFiles()) {
            url = db.getFiles().get(0).toURI().toString();
        } else if (db.hasUrl()) {
            url = db.getUrl();
        }
        if (url != null) {
            if (url.endsWith(".mp3")){
                song_file = db.getFiles().get(0);
                stopPrevisionsSong();
                Media drop_media = new Media(url);
                playNewSong(drop_media);
                isSongPlaying = true;
                is_opened = true;
            }else{
                System.out.println("Invalid file detected");
                return;
            }
        }
        event.setDropCompleted(url != null);
        event.consume();
    }
    private void stopPrevisionsSong() {
        if (isSongPlaying) mediaPlayer.dispose();
    }
    private void setFileLocationAsDefault(FileChooser fileChooser) {
        if (!default_dictionary.equals("")) fileChooser.setInitialDirectory(new File(default_dictionary));
    }
    private void makeVolVisible(){
        vol_slider.setVisible(true);
        vol_rect.setVisible(true);
        isVolVisible = true;
    }
    private void makeVolInvisible(){
        vol_slider.setVisible(false);
        vol_rect.setVisible(false);
        isVolVisible = false;
    }
    private void playNewSong(Media media){

        mediaPlayer = new MediaPlayer(media);

        setSongDetails();

        mediaPlayer.play();
    }
    private void setSongDetails() {

        try {

            SongDetail detail = new SongDetail(song_file);

            if (detail.getAlbum_art() != null){
                album_art.setImage(detail.getAlbum_art());
                back_img.setImage(detail.getAlbum_art());
            }else {
                album_art.setImage(new Image(Controller.class.getResourceAsStream("res/placeholder.png")));
                back_img.setImage(new Image(Controller.class.getResourceAsStream("res/newback.jpg")));
            }

            media_artist.setText(detail.getAlbum_artist());
            media_composer.setText(detail.getAlbum_composer());
            media_album.setText(detail.getAlbum_album());
            media_year.setText(detail.getAlbum_year());


            StringBuilder title = new StringBuilder();
            String real = detail.getAlbum_title();
            if (real.length() > 25){
                char[] chars = real.toCharArray();
                for(int i =0 ; i<=25 ; i++){
                    title.append(chars[i]);
                }
                title.append(" ...");
            }else {
                title = new StringBuilder(real);
            }
            m_title.setText(title.toString());
            media_genera.setText(detail.getAlbum_genera());

            handleTimeAndVolumeSlider();

        } catch (TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException | IOException e) {
            e.printStackTrace();
        }

    }
    private void handleTimeAndVolumeSlider() {
        time_slider.setValue(0.0);
        mediaPlayer.currentTimeProperty().addListener(ov -> updateValues());
        time_slider.valueProperty().addListener(ov -> {

            if(time_slider.isPressed()){
                mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(time_slider.getValue()/100));
            }

        });
        vol_slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (vol_slider.isValueChanging()) {
                mediaPlayer.setVolume(vol_slider.getValue() / 100.0);
            }
        });
    }
    private void updateValues(){ Platform.runLater(() -> time_slider.setValue(mediaPlayer.getCurrentTime().toMillis()/mediaPlayer.getTotalDuration().toMillis()*100)); }
}
