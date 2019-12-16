/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.tumblr;

import ahager.tutorial.DB.DBHelper;
import ahager.tutorial.Settings;
import ahager.tutorial.download.DownloadStatus;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.QuotePost;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;
import com.tumblr.jumblr.types.Post.PostType;
import com.tumblr.jumblr.types.SourceInterface;
import com.tumblr.jumblr.types.TextPost;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
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
    
    
    
    private void downloadObject(SourceInterface obj, String downloadDir)
    {
        URL url;
        String filename = "?????";
        boolean success = false;
        DownloadStatus status = null;
        String statusText = "";
        
        logInformationWithDate("Processing " + obj.getFileName());
        for (int i=0; i<retries && !success; i++) {
            try {
                url = new URL(obj.getSrc());
                File targetFile = new File(downloadDir + File.separator + obj.getFileName());
                filename = targetFile.getName();
                boolean fileExists = targetFile.exists() && !targetFile.isDirectory();
                boolean dbExists = Settings.getUniqueCheck() && DBHelper.exists(filename);
                if (!fileExists && !dbExists) {
                    FileUtils.copyURLToFile(url, targetFile, secsConnectTimeout*1000, secsReadTimeout*1000);
                    status = DownloadStatus.Downloaded;
                    statusText = "DOWNLOADED";
                } else {
                    status = DownloadStatus.Skipped;
                    statusText = "SKIPPED";
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
        }
        logInformation(" " + statusText + System.lineSeparator());
        
        downloaded(obj.getClass(), status);
    }    
    
    
    private void downloadPhoto(PhotoPost post) {
        for(Photo photo : post.getPhotos()) {
            downloadObject(photo.getOriginalSize(), Settings.getTargetPath());
        }
    }
    
    private void downloadVideo(VideoPost post) {
        
        Integer maxWidth = null;
        Video maxVideo = null;
        
        for(Video video : post.getVideos()) {                   
            if (maxWidth == null || maxVideo.getWidth() < video.getWidth()) {
                maxWidth = video.getWidth();
                maxVideo = video;
            }
        }
        if (maxVideo != null) {
            downloadObject(maxVideo, Settings.getTargetPath());
        }
    }
    
    private void downloadSinglePost(Post post) {
        PostType type = post.getType();
        
        // Downloading regular post elements
        switch(type) {
           case PHOTO:
               if (Settings.getImage()) {
                    downloadPhoto((PhotoPost)post);
               }
               break;
           case VIDEO:
               if (Settings.getVideo()) {
                   downloadVideo((VideoPost)post);
               }
               break;
           default:
                
       }    
       
       // Downloading figures elements of posts
       List<SourceInterface> objList = new ArrayList<SourceInterface>();
       switch (type) {
           case PHOTO: objList = SourceInterface.parseFigures(((PhotoPost)post).getCaption());
                       break;
           case TEXT:  objList = SourceInterface.parseFigures(((TextPost)post).getBody());
                       break;
           case QUOTE: objList = SourceInterface.parseFigures(((QuotePost)post).getSource());
                       break;
           default:
       }
       for(SourceInterface obj : objList) {
           downloadObject(obj, Settings.getTargetPath());
       }
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
        
        blog = client.blogInfo(Settings.getBlogName());       
        
        int i = 0;
        if (Settings.getStartFrom() && Settings.getStartPos() != null) {
            i = Settings.getStartPos();
        }
        
        while(!shouldStop(i)) {
            options = new HashMap<>();
            options.put("offset", i);
            
            List<Post> postList;
            if (Settings.getBlogType() == 0) {
                postList = blog.posts(options);
            } else {
                postList = blog.likedPosts(options);
            }
            if (postList.isEmpty()) {
                break;
            }
            for (Post post : postList) {
                downloadSinglePost(post);
                i++;
                if (shouldStop(i)) {
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
    abstract void downloaded(Class<? extends SourceInterface> aClass, DownloadStatus status);
}
