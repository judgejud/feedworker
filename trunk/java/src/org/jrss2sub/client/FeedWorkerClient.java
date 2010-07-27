package org.jrss2sub.client;

//IMPORT JUNIQUE
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique; //IMPORT JAVA
import java.awt.EventQueue; //IMPORT JAVAX
import javax.swing.JOptionPane; //IMPORT JRSS2SUB
import org.jrss2sub.client.frontend.MyJFrame;
import org.jrss2sub.client.frontend.MyJFrame_6;
import org.jrss2sub.util.Logging;
import org.jrss2sub.util.ResourceLocator; //IMPORT MYUTILS
import org.lp.myUtils.lang.JVM;

/**
 * Client
 * 
 * @author luca judge
 */
class FeedWorkerClient {
	public static void main(String args[]) {
		final JVM jvm = new JVM();
		final String applicationID = "FeedWorker";
		boolean alreadyRunning;
		
		if (!jvm.isOrLater(15)) {
			JOptionPane.showMessageDialog(null, "E' necessario disporre di una versione della JVM >= 1.5", applicationID,
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} else {
			try {
				JUnique.acquireLock(applicationID);
				alreadyRunning = false;
			} catch (AlreadyLockedException e) {
				JOptionPane.showMessageDialog(null,
						"C'è già la stessa applicazione avviata, Chiuderla",
						applicationID, JOptionPane.ERROR_MESSAGE);
				alreadyRunning = true;
			}
			
			if (!alreadyRunning) {
				ResourceLocator.setWorkspace();
				Property.getIstance();
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
}// end class