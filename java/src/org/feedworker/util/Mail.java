package org.feedworker.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author luca
 */
public class Mail {
    public static boolean send(String smtp, String to, String text, String user) 
                                                    throws MessagingException {
        String from = "FeedWorker@italiansubs.net";
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.host", smtp);
        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);
        // Set the RFC 822 "From" header field using the
        // value of the InternetAddress.getLocalAddress method.
        message.setFrom(new InternetAddress(from));
        // Add the given addresses to the specified recipient type.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        // Set the "Subject" header field.
        message.setSubject("Avviso sottotitolo/i estratti");
        // Sets the given String as this part's content,
        // with a MIME type of "text/plain".
        String msg = "Ciao " + user + "\n\n" + text;
        msg += "\nN.B. Questa è una mail di avviso generata dal tuo client FeedWorker,"
                + "usando un indirizzo fittizio, dal quale potrai ricevere avvisi, ma "
                + "non potrai rispondere.\nSe vuoi evitare le mail, sei pregato di "
                + "disabilitare la notifica e-mail da FeedWorker.";
        message.setText(msg);
        // Send message
        Transport.send(message);
        return true;
    }
}