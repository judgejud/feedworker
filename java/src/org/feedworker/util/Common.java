package org.feedworker.util;

//IMPORT JAVA
import java.awt.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.ImageIcon;

import org.jfacility.lang.Lang;
import org.jfacility.swing.Swing;

/**
 * Classe Utility di conversioni varie e raccolta di metodi comuni
 * 
 * @author luca judge
 */
public class Common {
	/**
	 * Converte una jav.util.Date in formato stringa
	 * 
	 * @param d
	 *            data
	 * @return gg/mm/aaaa - hh:mm:ss
	 */
	public static String dateToString(Date d) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		return calendarToString(cal);
	}

	/**
	 * Restituisce la data ed ora attuale
	 * 
	 * @return Stringa gg/mm/aaaa - hh:mm:ss
	 */
	public static String actualTime() {
		Calendar cal = new GregorianCalendar();
		return calendarToString(cal);
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

	/**
	 * converte calendar in stringa
	 * 
	 * @param cal
	 * @return
	 */
	private static String calendarToString(Calendar cal) {
		String s = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1)
				+ "/" + cal.get(Calendar.YEAR) + " "
				+ addZero(cal.get(Calendar.HOUR_OF_DAY)) + ":"
				+ addZero(cal.get(Calendar.MINUTE)) + ":"
				+ addZero(cal.get(Calendar.SECOND));
		return s;
	}

	/**
	 * Aggiunge lo zero ai numeri < 10
	 * 
	 * @param n
	 *            intero da controllare
	 * @return Stringa con/senza 0
	 */
	private static String addZero(int n) {
		String s = new String();
		if (n < 10)
			s = "0";
		s += String.valueOf(n);
		return s;
	}

	/**
	 * Restituisce l'icona per la systemtraybar
	 * 
	 * @param name
	 * @return icona
	 */
	public static Image getResourceImage(String name) {
		String stringa = ResourceLocator.getResourcePath() + name;
		ImageIcon icon = new ImageIcon(
				ResourceLocator.convertStringToURL(stringa));
		if (Lang.isWindows())
			icon = Swing.scaleImage(icon, 16, 16);
		return icon.getImage();
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
			if (num > -1)
				number = Lang.intToString(num);
		} else if (first.equalsIgnoreCase("e")) {
			int num = -1;
			try {
				num = Lang.stringToInt(text.substring(1, 3));
			} catch (NumberFormatException nfe) {
			}
			if (num > -1)
				number = "1";
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