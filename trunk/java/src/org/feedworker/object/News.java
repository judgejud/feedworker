package org.feedworker.object;

/**
 *
 * @author luca
 */
public class News {
    
    private String id, showId, name, image, date, thumb, episode;

    public News(String id, String showId, String name, String image, String date, 
            String thumb, String episode) {
        this.id = id;
        this.showId = showId;
        this.name = name;
        this.image = image;
        this.date = date;
        this.thumb = thumb;
        this.episode = episode;
    }

}