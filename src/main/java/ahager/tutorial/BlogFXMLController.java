package ahager.tutorial;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import ahager.tutorial.DB.BlogSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class BlogFXMLController implements Initializable {
    

    @FXML    
    private TextField txtDownloadPath;
    @FXML 
    private TextField txtBlogName;
    @FXML 
    private CheckBox chkPosts;
    @FXML 
    private CheckBox chkLikes;
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
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private CheckBox chkIgnoreEmpty;
    @FXML 
    private TextField txtEmptyCnt;
    @FXML
    private Button btnSelectDownloadPath;
    @FXML
    private CheckBox chkActive;

    BlogSettings retVal = null;

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
    private void handleButtonCancel(ActionEvent event) {
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleButtonSave(ActionEvent event) {
        
        if (txtBlogName.getText().length() > 0 && txtDownloadPath.getText() != null) {
            retVal = new BlogSettings();
            retVal.setActive(chkActive.isSelected());
            retVal.setBlogName(txtBlogName.getText());
            retVal.setEmptyCnt(txtEmptyCnt.getText());
            retVal.setIgnoreEmpty(chkIgnoreEmpty.isSelected());
            retVal.setImage(chkImage.isSelected());
            retVal.setLikes(chkLikes.isSelected());
            retVal.setPosts(chkPosts.isSelected());
            retVal.setStartFrom(chkStartFrom.isSelected());
            retVal.setStartPos(txtStartPos.getText());
            retVal.setStopAt(chkStopAt.isSelected());
            retVal.setStopPos(txtStopPos.getText());
            retVal.setTargetPath(txtDownloadPath.getText());
            retVal.setVideo(chkVideo.isSelected());
            
            handleButtonCancel(event);           
        }
        
    }
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setBlogValues(BlogSettings next) {
        chkActive.setSelected(next.getActive());
        txtBlogName.setText(next.getBlogName());
        txtEmptyCnt.setText(next.getEmptyCntAsString());
        chkIgnoreEmpty.setSelected(next.getIgnoreEmpty());
        chkImage.setSelected(next.getImage());
        chkLikes.setSelected(next.getLikes());
        chkPosts.setSelected(next.getPosts());
        chkStartFrom.setSelected(next.getStartFrom());
        txtStartPos.setText(next.getStartPosAsString());
        chkStopAt.setSelected(next.getStopAt());
        txtStopPos.setText(next.getStopPosAsString());
        txtDownloadPath.setText(next.getTargetPath());
        chkVideo.setSelected(next.getVideo());
    }    


    public BlogSettings getBlogValues() {
        return retVal;
    }
}