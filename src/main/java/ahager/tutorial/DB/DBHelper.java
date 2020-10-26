/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahager.tutorial.DB;

import java.io.File;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

/**
 *
 * @author ahage
 */
class DBFileInfo {
    @Id
    String filename;
    
    public DBFileInfo(String filename) {
        this.filename = filename;
    }
}

public class DBHelper {

    private static final String DBFILENAME = "scan.db";
    
    private static Nitrite db;
    private static ObjectRepository<DBFileInfo> repo;
  
    private DBHelper() {
        
    }

    public static void open() {
        if (db != null && !db.isClosed()) {
            db.close();
        }
        
        db = Nitrite.builder().compressed().filePath(DBFILENAME).openOrCreate();
        repo = db.getRepository(DBFileInfo.class);
    }
    
    public static void close() {
        if (db != null && !db.isClosed()) {
            db.close();
        } 
    }
    
    public static void compact() {
        if (db != null && !db.isClosed()) {
         db.compact();
        }
    }
   
    public static boolean exists(String filename) {
        if (repo == null) return false;

        Cursor<DBFileInfo> files = repo.find(ObjectFilters.eq("filename", filename));
        return files.size() > 0;
    } 
    
    public static void add(String filename) {
        if (!exists(filename)) {
            DBFileInfo file = new DBFileInfo(filename);
            repo.insert(file);
        }
    } 
    
    public static void reset() {
        if (repo != null) {
            repo.remove(ObjectFilters.ALL);
        }
    }
    
    public static long getFileCount() {
        if (repo == null) return 0;
        
        return repo.size();
    }
    
    
    public static void deleteDBFile() {
        File dbFile = new File(DBFILENAME);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }
}

