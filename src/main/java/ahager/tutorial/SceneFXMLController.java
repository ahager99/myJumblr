package ahager.tutorial;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.tumblr.jumblr.download.DownloadItem;
import com.tumblr.jumblr.types.Post.PostType;

import org.apache.commons.lang3.StringUtils;

import ahager.tutorial.DB.BlogSettings;
import ahager.tutorial.DB.DBHelper;
import ahager.tutorial.download.DownloadInformation;
import ahager.tutorial.download.DownloadStatus;
import ahager.tutorial.tumblr.TumblrService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


public class SceneFXMLController implements Initializable {
    
    private TumblrService tumblrService;
    private UIService uiService;
    private String logText = "";
    private boolean logTextUpdate = false;
    private DownloadInformation videoInformation;
    private DownloadInformation imageInformation;
    private ObservableList<DownloadInformation> tableData;
    

    @FXML    
    private TextField txtDownloadPath;
    @FXML 
    private TextField txtBlogName;
    @FXML 
    private RadioButton optPosts;
    @FXML 
    private RadioButton optLikes;
    @FXML 
    private CheckBox chkImage;
    @FXML 
    private CheckBox chkVideo;
    @FXML 
    private CheckBox chkStartFrom;
    @FXML 
    private CheckBox chkStopAt;
    @FXML 
    private TextField txtStartPos;
    @FXML 
    private TextField txtStopPos;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;
    @FXML
    private TextArea txtLog;
    @FXML
    private TableView<DownloadInformation> tblStatus;
    @FXML
    private TableColumn<DownloadInformation, String> colType;
    @FXML
    private TableColumn<DownloadInformation, String> colDownloaded;
    @FXML
    private TableColumn<DownloadInformation, String> colSkipped;
    @FXML
    private TableColumn<DownloadInformation, String> colFailed;   
    @FXML
    private Label lblFileAmount;
    @FXML
    private Button btnReinitialize;
    @FXML
    private CheckBox chkUniqueCheck;
    @FXML
    private Button btnSelectDownloadPath;
    @FXML
    private CheckBox chkIgnoreEmpty;
    @FXML 
    private TextField txtEmptyCnt;


