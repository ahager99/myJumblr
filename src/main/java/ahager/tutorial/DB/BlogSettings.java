package ahager.tutorial.DB;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.SimpleStringProperty;

public class BlogSettings {
    private String   strBlogName;
    private boolean  blnPosts;
    private boolean  blnLikes;
    private boolean  blnImage;
    private boolean  blnVideo;
    private boolean  blnStartFrom;
    private boolean  blnStopAt;
    private Integer  intStartPos;
    private Integer  intStopPos;
    private String   targetPath;
    private boolean  blnIgnoreEmpty;
    private Integer  intEmptyCnt;
    private boolean  blnActive;


    private void initializeEmpty() {
        strBlogName = "";
        blnPosts = true;
        blnLikes = true;
        blnImage = true;
        blnVideo = true;
        blnStartFrom = false;
        blnStopAt = false;
        intStartPos = null;
        intStopPos = null;  
        blnIgnoreEmpty = false;
        intEmptyCnt = null;
        targetPath = null;
    }

    public BlogSettings() {
        initializeEmpty();
    }

    public BlogSettings(String value) {
        List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
        if (items.size() != 13) {
            initializeEmpty();
            return;
        }

        strBlogName = items.get(0);
        blnPosts = new Boolean(items.get(1));
        blnLikes = new Boolean(items.get(2));
        blnImage = new Boolean(items.get(3));
        blnVideo = new Boolean(items.get(4));
        blnStartFrom = new Boolean(items.get(5));
        blnStopAt = new Boolean(items.get(6));
        this.setStartPos(items.get(7));
        this.setStopPos(items.get(8));
        blnIgnoreEmpty = new Boolean(items.get(9));
        this.setEmptyCnt(items.get(10));
        blnActive = new Boolean(items.get(11));
        targetPath = items.get(12);
    }

        
    public String toString() {
        String value = strBlogName;
        value+= "," + blnPosts;
        value+= "," + blnLikes;
        value+= "," + blnImage;
        value+= "," + blnVideo;
        value+= "," + blnStartFrom;
        value+= "," + blnStopAt;
        value+= "," + getStartPosAsString();
        value+= "," + getStopPosAsString();
        value+= "," + blnIgnoreEmpty;
        value+= "," + getEmptyCntAsString();
        value+= "," + blnActive;
        value+= "," + targetPath;
        return value;
    }

    public String getBlogName() {
        return strBlogName;
    
    }
    public void setBlogName(String strBlogName) {
        this.strBlogName = strBlogName;
    }


    public boolean getPosts() {
        return blnPosts;
    }

    public void setPosts(boolean posts) {
        blnPosts = posts;
    }

    public void setPosts(String strPosts) {
        if (strPosts == null || strPosts.isEmpty() || !StringUtils.isNumeric(strPosts)) {
            blnPosts = true;
        } else {
             Integer value = new Integer(strPosts);
             blnPosts = (value == 1);
        }       
    }


    public boolean getLikes() {
        return blnLikes;
    }

    public void setLikes(boolean likes) {
        blnLikes = likes;
    }

    public void setLikes(String strLikes) {
        if (strLikes == null || strLikes.isEmpty() || !StringUtils.isNumeric(strLikes)) {
            blnLikes = true;
        } else {
             Integer value = new Integer(strLikes);
             blnLikes = (value == 1);
        }       
    }
    

    public boolean getImage() {
        return blnImage;
    }

    public void setImage(boolean image) {
        blnImage = image;
    }

    public void setImage(String strImage) {
        if (strImage == null || strImage.isEmpty() || !StringUtils.isNumeric(strImage)) {
            blnImage = true;
        } else {
             Integer value = new Integer(strImage);
             blnImage = (value == 1);
        }       
    }


    public boolean getVideo() {
        return blnVideo;
    }
    
    public void setVideo(boolean video) {
        blnVideo = video;
    }
    
    public void setVideo(String video) {
         if (video == null || video.isEmpty() || !StringUtils.isNumeric(video)) {
            blnVideo = true;
        } else {
             Integer value = new Integer(video);
             blnVideo = (value == 1);
        }       
    }   

    public boolean getStartFrom() {
        return blnStartFrom;
    }
    
    public  void setStartFrom(boolean startFrom) {
        blnStartFrom = startFrom;
    }
    
