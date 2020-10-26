/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.tumblr;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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
import com.zenkey.net.prowser.Prowser;
import com.zenkey.net.prowser.Request;
import com.zenkey.net.prowser.Response;
import com.zenkey.net.prowser.Tab;

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
    Prowser prowser;

    public TumblrTask(JumblrClient client, Prowser prowser) {
        this.client = client;
        this.prowser = prowser;
    }

    private void downloadSingleItem(DownloadItem obj, String downloadDir) {
        URL url;
        String filename = "?????";
        boolean success = false;
        DownloadStatus status = null;
        String statusText = "";

        logInformationWithDate(obj.getFilename());
        for (int i = 0; i < retries && !success; i++) {

            // Process images and videos
            if ((Settings.getImage() && obj.getType() == PostType.PHOTO)
                    || (Settings.getVideo() && obj.getType() == PostType.VIDEO)) {
                try {
                    url = new URL(obj.getUrl());
                    File targetFile = new File(downloadDir + File.separator + obj.getFilename());
                    filename = targetFile.getName();
                    boolean fileExists = targetFile.exists() && !targetFile.isDirectory();
                    boolean dbExists = Settings.getUniqueCheck() && DBHelper.exists(filename);
                    if (!fileExists && !dbExists) {

                        // Check if object at URL does contain an image or video
                        // Because of some changes at tumblr directly downloading is not always
                        // supported and
                        // HTML is returned. In that case PROWSER plugin is used to fetch item in
                        // different way.
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        String contentType = connection.getContentType();
                        if (contentType.substring(0, 9).equals("text/html")) {

                            // HTML is returned try to get object with PROWSER plugin
                            Tab tab = prowser.createTab();
                            Request request = new Request(obj.getUrl());
                            Response response = tab.go(request);

                            String type = response.getContentType();
                            // If still an HTML is returned then skip
                            if (type.substring(0, 9).equals("text/html")) {
                                status = DownloadStatus.Skipped;
                                statusText = "HTML ONLY";
                            }
                            else {
                                byte[] data = response.getPageBytes();
                                FileUtils.writeByteArrayToFile(targetFile, data);
                                status = DownloadStatus.Downloaded;
                                statusText = "DOWNLOADED";
                            }
                            prowser.closeTab(tab);

                        } else {
                            // Direct download because seems to be an image or video
                            FileUtils.copyURLToFile(url, targetFile, secsConnectTimeout * 1000, secsReadTimeout * 1000);
                            status = DownloadStatus.Downloaded;
                            statusText = "DOWNLOADED";
                        }
                    } else {
                        status = DownloadStatus.Skipped;
                        statusText = "EXISTS";
                    }
                    if (!dbExists && status == DownloadStatus.Downloaded) {
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
                } catch (URISyntaxException ex) {
                    Logger.getLogger(TumblrTask.class.getName()).log(Level.SEVERE, null, ex);
                    statusText = "URLSYNTAXECEPTION";
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
