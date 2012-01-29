package org.feedworker.util;

//IMPORT JAVA
import java.awt.Image;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.ImageIcon;

import org.jfacility.java.lang.OS;
import org.jfacility.javax.swing.Swing;

/**
 * Classe Utility di conversioni varie e raccolta di metodi comuni
 * 
 * @author luca judge
 */
public class Common {
    private static int day_millisec = 86400000;
    private static SimpleDateFormat sdfAmerican = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ITALY);
    private static SimpleDateFormat sdfItalian = new SimpleDateFormat(
            "dd/MM/yyyy", Locale.ITALY);
    private static SimpleDateFormat sdfItalian2 = new SimpleDateFormat(
            "EEE dd/MM", Locale.ITALY);
    private static SimpleDateFormat sdfDateTime = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss", Locale.ITALY);    
    private static SimpleDateFormat sdfICS = new SimpleDateFormat(
            "yyyyMMdd'T'", Locale.ITALY);

    /**
     * Restituisce la data ed ora attuale
     * 
     * @return Stringa gg/mm/aaaa - hh:mm:ss
     */
    public static String actualTime() {
        return dateTimeString(actualDate());
    }

    public static Date actualDate() {
        return new GregorianCalendar().getTime();
    }

    public static Date tomorrowDate() {
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(System.currentTimeMillis() + day_millisec);
        return g.getTime();
    }
    
    public static Date afterDayDate(int day) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(System.currentTimeMillis() + (day_millisec*day));
        return g.getTime();
    }

    public static Date yesterdayDate() {
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(System.currentTimeMillis() - day_millisec);
        return g.getTime();
    }

    /**
     * Trasforma una stringa data ora in una data
     * 
     * @param s
     *            stringa dd/MM/yyyy HH:mm:ss
     * @return data nel tipo Date
     * @throws ParseException
     */
    public static Date stringDateTime(String s) throws ParseException {
        return sdfDateTime.parse(s);
    }

    /**
     * Converte una data in stringa data ora
     * 
     * @param d
     *            data
     * @return stringa dd/MM/yyyy HH:mm:ss
     */
    public static String dateTimeString(Date d) {
        return sdfDateTime.format(d);
    }

    /**
     * Trasforma una stringa data in una data
     * 
     * @param s
     *            stringa dd/MM/yyyy
     * @return data nel tipo Date
     * @throws ParseException
     */
    public static Date stringDate(String s) throws ParseException {
        return sdfItalian.parse(s);
    }

    /**
     * Converte una data in stringa data
     * 
     * @param d
     *            data
     * @return stringa dd/MM/yyyy
     */
    public static String dateString(Date d) {
        if (d == null) {
            return null;
        } else {
            return sdfItalian.format(d);
        }
    }

    /**
     * Converte una data in stringa data
     * 
     * @param d
     *            data
     * @return stringa dd/MM/yyyy
     */
    public static String dateStringAmerican(Date d) {
        if (d == null) {
            return null;
        } else {
            return sdfAmerican.format(d);
        }
    }

    /**
     * Converte una stringa yyyy-MM-dd in data
     * 
     * @param s
     *            stringa data americana yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static Date stringAmericanToDate(String s) throws ParseException {
        return sdfAmerican.parse(s);
    }
    
    /**Converte una stringa yyyy-MM-dd in dd/mm/YYYY
     * 
     * @param s
     * @return
     * @throws ParseException 
     */
    public static String stringAmericanToString(String s) throws ParseException {
        return dateString(sdfAmerican.parse(s));
    }

    public static String stringToAmerican(String s) throws ParseException {
        return dateStringAmerican(stringDate(s));
    }
    
    public static Date stringIcsToDate(String s) throws ParseException {
        return sdfICS.parse(s);
    }
    
    public static String dateIcsToString(Date d) {
        return sdfItalian2.format(d);
    }

    /**
     * Restituisce l'icona per la systemtraybar
     * 
     * @param name
     * @return icona
     */
    public static Image getResourceIcon(String name) {
        ImageIcon icon = getImage(name);
        if (OS.isWindows())
            icon = Swing.scaleImageARGB(icon, 16, 16);
        return icon.getImage();
    }
    
    private static ImageIcon getImage(String name){
        return new ImageIcon(
                ResourceLocator.convertStringToURL(getResourcePath(name)));
    }
    
    public static ImageIcon getResourceImageButton(String name) {
        return Swing.scaleImageARGB(getImage(name), 24, 24);
    }

    public static Image getResourceImage(String name) {
        return new ImageIcon(
                ResourceLocator.convertStringToURL(getResourcePath(name))).getImage();
    }

    public static String getResourcePath(String name) {
        return ResourceLocator.getResourcePath() + name;
    }
    
    public static ImageIcon getEmoticon(String name){
        URL u = ResourceLocator.convertStringToURL(ResourceLocator.getEmoticonPath() + name);
        return new ImageIcon(u);
    }
    
    public static int getStatus(String text){
        if (text.equalsIgnoreCase("Final Season"))
            return 1;
        else if (text.equalsIgnoreCase("Canceled/Ended"))
            return 0;
        else if (text.equalsIgnoreCase("New Series"))
            return 2;
        return -1;
    }
}