    public void setStartFrom(String startFrom) {
         if (startFrom == null || startFrom.isEmpty() || !StringUtils.isNumeric(startFrom)) {
            blnStartFrom = false;
        } else {
             Integer value = new Integer(startFrom);
             blnStartFrom = (value == 1);
        }       
    }   


    public  boolean getStopAt() {
        return blnStopAt;
    }
    
    public  void setStopAt(boolean stopAt) {
        blnStopAt = stopAt;
    }
    
    public  void setStopAt(String stopAt) {
         if (stopAt == null || stopAt.isEmpty() || !StringUtils.isNumeric(stopAt)) {
            blnStopAt = false;
        } else {
             Integer value = new Integer(stopAt);
             blnStopAt = (value == 1);
        }       
    }   


    public String getStartPosAsString() {
        if (intStartPos == null)
            return "";
        
        return intStartPos.toString();
    }

    public  Integer getStartPos() {
        return intStartPos;
    }
    
    public  void setStartPos(Integer startPos) {
        intStartPos = startPos;
    }
    
    public void setStartPos(String startPos) {
        if (startPos == null || StringUtils.isEmpty(startPos) || !StringUtils.isNumeric(startPos)) {
            intStartPos = null;
        } else {
            intStartPos = new Integer(startPos);
        }
    }
    

    public String getStopPosAsString() {
        if (intStopPos == null)
            return "";

        return intStopPos.toString();
    }

    public  Integer getStopPos() {
        return intStopPos;
    }
    
    public  void setStopPos(Integer stopPos) {
        intStopPos = stopPos;
    }


    public void setStopPos(String stopPos) {
        if (stopPos == null || StringUtils.isEmpty(stopPos) || !StringUtils.isNumeric(stopPos)) {
            intStopPos = null;
        } else {
            intStopPos = new Integer(stopPos);
        }
    }


    public String getTargetPath() {
        return targetPath;
    }
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }


    public boolean getIgnoreEmpty() {
        return blnIgnoreEmpty;
    }

    public void setIgnoreEmpty(boolean ignoreEmpty) {
        blnIgnoreEmpty = ignoreEmpty; 
    }

    public void setIgnoreEmpty(String ignoreEmpty) {
        if (ignoreEmpty == null || ignoreEmpty.isEmpty() || !StringUtils.isNumeric(ignoreEmpty)) {
            blnIgnoreEmpty = false;
        } else {
             Integer value = new Integer(ignoreEmpty);
             blnIgnoreEmpty = (value == 1);
        }   
    }


    public String getEmptyCntAsString() {
        if (intEmptyCnt == null)
            return "";
        return intEmptyCnt.toString();
    }

    public Integer getEmptyCnt() {
        return intEmptyCnt;
    }
    
    public void setEmptyCnt(Integer emptyCnt) {
        intEmptyCnt = emptyCnt;
    }

    public void setEmptyCnt(String emptyCnt) {
        if (emptyCnt != null && !StringUtils.isEmpty(emptyCnt) && StringUtils.isNumeric(emptyCnt)) {
            intEmptyCnt = new Integer(emptyCnt);
        } else {
            intEmptyCnt = null;
        }
    }


    public boolean getActive() {
        return blnActive;
    }
    
    public void setActive(boolean active) {
        blnActive = active;
    }
    
    public void setActive(String active) {
         if (active == null || active.isEmpty() || !StringUtils.isNumeric(active)) {
            blnActive = true;
        } else {
             Integer value = new Integer(active);
             blnActive = (value == 1);
        }       
    }   


    public SimpleStringProperty activeProperty() {
        SimpleStringProperty retVal= new SimpleStringProperty("");
        if (blnActive)
            retVal.setValue("x");
        return retVal;
    }

    public SimpleStringProperty blogNameProperty() {
        return new SimpleStringProperty(strBlogName);
    }

    public SimpleStringProperty contentTypeProperty() {
        String retVal = "";
        if (blnImage)
            retVal = "Image";
        if (blnVideo) {
            if (blnImage)
                retVal += ", ";
            retVal += "Video";    
        }
        return new SimpleStringProperty(retVal);
    }

    public SimpleStringProperty blogTypeProperty() {
        String retVal = "";
        if (blnPosts)
            retVal = "Posts";
        if (blnLikes) {
            if (blnPosts)
                retVal += ", ";
            retVal += "Likes";    
        }
        return new SimpleStringProperty(retVal);
    }

    public SimpleStringProperty pathProperty() {
        return new SimpleStringProperty(targetPath);
    }
}
