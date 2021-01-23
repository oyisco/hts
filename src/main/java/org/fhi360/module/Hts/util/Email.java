package org.fhi360.module.Hts.util;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class Email {
    public Email() {

    }


    String to, body;
    String subject;

    public Email(String to, String subject, String Body) {
        this.to = to;
        this.subject = subject;
        this.body = Body;

    }

    public void sendMail() {
        final String username = "eadrisoyibo@gmail.com";
        final String password = "@amina0909";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("zitnaija@gmail.com", "DDD"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport.send(message);
            System.err.println("oyisco done");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String activation(String username, String code) {
        String header = "Decentralized Drugs Distribution (DDD) App";
        String name = "Dear " + username;
        //String recieved = "Please foll on Bizzdesk Inventory Management Portal.";
        String please = "You DDD Activation code is";
        String copy = code;
        String body2 = "<html>" + "<head>" + "</title>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "</title>" + "</head>"
                + "<body style=\"margin: 40px; width: 90%\">"
                + "<h2 style=\"font-size: 20px; color: #004D40; font-family: 'lato', sans-serif\">" + header + "</h2>"
                + "<p style=\"font-size: 18px; color: black; font-family: Arial\">" + name + "</p>"
                + please + "<br>" + copy + "<br>"
                + "<a style=\"font-size: 20px; font-weight: bold; color: #004D40; font-family: 'lato', sans-serif\">"
                + "Decentralized Drugs Distribution Application" + "</a><br>"
                + "<a style=\"font-size: 18px; color:red; font-family: 'lato', sans-serif\">"
                + "For support contact us on" + "</a><br>"
                + "<a style=\"font-size: 16px; color:black; font-family: 'lato', sans-serif\">" + "support@fhi260.org"
                + "</a><br>" + "<a style=\"font-size: 16px; color:black; font-family: 'lato', sans-serif\">"
                + "+2347033575836" + "</a><br>" + "</div>" + "</body>" + "</html>";
        return body2;

    }


}
