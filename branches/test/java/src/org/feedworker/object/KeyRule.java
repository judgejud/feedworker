package org.feedworker.object;

/**
 * 
 * @author luca
 */
public class KeyRule implements Comparable<KeyRule> {

    private String name;
    private String season;
    private String quality;

    public KeyRule(String name, String season, String quality) {
        this.name = name.toLowerCase();
        this.season = season;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public String getSeason() {
        return season;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String q) {
        quality = q;
    }

    @Override
    public int compareTo(KeyRule o) {
        if (this.getName().equals(o.getName())) {
            if (this.getSeason().equals(o.getSeason())) {
                if (this.getQuality().equals(o.getQuality())
                        || this.getQuality().equals(Quality.ALL.toString())
                        || o.getQuality().equals(Quality.ALL.toString())) {
                    return 0;
                } else if (this.getQuality().equals(Quality.DIFF.toString()))
                    return +1;
                else if (o.getQuality().equals(Quality.DIFF.toString()))
                    return -1;
                else
                    return this.getQuality().compareTo(o.getQuality());
            } else
                return this.getSeason().compareTo(o.getSeason());
        } else
            return this.getName().compareTo(o.getName());
    }
}