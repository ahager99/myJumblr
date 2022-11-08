/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial;

import ahager.tutorial.DB.DBHelper;
import ahager.tutorial.DB.FileService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author ahage
 */
public class LoadingFXMLController implements Initializable {

    private FileService fileService;
     
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblFileCount;
    
    
    @FXML
    private void handleButtonCancel(ActionEvent event) {
        fileService.cancel();

    }
    

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
        DBHelper.close();
        DBHelper.deleteDBFile();
        DBHelper.open();
        fileService = new FileService(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        lblFileCount.textProperty().bind(fileService.valueProperty());
       
        fileService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                closeWindow();
            }
        });
        fileService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                closeWindow();
            }
        });
        fileService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                closeWindow();
            }
        });

        if (fileService.getState() == State.READY) {
            fileService.start();
        } else {
            closeWindow();
        }
    }    
    
    
    private void closeWindow() {
        btnCancel.getScene().getWindow().hide();
    }
    
}
