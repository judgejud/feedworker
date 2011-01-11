package org.feedworker.object;

/**
 * 
 * @author luca
 */
public enum Quality {

    ALL("*"), NORMAL("normale"), FORM_720p("720p"), FORM_1080p("1080p"),
    FORM_1080i("1080i"), BLURAY("bluray"), DVDRIP("dvdrip"), HR("hr"), DIFF("\\");

    private String quality;

    private Quality(String q) {
        quality = q;
    }

    @Override
    public String toString() {
        return quality.toLowerCase();
    }
    
    public static String[] toArray(){
        return new String[]{ALL.toString(), NORMAL.toString(), FORM_720p.toString(),
                            FORM_1080p.toString(), BLURAY.toString(), DVDRIP.toString(), 
                            HR.toString(), DIFF.toString()};
    }
}