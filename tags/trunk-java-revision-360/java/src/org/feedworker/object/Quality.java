package org.feedworker.object;

/**
 * 
 * @author luca
 */
public enum Quality {

    ALL("*"), NORMAL("normale"), _720p("720p"), _1080p("1080p"),
    _1080i("1080i"), BLURAY("bluray"), DVDRIP("dvdrip"), HR("hr"), DIFF("\\"), 
    WEB_DL("web-dl"), BRRIP("brrip"), BDRIP("bdrip");

    private String quality;

    private Quality(String q) {
        quality = q;
    }

    @Override
    public String toString() {
        return quality.toLowerCase();
    }
    
    public static String[] toArray(){
        return new String[]{ALL.toString(), NORMAL.toString(), _720p.toString(),
                            _1080p.toString(), _1080i.toString(), BLURAY.toString(), 
                            DVDRIP.toString(), HR.toString(), BDRIP.toString(), 
                            BRRIP.toString(), WEB_DL.toString(), DIFF.toString()};
    }
}