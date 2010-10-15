package org.feedworker.client;

import java.awt.EventQueue;
import java.awt.Image;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.EnhancedMainJF;
import org.feedworker.client.frontend.MainJF;
import org.feedworker.client.frontend.SplashScreenJW;
import org.feedworker.client.frontend.SplashTest;
import org.feedworker.util.Common;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
import org.jfacility.Application;
import org.jfacility.exception.AlreadyStartedApplicationException;
import org.jfacility.lang.JVM;
import org.jfacility.lang.MySystem;

/**
 * Client
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
        System.out.println(org.jfacility.lang.MySystem.getJavaHome());
        //final SplashScreenJW splash = new SplashScreenJW();
        //final SplashTest splash = new SplashTest(9);
        SplashTest splash = SplashTest.getInstance();
        //splash.setStatusText("Inizializzazione Feedworker");
        splash.updateStartupState("Inizializzazione Feedworker");
        
        feedWorker = Application.getInstance();
        feedWorker.setName("FeedWorker");
        feedWorker.setAuthor("Luka Judge");
        feedWorker.setBuild("129");
        feedWorker.enableSingleInstance(true);

        //splash.setStatusText("Workspace");
        splash.updateStartupState("Setting Workspace ...");
        ResourceLocator.setWorkspace();
        //splash.setStatusText("kernel");
        splash.updateStartupState("Preparing Kernel instance ...");
        K = Kernel.getIstance();
        //splash.setStatusText("LAF");
        splash.updateStartupState("Setting Look & Feel ...");
        K.setLookFeel();
        //splash.setStatusText("Controllo JVM");
        splash.updateStartupState("Checking JVM ...");
        final JVM jvm = new JVM();
        if (!jvm.isOrLater(15)) {
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di una versione della JVM >= 1.5",
                    feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
            FeedWorkerClient.getApplication().shutdown();
        } else {
            try {
                //splash.setStatusText("Junique");
            	splash.updateStartupState("Finding other FeedWorker instance ...");
                feedWorker.start();
                //splash.setStatusText("ApplicationSettings");
                splash.updateStartupState("Loading Application settings ...");
                ApplicationSettings.getIstance();
                //splash.setStatusText("Logging");
                splash.updateStartupState("Preparing Application logging ...");
                Logging.getIstance();
                //splash.setStatusText("Run");
                splash.updateStartupState("Running ...");

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        MainJF jframe = null;
                        SplashTest splash = SplashTest.getInstance();
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
                        //splash.setVisible(false);
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
                //splash.setVisible(false);
                JOptionPane.showMessageDialog(null,
                        "C'è già la stessa applicazione avviata.",
                        feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                FeedWorkerClient.getApplication().shutdown();
            }
        }
    }
}
