package com.denisreshetov;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.text.DecimalFormat;

public class FXMLController {
    @FXML
    private MediaView mediaView;

    @FXML
    private Slider videoSlider;

    @FXML
    private Label timeLabel;

    @FXML
    private Label videoName;

    private MediaPlayer mediaPlayer;

    @FXML
    private Button togglePlayPauseButton;

    @FXML
    private void handleOpenVideoAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Select a Video File", "*.MP4", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String path = file.toURI().toString();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media); // Initialize MediaPlayer first
            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == MediaPlayer.Status.READY) {
                    initializeVideoPlayer();
                }
            });
            mediaView.setMediaPlayer(mediaPlayer);
            videoName.setText(file.getName());
        }
    }




    @FXML
    private void handleTogglePlayPause() {
        setButtonIcon(togglePlayPauseButton, "play.png");
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                setButtonIcon(togglePlayPauseButton, "play.png");
            } else {
                mediaPlayer.play();
                setButtonIcon(togglePlayPauseButton, "pause.png");
            }
        }
    }

    private void setButtonIcon(Button button, String iconFileName) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/" + iconFileName)));
        button.setGraphic(icon);
    }

    private void initializeVideoPlayer() {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                videoSlider.setValue(newValue.toSeconds());
                String currentTime = new DecimalFormat("0.0").format(newValue.toSeconds());
                timeLabel.setText(currentTime + " s");
            }
        });

        videoSlider.maxProperty().bind(Bindings.createDoubleBinding(
                () -> mediaPlayer.getTotalDuration().toSeconds(),
                mediaPlayer.totalDurationProperty()));

        videoSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (videoSlider.isValueChanging() || videoSlider.isPressed()) {
                    if (mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO))
                        mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }

            }
        });
        setButtonIcon(togglePlayPauseButton, "play.png");
    }

}