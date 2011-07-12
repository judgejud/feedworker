package org.feedworker.object;

/**
 *
 * @author luca
 */
public class ItasaUser {
    
    private String authcode, id;
    private boolean myitasa;

    public ItasaUser(String authcode, String id, boolean myitasa) {
        this.authcode = authcode;
        this.id = id;
        this.myitasa = myitasa;
    }

    public String getAuthcode() {
        return authcode;
    }

    public String getId() {
        return id;
    }

    public boolean isMyitasa() {
        return myitasa;
    }
}