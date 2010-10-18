package org.feedworker.client;

import java.awt.EventQueue;
import java.awt.Image;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.ClassicSplashScreen;
import org.feedworker.client.frontend.EnhancedMainJF;
import org.feedworker.client.frontend.EnhancedSplashScreen;
import org.feedworker.client.frontend.MainJF;
import org.feedworker.util.Common;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
import org.jfacility.Application;
import org.jfacility.exception.AlreadyStartedApplicationException;
import org.jfacility.lang.JVM;

/**
 * Client
 * 
 * @author luca judge
 */
public class FeedWorkerClient {

    private static final String APPLICATION_ICON_FILE_NAME = "ApplicationIcon.png";
    public static Image APPLICATION_ICON = Common.getResourceIcon(APPLICATION_ICON_FILE_NAME);
    private static Kernel K;
    private static Application feedWorker;

    public static Application getApplication() {
        return feedWorker;
    }

    public static void main(String args[]) {
        System.out.println(org.jfacility.lang.MySystem.getJavaHome());

        feedWorker = Application.getInstance();
        feedWorker.setName("FeedWorker");
        feedWorker.setAuthor("Luka Judge");
        feedWorker.enableSingleInstance(true);

        final JVM jvm = new JVM();
        final ClassicSplashScreen splash;

        if (jvm.isOrLater(16)) {
            splash = EnhancedSplashScreen.getInstance(12);
        } else {
            splash = ClassicSplashScreen.getInstance(12);
        }
        splash.start();
        splash.updateStartupState("Inizializzazione Feedworker");
        splash.updateStartupState("Setting Workspace ...");
        ResourceLocator.setWorkspace();
        splash.updateStartupState("Preparing Kernel instance ...");
        K = Kernel.getIstance();
        splash.updateStartupState("Setting Look & Feel ...");
        K.setLookFeel();
        splash.updateStartupState("Checking JVM ...");

        if (!jvm.isOrLater(15)) {
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di una versione della JVM >= 1.5",
                    feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
            FeedWorkerClient.getApplication().shutdown();
        } else {
            try {
                splash.updateStartupState("Finding other FeedWorker instance ...");
                feedWorker.start();
                splash.updateStartupState("Loading Application settings ...");
                ApplicationSettings.getIstance();
                splash.updateStartupState("Preparing Application logging ...");
                Logging.getIstance();
                splash.updateStartupState("Running ...");

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        MainJF jframe = null;
                        splash.updateStartupState("Loading GUI ...");
                        if (jvm.isOrLater(16)) {
                            jframe = new EnhancedMainJF();
                        } else if (jvm.isOrLater(15)) {
                            jframe = new MainJF();
                        }
                        splash.updateStartupState("Loading xml");
                        K.loadXml();
                        splash.updateStartupState("Initializing RSS...");
                        K.runRss();
                        if (!ApplicationSettings.getIstance().isEnabledIconizedRun()) {
                            splash.close();
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
                FeedWorkerClient.getApplication().shutdown();
            }
        }
    }
}
