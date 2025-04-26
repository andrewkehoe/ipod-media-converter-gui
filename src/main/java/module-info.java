module org.example.ipodmediaconvertergui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.ipodmediaconvertergui to javafx.fxml;
    exports org.example.ipodmediaconvertergui;
}