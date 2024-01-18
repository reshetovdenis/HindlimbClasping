package com.denisreshetov;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;

public class FXMLController {
    @FXML
    private MediaView mediaView;

    @FXML
    private Slider videoSlider;

    private MediaPlayer mediaPlayer;

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
            mediaPlayer.play();
        }
    }


    @FXML
    private void handlePlayAction() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @FXML
    private void handlePauseAction() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void initializeVideoPlayer() {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                videoSlider.setValue(newValue.toSeconds());
            }
        });

        videoSlider.maxProperty().bind(Bindings.createDoubleBinding(
                () -> mediaPlayer.getTotalDuration().toSeconds(),
                mediaPlayer.totalDurationProperty()));

        videoSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (videoSlider.isValueChanging()) {
                    if (mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO))
                        mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });
    }

}