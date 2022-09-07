package com.cti.service;

import com.cti.models.ELanguage;
import com.cti.models.Project;
import fit.ColumnFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService extends ColumnFixture {

    private static String email = "gestionare.proiecte.web@gmail.com";
    private static String password = "gestionare_platforma_web";

    @Autowired
    private UserService userService;


    /* Private methods (session initialization) : */
    private static Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    // se creeaza sesiunea
    private static Session getSession(Properties props) {
        return Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailService.email, EmailService.password);
            }
        });
    }

    public boolean sendMail(Project project) {
        Properties props = getProperties();
        Session session = getSession(props);
        ELanguage language;
        System.out.println(project.getAssigneeAddress());

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(EmailService.email));

            language = userService.getPreferredLanguage(project.getAssigneeAddress());

            if (language.equals(ELanguage.ENGLISH))
                message.setSubject("Task was graded");
            else
                message.setSubject("Sarcina a fost notată");

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(project.getAssigneeAddress()));

            if (language.equals(ELanguage.ENGLISH))
                message.setText("Hello,!\n" + "Your task for " + project.getProjectName() + " was graded with: " + project.getGrade() + " by " + project.getOwner() + "\n\nFeedback: " + project.getFeedback() + "\n\nAll the best,\nProject management platform");
            else
                message.setText("Salut,!\n" + "Sarcina " + project.getProjectName() + " a fost nota tă cu: " + project.getGrade() + " de către " + project.getOwner() + "\n\nFeedback: " + project.getFeedback() + "\n\nToate cele bune,\nPlatforma de gestiune a proiectelor");

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
