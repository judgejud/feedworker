package org.feedworker.client;

import java.awt.EventQueue;
import java.awt.Image;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.jfMain;
import org.feedworker.core.Kernel;
import org.feedworker.util.Common;
import org.feedworker.exception.Logging;
import org.feedworker.util.ResourceLocator;

import org.jfacility.java.lang.JVM;

import org.opensanskrit.application.Application;
import org.opensanskrit.exception.AlreadyStartedApplicationException;
import org.opensanskrit.exception.NotAvailableLookAndFeelException;
import org.opensanskrit.widget.SplashScreen;
import org.opensanskrit.widget.interfaces.SplashableWindow;

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
        final SplashableWindow splash;
        final Image splashImage = Common.getResourceImage("SplashImage.png");
        JVM jvm = new JVM();

        splash = SplashScreen.getInstance(iteration, splashImage);
        splash.start();
        splash.updateStartupState("Inizializzazione Feedworker");
        ResourceLocator.setWorkspace();
        
        feedWorker = Application.getInstance(true);
        feedWorkerSettings = ApplicationSettings.getIstance();

        feedWorker.setName("FeedWorker");
        feedWorker.setAuthor("Luka Judge");
        feedWorker.setIcon(Common.getResourceIcon(ICON_FILENAME));
        feedWorker.enableSingleInstance(true);
        
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
                splash.updateStartupState("Preparing Kernel instance ...");
                K = Kernel.getIstance();
                splash.updateStartupState("Setting Look & Feel ...");
                try {
                    feedWorker.getIstanceLAF().addJavaLAF();        	
                    feedWorker.getIstanceLAF().addJTattooLAF();
                    feedWorker.getIstanceLAF().addSyntheticaStandardLAF();
                    feedWorker.getIstanceLAF().addSyntheticaFreeLAF();
                    feedWorker.getIstanceLAF().addSyntheticaNotFreeLAF();
                    feedWorker.getIstanceLAF().setLookAndFeel(
                                    feedWorkerSettings.getApplicationLookAndFeel());
                } catch (NotAvailableLookAndFeelException e) {
                    feedWorker.getIstanceLAF().setLookAndFeel();
                }
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
                        jframe = new jfMain();
                        splash.updateStartupState("Loading xml ...");
                        K.loadXml();
                        splash.updateStartupState("Initializing RSS...");
                        K.runRss();
                        K.searchDay(0);
                        splash.close();
                        if (!ApplicationSettings.getIstance().isEnabledIconizedRun())
                            jframe.setVisible(true);
                        else {
                            try {
                                jframe.initializeSystemTray();
                            } catch (URISyntaxException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }//end run
                }); //end runnable EventQueue.invokeLater
            } catch (AlreadyStartedApplicationException e) {
                JOptionPane.showMessageDialog(null,
                        "C'è già la stessa applicazione avviata.",
                        feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                feedWorker.shutdown();
            }
        }
    }
}