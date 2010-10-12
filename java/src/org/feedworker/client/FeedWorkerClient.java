package org.feedworker.client;

import java.awt.EventQueue;
import java.awt.Image;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;

import org.feedworker.client.frontend.EnhancedMainJF;
import org.feedworker.client.frontend.MainJF;
import org.feedworker.util.Common;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;

import org.jfacility.Application;
import org.jfacility.exception.AlreadyStartedApplicationException;
import org.jfacility.lang.JVM;

/**Client
 * 
 * @author luca judge
 */
public class FeedWorkerClient {

    private static final String APPLICATION_ICON_FILE_NAME = "ApplicationIcon.png";
    public static Image APPLICATION_ICON = Common.getResourceImage(APPLICATION_ICON_FILE_NAME);
    private static Kernel K;
    private static Application feedWorker;

    public static Application getApplication() {
        return feedWorker;
    }

    public static void main(String args[]) {

        feedWorker = Application.getInstance();
        feedWorker.setName("FeedWorker");
        feedWorker.setAuthor("Luka Judge");
        feedWorker.setBuild("72");
        feedWorker.enableSingleInstance(true);

        final JVM jvm = new JVM();
/*
        for (String s : System.getenv().keySet()) {
            System.out.println(s + " : " + System.getenv(s));
        }

        for (Object s : System.getProperties().keySet()) {
            System.out.println(s + " : " + System.getProperty((String) s));
        }
*/
        ResourceLocator.setWorkspace();
        K = Kernel.getIstance();
        K.setLookFeel();

        if (!jvm.isOrLater(15)) {
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di una versione della JVM >= 1.5",
                    feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
            feedWorker.shutdown();
        } else {
            try {
                feedWorker.start();
                ApplicationSettings.getIstance();
                Logging.getIstance();

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        MainJF jframe = null;
                        if (jvm.isOrLater(16)) {
                            jframe = new EnhancedMainJF();
                        } else if (jvm.isOrLater(15)) {
                            jframe = new MainJF();
                        }
                        K.loadXml();
                        K.runRss();
                        if (!ApplicationSettings.getIstance().isEnabledIconizedRun()) {
                            jframe.setVisible(true);
                        } else {
                            try {
                                jframe.initializeSysTray();
                            } catch (URISyntaxException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

            } catch (AlreadyStartedApplicationException e) {
                JOptionPane.showMessageDialog(null,
                        "C'è già la stessa applicazione avviata.",
                        feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                feedWorker.shutdown();
            }
        }
    }
}