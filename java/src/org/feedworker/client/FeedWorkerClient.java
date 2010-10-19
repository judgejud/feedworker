package org.feedworker.client;

import java.awt.EventQueue;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.ClassicSplashScreen;
import org.feedworker.client.frontend.EnhancedMainJF;
import org.feedworker.client.frontend.EnhancedSplashScreen;
import org.feedworker.client.frontend.MainJF;
import org.feedworker.util.Common;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
import org.jfacility.lang.JVM;
import org.opensanskrit.application.AlreadyStartedApplicationException;
import org.opensanskrit.application.Application;

/**
 * Client
 * 
 * @author luca judge
 */
public class FeedWorkerClient {

    public static String ICON_FILENAME = "ApplicationIcon.png";
    private static Kernel K;
    private static Application feedWorker;
    private static ApplicationSettings feedWorkerSettings;

    public static Application getApplication() {
        return feedWorker;
    }

    public static void main(String args[]) {
        System.out.println(org.jfacility.lang.MySystem.getJavaHome());

        feedWorker = Application.getInstance();
        feedWorkerSettings = ApplicationSettings.getIstance();

        feedWorker.setName("FeedWorker");
        feedWorker.setAuthor("Luka Judge");
        feedWorker.setIcon(Common.getResourceIcon(ICON_FILENAME));
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
        feedWorker.setApplicationLookAndFeel(feedWorkerSettings.getApplicationLookAndFeel());
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