package lk.ijse.gdse.controller;

import com.google.zxing.WriterException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import lk.ijse.gdse.qr.QrGenerator;
import java.io.File;
import java.io.IOException;


public class QrGeneratorController {
    @FXML
    private TextField txtQr;

    @FXML
    private AnchorPane pane;

    @FXML
    void generateBtnOnAction(ActionEvent event) {
        if (!txtQr.getText().isEmpty()) {

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selected =directoryChooser.showDialog(txtQr.getScene().getWindow());
            String text = txtQr.getText();
            if (selected != null) {
                try {
                    String filepath = selected.getAbsolutePath()+"\\"+text+".png";
                    QrGenerator.generateQrCode(text, 1250, 1250,filepath);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }else {
                new Alert(Alert.AlertType.WARNING,"Please select directory to generate QR").show();
            }
        }else {
            new Alert(Alert.AlertType.WARNING,"Please enter text to generate QR code").show();
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        pane.getChildren().clear();
        pane.getChildren().add(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml")));
    }
}
