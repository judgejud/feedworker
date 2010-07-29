package org.feedworker.client;

//IMPORT JUNIQUE
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JOptionPane;

import org.feedworker.client.frontend.EnhancedMainJF;
import org.feedworker.client.frontend.MainJF;
import org.feedworker.util.Convert;
import org.feedworker.util.Logging;
import org.feedworker.util.ResourceLocator;
import org.lp.myUtils.lang.JVM;

/**
 * Client
 * 
 * @author luca judge
 */
public class FeedWorkerClient {

	public static final String APPLICATION_NAME = "FeedWorker";
	public static final String AUTHOR_NAME = "Luka Judge";
	public static final String APPLICATION_BUILD = "173";
	private static final String APPLICATION_ICON_FILE_NAME = "ApplicationIcon2.png";
	public static Image APPLICATION_ICON = Convert.getResourceImage(APPLICATION_ICON_FILE_NAME);

	public static void main(String args[]) {
		final JVM jvm = new JVM();
		boolean alreadyRunning;

		if (!jvm.isOrLater(15)) {
			JOptionPane.showMessageDialog(null,
					"E' necessario disporre di una versione della JVM >= 1.5",
					APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} else {
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
						Kernel k = Kernel.getIstance();
						k.setLookFeel();
						if (jvm.isOrLater(16))
							new EnhancedMainJF();
						else if (jvm.isOrLater(15))
							new MainJF();
						k.loadXml();
						k.runRss();
					} // end run
				}); // end invokelater
			}
		}
	}// end main
}// end class