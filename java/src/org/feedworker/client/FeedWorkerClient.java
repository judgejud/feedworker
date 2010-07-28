package org.feedworker.client;
//IMPORT JUNIQUE
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
//IMPORT JAVA
import java.awt.EventQueue;
//IMPORT JAVAX
import javax.swing.JOptionPane;
//IMPORT JRSS2SUB
import org.feedworker.client.frontend.MyJFrame;
import org.feedworker.client.frontend.MyJFrame_6;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
import org.lp.myUtils.lang.JVM;

/**Client
 * 
 * @author luca judge
 */
class FeedWorkerClient {
    private static final String applicationName = "FeedWorker";

    public static void main(String args[]) {
        final JVM jvm = new JVM();
        boolean alreadyRunning;

        if (!jvm.isOrLater(15)) {
            JOptionPane.showMessageDialog(null,
                "E' necessario disporre di una versione della JVM >= 1.5",
                applicationName, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            try {
                JUnique.acquireLock(applicationName);
                alreadyRunning = false;
            } catch (AlreadyLockedException e) {
                JOptionPane.showMessageDialog(null,
                    "C'è già la stessa applicazione avviata.",
                    applicationName, JOptionPane.ERROR_MESSAGE);
                alreadyRunning = true;
            }

            if (!alreadyRunning) {
                ResourceLocator.setWorkspace();
                ApplicationSettings.getIstance();
                Logging.getIstance();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Kernel k = Kernel.getIstance();
                        k.setLookFeel();
                        if (jvm.isOrLater(16))
                                new MyJFrame_6();
                        else if (jvm.isOrLater(15))
                                new MyJFrame();
                        k.loadXml();
                        k.runRss();
                    } // end run
                }); // end invokelater
            }
        }
    }// end main

    public static String getApplicationName() {
        return applicationName;
    }
}// end class