    @FXML
    private void handleButtonSelectDownloadPath(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Download path");
        if (txtDownloadPath.getText() != null) {
            File defaultDirectory = new File(txtDownloadPath.getText());
            if (defaultDirectory.isDirectory()) {
                chooser.setInitialDirectory(defaultDirectory);
            }
        }
        File selectedDirectory = chooser.showDialog(null);
        if (selectedDirectory != null) {
            txtDownloadPath.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void handleButtonReinitialize(ActionEvent event) throws IOException {
        /*
        saveSettings();
       
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Loading.fxml"));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setIconified(false);
        stage.setResizable(false);
        stage.setTitle("Scan files");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        
        DBHelper.compact();
        updateDBCount();

        */

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Blog.fxml"));
        Parent root = loader.load();
        BlogFXMLController ctrl = loader.getController();
        ctrl.setBlogValues(Settings.getBlogs().values().iterator().next());

        Stage stage = new Stage();
        stage.setIconified(false);
        stage.setResizable(false);
        stage.setTitle("Scan files");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        

    }
    
    @FXML
    private void handleButtonStart(ActionEvent event) {

        saveSettings();
        if (tumblrService.getState() == Worker.State.READY) {
            imageInformation.reset();
            videoInformation.reset();
            
            btnStart.setDisable(true);
            btnStop.setDisable(false);
            setButtonStatus(true);
            
            tumblrService.reset();
            tumblrService.start();
            uiService.start();
        }
    } 

    @FXML
    private void handleButtonStop(ActionEvent event) {
        btnStop.setDisable(true);
        tumblrService.cancel();
    } 
    
    
    @FXML void handleUnqiueCheckAction(ActionEvent event) {
        setReinitializeBtnStatus();
    }
    
    
    
    private void setButtonStatus(boolean disable) {
        optPosts.setDisable(disable);
        optLikes.setDisable(disable);
        chkImage.setDisable(disable);
        chkVideo.setDisable(disable);
        chkStartFrom.setDisable(disable);
        chkStopAt.setDisable(disable);
        txtStartPos.setDisable(disable);
        txtStopPos.setDisable(disable);
        txtDownloadPath.setDisable(disable);
        btnSelectDownloadPath.setDisable(disable);
        chkUniqueCheck.setDisable(disable);
        chkIgnoreEmpty.setDisable(disable);
        txtEmptyCnt.setDisable(disable);
        if (disable) {
            btnReinitialize.setDisable(disable);
        } else {
            setReinitializeBtnStatus();
        }
    }
    
    private void setReinitializeBtnStatus() {
        btnReinitialize.setDisable(!chkUniqueCheck.isSelected());
    }
    
    
    private void tumblrServiceStopped() {
        btnStart.setDisable(false);
        btnStop.setDisable(true);
        setButtonStatus(false);
        updateProgressInformation();
        DBHelper.compact(); 
        updateDBCount();
        tumblrService.reset();
    }



    private void updateDBCount() {
        lblFileAmount.setText(Long.toString(DBHelper.getFileCount()));
        lblFileAmount.requestLayout();
    }
    
    
    private void updateProgressInformation() {
        if (logTextUpdate) {
            txtLog.setText(logText);
            txtLog.positionCaret(txtLog.getLength());
            logTextUpdate = false;
        }
    }
    
    private void loadSettings() {
        Settings.load();
        BlogSettings activeSetting = Settings.getBlogs().values().iterator().next();
        txtBlogName.setText(activeSetting.getBlogName());
        txtDownloadPath.setText(activeSetting.getTargetPath());
        optPosts.setSelected(activeSetting.getPosts());
        optLikes.setSelected(activeSetting.getLikes());
        chkImage.setSelected(activeSetting.getImage());
        chkVideo.setSelected(activeSetting.getVideo());
        chkStartFrom.setSelected(activeSetting.getStartFrom());
        chkStopAt.setSelected(activeSetting.getStopAt());
        chkIgnoreEmpty.setSelected(activeSetting.getIgnoreEmpty());
        chkUniqueCheck.setSelected(Settings.getUniqueCheck());
        if (activeSetting.getStartPos() == null) {
            txtStartPos.setText("");
        } else {
            txtStartPos.setText(activeSetting.getStartPos().toString());
        }
        if (activeSetting.getStopPos() == null) {
            txtStopPos.setText("");
        } else {
            txtStopPos.setText(activeSetting.getStopPos().toString());
        }
        if (activeSetting.getEmptyCnt() == null) {
            txtEmptyCnt.setText("");
        } else {
            txtEmptyCnt.setText(activeSetting.getEmptyCnt().toString());
        }
    }
    
    
    private void saveSettings() {
 /* 
        Settings.setUniqueCheck(chkUniqueCheck.isSelected());

        
        BlogSettings activeSetting = new BlogSettings();
        activeSetting.setBlogName(txtBlogName.getText());
        activeSetting.setTargetPath(txtDownloadPath.getText());
        activeSetting.setPosts(optPosts.isSelected());
        activeSetting.setLikes(optLikes.isSelected());
        activeSetting.setImage(chkImage.isSelected());
        activeSetting.setVideo(chkVideo.isSelected());
        activeSetting.setStartFrom(chkStartFrom.isSelected());
        activeSetting.setStopAt(chkStopAt.isSelected());
        activeSetting.setIgnoreEmpty(chkIgnoreEmpty.isSelected());
       
        if (!txtStartPos.getText().isEmpty() && StringUtils.isNumeric(txtStartPos.getText())) {
            activeSetting.setStartPos(new Integer(txtStartPos.getText()));
        } else {
            activeSetting.setStartPos(null);
        }
        if (!txtStopPos.getText().isEmpty() && StringUtils.isNumeric(txtStopPos.getText())) {
            activeSetting.setStopPos(new Integer(txtStopPos.getText()));
        } else {
            activeSetting.setStopPos(null);
        }
        if (!txtEmptyCnt.getText().isEmpty() && StringUtils.isNumeric(txtEmptyCnt.getText())) {
            activeSetting.setEmptyCnt(new Integer(txtEmptyCnt.getText()));
        } else {
            activeSetting.setEmptyCnt(null);
        }
        Settings.save();
        */
    }
    
    
    private class UIService extends ScheduledService<Void> {

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                    @Override protected Void call() throws InterruptedException {
                        updateProgressInformation();
                        tableData = FXCollections.observableArrayList(imageInformation, videoInformation);
                        tblStatus.setItems(tableData);
                        tblStatus.refresh();
                        updateDBCount();
                        return null;
                    }
                };
        } 
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

       
        
        //MyProxySelector ps = new MyProxySelector(ProxySelector.getDefault());
        //ProxySelector.setDefault(ps);
       
               
        // Create a new trust manager that trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                @Override
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Activate the new trust manager to enable download via SSL
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ex) {
            Logger.getLogger(SceneFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        
        // Creating table content for download information
        colType.setCellValueFactory(new PropertyValueFactory<DownloadInformation, String>("name"));
        colDownloaded.setCellValueFactory(new PropertyValueFactory<DownloadInformation, String>("downloaded"));
        colSkipped.setCellValueFactory(new PropertyValueFactory<DownloadInformation, String>("skipped"));
        colFailed.setCellValueFactory(new PropertyValueFactory<DownloadInformation, String>("failed"));
        videoInformation = new DownloadInformation("Video");
        imageInformation = new DownloadInformation("Image");

        
        // Load settings of last application start
        // Settings are stored always when download is started
        loadSettings();
        
        
        // Initialize unique DB and load file count stored
        DBHelper.open();
        updateDBCount();

        // Set correct status of buttons
        btnStart.setDisable(false);
        btnStop.setDisable(true);
        setButtonStatus(false);
        
        // Initialize Tumblr Download Service
        tumblrService = new TumblrService() {
            @Override
            protected void logTaskInformation(String text) {
                logText += text;
                if (logText.length() > 5000) {
                    logText = logText.substring(2000);
                }
                logTextUpdate = true;
            }

            @Override
            protected void downloadedObject(DownloadItem aClass, DownloadStatus status) {
                if (aClass.getType() == PostType.PHOTO) {
                    imageInformation.add(status);
                } else if (aClass.getType() == PostType.VIDEO) {
                    videoInformation.add(status);
                }
            }
        };
        tumblrService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                tumblrServiceStopped();
            }
        });
        tumblrService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                tumblrServiceStopped();
            }
        });
        tumblrService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                tumblrServiceStopped();
            }
        });
    
        // Initialize UI Service to update UI during execution
        uiService = new UIService();
        uiService.setPeriod(Duration.seconds(1));
 
    }    
}