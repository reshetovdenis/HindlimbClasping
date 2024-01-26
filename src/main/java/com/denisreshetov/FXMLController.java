package com.denisreshetov;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.util.*;

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
    private Button addVideoButton;

    @FXML
    private Button removeVideoButton;

    @FXML
    private Button removeMouseButton;

    @FXML
    private TextField mouseIdInput;

    @FXML
    private Button submitMouseIdButton;

    private File projectDirectory = null;

    @FXML
    private ListView<String> videoListView;
//    @FXML
//    private ListView<String> miceListView;

    private List<File> videoFiles = new ArrayList<>();

    private Map<File, Map<String, Duration>> fileToMouseIdMap = new HashMap<>();

    private List<String> miceIds = new ArrayList<>();
    private Map<String, File> mouseIdToFileMap = new HashMap<>();


    private File selectedVideo = null;
    private String selectedMouse = null;

    @FXML
    private TableView<MouseData> miceTableView;
    @FXML
    private TableColumn<MouseData, String> mouseIdColumn;
    @FXML
    private TableColumn<MouseData, String> timeColumn;
    @FXML
    private TableColumn<MouseData, String> fileColumn;

    private Duration pendingSeekTime = null;
    private boolean allowSliderEvent = true;

    public static class MouseData {
        private final SimpleStringProperty mouseId;
        private final SimpleStringProperty time;
        private final SimpleStringProperty file;

        public MouseData(String mouseId, String time, String file) {
            this.mouseId = new SimpleStringProperty(mouseId);
            this.time = new SimpleStringProperty(time);
            this.file = new SimpleStringProperty(file);
        }

        public String getMouseId() {
            return mouseId.get();
        }

        public String getTime() {
            return time.get();
        }

        public String getFile() {
            return file.get();
        }
    }

    private String getDefaultFilePath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + ".hindlimbClaspingSettings";
    }

    private void saveProjectDirectoryPath(File projectDirectory) {
        String filePath = getDefaultFilePath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(projectDirectory.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message)
        }
    }

    private void loadProjectDirectoryPath() {
        String filePath = getDefaultFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String directoryPath = reader.readLine();
                projectDirectory = new File(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateMiceTableView() {
        miceTableView.getItems().clear();
        for (String id : miceIds.stream().sorted().toList()) {
            File correspondingVideo = mouseIdToFileMap.get(id);
            Duration time = fileToMouseIdMap.get(correspondingVideo).get(id);
            String label = id + ": " + time.toString();
            miceTableView.getItems().add(new MouseData(id, time.toString(), correspondingVideo.getName()));
        }
    }

    public void initialize() {
        loadProjectDirectoryPath();
        projectInitialize();

        videoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                pausePlaying();
                int index = videoListView.getSelectionModel().getSelectedIndex();
                File selectedFile = videoFiles.get(index);
                setVideo(selectedFile);
            }
        });
        mouseIdColumn.setCellValueFactory(cellData -> cellData.getValue().mouseId);
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().time);
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().file);

        miceTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                pausePlaying();
                MouseData selectedMouseData = newSelection;
                String mouseId = selectedMouseData.getMouseId();
                setSelectedMouse(mouseId);
                File correspondingFile = mouseIdToFileMap.get(mouseId);
                String timeString = selectedMouseData.getTime().replace(" ms", "");
                double timeInMilliseconds = Double.parseDouble(timeString);
                pendingSeekTime = Duration.millis(timeInMilliseconds);
                if (!correspondingFile.equals(selectedVideo)) {
                    videoListView.getSelectionModel().select(correspondingFile.getName());
                } else {
                    setVideo(selectedVideo);
                }
            }
        });
        if(videoFiles.size()>0) {
            setVideo(videoFiles.get(0));
        }
    }

    private void setSelectedMouse(String mouseId) {
        this.selectedMouse = mouseId;
        mouseIdInput.setText(mouseId);
    }


    private void setVideo(File file) {
        selectedVideo = file;
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
    private void handleSubmitMouseId(ActionEvent event) {
        String mouseId = mouseIdInput.getText();
        if (mouseId.isEmpty() || selectedVideo == null) {
            return;
        }
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            fileToMouseIdMap.computeIfAbsent(selectedVideo, k -> new HashMap<>()).put(mouseId, currentTime);
            if (!mouseIdToFileMap.containsKey(mouseId)) {
                mouseIdToFileMap.put(mouseId, selectedVideo);
                miceIds.add(mouseId);
            }
            mouseIdInput.clear();
            updateMiceTableView();
            updateProjectXML();
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

    private void pausePlaying() {
        setButtonIcon(togglePlayPauseButton, "play.png");
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
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

    private void seekToTime(Duration seekTime) {
        mediaPlayer.seek(seekTime);
        this.allowSliderEvent = false;
        videoSlider.setValue(seekTime.toSeconds());
        this.allowSliderEvent = true;
        updateTimeLabel(seekTime);
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
            Duration seekTime = Duration.seconds(0);
            if (pendingSeekTime != null) {
                seekTime = pendingSeekTime;
            }
            seekToTime(seekTime);
        }

        videoSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (allowSliderEvent) {
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
            }
        });
        setButtonIcon(togglePlayPauseButton, "play.png");
        videoSlider.setDisable(false);
        togglePlayPauseButton.setDisable(false);
        mouseIdInput.setDisable(false);
        submitMouseIdButton.setDisable(false);
    }

    public void handleProject(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Directory");
        projectDirectory = directoryChooser.showDialog(null);
        if (projectDirectory != null) {
            saveProjectDirectoryPath(projectDirectory);
            projectInitialize();
        }
    }

    private void projectInitialize() {
        if (projectDirectory != null) {
            MainApp.showProjectName(projectDirectory.getName());
            addVideoButton.setDisable(false);
            removeVideoButton.setDisable(false);
            removeMouseButton.setDisable(false);

            File xmlFile = new File(projectDirectory, "clasping.xml");
            if (xmlFile.exists()) {
                videoFiles.clear();
                loadVideosFromProjectXML(xmlFile);
                loadMouseIdMapFromXML(xmlFile);
                updateVideoListView();
                updateMiceTableView();
            } else {
                // Handle the case where the XML file does not exist in the directory
            }

            // Create snapshots directory
            File snapshotsDir = new File(projectDirectory, "snapshots");
            if (!snapshotsDir.exists()) {
                boolean isDirCreated = snapshotsDir.mkdir();
                if (!isDirCreated) {
                    // Handle the failure of directory creation
                }
            }
        }
    }

    private void writeXMLContent(File file) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("project");
            document.appendChild(root);

            // Iterate over videoFiles to maintain order
            for (File videoFile : videoFiles) {
                Element videoElement = document.createElement("video");
                videoElement.setAttribute("path", videoFile.getPath());

                // Check if there is mouse data for this video
                Map<String, Duration> mouseData = fileToMouseIdMap.get(videoFile);
                if (mouseData != null) {
                    for (Map.Entry<String, Duration> mouseEntry : mouseData.entrySet()) {
                        Element mouseElement = document.createElement("mouse");
                        mouseElement.setAttribute("id", mouseEntry.getKey());
                        mouseElement.setAttribute("time", String.valueOf(mouseEntry.getValue().toMillis()));
                        videoElement.appendChild(mouseElement);
                    }
                }

                root.appendChild(videoElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);

            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }


    private void updateProjectXML() {
        if (projectDirectory != null) {
            File xmlFile = new File(projectDirectory, "clasping.xml");
            writeXMLContent(xmlFile);
        }
    }

    private void loadMouseIdMapFromXML(File xmlFile) {
        if (xmlFile.exists()) {
            fileToMouseIdMap = new HashMap<>();
            try {
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(xmlFile);
                document.getDocumentElement().normalize();

                NodeList videoList = document.getElementsByTagName("video");
                for (int i = 0; i < videoList.getLength(); i++) {
                    Node videoNode = videoList.item(i);
                    if (videoNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element videoElement = (Element) videoNode;
                        String videoPath = videoElement.getAttribute("path");
                        File videoFile = new File(videoPath);

                        Map<String, Duration> mouseIdMap = new HashMap<>();
                        NodeList mouseList = videoElement.getElementsByTagName("mouse");
                        for (int j = 0; j < mouseList.getLength(); j++) {
                            Node mouseNode = mouseList.item(j);
                            if (mouseNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element mouseElement = (Element) mouseNode;
                                String id = mouseElement.getAttribute("id");
                                Duration time = Duration.millis(Double.parseDouble(mouseElement.getAttribute("time")));
                                mouseIdMap.put(id, time);
                                miceIds.add(id);
                                mouseIdToFileMap.put(id, videoFile);
                            }
                        }

                        fileToMouseIdMap.put(videoFile, mouseIdMap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions
            }
        }
    }

    private void loadVideosFromProjectXML(File xmlFile) {
        videoFiles = new ArrayList<>();
        if (xmlFile.exists()) {
            try {
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(xmlFile);
                document.getDocumentElement().normalize();

                NodeList videoList = document.getElementsByTagName("video");
                for (int i = 0; i < videoList.getLength(); i++) {
                    Node videoNode = videoList.item(i);
                    if (videoNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element videoElement = (Element) videoNode;
                        String videoName = videoElement.getAttribute("path");
                        File videoFile = new File(videoName);
                        if (videoFile.exists()) {
                            videoFiles.add(videoFile);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions related to XML reading
            }
        }
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
            updateProjectXML();
        }
    }

    private void updateVideoListView() {
        videoListView.getItems().clear();
        for (File file : videoFiles) {
            videoListView.getItems().add(file.getName());
        }
        if (videoFiles.size() > 0) {
            videoListView.getSelectionModel().select(0);
        }
    }

    public void handleRemoveVideo(ActionEvent actionEvent) {
        if (selectedVideo != null) {
            pausePlaying();
            videoFiles.remove(selectedVideo);
            if (videoFiles.size() == 0) {
                setVideo(null);
            }
            Set<String> obsoleteMiceIds = fileToMouseIdMap.get(selectedVideo).keySet();
            miceIds.removeAll(obsoleteMiceIds);
            Map<String, Duration> mouseData = fileToMouseIdMap.remove(selectedVideo);
            if (mouseData != null) {
                for (String mouseId : mouseData.keySet()) {
                    miceIds.remove(mouseId);
                    mouseIdToFileMap.remove(mouseId);
                }
            }
            updateVideoListView();
            updateMiceTableView();
            updateProjectXML();
        }
    }

    public void handleRemoveMouse(ActionEvent actionEvent) {
        if (selectedMouse != null) {
            pausePlaying();
            miceIds.remove(selectedMouse);
            File correspondingFile = mouseIdToFileMap.get(selectedMouse);
            fileToMouseIdMap.get(correspondingFile).remove(selectedMouse);
            mouseIdToFileMap.remove(selectedMouse);
            updateMiceTableView();
            updateProjectXML();
            selectedMouse = null;
            pendingSeekTime = null;
        }
    }
}