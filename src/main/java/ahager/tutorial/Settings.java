/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ahage
 */
public class Settings {
    
    private static final String BLOGNAME = "BLOGNAME";
    private static final String BLOGTYPE = "BLOGTYPE";
    private static final String IMAGE = "IMAGE";
    private static final String VIDEO = "VIDEO";
    private static final String STARTFROM = "STARTFROM";
    private static final String STOPAT = "STOPAT";
    private static final String STARTPOS = "STARTPOS";
    private static final String STOPPOS = "STOPPOS";
    private static final String TARGETPATH = "TARGETPATH";
    private static final String UNIQUECHECK = "UNIQUECHECK";
    private static final String IGNOREEMPTY = "IGNOREEMPTY";
    private static final String EMPTYCNT = "EMPTYCNT";
    
    private static String blogName;
    private static Integer intBlogType;
    private static boolean blnImage;
    private static boolean blnVideo;
    private static boolean blnStartFrom;
    private static boolean blnStopAt;
    private static Integer intStartPos;
    private static Integer intStopPos;
    private static String settingPath;
    private static String targetPath;
    private static boolean blnUniqueCheck;
    private static boolean blnIgnoreEmpty;
    private static Integer intEmptyCnt;
   
    
    
    static {
        blogName = "";
        intBlogType = 0;
        blnImage = true;
        blnVideo = true;
        blnStartFrom = false;
        blnStopAt = false;
        intStartPos = null;
        intStopPos = null;   
        blnUniqueCheck = true;
        settingPath = "settings.conf";
        blnIgnoreEmpty = false;
        intEmptyCnt = null;
    }

    
    
    public static String getBlogName() {
        return blogName;
    }
    
    public static void setBlogName(String name) {
        blogName = name;
    } 
    
    
    public static int getBlogType() {
        return intBlogType;
    }
    
    public static void setBlogType(int blogType) {
        intBlogType = blogType;
    }
    
    private static void setBlogType(String blogType) {
        if (blogType == null || blogType.isEmpty() || !StringUtils.isNumeric(blogType)) {
            intBlogType = null;
        } else {
            intBlogType = new Integer(blogType);
        }
    }
      
    public static boolean getImage() {
        return blnImage;
    }
    
    public static void setImage(boolean image) {
        blnImage = image;
    }
    
    public static void setImage(String image) {
         if (image == null || image.isEmpty() || !StringUtils.isNumeric(image)) {
            blnImage = true;
        } else {
             Integer value = new Integer(image);
             blnImage = (value == 1);
        }       
    }
    
    public static boolean getVideo() {
        return blnVideo;
    }
    
    public static void setVideo(boolean video) {
        blnVideo = video;
    }
    
    public static void setVideo(String video) {
         if (video == null || video.isEmpty() || !StringUtils.isNumeric(video)) {
            blnVideo = true;
        } else {
             Integer value = new Integer(video);
             blnVideo = (value == 1);
        }       
    }   

    
    public static boolean getStartFrom() {
        return blnStartFrom;
    }
    
    public static void setStartFrom(boolean startFrom) {
        blnStartFrom = startFrom;
    }
    
    public static void setStartFrom(String startFrom) {
         if (startFrom == null || startFrom.isEmpty() || !StringUtils.isNumeric(startFrom)) {
            blnStartFrom = false;
        } else {
             Integer value = new Integer(startFrom);
             blnStartFrom = (value == 1);
        }       
    }   

    public static boolean getStopAt() {
        return blnStopAt;
    }
    
    public static void setStopAt(boolean stopAt) {
        blnStopAt = stopAt;
    }
    
    public static void setStopAt(String stopAt) {
         if (stopAt == null || stopAt.isEmpty() || !StringUtils.isNumeric(stopAt)) {
            blnStopAt = false;
        } else {
             Integer value = new Integer(stopAt);
             blnStopAt = (value == 1);
        }       
    }   


    public static Integer getStartPos() {
        return intStartPos;
    }
    
    public static void setStartPos(Integer startPos) {
        intStartPos = startPos;
    }
    
    
    public static Integer getStopPos() {
        return intStopPos;
    }
    
    public static void setStopPos(Integer stopPos) {
        intStopPos = stopPos;
    }
    
