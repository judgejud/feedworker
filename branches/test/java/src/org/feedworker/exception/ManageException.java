package org.feedworker.exception;

//IMPORT JAVA
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.zip.ZipException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.ManageListener;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;

import jcifs.smb.SmbException;

import org.jdom.JDOMException;

import org.xml.sax.SAXException;
/**
 * Stampa nella textpane i messaggi d'errore se è un messaggio d'errore "rosso"
 * scrive nel file di log lo stacktrace.
 * 
 * @author luca
 */
public class ManageException {
    // VARIABLES PRIVATE STATIC
    private static ManageException core = null;

    public static ManageException getIstance() {
        if (core == null)
            core = new ManageException();
        return core;
    }

    public void launch(ClassNotFoundException ex, Class c) {
        printError(ex, c);
    }

    public void launch(Exception e, Class c) {
        printError(e, c);
    }

    public void launch(FeedException ex, Class c, String text) {
        String msg = ex.getMessage();
        String err01 = "Invalid XML: Error on line";
        if (msg.substring(0, err01.length()).equalsIgnoreCase(err01)) {
            printAlert("Non posso analizzare il feed XML " + text, false);
        } else {
            printError(ex, c);
        }
    }

    public void launch(GeneralSecurityException ex, Class c) {
        printError(ex, c);
    }

    public void launch(IllegalAccessException ex, Class c) {
        printError(ex, c);
    }

    public void launch(IllegalArgumentException ex, Class c) {
        String msg = ex.getMessage();
        String error01 = "Name may not be null";
        printError(ex, c);
    }

    public void launch(IllegalStateException ex, Class c) {
        String msg = ex.getMessage();
        String error01 = "Manager is shut down.";
        if (msg.equals(error01)) {
            printAlert("Synology " + error01, true);
        } else {
            printError(ex, c);
        }
    }
    
    public void launch(ConnectException ex, Class c) {
        String msg = ex.getMessage();
        String error01 = "Connection timed out: connect";
        if (msg.equals(error01)) {
            printAlert("Timeout di connessione, riprovare", true);
        } else {
            printError(ex, c);
        }
    }

    public void launch(IOException ex, Class c) {
        String msg = ex.getMessage();
        String error01 = "Failed to open file://" + 
                ApplicationSettings.getIstance().getCifsShareLocation();
        String error02 = "Failed to open http://";
        if (msg.startsWith(error01))
            printAlert("Impossibile trovare/aprire il path desiderato, controllare "
                    + "l'esistenza del path o i permessi per accedervi ", true);
        else
            printError(ex, c);
    }

    public void launch(InstantiationException ex, Class c) {
        printError(ex, c);
    }

    public void launch(IOException ex, Class c, String text) {
        String msg = ex.getMessage();
        String error01 = "The system cannot find the path specified.";
        String error02 = "Connect to";
        String error03 = "Connection to";
        String error04 = "Read timed out";
        String error05 = "www.italiansubs.net";
        String error06 = "www.subsfactory.it";
        String error07 = "rss.bt-chat.com";
        String error08 = "ezrss.it";

        if (msg.equals(error01))
            printAlert("Il sistema non trova il seguente percorso " + text, true);
        else if (msg.equals(error04))
            printAlert("Timeout di lettura " + text, false);
        else if (msg.equals(error05) || msg.equals(error06) || msg.equals(error07) 
                                                            || msg.equals(error08))
            printAlert("Connessione con " + msg + " assente. verificare la " + 
                                                "connessione alla rete.", true);
        else if (msg.length() < error02.length())
            printError(ex, c);
        else if (msg.substring(0, error02.length()).equals(error02))
            printAlert("Non posso collegarmi a " + text, false);
        else if (msg.substring(0, error03.length()).equals(error03))
            printAlert("Non posso collegarmi a " + text, false);
        else if (text!=null)
            printError(ex, text);
        else
            printError(ex, c);
    }

    public void launch(JDOMException ex, Class c) {
        printError(ex, c);
    }

