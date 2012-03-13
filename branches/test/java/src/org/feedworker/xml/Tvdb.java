package org.feedworker.xml;

/**
 *
 * @author luca judge
 */
public class Tvdb {
    private final String API_KEY = "EE7A2ACED5E56816";
    private final String URL_BASE = "http://thetvdb.com/api/";
    private final String URL_LANGUAGES = URL_BASE + API_KEY + "/languages.xml";
    //<abbreviation>en</abbreviation><id>7</id> <abbreviation>it</abbreviation><id>15</id>
    private final String URL_MIRRORS = URL_BASE + API_KEY + "/mirrors.xml";
    //<mirrorpath>http://thetvdb.com</mirrorpath><typemask>7</typemask>
    private final String URL_TIME_UPDATES = URL_BASE + "Updates.php?type=none";
    private final String URL_FIND_SHOW = URL_BASE + "GetSeries.php?seriesname=";
    //http://thetvdb.com/api/GetSeries.php?seriesname=house
    //http://thetvdb.com/api/EE7A2ACED5E56816/series/73255/
    //http://thetvdb.com/api/EE7A2ACED5E56816/series/73255/all/it.zip
}