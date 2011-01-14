package org.feedworker.object;

import java.util.ArrayList;

/**
 *
 * @author luca
 */
public class Show {
    private String name, id, id_tvdb, id_tvrage, plot, banner, icon;
    private ArrayList<String> genres;

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
    
    public String[] toArrayMinimal(){
        return new String[]{id, id_tvdb, id_tvrage, name};
    }
    
    public Object[] toArray(){
        return new Object[]{id, id_tvdb, id_tvrage, name, plot, banner, icon, genres};
    }
}