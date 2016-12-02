package com.qfree.obo.report.apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexTestHarness {

	private static final Logger logger = LoggerFactory.getLogger(RegexTestHarness.class);

	public static void main(String[] args) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				//	System.out.print("Enter input string to search: ");
				//	String string = br.readLine();
				String string = "jobStatus.eq.\"FAILED\".or.jobStatus.eq.\"COMPLETED\".and.someField.ne.\"some value\".and.annotherField.gt.\"10\"";
				System.out.print("string = " + string);

				System.out.print("\nEnter your regex: ");
				String regex = br.readLine();

				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(string);

				boolean found = false;
				while (matcher.find()) {
					System.out.println(String.format("I found the text" +
							" §%s§ starting at " +
							"index %d and ending at index %d.",
							matcher.group(),
							matcher.start(),
							matcher.end()));
					//				console.format("I found the text" +
					//						" \"%s\" starting at " +
					//						"index %d and ending at index %d.%n",
					//						matcher.group(),
					//						matcher.start(),
					//						matcher.end());
					found = true;
				}
				if (!found) {
					System.out.println("No match found.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
