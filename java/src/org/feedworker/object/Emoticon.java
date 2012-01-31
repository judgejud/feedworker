package org.feedworker.object;

import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.feedworker.util.Common;
/**
 *
 * @author luca judge
 */
public class Emoticon {
    private static Emoticon e;
    private TreeMap<String, ImageIcon> map;
    
    public static Emoticon getInstance(){
        if (e==null)
            e = new Emoticon();
        return e;
    }

    private Emoticon() {
        map = new TreeMap<String, ImageIcon>();
        map.put(":@", Common.getEmoticon("angry.gif"));
        map.put(":D", Common.getEmoticon("bigsmile.gif"));
        map.put(":$", Common.getEmoticon("blush.gif"));
        map.put(":C", Common.getEmoticon("clapping.gif"));
        map.put("8)", Common.getEmoticon("cool.gif"));
        map.put(";(", Common.getEmoticon("crying.gif"));
        map.put(":G", Common.getEmoticon("giggle.gif"));
        map.put(":L", Common.getEmoticon("inlove.gif"));        
        map.put(":X", Common.getEmoticon("lipssealed.gif"));
        map.put(":*", Common.getEmoticon("kiss.gif"));
        map.put(":(", Common.getEmoticon("sadsmile.gif"));
        map.put(":)", Common.getEmoticon("smile.gif"));
        map.put(":|", Common.getEmoticon("speechless.gif"));
        map.put(":?", Common.getEmoticon("thinking.gif"));
        map.put(";)", Common.getEmoticon("wink.gif"));
        map.put(":S", Common.getEmoticon("worried.gif"));
        map.put(":Y", Common.getEmoticon("yawn.gif"));
    }
    
    public Iterator<String> getEmoString(){
        return map.keySet().iterator();
    }
    
    public Iterator<ImageIcon> getEmoIcon(){
        return map.values().iterator();
    }
}