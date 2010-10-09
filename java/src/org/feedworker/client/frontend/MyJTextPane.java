package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.Color;
//IMPORT JAVAX
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
//IMPORT JRSS2SUB
import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.util.Common;
/**
 *
 * @author luca
 */
public class MyJTextPane extends JTextPane implements MyTextPaneEventListener{
    //PRIVATE FINAL
    private final Color GOLD = new Color(255,215,0);
    private final Color ORANGE = new Color(255,165,0);
    private final Color HELIOTROPE = new Color(223,115,255);
    //PRIVATE
    private StyledDocument mysd;
    private Style styleOK, styleError, styleAlert, styleSub, styleTorrent,
            styleFeedItasa, styleFeedMyItasa, styleFeedSubsf, styleFeedTorrent1,
            styleFeedTorrent2, styleSynology;
    /**Costruttore
     * Inizializza la myjtextpane
     */
    public MyJTextPane() {
        super();
        this.setEditable(false);
        this.setBackground(Color.black);
        addStyle();
    }
    /**aggiunge gli stili per la jtextpane*/
    private void addStyle(){
        int dim = 12;
        mysd = (StyledDocument)this.getDocument();
        // Create a style object and then set the style attributes
        styleOK = mysd.addStyle("StyleOK", null);
        styleError = mysd.addStyle("StyleError", null);
        styleAlert = mysd.addStyle("StyleAlert", null);
        styleFeedItasa = mysd.addStyle("StyleFeedItasa", null);
        styleFeedMyItasa = mysd.addStyle("StyleFeedMyItasa", null);
        styleFeedSubsf = mysd.addStyle("StyleFeedSubsf", null);
        styleFeedTorrent1 = mysd.addStyle("StyleFeedTorrent1", null);
        styleFeedTorrent2 = mysd.addStyle("StyleFeedTorrent2", null);
        styleSub = mysd.addStyle("StyleSub", null);
        styleTorrent = mysd.addStyle("StyleTorrent", null);
        styleSynology = mysd.addStyle("StyleSynology", null);
        // Italic
        StyleConstants.setItalic(styleFeedMyItasa, true);
        // Bold
        //StyleConstants.setBold(style, false);
        // Font family
        StyleConstants.setFontFamily(styleOK, "SansSerif");
        StyleConstants.setFontFamily(styleError, "SansSerif");
        StyleConstants.setFontFamily(styleAlert, "SansSerif");
        StyleConstants.setFontFamily(styleFeedItasa, "SansSerif");
        StyleConstants.setFontFamily(styleFeedMyItasa, "SansSerif");
        StyleConstants.setFontFamily(styleFeedSubsf, "SansSerif");
        StyleConstants.setFontFamily(styleFeedTorrent1, "SansSerif");
        StyleConstants.setFontFamily(styleFeedTorrent2, "SansSerif");
        StyleConstants.setFontFamily(styleSub, "SansSerif");
        StyleConstants.setFontFamily(styleTorrent, "SansSerif");
        StyleConstants.setFontFamily(styleSynology, "SansSerif");
        // Font size
        StyleConstants.setFontSize(styleOK, dim);
        StyleConstants.setFontSize(styleError, dim);
        StyleConstants.setFontSize(styleAlert, dim);
        StyleConstants.setFontSize(styleFeedItasa, dim);
        StyleConstants.setFontSize(styleFeedMyItasa, dim);
        StyleConstants.setFontSize(styleFeedSubsf, dim);
        StyleConstants.setFontSize(styleFeedTorrent1, dim);
        StyleConstants.setFontSize(styleFeedTorrent2, dim);
        StyleConstants.setFontSize(styleSub, dim);
        StyleConstants.setFontSize(styleTorrent, dim);
        StyleConstants.setFontSize(styleSynology, dim);
        // Foreground color
        StyleConstants.setForeground(styleOK, Color.green);
        StyleConstants.setForeground(styleError, Color.red);
        StyleConstants.setForeground(styleAlert, Color.yellow);
        StyleConstants.setForeground(styleFeedItasa, Color.cyan);
        StyleConstants.setForeground(styleFeedMyItasa, Color.cyan);
        StyleConstants.setForeground(styleFeedSubsf, ORANGE);
        StyleConstants.setForeground(styleFeedTorrent1, HELIOTROPE);
        StyleConstants.setForeground(styleFeedTorrent2, Color.gray);
        StyleConstants.setForeground(styleSub, Color.white);
        StyleConstants.setForeground(styleTorrent, Color.magenta);
        StyleConstants.setForeground(styleSynology, GOLD);
    }
    /**Aggiunge alla textpane il testo con stile OK
     *
     * @param msg testo da aggiungere
     */
    public void appendOK(String msg){
        append(msg, styleOK);
    }
    /**Aggiunge alla textpane il testo con stile ALERT
     *
     * @param msg testo da aggiungere
     */
    public void appendAlert(String msg){
        append(msg, styleAlert);
    }
    /**Aggiunge alla textpane il testo con stile ERROR
     *
     * @param msg testo da aggiungere
     */
    public void appendError(String msg){
        append(msg, styleError);
    }
    /**Aggiunge alla textpane il testo con stile FEED ITASA
     *
     * @param msg testo da aggiungere
     */
    private void appendFeedItasa(String msg){
        append(msg, styleFeedItasa);
    }
    /**Aggiunge alla textpane il testo con stile FEED MYITASA
     *
     * @param msg testo da aggiungere
     */
    private void appendFeedMyItasa(String msg){
        append(msg, styleFeedMyItasa);
    }
    /**Aggiunge alla textpane il testo con stile SUBSF
     *
     * @param msg testo da aggiungere
     */
    private void appendFeedSubsf(String msg){
        append(msg, styleFeedSubsf);
    }
    /**Aggiunge alla textpane il testo con stile FEED TORRENT1
     *
     * @param msg testo da aggiungere
     */
    private void appendFeedTorrent1(String msg){
        append(msg, styleFeedTorrent1);
    }
    /**Aggiunge alla textpane il testo con stile FEED TORRENT2
     *
     * @param msg testo da aggiungere
     */
    private void appendFeedTorrent2(String msg){
        append(msg, styleFeedTorrent2);
    }
    /**Aggiunge alla textpane il testo con stile SUB
     *
     * @param msg testo da aggiungere
     */
    private void appendSub(String msg){
        append(msg, styleSub);
    }
    /**Aggiunge alla textpane il testo con stile TORRENT
     *
     * @param msg testo da aggiungere
     */
    private void appendTorrent(String msg){
        append(msg, styleTorrent);
    }
    /**Aggiunge alla textpane il testo con stile VIDEO
     *
     * @param msg testo da aggiungere
     */
    private void appendSynology(String msg){
        append(msg, styleSynology);
    }
    /**Aggiunge alla textpane il testo con stile da applicare
     *
     * @param msg testo da aggiungere
     * @param s stile da applicare
     */
    public void append(String msg, Style s){
        try {
            mysd.insertString(0, Common.actualTime() + " " + msg + "\n", s);
        } catch (BadLocationException ex) {}
    }
    @Override
    public void objReceived(MyTextPaneEvent evt) {
        if (evt.getType().equals(MyTextPaneEvent.ERROR))
            appendError(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.OK))
            appendOK(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.SUB))
            appendSub(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.ALERT))
            appendAlert(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.TORRENT))
            appendTorrent(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.FEED_ITASA))
            appendFeedItasa(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.FEED_MYITASA))
            appendFeedMyItasa(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.FEED_SUBSF))
            appendFeedSubsf(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.FEED_TORRENT1))
            appendFeedTorrent1(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.FEED_TORRENT2))
            appendFeedTorrent2(evt.getMsg());
        else if (evt.getType().equals(MyTextPaneEvent.SYNOLOGY))
            appendSynology(evt.getMsg());
    }
}//end class