    public void launch(LineUnavailableException ex, Class c) {
        String msg = ex.getMessage();
        String error01 = "Audio Device Unavailable";
        if (msg.equals(error01)) {
            printAlert("Periferica audio non disponibile", true);
        } else {
            printError(ex, c);
        }
    }

    public void launch(MalformedURLException ex, Class c, String text) {
        String msg = ex.getMessage();
        String error01 = "no protocol:";
        if (c.getName().equals(org.feedworker.client.frontend.Mediator.class.getName())) {
            if (msg.substring(0, error01.length()).equals(error01)) {
                printAlert("Link " + text
                        + " errato; immettere in questa forma http://www.", true);
            } else {
                printError(ex, c);
            }
        } else {
            printError(ex, c);
        }
    }

    public void launch(NumberFormatException ex, Class c, String text) {
        String msg = ex.getMessage();
        String error01 = "For input string:";
        if (msg.substring(0, error01.length()).equalsIgnoreCase(error01)) {
            printAlert("Riga: " + text + " immettere un numero alla stagione", true);
        } else {
            printError(ex, c);
        }
    }

    public void launch(ParseException ex, Class c) {
        printError(ex, c);
    }

    public void launch(ParsingFeedException ex, Class c, String text) {
        String error01 = "Invalid XML: Error on line";
        String msg = ex.getMessage();
        if ((msg.length()>=error01.length()) &&
                ((msg.substring(0, error01.length())).equalsIgnoreCase(error01)))
            printAlert("Non posso analizzare il feed XML " + text, false);
        else
            printError(ex, c);
    }

    public void launch(ParserConfigurationException ex, Class c){
        printError(ex, c);
    }

    public void launch(SAXException ex, Class c){
        printError(ex, c);
    }

    public void launch(SmbException ex, Class c, String text) {
        String msg = ex.getMessage();
        String error01 = "The system cannot find the file specified.";
        String error02 = "Cannot create a file when that file already exists.";
        String error03 = "Access is denied.";
        String error04 = "Logon failure: unknown user name or bad password.";
        String error05 = "The network name cannot be found.";
        
        if (msg.equals(error01))
            printAlert("Samba non trova il file d'origine" + text, true);
        else if (msg.equals(error02))
            printAlert("Samba non può rinominare un file che esiste " + text, true);
        else if (msg.equals(error03))
            printAlert("Controllare i permessi della cartella samba " + text, true);
        else if (msg.equals(error04))
            printAlert("Login fallito, controllare username e/o password", true);
        else if (msg.equals(error05))
            printAlert("Cartella condivisa "+ text + " inesistente/errata", true);
        else
            printError(ex, c);
    }

    public void launch(IndexOutOfBoundsException ex, Class c, boolean flag_itasa) {
        String msg = ex.getMessage();
        String error01 = "String index out of range: -1";
        //TODO: gestire l'eventuale errore del numero dei sub downloadati
        if (flag_itasa && msg.equals(error01))
            printAlert("Bisogna essere registrati ad italiansubs per scaricare i sottotitoli. "
                    + "Controlla username e/o password", true);
        else
            printError(ex, c);
    }

    public void launch(UnsupportedAudioFileException ex, Class c) {
        printError(ex, c);
    }

    public void launch(UnsupportedEncodingException ex, Class c) {
        printError(ex, c);
    }

    public void launch(UnsupportedLookAndFeelException ex, Class c) {
        printError(ex, c);
    }

    public void launch(URISyntaxException ex, Class c) {
        printError(ex, c);
    }

    public void launch(XPathExpressionException ex, Class c){
        printError(ex, c);
    }

    public void launch(ZipException ex, Class c) {
        printError(ex, c);
    }

    private void printAlert(String msg, boolean status) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, status);
    }

    private void printError(Exception e, Class c) {
        ManageListener.fireTextPaneEvent(this, e.getMessage(), TextPaneEvent.ERROR, true);
        Logging.getIstance().printError(e);
    }
    
    private void printError(Exception e, String text) {
        ManageListener.fireTextPaneEvent(this, e.getMessage() + " - " + text, 
                                                        TextPaneEvent.ERROR, true);
        Logging.getIstance().printError(e);
    }
}