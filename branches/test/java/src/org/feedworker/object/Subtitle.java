package org.feedworker.object;
/**
 *
 * @author luca
 */
public class Subtitle {
    private String id, showId, showName, name, version, fileName, fileSize, description, infoUrl;

    public Subtitle(String id, String showId, String showName, String name, 
                    String version, String fileName, String fileSize, 
                    String description, String infoUrl) {
        this.id = id;
        this.showId = showId;
        this.showName = showName;
        this.name = name;
        this.version = version;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.description = description;
        this.infoUrl = infoUrl;
    }
    
    public String[] toArraySingle(){
        return new String[]{name, version, fileName, fileSize, description, infoUrl};
    }
}