package org.feedworker.client;

import java.awt.EventQueue;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.SplashScreenClassic;
import org.feedworker.client.frontend.SplashScreenEnhanced;
import org.feedworker.client.frontend.jfMain;
import org.feedworker.client.frontend.jfMainNewer;
import org.feedworker.util.Common;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
import org.jfacility.java.lang.JVM;
import org.opensanskrit.application.Application;
import org.opensanskrit.exception.AlreadyStartedApplicationException;
import org.opensanskrit.exception.NotAvailableLookAndFeelException;

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
    private static int iteration = 11;

    public static Application getApplication() {
        return feedWorker;
    }

    public static void main(String args[]) {
        final JVM jvm = new JVM();
        final SplashScreenClassic splash;

        if (jvm.isOrLater(16))
            splash = SplashScreenEnhanced.getInstance(iteration);
        else
            splash = SplashScreenClassic.getInstance(iteration);
        splash.start();
        splash.updateStartupState("Inizializzazione Feedworker");
        ResourceLocator.setWorkspace();
        
        feedWorker = Application.getInstance(true);
        feedWorkerSettings = ApplicationSettings.getIstance();

        feedWorker.setName("FeedWorker");
        feedWorker.setAuthor("Luka Judge");
        feedWorker.setIcon(Common.getResourceIcon(ICON_FILENAME));
        feedWorker.enableSingleInstance(true);
        
        splash.updateStartupState("Preparing Kernel instance ...");
        K = Kernel.getIstance();
        splash.updateStartupState("Setting Look & Feel ...");
        try {
            feedWorker.getIstanceLAF().addJavaLAF();        	
            feedWorker.getIstanceLAF().addJTattooLAF();
            feedWorker.getIstanceLAF().addSyntheticaStandardLAF();
            feedWorker.getIstanceLAF().addSyntheticaFreeLAF();
            feedWorker.getIstanceLAF().addSyntheticaNotFreeLAF();
            feedWorker.getIstanceLAF().setLookAndFeel(feedWorkerSettings.getApplicationLookAndFeel());
        } catch (NotAvailableLookAndFeelException e) {
            feedWorker.getIstanceLAF().setLookAndFeel();
        }
        splash.updateStartupState("Checking JVM ...");
        if (!JVM.isVendorSun()){
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di JavaVendor della Sun e non altre come openJDK",
                    feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
            feedWorker.shutdown();
        } else if (!jvm.isOrLater(15)) {
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di una versione della JVM >= 1.5",
                    feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
            feedWorker.shutdown();
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
                        jfMain jframe = null;
                        splash.updateStartupState("Loading GUI ...");
                        if (jvm.isOrLater(16))
                            jframe = new jfMainNewer();
                        else if (jvm.isOrLater(15))
                            jframe = new jfMain();
                        splash.updateStartupState("Loading xml ...");
                        K.loadXml();
                        splash.updateStartupState("Initializing RSS...");
                        K.runRss();
                        //TODO: ripristinare 1volta implementato salvataggio calendar.xml
//                        K.searchDay(0);
                        splash.close();
                        if (!ApplicationSettings.getIstance().isEnabledIconizedRun())
                            jframe.setVisible(true);
                        else {
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