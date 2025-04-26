package org.example.ipodmediaconvertergui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.io.File;

public class HelloController {

    @FXML private TextField mediaDirectoryField;
    @FXML private TextField ffmpegPathField;
    @FXML private Label osLabelA;
    @FXML private Label osLabelS;
    @FXML private ImageView convertIcon;
    @FXML private ChoiceBox<String> sourceChoice;
    @FXML private ChoiceBox<String> targetChoice;
    @FXML private CheckBox deleteCheck;
    @FXML private Label ffmpegStatusLabel;
    @FXML private Button convertButton;

    String mediaDirectory;
    String detectedOS;
    boolean ffmpegWorks;
    boolean converting = false;

    String[] inputFormats = {"FLAC"};
    String[] outputFormats = {"ALAC (Apple Lossless)"};

    @FXML private void mediaDirectoryPicker(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            mediaDirectoryField.setText(selectedDirectory.getAbsolutePath());
            mediaDirectory = selectedDirectory.getAbsolutePath();
        }
    }

    @FXML private void ffmpegPicker(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            ffmpegPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private String generateCommand() {
        StringBuilder command = new StringBuilder();
        command.append("for i in *.");
        command.append(sourceChoice.getValue().toLowerCase());
        command.append("; do echo $i; ");
        command.append("ffmpeg -i \"$i\" -y -v 0 -vcodec copy -acodec alac \"${i%.");
        command.append(sourceChoice.getValue().toLowerCase());
        command.append("}\".m4a; done");

        return command.toString();
    }

    @FXML private void convert(ActionEvent event) throws IOException {
        //animation
        RotateTransition rt = new RotateTransition(Duration.millis(3000), convertIcon);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);

        if (!ffmpegWorks) {
            alert("FFmpeg is currently not set up properly. Please go to the settings tab and locate it.");
        } else if (sourceChoice.getValue() == null || sourceChoice.getValue().isEmpty()) {
            alert("Please select a source format.");
        } else if (mediaDirectoryField.getText() == null || mediaDirectoryField.getText().isEmpty()) {
            alert("Please select the directory of your " + sourceChoice.getValue() + "s.");
        } else {
//            convertButton.setText("Converting...");
//            convertButton.setDisable(true);

            String command = generateCommand();
            System.out.println("Running: " + command);
            Converter c = new Converter(command, mediaDirectory,rt,convertButton);
            c.start();

        }
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("iPod Media Converter GUI");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void initialize() throws IOException {
        //Check OS
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            alert("Sorry, but this application does not currently support Windows. Please look forward to a future release.");
            detectedOS = "windows";
            System.exit(0);
        } else if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            osLabelA.setText("Detected Platform: macOS");
            osLabelS.setText("Detected Platform: macOS");
            detectedOS = "macOS";
        } else if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
            osLabelA.setText("Detected Platform: linux");
            osLabelS.setText("Detected Platform: linux");
            detectedOS = "linux";
            alert("This application should work with Linux systems, but it has not yet been tested. Use at your own risk.");
        }

        //Populate Dropdowns
        sourceChoice.getItems().addAll(inputFormats);
        targetChoice.getItems().addAll(outputFormats);
        targetChoice.getSelectionModel().selectFirst();

        //check for ffmpeg
        ffmpegStatusLabel.setText("FFmpeg status: Unknown");
        try {
            Process p = new ProcessBuilder("ffmpeg","-version").start();
            String result = new String(p.getInputStream().readAllBytes());
            System.out.println(result);
            if (result.startsWith("ffmpeg")) {
                System.out.println("ffmpeg found!");
                ffmpegStatusLabel.setText("FFmpeg status: All good.");
                ffmpegWorks = true;
            }
        } catch (IOException e) { //ToDo: fix this so that it alerts even without i/o exception
            alert("FFmpeg not found. Please select where it is or download it via the settings tab.");
            ffmpegStatusLabel.setText("FFmpeg status: Not found.");
        }

        //convertIcon.setOpacity(0.0);
    }

}