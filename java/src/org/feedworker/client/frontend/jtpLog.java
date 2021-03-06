package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.util.Common;

/**
 * 
 * @author luca
 */
public class jtpLog extends JTextPane implements TextPaneEventListener {
    // PRIVATE FINAL

    private final Color GOLD = new Color(255, 215, 0);
    private final Color HELIOTROPE = new Color(223, 115, 255);
    // PRIVATE
    private StyledDocument mysd;
    private Style styleOK, styleError, styleAlert, styleSub, styleTorrent,
            styleFeedItasa, styleFeedMyItasa, styleFeedSubsf, styleFeedMySubsf,
            styleFeedEztv, styleFeedBtchat, styleSynology, styleDaySerial;
    
    private boolean flag_msg_normal;
    
    /**Costruttore Inizializza la myjtextpane */
    public jtpLog(boolean flag) {
        super();
        this.setEditable(false);
        this.setBackground(Color.black);
        flag_msg_normal = flag;
        addStyle();
    }

    /** aggiunge gli stili per la jtextpane */
    private void addStyle() {
        int dim = 12;
        mysd = (StyledDocument) this.getDocument();
        // Create a style object and then set the style attributes
        styleOK = mysd.addStyle("StyleOK", null);
        styleError = mysd.addStyle("StyleError", null);
        styleAlert = mysd.addStyle("StyleAlert", null);
        styleFeedItasa = mysd.addStyle("StyleFeedItasa", null);
        styleFeedMyItasa = mysd.addStyle("StyleFeedMyItasa", null);
        styleFeedSubsf = mysd.addStyle("StyleFeedSubsf", null);
        styleFeedMySubsf = mysd.addStyle("StyleFeedMySubsf", null);
        styleFeedEztv = mysd.addStyle("StyleFeedEztv", null);
        styleFeedBtchat = mysd.addStyle("StyleFeedBtchat", null);
        styleSub = mysd.addStyle("StyleSub", null);
        styleTorrent = mysd.addStyle("StyleTorrent", null);
        styleSynology = mysd.addStyle("StyleSynology", null);
        styleDaySerial = mysd.addStyle("StyleDaySerial", null);
        // Italic
        StyleConstants.setItalic(styleFeedMyItasa, true);
        StyleConstants.setItalic(styleFeedMySubsf, true);
        StyleConstants.setItalic(styleFeedBtchat, true);
        // Bold
        // StyleConstants.setBold(style, false);
        // Font family
        StyleConstants.setFontFamily(styleOK, "SansSerif");
        StyleConstants.setFontFamily(styleError, "SansSerif");
        StyleConstants.setFontFamily(styleAlert, "SansSerif");
        StyleConstants.setFontFamily(styleFeedItasa, "SansSerif");
        StyleConstants.setFontFamily(styleFeedMyItasa, "SansSerif");
        StyleConstants.setFontFamily(styleFeedSubsf, "SansSerif");
        StyleConstants.setFontFamily(styleFeedMySubsf, "SansSerif");
        StyleConstants.setFontFamily(styleFeedEztv, "SansSerif");
        StyleConstants.setFontFamily(styleFeedBtchat, "SansSerif");
        StyleConstants.setFontFamily(styleSub, "SansSerif");
        StyleConstants.setFontFamily(styleTorrent, "SansSerif");
        StyleConstants.setFontFamily(styleSynology, "SansSerif");
        StyleConstants.setFontFamily(styleDaySerial, "SansSerif");
        // Font size
        StyleConstants.setFontSize(styleOK, dim);
        StyleConstants.setFontSize(styleError, dim);
        StyleConstants.setFontSize(styleAlert, dim);
        StyleConstants.setFontSize(styleFeedItasa, dim);
        StyleConstants.setFontSize(styleFeedMyItasa, dim);
        StyleConstants.setFontSize(styleFeedSubsf, dim);
        StyleConstants.setFontSize(styleFeedMySubsf, dim);
        StyleConstants.setFontSize(styleFeedEztv, dim);
        StyleConstants.setFontSize(styleFeedBtchat, dim);
        StyleConstants.setFontSize(styleSub, dim);
        StyleConstants.setFontSize(styleTorrent, dim);
        StyleConstants.setFontSize(styleSynology, dim);
        StyleConstants.setFontSize(styleDaySerial, dim);
        // Foreground color
        StyleConstants.setForeground(styleOK, Color.green);
        StyleConstants.setForeground(styleError, Color.red);
        StyleConstants.setForeground(styleAlert, Color.yellow);
        StyleConstants.setForeground(styleFeedItasa, Color.cyan);
        StyleConstants.setForeground(styleFeedMyItasa, Color.cyan);
        StyleConstants.setForeground(styleFeedSubsf, HELIOTROPE);
        StyleConstants.setForeground(styleFeedMySubsf, HELIOTROPE);
        StyleConstants.setForeground(styleFeedEztv, Color.gray);
        StyleConstants.setForeground(styleFeedBtchat, Color.gray);
        StyleConstants.setForeground(styleSub, Color.white);
        StyleConstants.setForeground(styleTorrent, Color.magenta);
        StyleConstants.setForeground(styleSynology, GOLD);
        StyleConstants.setForeground(styleDaySerial, Color.pink);
    }

