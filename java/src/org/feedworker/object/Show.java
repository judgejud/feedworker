package org.feedworker.object;

import java.util.ArrayList;

/**
 *
 * @author luca
 */
public class Show {
    private String name, id_tvrage, plot, banner, icon, season, country, status, 
            network, started, ended;
    private ArrayList<String> genres;
    private ArrayList<String[]> actors;

    public Show(String name, String plot, String banner, String season, String country, 
                String network, String started, String ended, String status, 
                ArrayList<String> genres, ArrayList<String[]> actors) {
        this.name = name;
        this.plot = plot;
        this.banner = banner;
        this.season = season;
        this.country = country;
        this.network = network;
        this.started = started;
        this.ended = ended;
        this.status = status;
        this.genres = genres;
        this.actors = actors;
    }
    
    public String getHtmlShow(){
        String html = new String("<html><body><center><img src=\""+banner+"\"></img><br>"
                + "<h1>"+name+"</h1></center>");
        html += "<table width=\"97%\" cellspacing=\"2\" cellpadding=\"2\">";
        html += "<tr><td><b>Genere/i</b></td><td>"+genres.get(0);
        for (int i=1; i<genres.size(); i++)
            html += ", " + genres.get(i);
        html += "</td></tr>";
        html += "<tr><td valign=\"top\"><b>Trama</b></td><td>"+plot+"</td></tr>";
        html += "<tr><td><b>Network</b></td><td>"+network+"</td></tr>";
        html += "<tr><td><b>Stagioni</b></td><td>"+season+"</td></tr>";
        html += "<tr><td><b>Status</b></td><td>"+status+"</td></tr>";
        html += "<tr><td><b>Nazione</b></td><td>"+country+"</td></tr>";
        
        html += "</table>";
        html += "</body></html>";
        return html;
    }
    
    public String getHtmlActors(){
        String html = new String("<html><body>");
        html += "<table cellspacing=\"3\" cellpadding=\"2\">";
        html += "<tr><td><b>Attore</b></td><td><b>Peronaggio</b></td><td><b>Foto</b></td></tr>";
        for (int i=0; i<actors.size(); i++){
            String[] row = actors.get(i);
            html += "<tr><td>" + row[0] + "</td><td>" + row[1] + "</td><td><img src=\"" + 
                    row[2] +"\"></img></td></tr>";
        }
        html += "</table>";
        html += "</body></html>";
        return html;
    }
}