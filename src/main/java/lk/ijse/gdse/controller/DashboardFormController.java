package lk.ijse.gdse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class DashboardFormController {

    @FXML
    private AnchorPane pane;

    @FXML
    void btnQRgeneratorOnAction(ActionEvent event) throws IOException {
        pane.getChildren().clear();
        pane.getChildren().add(FXMLLoader.load(getClass().getResource("/view/qrGeneratorForm.fxml")));
    }

    @FXML
    void btnQRscannerOnAction(ActionEvent event) throws IOException {
        pane.getChildren().clear();
        pane.getChildren().add(FXMLLoader.load(getClass().getResource("/view/qrScannerForm.fxml")));
    }
}
