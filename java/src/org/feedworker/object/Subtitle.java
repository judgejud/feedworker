package org.feedworker.object;
/**
 *
 * @author luca
 */
public class Subtitle {
    private String id, name, version, fileName, fileSize;

    public Subtitle(String id, String name, String version, String fileName, String fileSize) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
    
    public String[] toArray(){
        return new String[]{id, name, version, fileName, fileSize};
    }
}