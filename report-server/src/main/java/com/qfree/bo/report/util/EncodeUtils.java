package com.qfree.obo.report.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(EncodeUtils.class);

	public static String encryptPassword(String password) {
		String sha1 = null;
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(saltPassword(password).getBytes("UTF-8"));
			sha1 = Base64.encodeBase64String(crypt.digest());	// raw byte array encoded into a String using Base64
			//			sha1 = bytesToHex(crypt.digest());	// raw byte array encoded into a hex String
		}
		catch(NoSuchAlgorithmException e) {
			logger.error("Error performing SHA-1 digest.", e);
		}
		catch(UnsupportedEncodingException e) {
			logger.error("Error encoding hashed password in UTF-8.", e);
		}
		return sha1;
	}
	
	public static String saltPassword(String password) {
		return "" + password + "";	// Do nothing for now
	}

	public static String bytesToHex(byte[] bytes) {
		final StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
}