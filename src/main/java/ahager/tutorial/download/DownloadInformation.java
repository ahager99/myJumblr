/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.download;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author ahage
 */
public class DownloadInformation {
    private final SimpleStringProperty  name;
    private final SimpleIntegerProperty downloaded;
    private final SimpleIntegerProperty skipped;
    private final SimpleIntegerProperty failed;

    public DownloadInformation(String name) {
        this.name = new SimpleStringProperty(name);
        this.downloaded = new SimpleIntegerProperty(0);
        this.skipped = new SimpleIntegerProperty(0);
        this.failed = new SimpleIntegerProperty(0);
    }


    public void reset() {
        this.downloaded.set(0);
        this.skipped.set(0);
        this.failed.set(0);
    }

    public void add(DownloadStatus status) {
        if (status != null) {
            switch(status) {
                case Downloaded:
                    downloaded.set(downloaded.get() + 1);
                    break;
                case Failed:
                    failed.set(failed.get() + 1);
                    break;
                case Skipped:
                    skipped.set(skipped.get() + 1);
                    break;
            }
        }
    }

    public String getName() {
        return name.get();
    }

    public int getDownloaded() {
        return downloaded.get();
    }

    public int getSkipped() {
        return skipped.get();
    }

    public int getFailed() {
        return failed.get();
    }
    
    
}
