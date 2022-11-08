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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import ahager.tutorial.DB.BlogSettings;

/**
 *
 * @author ahage
 */
public class Settings {
    
    private static final String BLOGCNT = "BLOGCNT";
    private static final String BLOGPREFIX = "BLOG-";
    private static final String UNIQUECHECK = "UNIQUECHECK";
    
    private static String settingPath;

    private static Map<String, BlogSettings> blogList;

    private static boolean blnUniqueCheck;
   
    
    
    static {
        blogList = new HashMap<String, BlogSettings>(); 
        blnUniqueCheck = true;
        settingPath = "settings.conf";
    }

    public static Map<String, BlogSettings> getBlogs() {
        return blogList;
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
    

    
    private static String booleanToString(boolean value) {
        if (value) {
            return "1";
        }
        return "0";
    }
    
    
    private static String integerToString(Integer value) {
        if (value != null) {
            return value.toString();
        }
        return "";
    }
    
    
    
    public static void save() {
        Properties props = new Properties();
        props.setProperty(UNIQUECHECK, booleanToString(blnUniqueCheck));
        props.setProperty(BLOGCNT, integerToString(blogList.size()));
        int cnt = 1;
        for (BlogSettings blog: blogList.values()) {
            String value = blog.toString(); 
            props.setProperty(BLOGPREFIX + cnt, value);
            cnt++;
        }
    
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
                setUniqueCheck(props.getProperty(UNIQUECHECK,"1"));
                String blogCnt = props.getProperty(BLOGCNT, "0");
                if (StringUtils.isNumeric(blogCnt)) {
                    int intBlogCnt = Integer.valueOf(blogCnt);
                    for (int i=1; i<=intBlogCnt; i++) {
                        String value = props.getProperty(BLOGPREFIX+ i, "");
                        if (value != "") {
                            BlogSettings blog = new BlogSettings(value);
                            if (blog.getBlogName() != "") {
                                blogList.put(blog.getBlogName(), blog);
                            }
                        }
                    }
                }
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

    
}
