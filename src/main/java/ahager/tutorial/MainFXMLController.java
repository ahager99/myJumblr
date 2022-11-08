package ahager.tutorial;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


public class MainFXMLController implements Initializable {
    
    private TumblrService tumblrService;
    private UIService uiService;
    private String logText = "";
    private boolean logTextUpdate = false;
    private DownloadInformation videoInformation;
    private DownloadInformation imageInformation;
    private ObservableList<DownloadInformation> tableDataStatus;
    private ObservableList<BlogSettings> tableDataBlogs;
    

    @FXML    
    private TableView<BlogSettings> tblBlogs;
    @FXML
    private TableColumn<BlogSettings, String> colActive;
    @FXML
    private TableColumn<BlogSettings, String> colBlogName;
    @FXML
    private TableColumn<BlogSettings, String> colBlogType;
    @FXML
    private TableColumn<BlogSettings, String> colContentType;
    @FXML
    private TableColumn<BlogSettings, String> colPath;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
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
    

    @FXML
    private void handleButtonAdd(ActionEvent event) throws IOException {
        BlogSettings newSettings = openBlogEditWindow (new BlogSettings());   
        if (newSettings != null) {
            Settings.getBlogs().put(newSettings.getBlogName(), newSettings);
            loadBlogTable();
            saveSettings();
        } 
    } 
    
    @FXML
    private void handleButtonDelete(ActionEvent event) throws IOException {
        BlogSettings item = tblBlogs.getSelectionModel().getSelectedItem();
        if (item != null) {
            Settings.getBlogs().remove(item.getBlogName());
            loadBlogTable();
            saveSettings();
        }
    } 


    private BlogSettings openBlogEditWindow(BlogSettings settings) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Blog.fxml"));
        Parent root;
        try {
            root = loader.load();
            BlogFXMLController ctrl = loader.getController();
            ctrl.setBlogValues(settings);

            Stage stage = new Stage();
            stage.setIconified(false);
            stage.setResizable(false);
            stage.setTitle("Scan files");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();   

            return ctrl.getBlogValues();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
    }


    private void loadBlogTable() {
        tableDataBlogs = FXCollections.observableArrayList(Settings.getBlogs().values());
        tblBlogs.setItems(tableDataBlogs);
        tblBlogs.refresh();
    }


    @FXML void handleUnqiueCheckAction(ActionEvent event) {
        setReinitializeBtnStatus();
    }
    
    
    
    private void setButtonStatus(boolean disable) {
        chkUniqueCheck.setDisable(disable);
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
        chkUniqueCheck.setSelected(Settings.getUniqueCheck());
    }
    
    
    private void saveSettings() {
        Settings.setUniqueCheck(chkUniqueCheck.isSelected());
        Settings.save();
    }
    
    
    private class UIService extends ScheduledService<Void> {

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                    @Override protected Void call() throws InterruptedException {
                        updateProgressInformation();
                        tableDataStatus = FXCollections.observableArrayList(imageInformation, videoInformation);
                        tblStatus.setItems(tableDataStatus);
                        tblStatus.refresh();
                        updateDBCount();
                        return null;
                    }
                };
        } 
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
               
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


        // Creating table blogs structure
        colActive.setCellValueFactory(new PropertyValueFactory<BlogSettings, String>("active"));
        colBlogName.setCellValueFactory(new PropertyValueFactory<BlogSettings, String>("blogName"));
        colBlogType.setCellValueFactory(new PropertyValueFactory<BlogSettings, String>("blogType"));
        colContentType.setCellValueFactory(new PropertyValueFactory<BlogSettings, String>("contentType"));
        colPath.setCellValueFactory(new PropertyValueFactory<BlogSettings, String>("path"));

        // Set handler to 
        tblBlogs.setRowFactory( tv -> {
            TableRow<BlogSettings> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    BlogSettings rowData = row.getItem();
                    
                    BlogSettings newSettings = openBlogEditWindow (rowData);   
                    if (newSettings != null) {
                        Settings.getBlogs().remove(rowData.getBlogName());
                        Settings.getBlogs().put(newSettings.getBlogName(), newSettings);
                        loadBlogTable();
                        saveSettings();
                    } 
                }
            });
            return row ;
        });
        
        // Load settings of last application start
        // Settings are stored always when download is started
        loadSettings();

        // add blogs to table and display
        loadBlogTable();

        
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