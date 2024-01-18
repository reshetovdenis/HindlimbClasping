package com.denisreshetov;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    private File projectDirectory = null;

    @FXML
    private ListView<String> videoListView; // ListView to display video names

    private List<File> videoFiles = new ArrayList<>();

    private File selectedVideo = null;

    public void initialize() {
        videoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int index = videoListView.getSelectionModel().getSelectedIndex();
                File selectedFile = videoFiles.get(index);
                selectedVideo = selectedFile;
                setVideo(selectedFile);
            }
        });
    }



    private void setVideo(File file) {
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
        } else {
            mediaView.setMediaPlayer(null);
            videoName.setText("");
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

    private void updateTimeLabel(Duration duration) {
        String currentTime = new DecimalFormat("0.0").format(duration.toSeconds());
        timeLabel.setText(currentTime + " s");
    }

    private void restoreDefaults() {
        updateTimeLabel(new Duration(0));
    }

    private void initializeVideoPlayer() {
        restoreDefaults();
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                videoSlider.setValue(newValue.toSeconds());
                updateTimeLabel(newValue);
            }
        });

        videoSlider.maxProperty().bind(Bindings.createDoubleBinding(
                () -> mediaPlayer.getTotalDuration().toSeconds(),
                mediaPlayer.totalDurationProperty()));

        // Set initial seek to start position when MediaPlayer is ready
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.READY)) {
            mediaPlayer.seek(Duration.seconds(0));
        }

        videoSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                // Update timeLabel whenever the slider value changes
                Duration newDuration = Duration.seconds(newValue.doubleValue());
                updateTimeLabel(newDuration);

                if (videoSlider.isValueChanging() || videoSlider.isPressed()) {
                    // Seek in mediaPlayer only if it is in a state to handle it
                    if (mediaPlayer.getStatus().compareTo(MediaPlayer.Status.READY) >= 0 &&
                            mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                        mediaPlayer.seek(newDuration);
                    }
                }
            }
        });
        setButtonIcon(togglePlayPauseButton, "play.png");
    }

    public void handleProject(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Directory");
        projectDirectory = directoryChooser.showDialog(null);

        if (projectDirectory != null) {
            MainApp.showProjectName(projectDirectory.getName());
            System.out.println("Selected Directory: " + projectDirectory.getAbsolutePath());
        } else {
            System.out.println("No Directory Selected");
        }
    }

    public void handleOpenProject(ActionEvent actionEvent) {
    }

    @FXML
    private void handleAddVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Video Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            videoFiles.addAll(selectedFiles);
            updateVideoListView();
        }
    }
    private void updateVideoListView() {
        videoListView.getItems().clear();
        for (File file : videoFiles) {
            videoListView.getItems().add(file.getName());
        }
        if(videoFiles.size() > 0){
            videoListView.getSelectionModel().select(0);
        }
    }

    public void handleRemoveVideo(ActionEvent actionEvent) {
        if(selectedVideo != null){

            videoFiles.remove(selectedVideo);
            if(videoFiles.size() == 0){
                setVideo(null);
            }
            updateVideoListView();
        }

    }
}