    private static void setStopPos(String stopPos) {
         if (stopPos == null || stopPos.isEmpty() || !StringUtils.isNumeric(stopPos)) {
            intStopPos = null;
        } else {
             intStopPos = new Integer(stopPos);
        }       
    }   

    
    public static boolean getUniqueCheck() {
        return blnUniqueCheck;
    }
    
    public static void setUniqueCheck(boolean uniqueCheck) {
        blnUniqueCheck = uniqueCheck;
    }
    
    public static void setUniqueCheck(String uniqueCheck) {
         if (uniqueCheck == null || uniqueCheck.isEmpty() || !StringUtils.isNumeric(uniqueCheck)) {
            blnUniqueCheck = true;
        } else {
             Integer value = new Integer(uniqueCheck);
             blnUniqueCheck = (value == 1);
        }       
    }   
    
    
    public static String getTargetPath() {
        return targetPath;
    }
    
    public static void setTargetPath(String path) {
        targetPath = path;
    }
    
    public static String booleanToString(boolean value) {
        if (value) {
            return "1";
        }
        return "0";
    }
    
    
    public static String integerToString(Integer value) {
        if (value != null) {
            return value.toString();
        }
        return "";
    }
    
    
    
    public static void save() {
        Properties props = new Properties();
        props.setProperty(BLOGNAME, blogName);
        props.setProperty(BLOGTYPE, integerToString(intBlogType));
        props.setProperty(IMAGE, booleanToString(blnImage));
        props.setProperty(VIDEO, booleanToString(blnVideo));
        props.setProperty(STARTFROM, booleanToString(blnStartFrom));
        props.setProperty(STOPAT, booleanToString(blnStopAt));
        props.setProperty(IGNOREEMPTY, booleanToString(blnIgnoreEmpty));
        props.setProperty(STARTPOS, integerToString(intStartPos));
        props.setProperty(STOPPOS, integerToString(intStopPos));
        props.setProperty(EMPTYCNT, integerToString(intEmptyCnt));
        if (targetPath == null) {
            props.setProperty(TARGETPATH, "");
        } else {
            props.setProperty(TARGETPATH, targetPath);
        }
        props.setProperty(UNIQUECHECK, booleanToString(blnUniqueCheck));

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(settingPath);
            props.store(outputStream, "Info");
            outputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public static void load() {
        
        try {
            InputStream inputStream = new FileInputStream(settingPath);
            Properties props = new Properties();
            try {
                props.load(inputStream);
                blogName = props.getProperty(BLOGNAME, "");
                setBlogType(props.getProperty(BLOGTYPE, "0"));
                setImage(props.getProperty(IMAGE, "1"));
                setVideo(props.getProperty(VIDEO, "1"));
                setStartFrom(props.getProperty(STARTFROM, "0"));
                setStopAt(props.getProperty(STOPAT, "0"));
                setIgnoreEmpty(props.getProperty(IGNOREEMPTY, "0"));
                setStartFrom(props.getProperty(STARTPOS));
                setStopPos(props.getProperty(STOPPOS));
                setEmptyCnt(props.getProperty(EMPTYCNT));
                setUniqueCheck(props.getProperty(UNIQUECHECK,"1"));
                targetPath = props.getProperty(TARGETPATH);
            } catch (IOException ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            } 
            inputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public static boolean getIgnoreEmpty() {
        return blnIgnoreEmpty;
    }

    public static void setIgnoreEmpty(boolean ignoreEmpty) {
        blnIgnoreEmpty = ignoreEmpty; 
    }

    public static void setIgnoreEmpty(String ignoreEmpty) {
        if (ignoreEmpty == null || ignoreEmpty.isEmpty() || !StringUtils.isNumeric(ignoreEmpty)) {
            blnIgnoreEmpty = false;
        } else {
             Integer value = new Integer(ignoreEmpty);
             blnIgnoreEmpty = (value == 1);
        }   
    }


    public static Integer getEmptyCnt() {
        return intEmptyCnt;
    }
    
    public static void setEmptyCnt(Integer emptyCnt) {
        intEmptyCnt = emptyCnt;
    }
    
    private static void setEmptyCnt(String emptyCnt) {
         if (emptyCnt == null || emptyCnt.isEmpty() || !StringUtils.isNumeric(emptyCnt)) {
            intEmptyCnt = null;
        } else {
            intEmptyCnt = new Integer(emptyCnt);
        }       
    }   
    
}
