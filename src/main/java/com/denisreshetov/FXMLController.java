package com.denisreshetov;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FXMLController {
    private Parent originalMediaViewParent;
    private static final Integer MAX_FRAME = 13;
    @FXML
    private MediaView mediaView;
    @FXML
    private VBox mouseInput;
    @FXML
    private MediaView scoringView;

    @FXML
    private VBox player;

    @FXML
    private Slider videoSlider;

    @FXML
    private Label timeLabel;

    @FXML
    private HBox frameButtons;

    @FXML
    private Label videoName;

    private MediaPlayer mediaPlayer;
    private MediaPlayer scoringPlayer;

    @FXML
    private Button togglePlayPauseButton;

    @FXML
    private Button toggleFullScreenButton;

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

    @FXML
    private Button leftRetracted;

    @FXML
    private Button leftOK;

    @FXML
    private Button rightRetracted;

    @FXML
    private Button rightOK;

    @FXML
    private Button naFrame;

    private File projectDirectory = null;

    @FXML
    private ListView<String> videoListView;
//    @FXML
//    private ListView<String> miceListView;

    private List<File> videoFiles = new ArrayList<>();

    private Map<File, Map<String, Duration>> fileToMouseIdMap = new HashMap<>();

    private List<String> miceIds = new ArrayList<>();
    private Map<String, File> mouseIdToFileMap = new HashMap<>();
    private Map<String, Map<Integer, StateOfLegs>> mouseToScoring = new HashMap<>();
    private Integer currentFrame = 0;

    private boolean isFullScreen = false;

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

    private Integer selectedVideoIndex = 0;

    Stage originalStage = null;
    Scene originalScene = null;
    private Integer currentMouseIndex = null;

    @FXML
    private void handleMediaViewFullScreenToggle() {
        if (!isFullScreen) {
            originalStage = (Stage) player.getScene().getWindow();
            originalScene = originalStage.getScene();
            isFullScreen = true;
            setButtonIcon(toggleFullScreenButton, "compress.png");
            // Save the original parent and remove the MediaView from its current location
            originalMediaViewParent = player.getParent();
            ((Pane) originalMediaViewParent).getChildren().remove(player);
            mouseInput.setVisible(false);

            // Create a new layout with just the MediaView
            StackPane fullscreenPane = new StackPane(player);
            Scene fullscreenScene = new Scene(fullscreenPane);

            // Bind MediaView properties to fill the screen while preserving aspect ratio
            mediaView.fitWidthProperty().bind(fullscreenScene.widthProperty());
            mediaView.fitHeightProperty().bind(fullscreenScene.heightProperty());
            mediaView.setPreserveRatio(true);

            // Set the new scene and go fullscreen
            originalStage.setScene(fullscreenScene);
            originalStage.setFullScreen(true);

            // Exit fullscreen on ESC
            fullscreenScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    exitMediaViewFullScreen(originalStage, originalScene);
                }
            });
        } else {
            exitMediaViewFullScreen(originalStage, originalScene);
        }
    }

    private void exitMediaViewFullScreen(Stage stage, Scene originalScene) {
        stage.setFullScreen(false);
        isFullScreen = false;
        // Unbind MediaView properties and reset to original/preferred size
        mediaView.fitWidthProperty().unbind();
        mediaView.fitHeightProperty().unbind();
        mediaView.setFitHeight(500); // Reset to original width
        mediaView.setPreserveRatio(true);

        // Restore the original scene
        stage.setScene(originalScene);

        // Re-add the MediaView to its original parent
        if (originalMediaViewParent instanceof Pane) {
            Pane parentPane = (Pane) originalMediaViewParent;
            parentPane.getChildren().add(0, player);
        }
        mouseInput.setVisible(true);
        setButtonIcon(toggleFullScreenButton, "expand.png");
    }


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
        if(currentMouseIndex != null){
            miceTableView.getSelectionModel().select(currentMouseIndex);
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
                if (
                        !(fileToMouseIdMap.containsKey(selectedFile)
                                && fileToMouseIdMap.get(selectedFile).containsKey(selectedMouse))
                ) {
                    miceTableView.getSelectionModel().clearSelection();
                    selectedMouse = null;
                    mouseIdInput.clear();
                    refreshScoringTab();
                }
            }
        });
        mouseIdColumn.setCellValueFactory(cellData -> cellData.getValue().mouseId);
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().time);
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().file);

        miceTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentMouseIndex = miceTableView.getSelectionModel().getSelectedIndex();
                pausePlaying();
                MouseData selectedMouseData = newSelection;
                String mouseId = selectedMouseData.getMouseId();
                setSelectedMouse(mouseId);
                File correspondingFile = mouseIdToFileMap.get(mouseId);
                pendingSeekTime = getTimeOfMouse(mouseId);
                if (!correspondingFile.equals(selectedVideo)) {
                    videoListView.getSelectionModel().select(correspondingFile.getName());
                } else {
                    setVideo(selectedVideo);
                }
                updateProjectXML();
            }
        });
        if (videoFiles.size() > 0) {
            setVideo(videoFiles.get(0));
        }
    }

    private Duration getTimeOfMouse(String mouseId) {
        File correspondingFile = mouseIdToFileMap.get(mouseId);
        return fileToMouseIdMap.get(correspondingFile).get(mouseId);
    }

    private void setSelectedMouse(String mouseId) {
        this.selectedMouse = mouseId;
        mouseIdInput.setText(mouseId);
    }

    private void refreshScoringTab() {
        frameButtons.getChildren().clear();
        Button[] legButtons = {leftOK, leftRetracted, rightOK, rightRetracted, naFrame};
        scoringView.setVisible(false);
        for (Button b : legButtons) {
            b.setVisible(false);
            b.setDisable(true);
        }
        if (selectedMouse != null) {
            scoringView.setVisible(true);
            for (Button b : legButtons) {
                b.setVisible(true);
                b.setDisable(false);
            }
            Duration totalDuration = scoringPlayer.getTotalDuration();
            Label experimentSec = new Label("Test time:");
            frameButtons.getChildren().add(experimentSec);
            for (int i = 0; i <= MAX_FRAME; i++) {
                String buttonName = "-|-\n" + i;
                if (mouseToScoring.get(selectedMouse) != null &&
                        mouseToScoring.get(selectedMouse).containsKey(i)) {
                    StateOfLegs state = mouseToScoring.get(selectedMouse).get(i);
                    buttonName = StateOfLegs.text(state) + "\n" + i;
                }
                Button button = new Button(buttonName);
                button.setId("frameButton" + i);
                if (currentFrame == i) {
                    button.getStyleClass().add("current-frame-button");
                } else {
                    button.getStyleClass().add("frame-button");
                }
                Duration time = getTimeOfMouse(selectedMouse).add(Duration.seconds(i));
                if (totalDuration.greaterThan(time)) {
                    int finalI = i;
                    button.setOnAction(event -> {
                        moveToFrame(finalI);
                    });
                    frameButtons.getChildren().add(button);
                }
            }
        }
    }

    private void moveToFrame(int frame) {
        if (selectedMouse != null) {
            Duration time = getTimeOfMouse(selectedMouse).add(Duration.seconds(frame));
            currentFrame = frame;
            seekToTime(time);
            scoringSeekToTime(time);
            refreshScoringTab();
        }
    }

    private void setVideo(File file) {
        selectedVideo = file;
        currentFrame = 0;
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
            //---
            scoringPlayer = new MediaPlayer(media); // Initialize MediaPlayer first
            scoringPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == MediaPlayer.Status.READY) {
                    initializeScoringPlayer();
                }
            });
            scoringView.setMediaPlayer(scoringPlayer);
        } else {
            mediaView.setMediaPlayer(null);
            scoringView.setMediaPlayer(null);
            videoName.setText("");
        }
        updateProjectXML();
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
            selectedMouse = null;
            updateMiceTableView();
            updateProjectXML();
            refreshScoringTab();
        }
    }

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
        videoSlider.setValue(seekTime.toSeconds());
        updateTimeLabel(seekTime);
    }

    private void scoringSeekToTime(Duration seekTime) {
        scoringPlayer.seek(seekTime);
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
            if (pendingSeekTime != null && selectedMouse != null) {
                seekTime = pendingSeekTime;
            }
            seekToTime(seekTime);
        }

        videoSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
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
        videoSlider.setDisable(false);
        togglePlayPauseButton.setDisable(false);
        mouseIdInput.setDisable(false);
        submitMouseIdButton.setDisable(false);

        Stage stage = (Stage) player.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
                handleTogglePlayPause();
            }
        });
        togglePlayPauseButton.setOnAction(event -> {
            handleTogglePlayPause();
        });
    }

    private void initializeScoringPlayer() {
        if (scoringPlayer.getStatus().equals(MediaPlayer.Status.READY)) {
            Duration seekTime = Duration.seconds(0);
            if (pendingSeekTime != null && selectedMouse != null) {
                seekTime = pendingSeekTime;
            }
            scoringSeekToTime(seekTime);
            refreshScoringTab();
        }
    }

    @FXML
    public void handleProject(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Directory");
        projectDirectory = directoryChooser.showDialog(null);
        if (projectDirectory != null) {
            saveProjectDirectoryPath(projectDirectory);
            projectInitialize();
        }
    }

    @FXML
    private void handleExport(ActionEvent actionEvent) {
        Map<String, Integer> scores = new HashMap<>();
        for (String mouse : miceIds) {
            Map<Integer, StateOfLegs> states = mouseToScoring.get(mouse);
            Integer score = Scoring.getScore(states);
            scores.put(mouse, score);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Export File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            exportScoresToExcel(scores, file);
        }
    }

    private void exportScoresToExcel(Map<String, Integer> scores, File file) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Scores");

            int rowNum = 0;
            // Creating header row
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Mouse ID");
            headerRow.createCell(1).setCellValue("Score");

            // Filling data
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                Integer score = entry.getValue();
                if (score != null) {
                    row.createCell(1).setCellValue(score);
                } else {
                    row.createCell(1).setCellValue("NA");
                }
            }
            // Auto size columns
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }

    private void projectInitialize() {
        MainApp.showProjectName("");
        if (projectDirectory != null) {
            MainApp.showProjectName(projectDirectory.getName());
            addVideoButton.setDisable(false);
            removeVideoButton.setDisable(false);
            removeMouseButton.setDisable(false);

            File xmlFile = new File(projectDirectory, "clasping.xml");
            if (xmlFile.exists()) {
                videoFiles.clear();
                loadDataFromProjectXML(xmlFile);
                updateVideoListView();
                updateMiceTableView();
            } else {
                // Handle the case where the XML file does not exist in the directory
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
                if(videoFile.equals(selectedVideo)){
                    videoElement.setAttribute("isSelected", String.valueOf(true));
                }
                // Check if there is mouse data for this video
                Map<String, Duration> mouseData = fileToMouseIdMap.get(videoFile);
                if (mouseData != null) {
                    for (Map.Entry<String, Duration> mouseEntry : mouseData.entrySet()) {
                        String mouseId = mouseEntry.getKey();
                        Element mouseElement = document.createElement("mouse");
                        mouseElement.setAttribute("id", mouseId);
                        mouseElement.setAttribute("time", String.valueOf(mouseEntry.getValue().toMillis()));
                        if(mouseId.equals(selectedMouse)){
                            mouseElement.setAttribute("isSelected", String.valueOf(true));
                        }
                        videoElement.appendChild(mouseElement);
                        if (mouseToScoring.containsKey(mouseId)) {
                            for (Map.Entry<Integer, StateOfLegs> experiment : mouseToScoring.get(mouseId).entrySet()) {
                                Element frameElement = document.createElement("frame");
                                frameElement.setAttribute("num", experiment.getKey() + "");
                                frameElement.setAttribute("state", StateOfLegs.text(experiment.getValue()));
                                mouseElement.appendChild(frameElement);
                            }
                        }
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

    private void loadDataFromProjectXML(File xmlFile) {
        videoFiles = new ArrayList<>();
        if (xmlFile.exists()) {
            try {
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(xmlFile);
                document.getDocumentElement().normalize();

                NodeList videoList = document.getElementsByTagName("video");
                int videoIndex = 0;
                for (int i = 0; i < videoList.getLength(); i++) {
                    Node videoNode = videoList.item(i);
                    if (videoNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element videoElement = (Element) videoNode;
                        String videoName = videoElement.getAttribute("path");
                        File videoFile = new File(videoName);
                        if (videoFile.exists()) {
                            videoFiles.add(videoFile);
                            if(videoElement.hasAttribute("isSelected")){
                                selectedVideo = videoFile;
                                selectedVideoIndex = videoIndex;
                            }
                        }
                        videoIndex++;
                    }
                }
                for (int i = 0; i < videoList.getLength(); i++) {
                    Node videoNode = videoList.item(i);
                    if (videoNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element videoElement = (Element) videoNode;
                        String videoPath = videoElement.getAttribute("path");
                        File videoFile = new File(videoPath);

                        Map<String, Duration> mouseIdMap = new HashMap<>();
                        NodeList mouseList = videoElement.getElementsByTagName("mouse");
                        int mouseIndex = 0;
                        for (int j = 0; j < mouseList.getLength(); j++) {
                            Node mouseNode = mouseList.item(j);
                            if (mouseNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element mouseElement = (Element) mouseNode;
                                String id = mouseElement.getAttribute("id");
                                Duration time = Duration.millis(Double.parseDouble(mouseElement.getAttribute("time")));
                                mouseIdMap.put(id, time);
                                miceIds.add(id);
                                mouseIdToFileMap.put(id, videoFile);
                                if(mouseElement.hasAttribute("isSelected")){
                                    currentMouseIndex = mouseIndex;
                                }
                                //
                                NodeList frameList = mouseElement.getElementsByTagName("frame");
                                for (int k = 0; k < frameList.getLength(); k++) {
                                    Node frameNode = frameList.item(k);
                                    if (frameNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element frameElement = (Element) frameNode;
                                        String num = frameElement.getAttribute("num");
                                        String state = frameElement.getAttribute("state");
                                        StateOfLegs stateOfLegs = StateOfLegs.fromText(state);
                                        Map<Integer, StateOfLegs> mouseStates = mouseToScoring
                                                .computeIfAbsent(id, s -> new HashMap<>());
                                        mouseStates.put(Integer.valueOf(num), stateOfLegs);
                                    }
                                }
                                mouseIndex++;
                            }
                        }

                        fileToMouseIdMap.put(videoFile, mouseIdMap);
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
        if(selectedVideoIndex != null){
            videoListView.getSelectionModel().select(selectedVideoIndex);
        } else if (videoFiles.size() > 0) {
            videoListView.getSelectionModel().select(0);
            selectedVideoIndex = 0;
        }
    }

    public void handleRemoveVideo(ActionEvent actionEvent) {
        if (selectedVideo != null) {
            pausePlaying();
            videoFiles.remove(selectedVideo);
            Set<String> obsoleteMiceIds = fileToMouseIdMap.get(selectedVideo).keySet();
            miceIds.removeAll(obsoleteMiceIds);
            Map<String, Duration> mouseData = fileToMouseIdMap.remove(selectedVideo);
            if (mouseData != null) {
                for (String mouseId : mouseData.keySet()) {
                    miceIds.remove(mouseId);
                    mouseIdToFileMap.remove(mouseId);
                }
            }
            if (videoFiles.size() == 0) {
                setVideo(null);
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

    private int getCurrentLastFrameIndex(){
        return frameButtons.getChildren().size() - 2;
    }

    private void moveToNextFrame(){
        if(currentFrame < getCurrentLastFrameIndex()){
            moveToFrame(++currentFrame);
        } else {
            //Move to next mouse
            currentFrame = 0;
            int currentMouseIndex = miceTableView.getSelectionModel().getSelectedIndex();
            if(currentMouseIndex < miceTableView.getItems().size()){
                miceTableView.getSelectionModel().select(++currentMouseIndex);
            }
        }

    }
    public void setLegState(boolean isLeftLeg, boolean isLegOk) {
        StateOfLegs legState = mouseToScoring
                .computeIfAbsent(selectedMouse, k -> new HashMap<>())
                .computeIfAbsent(currentFrame, k -> new StateOfLegs());
        if (legState == null) {//When was initially marked as NA
            legState = new StateOfLegs();
        }
        if (isLeftLeg) {
            legState.leftOK = isLegOk;
        } else {
            legState.rightOK = isLegOk;
        }
        if(!legState.toString().matches(".*-.*")){
            moveToNextFrame();
        }
        refreshScoringTab();
        updateProjectXML();
    }

    public void handleLeftRetracted(ActionEvent actionEvent) {
        setLegState(true, false);
    }

    public void handleLeftOK(ActionEvent actionEvent) {
        setLegState(true, true);
    }

    public void handleRightRetracted(ActionEvent actionEvent) {
        setLegState(false, false);
    }

    public void handleRightOK(ActionEvent actionEvent) {
        setLegState(false, true);
    }

    public void handleNAFrame(ActionEvent actionEvent) {
        Map<Integer, StateOfLegs> mouseStates = mouseToScoring
                .computeIfAbsent(selectedMouse, k -> new HashMap<>());
        mouseStates.put(currentFrame, null);
        moveToNextFrame();
        refreshScoringTab();
        updateProjectXML();
    }
}