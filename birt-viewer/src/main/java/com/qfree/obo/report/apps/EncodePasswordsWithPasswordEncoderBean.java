package com.qfree.obo.report.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.qfree.obo.report.ApplicationConfig;

public class EncodePasswordsWithPasswordEncoderBean {

	private static final Logger logger = LoggerFactory.getLogger(EncodePasswordsWithPasswordEncoderBean.class);

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
		//	PasswordEncoder passwordEncoder = (PasswordEncoder) context.getBean(PasswordEncoder.class);
		PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
		logger.debug("passwordEncoder = {}", passwordEncoder);

		//String encodedPassword=null;
		String unencodedPassword = null;

		unencodedPassword = "reportadmin";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));

		unencodedPassword = "password1";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));
		unencodedPassword = "password2";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));
		unencodedPassword = "password3";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));
		unencodedPassword = "password4";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));

		boolean matches1 = passwordEncoder.matches("password1",
				"$2a$08$WZyAu04Hp28LIAdclPsd9.bxXuuy6QHwpKY7aOLIzEQo7vjWZnokS");
		boolean matches2 = passwordEncoder.matches("password1",
				"$2a$08$w7EIhpNGq7scv5N.uUPBS.hW9Z2jIAeMmlqpjPz5Qv3eAOAtvypLK");
		logger.info("matches1 = {}", matches1);
		logger.info("matches2 = {}", matches2);

		unencodedPassword = "qfreereportserveradmin_Af5Dj%4$";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));

		unencodedPassword = "ReportServer*RESTADMIN";
		logger.info("unencodedPassword = {}, encodedPassword = {}", unencodedPassword,
				passwordEncoder.encode(unencodedPassword));

		context.close();
	}

}