/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.tumblr;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.download.DownloadItem;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Post.PostType;

import org.apache.commons.io.FileUtils;

import ahager.tutorial.Settings;
import ahager.tutorial.DB.DBHelper;
import ahager.tutorial.download.DownloadStatus;
import javafx.concurrent.Task;

/**
 *
 * @author ahage
 */
public abstract class TumblrTask extends Task<Void> {
     
    private static final int secsConnectTimeout = 3;
    private static final int secsReadTimeout = 3;
    private static final int retries = 3;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    
    Blog blog;
    JumblrClient client;
    
    
    public TumblrTask(JumblrClient client) {
        this.client = client;
    }
    
    
    
    private void downloadSingleItem(DownloadItem obj, String downloadDir)
    {
        URL url;
        String filename = "?????";
        boolean success = false;
        DownloadStatus status = null;
        String statusText = "";
        
        logInformationWithDate(obj.getFilename());
        for (int i=0; i<retries && !success; i++) {

            if ((Settings.getImage() && obj.getType() == PostType.PHOTO) ||
                (Settings.getVideo() && obj.getType() == PostType.VIDEO)) {
                try {
                    url = new URL(obj.getUrl());
                    File targetFile = new File(downloadDir + File.separator + obj.getFilename());
                    filename = targetFile.getName();
                    boolean fileExists = targetFile.exists() && !targetFile.isDirectory();
                    boolean dbExists = Settings.getUniqueCheck() && DBHelper.exists(filename);
                    if (!fileExists && !dbExists) {
                        FileUtils.copyURLToFile(url, targetFile, secsConnectTimeout*1000, secsReadTimeout*1000);
                        status = DownloadStatus.Downloaded;
                        statusText = "DOWNLOADED";
                    } else {
                        status = DownloadStatus.Skipped;
                        statusText = "EXISTS";
                    }
                    if (!dbExists) {
                        DBHelper.add(filename);
                    }
                    success = true;
                } catch (MalformedURLException ex) {
                    Logger.getLogger(TumblrTask.class.getName()).log(Level.SEVERE, null, ex);
                    statusText = "URLEXCEPTION";
                    status = DownloadStatus.Failed;
                } catch (IOException ex) {
                    Logger.getLogger(TumblrTask.class.getName()).log(Level.SEVERE, null, ex);
                    statusText = "IOEXCEPTION";
                    status = DownloadStatus.Failed;
                }
            } else {
                status = DownloadStatus.Skipped;
                statusText = "SKIPPED";
            }
        }

 
        logInformation(" " + statusText + System.lineSeparator());
        
        downloaded(obj, status);
    }  
    
    
    private boolean shouldStop(int pos) {
        if (this.isCancelled()) {
            return true;
        }
        if (Settings.getStopAt() && Settings.getStopPos() != null && Settings.getStopPos() < pos) {
            return true;
        }
        return false;
    }
    

    @Override
    protected Void call() throws Exception  {
        Map<String, Integer> options;
        
        int postCnt = 0;
        int emptyIgnored = 0;

        // Store blog name for logging
        blog = client.blogInfo(Settings.getBlogName());       
        
        // Setting start position if not loop from beginning
        if (Settings.getStartFrom() && Settings.getStartPos() != null) {
            postCnt = Settings.getStartPos();
        }
        
        // Loop until end is reached ot cancelled by user
        while(!shouldStop(postCnt)) {
            options = new HashMap<>();
            options.put("offset", postCnt);
            
            List<Post> postList;
            if (Settings.getBlogType() == 0) {
                postList = blog.posts(options);
            } else {
                postList = blog.likedPosts(options);
            }

            // If no posts are fetched continue related to settings. This is needed because
            // sometimes leaks returned
            if (postList.isEmpty()) {
                emptyIgnored++; 
                if (!Settings.getIgnoreEmpty() || emptyIgnored > Settings.getEmptyCnt())
                    break;

                logInformation("                                   Empty offset #" + postCnt + " - ignored " + emptyIgnored + "/" +  Settings.getEmptyCnt() + System.lineSeparator());
                postCnt++;
            }
            
            // Perform the return posts
            for (Post post : postList) {
                postCnt++;
                emptyIgnored = 0; // reset empty ignored, so only direct following leaks are counted
                logInformation("                                   Post #" + postCnt + ": " + post.getType().toString() + System.lineSeparator());
                for (DownloadItem item : post.getDownloadItems()) {
                    downloadSingleItem(item, Settings.getTargetPath());
                }
                
                if (shouldStop(postCnt)) {
                    break;
                }
            }        
        }
        
        return null;
    }
    
    
    private void logInformationWithDate(String text) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logInformation(sdf.format(timestamp) + "    " + text);
    }

    abstract void logInformation(String text);
    abstract void downloaded(DownloadItem aClass, DownloadStatus status);
}
