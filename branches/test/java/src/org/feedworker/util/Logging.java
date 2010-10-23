package org.feedworker.util;

//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * @author luca
 */
public class Logging {

	private static Logging log = null;

	private Logger logger;

	/** Costruttore privato */
	private Logging() {
		logger = Logger.getLogger("log");
		int limit = 100000; // 100k
		File f = new File("feedworker.log");
		FileHandler fh = null;
		try {
			fh = new FileHandler(f.getAbsolutePath(), limit, 2);
		} catch (IOException ex) {
		} catch (SecurityException ex) {
		}
		logger.addHandler(fh);
		logger.setLevel(Level.SEVERE);
		fh.setFormatter(new SimpleFormatter());
	}

	/**
	 * Restituisce l'istanza corrente della classe
	 * 
	 * @return istanza di Logging
	 */
	public static Logging getIstance() {
		if (log == null)
			log = new Logging();
		return log;
	}

	/**
	 * Stampa il nome della classe che genera l'errore
	 * 
	 * @param c
	 *            classe
	 */
	public void printClass(Class c) {
		logger.severe(c.getName() + "\n");
	}

	/**
	 * Stampa il messaggio d'errore e lo stacktrace connesso.
	 * 
	 * @param e
	 *            eccezione da stampare
	 */
	public void printError(Exception e) {
		StackTraceElement[] ste = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(e.getMessage() + "\n");
		for (int i = 0; i < ste.length; i++)
			sb.append(ste[i].toString() + "\n");
		logger.severe(sb.toString());
	}
}