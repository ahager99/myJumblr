/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.DB;

import javafx.concurrent.Service;

/**
 *
 * @author ahage
 */
public class FileService extends Service<String> {

    String path;
    
    public FileService(String path) {
        this.path = path;
    }
    
    
    
    @Override
    protected FileTask createTask() {
        return new FileTask(path);
    }

}