    /**Aggiunge alla textpane il testo con stile OK
     *
     * @param msg testo da aggiungere
     */
    public void appendOK(String msg) {
        append(msg, styleOK);
    }

    /**
     * Aggiunge alla textpane il testo con stile ALERT
     *
     * @param msg
     *            testo da aggiungere
     */
    public void appendAlert(String msg) {
        append(msg, styleAlert);
    }

    /**
     * Aggiunge alla textpane il testo con stile ERROR
     *
     * @param msg
     *            testo da aggiungere
     */
    public void appendError(String msg) {
        append(msg, styleError);
    }

    /**
     * Aggiunge alla textpane il testo con stile FEED ITASA
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendFeedItasa(String msg) {
        append(msg, styleFeedItasa);
    }

    /**
     * Aggiunge alla textpane il testo con stile FEED MYITASA
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendFeedMyItasa(String msg) {
        append(msg, styleFeedMyItasa);
    }

    /**
     * Aggiunge alla textpane il testo con stile SUBSF
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendFeedSubsf(String msg) {
        append(msg, styleFeedSubsf);
    }

    private void appendFeedMySubsf(String msg) {
        append(msg, styleFeedMySubsf);
    }

    /**
     * Aggiunge alla textpane il testo con stile FEED TORRENT1
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendFeedEztv(String msg) {
        append(msg, styleFeedEztv);
    }

    /**
     * Aggiunge alla textpane il testo con stile FEED TORRENT2
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendFeedBtchat(String msg) {
        append(msg, styleFeedBtchat);
    }

    /**
     * Aggiunge alla textpane il testo con stile SUB
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendSub(String msg) {
        append(msg, styleSub);
    }

    /**
     * Aggiunge alla textpane il testo con stile TORRENT
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendTorrent(String msg) {
        append(msg, styleTorrent);
    }

    /**Aggiunge alla textpane il testo con stile VIDEO
     *
     * @param msg
     *            testo da aggiungere
     */
    private void appendSynology(String msg) {
        append(msg, styleSynology);
    }

    private void appendDaySerial(String msg) {
        append(msg, styleDaySerial);
    }

    /**
     * Aggiunge alla textpane il testo con stile da applicare
     *
     * @param msg
     *            testo da aggiungere
     * @param s
     *            stile da applicare
     */
    public void append(String msg, Style s) {
        try {
            mysd.insertString(0, Common.actualTime() + " " + msg + "\n", s);
            this.setCaretPosition(0);
        } catch (BadLocationException ex) {
        }
    }

    @Override
    public void objReceived(TextPaneEvent evt) {
        if (flag_msg_normal){
            if (evt.getType().equals(TextPaneEvent.OK))
                appendOK(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.SUB))
                appendSub(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.TORRENT))
                appendTorrent(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.FEED_ITASA))
                appendFeedItasa(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.FEED_MYITASA))
                appendFeedMyItasa(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.FEED_SUBSF))
                appendFeedSubsf(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.FEED_MYSUBSF))
                appendFeedMySubsf(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.FEED_EZTV))
                appendFeedEztv(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.FEED_BTCHAT))
                appendFeedBtchat(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.SYNOLOGY))
                appendSynology(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.DAY_SERIAL))
                appendDaySerial(evt.getMsg());
        } else if (!flag_msg_normal){
            if (evt.getType().equals(TextPaneEvent.ERROR))
                appendError(evt.getMsg());
            else if (evt.getType().equals(TextPaneEvent.ALERT))
                appendAlert(evt.getMsg());
        }
    }
}// end class