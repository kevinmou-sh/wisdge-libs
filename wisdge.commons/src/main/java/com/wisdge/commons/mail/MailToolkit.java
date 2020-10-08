package com.wisdge.commons.mail;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailToolkit {

	public static void send(MailSender mailSender, String subject, String recipient, String content) throws MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.auth", Boolean.toString(mailSender.isAuth()));
		props.setProperty("mail.smtp.port", Integer.toString(mailSender.getPort()));
		props.setProperty("mail.smtp.socketFactory.port", Integer.toString(mailSender.getPort()));

		Session session = Session.getInstance(props);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(mailSender.getFrom(), mailSender.getName()));
		message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));
		message.setSubject(subject, "utf-8");
		message.setContent(content, "text/html; charset=utf-8");
		message.setReplyTo(new InternetAddress[0]);
		
		Transport transport = session.getTransport();  
        transport.connect(mailSender.getHost(), mailSender.getUser(), mailSender.getPwd());  
        transport.sendMessage(message, new Address[] {new InternetAddress(recipient)});  
        transport.close();
	}
}
