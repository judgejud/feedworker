package org.feedworker.util;

//IMPORT JAVA
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
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

    /**
     * Trasforma una stringa in una data
     *
     * @param s
     *            stringa dd/MM/yyyy HH:mm:ss
     * @return data nel tipo Date
     * @throws ParseException
     */
    public static Date stringToDate(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                Locale.ITALY);
        Date d = sdf.parse(s);
        return d;
    }
    
    public static String dateTimeString(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY);
        return sdf.format(d);
    }
    
    /**Restituisce la data come stringa in formato yyyy-MM-dd
     * 
     * @param d
     * @return 
     */
    public static String dateString(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        return sdf.format(d);
    }

    /**Converte una data di tipo stringa yyyy-MM-dd
     * 
     * @param s
     * @return
     * @throws ParseException 
     */
    public static Date stringAmericanToDate(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        Date d = sdf.parse(s);
        return d;
    }

    /**
     * Restituisce l'icona per la systemtraybar
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

    /**
     * Effettua il download dell'inputStream sotto forma di file
     *
     * @param is
     *            http content-stream
     * @param f
     *            file di riferimento su cui mandare il flusso di inputstream
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void downloadSingle(InputStream is, File f)
            throws FileNotFoundException, IOException {
        OutputStream out = new FileOutputStream(f);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
        out.close();
        is.close();
    }
}