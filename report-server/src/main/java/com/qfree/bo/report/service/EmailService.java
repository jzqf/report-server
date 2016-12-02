package com.qfree.bo.report.service;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
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

	private final String defaultSender;
	private final String defaultFrom;
	private final String defaultSmtpHost;
	private final String defaultSmtpPort;
	private final String defaultSmtpAuth;
	private final String defaultSmtpAuthUsername;
	private final String defaultSmtpAuthPassword;
	private final String defaultSmtpStarttlsEnable;
	private final String defaultSmtpStarttlsRequired;

	/*
	 * These instance fields are used to pass the SMTP user name and password to
	 * the SMTP Authenticator inner class, SmtpAuthenticator.
	 */
	private String smtpAuthUsername = null;
	private String smtpAuthPassword = null;

	@Autowired
	public EmailService(Environment env) {
		this.env = env;
		this.defaultSender = env.getProperty("mail.sender");
		this.defaultFrom = env.getProperty("mail.from");
		this.defaultSmtpHost = env.getProperty("mail.smtp.host");
		this.defaultSmtpPort = env.getProperty("mail.smtp.port");
		this.defaultSmtpAuth = env.getProperty("mail.smtp.auth");
		this.defaultSmtpAuthUsername = env.getProperty("mail.smtp.auth.username");
		this.defaultSmtpAuthPassword = env.getProperty("mail.smtp.auth.password");
		this.defaultSmtpStarttlsEnable = env.getProperty("mail.smtp.starttls.enable");
		this.defaultSmtpStarttlsRequired = env.getProperty("mail.smtp.starttls.required");
	}

	public void sendEmail(
			String recipient,
			String subject,
			String msgBody,
			byte[] attachmentBytes,
			String attachmentInternetMediaType,
			String attachmentFilename) throws MessagingException {
		sendEmail(recipient, subject, msgBody, attachmentBytes, attachmentInternetMediaType, attachmentFilename,
				null, null, null, null, null, null, null, null, null);
	}

	public void sendEmail(
			String recipient,
			String subject,
			String msgBody,
			byte[] attachmentBytes,
			String attachmentInternetMediaType,
			String attachmentFilename,
			String sender,
			String from,
			String smtpHost,
			String smtpPort,
			String smtpAuth,
			String smtpAuthUsername,
			String smtpAuthPassword,
			String smtpStarttlsEnable,
			String smtpStarttlsRequired) throws MessagingException {

		logger.info("recipient = {}, attachmentBytes.length = {}", recipient, attachmentBytes.length);
		
		sender = (sender != null) ? sender : defaultSender;
		from = (from != null) ? from : defaultFrom;
		
		smtpHost = (smtpHost != null) ? smtpHost : defaultSmtpHost;
		smtpPort = (smtpPort != null) ? smtpPort : defaultSmtpPort;

		smtpAuth = (smtpAuth != null) ? smtpAuth : defaultSmtpAuth;
		this.smtpAuthUsername = (smtpAuthUsername != null) ? smtpAuthUsername : defaultSmtpAuthUsername;
		this.smtpAuthPassword = (smtpAuthPassword != null) ? smtpAuthPassword : defaultSmtpAuthPassword;
		smtpStarttlsEnable = (smtpStarttlsEnable != null) ? smtpStarttlsEnable : defaultSmtpStarttlsEnable;
		smtpStarttlsRequired = (smtpStarttlsRequired != null) ? smtpStarttlsRequired : defaultSmtpStarttlsRequired;

		/*
		 * Check that String parameters that should represent Boolean values do,
		 * in fact, represent Boolean values; if not, we replace their values
		 * with default values.
		 */
		if (!smtpAuth.equals("true") && !smtpAuth.equals("false")) {
			smtpAuth = "false";
		}
		if (!smtpStarttlsEnable.equals("true") && !smtpStarttlsEnable.equals("false")) {
			smtpStarttlsEnable = "false";
		}
		if (!smtpStarttlsRequired.equals("true") && !smtpStarttlsRequired.equals("false")) {
			smtpStarttlsRequired = "false";
		}

		boolean smtpAuthBoolean = Boolean.parseBoolean(smtpAuth);

		logger.info("sender = {}, from = {}, smtpHost = {}, smtpPort = {}", sender, from,smtpHost, smtpPort);

		Properties properties = new Properties();
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.auth", smtpAuth);
		properties.put("mail.smtp.starttls.enable", smtpStarttlsEnable);
		properties.put("mail.smtp.starttls.required", smtpStarttlsRequired);
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

	/**
	 * {@link Authenticator} for SMTP servers that require username/password
	 * authentication.
	 * 
	 * @author jeffreyz
	 *
	 */
	private class SmtpAuthenticator extends Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			String username = EmailService.this.smtpAuthUsername;
			String password = EmailService.this.smtpAuthPassword;
			return new PasswordAuthentication(username, password);
		}
	}

}
