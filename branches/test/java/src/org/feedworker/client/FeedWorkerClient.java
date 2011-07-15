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
import org.opensanskrit.application.LookAndFeel;
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
    private static Kernel core;
    private static Application feedWorker;
    private static ApplicationSettings feedWorkerSettings;
    private static int iteration = 11;

    public static Application getApplication() {
        return feedWorker;
    }

    public static void main(String args[]) {
        boolean debug = false;
        boolean autodownload = true;
        if (args.length>0){
            String temp = args[0].toLowerCase();
            if (temp.equalsIgnoreCase("debug=true"))
                debug=true;
            else if (temp.equalsIgnoreCase("autodownload=false"))
                autodownload=false;
            if (args.length>1){
                temp = args[1].toLowerCase();
                if (temp.equalsIgnoreCase("debug=true"))
                    debug=true;
                else if (temp.equalsIgnoreCase("autodownload=false"))
                    autodownload=false;
            }
        }
        final SplashableWindow splash;
        final Image splashImage = Common.getResourceImage("SplashImage.png");
        JVM jvm = new JVM();
        
        if (!JVM.isRuntimeJavaSun()){
            JOptionPane.showMessageDialog(null,
                    "E' necessario disporre di Java Sun e non altre come openJDK",
                    "FeedWorker", JOptionPane.ERROR_MESSAGE);
            Application.getInstance(debug).shutdown();
        } else {
            splash = SplashScreen.getInstance(iteration, splashImage);
            splash.start();
            splash.updateStartupState("Inizializzazione Feedworker");
            ResourceLocator.setWorkspace();

            feedWorker = Application.getInstance(debug);
            feedWorkerSettings = ApplicationSettings.getIstance();

            feedWorker.setName("FeedWorker");
            feedWorker.setAuthor("Luka Judge");
            feedWorker.setIcon(Common.getResourceIcon(ICON_FILENAME));
            feedWorker.enableSingleInstance(true);

            splash.updateStartupState("Checking JVM ...");
            if (!jvm.isOrLater(16)) {
                JOptionPane.showMessageDialog(null,
                        "E' necessario disporre di una versione della JVM >= 1.6",
                        feedWorker.getName(), JOptionPane.ERROR_MESSAGE);
                feedWorker.shutdown();
            } else {
                try {
                    splash.updateStartupState("Finding other FeedWorker instance ...");
                    feedWorker.start();
                    splash.updateStartupState("Preparing Kernel instance ...");
                    core = Kernel.getIstance(debug);
                    splash.updateStartupState("Setting Look & Feel ...");
                    LookAndFeel laf = feedWorker.getLookAndFeelInstance();
                    try {
                        laf.addJavaLAF();
                        laf.addJTattooLAF();
                        laf.addSyntheticaStandardLAF();
                        laf.addSyntheticaFreeLAF();
                        laf.addSyntheticaNotFreeLAF();
                        laf.setLookAndFeel(feedWorkerSettings.getApplicationLookAndFeel());
                    } catch (NotAvailableLookAndFeelException e) {
                        laf.setLookAndFeel();
                    }
                    splash.updateStartupState("Loading Application settings ...");
                    ApplicationSettings.getIstance();
                    splash.updateStartupState("Preparing Application logging ...");
                    Logging.getIstance();
                    splash.updateStartupState("Running ...");
                    final boolean auto = autodownload;

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {                        
                            splash.updateStartupState("Loading GUI ...");
                            jfMain jframe = new jfMain();
                            splash.updateStartupState("Loading xml ...");
                            core.loadXml();
                            core.loadItasaSeries();
                            splash.updateStartupState("Initializing RSS...");
                            core.runRss(auto);
                            core.searchDay(0);
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
}