package com.qfree.obo.report.service;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation.
	 */
	private final Environment env;

	private final String defaultSmtpHost;
	private final String defaultSmtpPort;
	private final String defaultSender;
	private final String defaultFrom;

	@Autowired
	public EmailService(Environment env) {
		this.env = env;
		this.defaultSmtpHost = env.getProperty("mail.smtp.host");
		this.defaultSmtpPort = env.getProperty("mail.smtp.port");
		this.defaultSender = env.getProperty("mail.sender");
		this.defaultFrom = env.getProperty("mail.from");
	}

	public void sendEmail(
			String recipient,
			String subject,
			String msgBody,
			byte[] attachmentBytes,
			String attachmentInternetMediaType,
			String attachmentFilename) throws MessagingException {
		sendEmail(recipient, subject, msgBody, attachmentBytes, attachmentInternetMediaType, attachmentFilename,
				null, null, null, null);
	}

	public void sendEmail(
			String recipient,
			String subject,
			String msgBody,
			byte[] attachmentBytes,
			String attachmentInternetMediaType,
			String attachmentFilename,
			String smtpHost,
			String smtpPort,
			String sender,
			String from) throws MessagingException {
		logger.info("recipient = {}, attachmentBytes.length = {}", recipient, attachmentBytes.length);
		smtpHost = (smtpHost != null) ? smtpHost : defaultSmtpHost;
		smtpPort = (smtpPort != null) ? smtpPort : defaultSmtpPort;
		sender = (sender != null) ? sender : defaultSender;
		from = (from != null) ? from : defaultFrom;
		logger.info("smtpHost = {}, smtpPort = {}, sender = {}, from = {}", smtpHost, smtpPort, sender, from);

		Properties properties = new Properties();
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.auth", "false");
		properties.put("mail.smtp.starttls.enable", "false");
		Session session = Session.getDefaultInstance(properties, null);


		/*
		 * Create the text body part of the multi-part message.
		 */
		MimeBodyPart textBodyPart = new MimeBodyPart();
		textBodyPart.setText(msgBody);

		/*
		 * Create the attachment body part of the multi-part message.
		 */
		DataSource dataSource = new ByteArrayDataSource(attachmentBytes, attachmentInternetMediaType);
		MimeBodyPart attachmentBodyPart = new MimeBodyPart();
		attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
		attachmentBodyPart.setFileName(attachmentFilename);

		/*
		 * Create a container that holds multiple body parts.
		 */
		MimeMultipart mimeMultipart = new MimeMultipart();
		mimeMultipart.addBodyPart(textBodyPart);
		mimeMultipart.addBodyPart(attachmentBodyPart);

		/*
		 * Create the multi-part message itself.
		 */
		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setSender(new InternetAddress(sender));
		mimeMessage.setFrom(new InternetAddress(from));
		mimeMessage.setSubject(subject);
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		mimeMessage.setContent(mimeMultipart);

		/*
		 * Send the message,
		 */
		Transport.send(mimeMessage);

	}

}
