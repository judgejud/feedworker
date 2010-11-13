package org.feedworker.util;

/**
 *
 * @author luca
 */
public class FilterSub implements Comparable<FilterSub>{
    private String name;
    private String season;
    private String quality;
    private String day;
    private String status;

    public FilterSub(String name, String season, String quality, String status, String day) {
        this.name = name.toLowerCase();
        this.season = season;
        this.quality = quality;
        this.day = day;
        this.status = status;
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

    public void setQuality(String q){
        quality = q;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(FilterSub o) {
        if (this.getName().equals(o.getName())){
            if (this.getSeason().equals(o.getSeason())){
                if (this.getQuality().equals(o.getQuality()) ||
                        this.getQuality().equals(Quality.ALL.toString()) ||
                        o.getQuality().equals(Quality.ALL.toString()))
                    return 0;
                else if (this.getQuality().equals(Quality.DIFF.toString()))
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
