package org.feedworker.client;
//IMPORT JUNIQUE
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
//IMPORT JAVA
import java.awt.EventQueue;
import java.awt.Image;
import java.net.URISyntaxException;
//IMPORT JAVAX
import javax.swing.JOptionPane;
//IMPORT FEEDWORK
import org.feedworker.client.frontend.EnhancedMainJF;
import org.feedworker.client.frontend.MainJF;
import org.feedworker.util.Common;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
//IMPORT JFACILITY
import org.jfacility.lang.JVM;
/**Client
 * 
 * @author luca judge
 */
public class FeedWorkerClient {

    public static final String APPLICATION_NAME = "FeedWorker";
    public static final String AUTHOR_NAME = "Luka Judge";
    public static final String APPLICATION_BUILD = "72";
    private static final String APPLICATION_ICON_FILE_NAME = "ApplicationIcon2.png";
    public static Image APPLICATION_ICON = Common.getResourceImage(APPLICATION_ICON_FILE_NAME);
    private static final Kernel k = Kernel.getIstance(); 
    
    public static void main(String args[]) {
        final JVM jvm = new JVM();
        boolean alreadyRunning;
        
        for (String s : System.getenv().keySet()) {
        	System.out.println(s + " : " + System.getenv(s));
        }
        
        for (Object s : System.getProperties().keySet()) {
        	System.out.println(s + " : " + System.getProperty((String)s));
        }
        System.out.println(System.getProperties().keySet());

        if (!jvm.isOrLater(15)) {
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di una versione della JVM >= 1.5",
                    APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            k.setLookFeel();
        	
            try {
                JUnique.acquireLock(APPLICATION_NAME);
                alreadyRunning = false;
            } catch (AlreadyLockedException e) {
                JOptionPane.showMessageDialog(null,
                        "C'è già la stessa applicazione avviata.",
                        APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
                alreadyRunning = true;
            }
            
            if (!alreadyRunning) {
                ResourceLocator.setWorkspace();
                ApplicationSettings.getIstance();
                Logging.getIstance();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //Kernel k = Kernel.getIstance();
                        //k.setLookFeel();
                        MainJF jframe = null;
                        if (jvm.isOrLater(16))
                            jframe = new EnhancedMainJF();
                        else if (jvm.isOrLater(15))
                            jframe = new MainJF();
                        k.loadXml();
                        k.runRss();
                        if (!ApplicationSettings.getIstance().isEnabledIconizedRun())
                            jframe.setVisible(true);
                        else {
                            try {
                                jframe.initializeSysTray();
                            } catch (URISyntaxException ex) {
                                ex.printStackTrace();
                            }
                        }                        
                    } // end run
                }); // end invokelater
            } //end if
        }
    }// end main
}// end class