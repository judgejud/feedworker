package org.feedworker.util;
/**
 *
 * @author Administrator
 */
public class Translate {
    /**Traduce il giorno dall'inglese all'italiano
     * 
     * @param ing giorno inglese
     * @return giorno italiano
     */
    public static String Day(String ing){
        if (ing.toLowerCase().equalsIgnoreCase("sunday"))
            return "Domenica";
        else if(ing.toLowerCase().equalsIgnoreCase("monday"))
            return "Lunedì";
        else if(ing.toLowerCase().equalsIgnoreCase("tuesday"))
            return "Martedì";
        else if(ing.toLowerCase().equalsIgnoreCase("wednesday"))
            return "Mercoledì";
        else if(ing.toLowerCase().equalsIgnoreCase("thursday"))
            return "Giovedì";
        else if(ing.toLowerCase().equalsIgnoreCase("friday"))
            return "Venerdì";
        else if(ing.toLowerCase().equalsIgnoreCase("saturday"))
            return "Sabato";
        return "";
    }
    
    public static String Status(String ing){
        if (ing.toLowerCase().equalsIgnoreCase("canceled/ended"))
            return "Cancellato/Terminato";
        else if(ing.toLowerCase().equalsIgnoreCase("returning series"))
            return "In corso";
        else if(ing.toLowerCase().equalsIgnoreCase("new series"))
            return "Nuova serie";
        else if(ing.toLowerCase().equalsIgnoreCase("final season"))
            return "Ultima stagione";
        return ing;
    }
}