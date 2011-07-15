package org.feedworker.object;

import java.util.ArrayList;

/**
 *
 * @author luca
 */
public class Show {
    private String name, id_tvdb, id_tvrage, plot, banner, icon, season, country,
            network, started, ended;
    private ArrayList<String> genres;
    private ArrayList<String[]> actors;

    public Show(String name, String plot, String banner, String season, String country, 
                String network, String started, String ended, 
                ArrayList<String> genres, ArrayList<String[]> actors) {
        this.name = name;
        this.plot = plot;
        this.banner = banner;
        this.season = season;
        this.country = country;
        this.network = network;
        this.started = started;
        this.ended = ended;
        this.genres = genres;
        this.actors = actors;
    }
    
    public String getTextHtml(){
        String html = new String("<html><body><center><img src=\""+banner+"\"></img><br>"
                + "<h1>"+name+"</h1></center><br>");
        html += "<table cellspacing=\"3\" cellpadding=\"2\">";
        html += "<tr><td><b>Genere/i</b></td><td>"+genres.get(0);
        for (int i=1; i<genres.size(); i++)
            html += ", " + genres.get(i);
        html += "</td></tr>";
        html += "<tr><td valign=\"top\"><b>Trama</b></td><td>"+plot+"</td></tr>";
        
        html += "</table>";
        html += "</body></html>";
        return html;
    }
}