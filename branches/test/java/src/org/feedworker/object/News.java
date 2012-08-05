package org.feedworker.object;

import java.util.ArrayList;
/**
 *
 * @author luca
 */
public class News {    
    private String title, image, translation, sync, info, imageby, submitted;
    private ArrayList<String> subtitles;

    public News(String title, String image, String translation, String sync, String info, 
            String imageby, String submitted, ArrayList<String> subtitles) {
        this.title = title;
        this.image = image;
        this.translation = translation;
        this.sync = sync;
        this.info = info;
        this.imageby = imageby;
        this.submitted = submitted;
        this.subtitles = subtitles;
    }

    public ArrayList<String> getSubtitles() {
        return subtitles;
    }
    
    public String getHtmlNews(){
        String html = new String("<html><body>");
        html += "<table width=\"96%\" cellspacing=\"1\" cellpadding=\"1\">";
        html += "<tr><td width=\"210\" rowspan=\"5\"><img src=\""+image+"\"></img></td>";
        html += "<td>"+ info + "</td></tr>";
        html += "<tr><td><b>Revisione</b>: "+ submitted +" </td></tr>";
        html += "<tr><td><b>Traduzione</b>: "+ translation +"</td></tr>";
        html += "<tr><td><b>Sync</b>: "+ sync +" </td></tr>";
        html += "<tr><td><b>Immagine by</b>: "+ imageby +" </td></tr>";
        html += "</table></body></html>";
        return html;
    }

    public String getSubmitted() {
        return submitted;
    }

    public String getTitle() {
        return title;
    }
}