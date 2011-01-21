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
    
    public static boolean send(String smtp, String to, String text) throws MessagingException {
        String from = "FeedWorker@test.org";
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
        message.setText(text);
        // Send message
        Transport.send(message);
        return true;
    }
    
    public static void main(String[] args){
        String msg = "04/01/2011 21:55:16 Estratto "
                + "Pretty.Little.Liars.s01e11.sub.itasa.srt e rinominato in 11.srt "
                + "nella cartella condivisa samba\\pretty little liars";
        try {
            send("out.alice.it", "judgejud@gmail.com", msg);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
    
}