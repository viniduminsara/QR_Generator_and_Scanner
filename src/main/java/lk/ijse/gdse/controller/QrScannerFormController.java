package lk.ijse.gdse.controller;

import com.github.sarxos.webcam.Webcam;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.gdse.qr.WebCamView;
import lk.ijse.gdse.qr.WebcamService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class QrScannerFormController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView imageView;

    @FXML
    private ProgressBar prograss;

    @FXML
    private Label lbltext;

    private WebcamService service;

    @FXML
    void startBtnOnAction(ActionEvent event) {
        service.restart();
        prograss.setVisible(true);
    }

    @FXML
    void stopBtnOnAction(ActionEvent event) {
        service.cancel();
        prograss.setVisible(false);
    }

    @FXML
    void backBtnOnAction(ActionEvent event) throws IOException {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml")));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        prograss.setVisible(false);
        Webcam cam = Webcam.getWebcams().get(0);
        service = new WebcamService(cam);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebCamView view = new WebCamView(service,imageView);
        pane.getChildren().add(view.getView());

        prograss.progressProperty().bind(service.progressProperty());

        service.messageProperty().addListener((a,old,c)->{
            if(c!=null){
                if(old==null){
                    System.out.println(c);
                }else
                if(!old.equals(c)) {
                    lbltext.setText(c);
                }
            }
        });
    }
}
