package org.feedworker.object;

import java.util.ArrayList;
/**
 *
 * @author luca
 */
public class News {    
    private String image, date, translation, sync, resync, info, imageby, submitted;
    private ArrayList<String> subtitles;

    public News(String image, String date, String translation, String sync, 
                String resync, String info, String imageby, String submitted, 
                ArrayList<String> subtitles) {
        this.image = image;
        this.date = date;
        this.translation = translation;
        this.sync = sync;
        this.resync = resync;
        this.info = info;
        this.imageby = imageby;
        this.submitted = submitted;
        this.subtitles = subtitles;
    }
}