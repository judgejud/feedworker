package org.feedworker.util;

//IMPORT JAVA
import java.awt.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.ImageIcon;

import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.MySystem;
import org.jfacility.javax.swing.Swing;

/**Classe Utility di conversioni varie e raccolta di metodi comuni
 * 
 * @author luca judge
 */
public class Common {
    private static int day_millisec = 86400000;

    /** Restituisce la data ed ora attuale
     *
     * @return Stringa gg/mm/aaaa - hh:mm:ss
     */
    public static String actualTime() {
        return dateTimeString(actualDate());
    }

    public static Date actualDate(){
        return new GregorianCalendar().getTime();
    }
    
    public static Date tomorrowDate(){
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(System.currentTimeMillis() + day_millisec);
        return g.getTime();
    }

    public static Date yesterdayDate(){
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(System.currentTimeMillis() - day_millisec);
        return g.getTime();
    }

    /**Trasforma una stringa data ora in una data
     *
     * @param s stringa dd/MM/yyyy HH:mm:ss
     * @return data nel tipo Date
     * @throws ParseException
     */
    public static Date stringDateTime(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                Locale.ITALY);
        return sdf.parse(s);
    }
    
    /**Converte una data in stringa data ora
     * 
     * @param d data
     * @return stringa dd/MM/yyyy HH:mm:ss
     */
    public static String dateTimeString(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY);
        return sdf.format(d);
    }
    
    /**Trasforma una stringa data in una data
     *
     * @param s stringa dd/MM/yyyy
     * @return data nel tipo Date
     * @throws ParseException
     */
    public static Date stringDate(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        return sdf.parse(s);
    }
    
    /**Converte una data in stringa data
     * 
     * @param d data
     * @return stringa dd/MM/yyyy
     */
    public static String dateString(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        if (d==null)
            return null;
        else
            return sdf.format(d);
    }
    
    /**Converte una stringa yyyy-MM-dd in data
     * 
     * @param s stringa data americana yyyy-MM-dd
     * @return
     * @throws ParseException 
     */
    public static Date stringAmericanToDate(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        return sdf.parse(s);
    }

    /**Restituisce l'icona per la systemtraybar
     *
     * @param name
     * @return icona
     */
    public static Image getResourceIcon(String name) {
        ImageIcon icon = new ImageIcon(
                ResourceLocator.convertStringToURL(getResourcePath(name)));
        if (MySystem.isWindows()) {
            icon = Swing.scaleImage(icon, 16, 16);
        }
        return icon.getImage();
    }

    public static Image getResourceImage(String name) {
        return new ImageIcon(
                ResourceLocator.convertStringToURL(getResourcePath(name))).getImage();
    }

    public static String getResourcePath(String name) {
        return ResourceLocator.getResourcePath() + name;
    }

    /**
     * cerca il numero della serie nel testo
     *
     * @param text
     * @return numero serie/stagione
     */
    public static String searchNumberSeries(String text) {
        String number = null;
        String first = text.substring(0, 1).toLowerCase();
        if (first.equalsIgnoreCase("s")) {
            int num = -1;
            try {
                num = Lang.stringToInt(text.substring(1, 3));
            } catch (NumberFormatException nfe) {
            }
            if (num > -1) {
                number = Lang.intToString(num);
            }
        } else if (first.equalsIgnoreCase("e")) {
            int num = -1;
            try {
                num = Lang.stringToInt(text.substring(1, 3));
            } catch (NumberFormatException nfe) {
            }
            if (num > -1) {
                number = "1";
            }
        }
        return number;
    }

    /**
     * cerca la posizione della stringa corrispondente al numero di serie ed
     * episodio nell'array; es: s01e01
     *
     * @param _array
     * @return restituisce la posizione se l'ha trovato, altrimenti -1
     */
    public static int searchPosSeries(String[] _array) {
        int pos = -1;
        for (int i = 0; i < _array.length; i++) {
            if (searchNumberSeries(_array[i]) != null) {
                pos = i;
                break;
            }
        }
        return pos;
    }
}