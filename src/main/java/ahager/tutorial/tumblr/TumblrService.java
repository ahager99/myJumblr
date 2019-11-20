/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.tumblr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.SourceInterface;

import ahager.tutorial.download.DownloadStatus;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javafx.concurrent.Service;

/**
 *
 * @author ahage
 */
public abstract class TumblrService extends Service<Void> {

    private TumblrTask task;
    private static JumblrClient client;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public TumblrService() {
        if (client == null) {
            client = new JumblrClient("OVPVcW5s5rgKRpBa6ffQsaYKzvuUcbjwJjFM5pyleq3McHcOJ4", "eknWQvHC21rvtXzIrXolh9hhi3p334C3H3pEYlwUzVDQ00cvbh");
            client.setToken("HbEQvpN1dvqlOlK3kQr1au2jGkAha3V0ofQLKAkDaJDILrHhOF", "bSjdQCo3mVc9MIeq2eYnX43AXkohDthzvJZ0kQm9HIUkgQqAjp");
        }
    }


    @Override
    protected void failed() {
        super.failed(); //To change body of generated methods, choose Tools | Templates.
        logTaskInformationWithDate("Exception occured" + System.lineSeparator());
        Throwable e = task.getException();
        if (e != null) {
            logTaskInformationWithDate(e.toString() + System.lineSeparator());
            logTaskInformationWithDate(System.lineSeparator());
            logTaskInformationWithDate("----------------------------------------------------------" + System.lineSeparator());
            logTaskInformationWithDate(System.lineSeparator());
            e.printStackTrace();
        }
    }

    @Override
    protected void cancelled() {
        super.cancelled(); //To change body of generated methods, choose Tools | Templates.
        logTaskInformationWithDate("Download cancelled ...." + System.lineSeparator());
        logTaskInformationWithDate(System.lineSeparator());
        logTaskInformationWithDate("----------------------------------------------------------" + System.lineSeparator());
        logTaskInformationWithDate(System.lineSeparator());
    }

    @Override
    public void start() {
        logTaskInformationWithDate("Download startet ...." + System.lineSeparator());
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void succeeded() {
        super.succeeded(); //To change body of generated methods, choose Tools | Templates.
        logTaskInformationWithDate("Download finished ...." + System.lineSeparator());
        logTaskInformationWithDate(System.lineSeparator());
        logTaskInformationWithDate("----------------------------------------------------------" + System.lineSeparator());
        logTaskInformationWithDate(System.lineSeparator());
    }
    
    
    @Override
    public boolean cancel() {
        if (task != null) {
            return task.cancel(false);
        } else {
            return false;
        }
    }


    @Override
    protected TumblrTask createTask() {
        task = new TumblrTask(client) {
            @Override
            void logInformation(String text) {
                logTaskInformation(text);
            }

            @Override
            void downloaded(Class<? extends SourceInterface> aClass, DownloadStatus status) {
                downloadedObject(aClass, status);
            }
        };
        return task;
    }
    
    
    private void logTaskInformationWithDate(String text) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logTaskInformation(sdf.format(timestamp) + "    " + text);
    }



    protected abstract void logTaskInformation(String text);
    protected abstract void downloadedObject(Class<? extends SourceInterface> aClass, DownloadStatus status); 
}
