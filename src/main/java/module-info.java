module com.roleanalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.roleanalyzer to javafx.fxml;
    exports com.roleanalyzer;
    exports com.roleanalyzer.DataCreationFiles;
    opens com.roleanalyzer.DataCreationFiles to javafx.fxml;
}