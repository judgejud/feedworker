package org.feedworker.object;

import java.text.ParseException;
import java.util.ArrayList;

import org.feedworker.util.Common;
import org.feedworker.util.Translate;

/**
 *
 * @author luca
 */
public class Show {
    private String name, tvrage, plot, banner, season, country, status, network, 
                started, ended;
    private ArrayList<String> genres;
    private ArrayList<String[]> actors;

    public Show(String name, String plot, String banner, String season, String country, 
                String network, String started, String ended, String status, String tvrage, 
                ArrayList<String> genres, ArrayList<String[]> actors, String link) {
        this.name = name;
        this.plot = plot;
        this.banner = banner;
        this.season = season;
        this.country = country;
        this.network = network;
        try {
            this.started = Common.stringAmericanToString(started);
        } catch (ParseException ex) {}
        if (ended.equalsIgnoreCase("0000-00-00"))
            this.ended = "";
        else
            try {
                this.ended = Common.stringAmericanToString(ended);
            } catch (ParseException ex) {}
        this.status = status;
        this.genres = genres;
        this.actors = actors;
        this.tvrage = tvrage;
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
        html += "<tr><td><b>Status</b></td><td>"+Translate.Status(status)+"</td></tr>";
        html += "<tr><td><b>Nazione</b></td><td>"+country+"</td></tr>";
        html += "<tr><td><b>Iniziato</b></td><td>"+started+"</td></tr>";
        
        html += "<tr><td><b>Terminato</b></td><td>"+ended+"</td></tr>";
        html += "</table>";
        html += "</body></html>";
        return html;
    }
    
    public String getHtmlActors(){
        String html = new String("<html><body>");
        html += "<table cellspacing=\"3\" cellpadding=\"2\">";
        html += "<tr><td><b>Foto</b></td><td><b>Attore</b></td><td><b>Personaggio</b></td>"
                + "<td><b>Foto</b></td><td><b>Attore</b></td><td><b>Personaggio</b></td></tr>";
        for (int i=0; i<actors.size(); i+=2){
            String[] row = actors.get(i);
            html += "<tr><td><img src=\"" + row[2] +"\" width=\"75\" height=\"120\"></img></td>"
                    + "<td>" + row[0] + "</td><td>" + row[1] + "</td>";
            if (i+1<actors.size()){
                row = actors.get(i+1);
                html += "<td><img src=\"" + row[2] +"\" width=\"75\" height=\"120\"></img></td>"
                    + "<td>" + row[0] + "</td><td>" + row[1] + "</td>";
            } else
                html += "<td colspan=\"3\">&nbsp;</td>";
            html += "</tr>";
        }
        html += "</table>";
        html += "</body></html>";
        return html;
    }

    public String getTvrage() {
        return tvrage;
    }
}