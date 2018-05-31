package com.qfree.bo.report.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.util.EncodeUtils;

public class MiscTest {

	private static final Logger logger = LoggerFactory.getLogger(MiscTest.class);

	public static void main(String[] args) {

		String password;

		password = "password1";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));
		password = "password2";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));
		password = "password3";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));
		password = "password4";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));

		password = "The quick brown fox jumps over the lazy dog";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));

		password = "newpassword";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));
		password = "newpassword2";
		System.out.println("password = " + password + ", encoded = " + EncodeUtils.encryptPassword(password));

	}
}