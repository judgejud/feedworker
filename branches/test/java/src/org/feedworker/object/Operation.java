package org.feedworker.object;

/**
 *
 * @author luca judge
 */
public enum Operation {
    DELETE("Elimina sub"), 
    TRUNCATE("Tronca sub"), 
    EQUAL_VIDEO("Rinomina sub uguale nome video"), 
    TVRAGE("Rinomina nome sub e video by tvrage");
    
    private String oper;

    private Operation(String oper) {
        this.oper = oper;
    }
    
    @Override
    public String toString() {
        return oper.toLowerCase();
    }
    
    public static String[] toArray(){
        return new String[]{null, DELETE.toString(), TRUNCATE.toString(), 
                            EQUAL_VIDEO.toString(), TVRAGE.toString()};
    }
}