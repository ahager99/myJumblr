/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.DB;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.concurrent.Task;

/**
 *
 * @author ahage
 */
public class FileTask extends Task<String> {

    
    Path dirPath;
    long value = 0;
    
    public FileTask(String path) {
        dirPath = Paths.get(path);
        updateValue(0);
    }

    @Override
    protected String call() throws Exception {
        fetchFileNames(dirPath);
        
        return null;
    }
    
    private void updateValue(long value) {
        updateValue(Long.toString(value));
    }
    
    
    
    private void fetchFileNames(Path dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (this.isCancelled()) {
                    break;
                }
                
                if(path.toFile().isDirectory()) {
                    fetchFileNames(path);
                } else {
                    DBHelper.add(path.getFileName().toString());
                    updateValue(++value);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    } 

    @Override
    protected void cancelled() {
        super.cancelled(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
