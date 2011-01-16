package org.feedworker.object;

import java.util.ArrayList;

/**
 *
 * @author luca
 */
public class Show {
    private String name, id, id_tvdb, id_tvrage, plot, banner, icon, season, country,
            network, started, ended;
    private ArrayList<String> genres;
    private ArrayList<String[]> actors;

    public Show(String name, String id, String id_tvdb, String id_tvrage) {
        this.name = name;
        this.id = id;
        this.id_tvdb = id_tvdb;
        this.id_tvrage = id_tvrage;
    }

    public Show(String name, String id, String id_tvdb, String id_tvrage, 
                String plot, String banner, String icon, ArrayList<String> genres) {
        this.name = name;
        this.id = id;
        this.id_tvdb = id_tvdb;
        this.id_tvrage = id_tvrage;
        this.plot = plot;
        this.banner = banner;
        this.icon = icon;
        this.genres = genres;
    }

    public Show(String plot, String banner, String season, String country, 
                String network, String started, String ended, 
                ArrayList<String> genres, ArrayList<String[]> actors) {
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
    
    public String[] toArrayIdName(){
        return new String[]{id, id_tvdb, id_tvrage, name};
    }
    
    public Object[] toArrayResultSearch(){
        return new Object[]{id, id_tvdb, id_tvrage, name, plot, banner, icon, genres};
    }
    
    public Object[] toArrayInfoSingleShow(){
        return new Object[]{plot, banner, season, country, network, started, 
                            ended, genres, actors};
    }
}