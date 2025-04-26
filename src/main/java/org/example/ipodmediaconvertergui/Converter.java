package org.example.ipodmediaconvertergui;

import javafx.animation.RotateTransition;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;

public class Converter extends Thread {
    String command;
    String mediaDirectory;
    RotateTransition rt;
    Button convertButton;

    Converter(String command, String mediaDirectory, RotateTransition rt, Button convertButton) {
        this.command = command;
        this.mediaDirectory = mediaDirectory;
        this.rt = rt;
        this.convertButton = convertButton;
    }

    @Override
    public void run() {
        Process p = null;
        rt.play();
        convertButton.setDisable(true);
        convertButton.setText("Converting...");
        try {
            p = new ProcessBuilder("sh","-c",command).directory(new File(mediaDirectory)).start();
            String result = new String(p.getInputStream().readAllBytes());
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("convert failed");
        }
        rt.stop();
        convertButton.setDisable(false);
        convertButton.setText("Convert");
        interrupt();
    }
}
