package org.feedworker.object;

/**
 *
 * @author luca judge
 */
public enum Operation {
    
    DELETE("Elimina sub"), TRUNCATE("Tronca sub");
    
    private String oper;

    private Operation(String oper) {
        this.oper = oper;
    }
    
    @Override
    public String toString() {
        return oper.toLowerCase();
    }
    
    public static String[] toArray(){
        return new String[]{null, DELETE.toString(), TRUNCATE.toString()};
    }
}