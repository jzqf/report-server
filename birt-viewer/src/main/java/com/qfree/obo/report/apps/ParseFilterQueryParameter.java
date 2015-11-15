package com.qfree.obo.report.apps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseFilterQueryParameter {

	private static final Logger logger = LoggerFactory.getLogger(ParseFilterQueryParameter.class);

	private static final String CONDITION_ATTR_NAME = "attributeName";
	private static final String CONDITION_OPERATOR = "comparisonOperator";
	private static final String CONDITION_VALUE = "comparisonValue";

	public static void main(String[] args) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//		while (true) {
		try {
			//			String filterQueryParamText = "jobStatus.eq.\"FAILED\"";
			String filterQueryParamText = "jobStatus.eq.\"FAILED\".or.jobStatus.eq.\"COMPLETED\".and.someField.ne.\"some value\".and.annotherField.gt.\"10\"";
			System.out.println("filterQueryParamText = " + filterQueryParamText);

			//	String filterQueryParamRegex = "((\\w+\\.(eq|ne|lt|le|ge|gt)\\.\".*?\"(\\.or\\.)*)+)(\\.and\\.)*";
			//	System.out.println("filterQueryParamRegex = " + filterQueryParamRegex);
			//	Pattern pattern = Pattern.compile(filterQueryParamRegex);
			String filterQueryParamRegex = "" +
					"(                              # Group 1 to capture the condition string that appears \n" +
					"                               # between each '.and.' operator.                       \n" +
					"                                                                                      \n" +
					"  (                            # Group 2 to capture a single logical condition, i.e., \n" +
					"                               #   attrname.(eq|ne|lt|le|ge|gt).\"some value\"[.or.]  \n" +
					"                               # The final '.or.' is optional so it might not appear. \n" +
					"                                                                                      \n" +
					"    \\w+                       # Attribute name, attrname.                            \n" +
					"    \\.(eq|ne|lt|le|ge|gt)\\.  # The logical comparison operator:                     \n" +
					"                               #   .eq. | .ne. | .lt. | .le. | .ge | .gt.             \n" +
					"                               # The 2-letter operator name is captured in group 3.   \n" +
					"    \".*?\"                    # Comparison value, enclosed in double quotes.         \n" +
					"                               # '*' is used because the comparison term may be a     \n" +
					"                               # zero-length string.                                  \n" +
					"                               # '?' is used to make the match lazy (ungreedy or      \n" +
					"                               # reluctant) so that we match on the first closing     \n" +
					"                               # double quote. This means that the comparison value   \n" +
					"                               # should NOT contain a double quote itself. If it      \n" +
					"                               # does, we will need to treat it specially somehow one \n" +
					"                               # day (currently, this is NOT supported).              \n" +
					"    (\\.or\\.)*                # Group 4 to capture and optional '.or.' operator.     \n" +
					"                                                                                      \n" +
					"  )+                           # Group 2 close. There must be at least one condition; \n" +
					"                               # hence, the '+' here is necessary.                    \n" +
					"                                                                                      \n" +
					")                              # Group 1 close                                        \n" +
					"(\\.and\\.)*                   # Group 5 to capture the '.and.' operator. This string \n" +
					"                               # might not appear and never at the end, so '*' is     \n" +
					"                               # necessary.\n";
			System.out.println("filterQueryParamRegex = \n" + filterQueryParamRegex);
			Pattern pattern = Pattern.compile(filterQueryParamRegex, Pattern.COMMENTS);

			Matcher matcher = pattern.matcher(filterQueryParamText);

			/*
			 * Extract substrings of filterQueryParamText, each of which 
			 * corresponds to a conditional expression that is logically and'ed
			 * with each other.
			 */
			List<String> andConditionStrings = new ArrayList<>();
			while (matcher.find()) {
				//	System.out.println(String.format("I found the text" +
				//			" §%s§ starting at " +
				//			"index %d and ending at index %d.",
				//			matcher.group(),
				//			matcher.start(),
				//			matcher.end()));
				System.out.println("matcher.groupCount() = " + matcher.groupCount());
				for (int i = 0; i <= matcher.groupCount(); i++) {
					/*
					 * Some of the capture groups are matched multiple times.
					 * Here, matcher.group(i) seems to return the LAST value
					 * captured for the most recent match.
					 */
					System.out.println("group " + i + " |" + matcher.group(i) + "|");
				}
				andConditionStrings.add(matcher.group(1));
			}
			System.out.println("\nandConditionStrings = ");
			for (String s : andConditionStrings) {
				System.out.println(s);
			}

			/*
			 * Now parse each conditional expression that was extracted from 
			 * filterQueryParamText that are to be AND'ed together. The first
			 * parsing level above parsed filterQueryParamText into strings:
			 * 
			 * andConditiona1, andConditional2, ...
			 * 
			 * where the conditions associated each of these strings must be 
			 * AND'ed together.
			 * 
			 * Each string andConditiona1, andConditional2, ..., consists of 
			 * multiple logical conditions that are OR'ed together, although
			 * each may just consist of a single condition that is not OR'ed at
			 * all.
			 * 
			 * Each logical condition to be OR'ed is made up of 3 strings:
			 * 
			 *   1. Attribute name.
			 *   2. Logical operator. One of:
			 *       "eq", "ne", "lt", "le", "ge", "gt"
			 *   3. Comparison string.
			 * 
			 * For example, to filter on roleId, these three strings might be:
			 * 
			 *   "roleId", "eq", "b85fd129-17d9-40e7-ac11-7541040f8627".
			 * 
			 * Each such logical condition is represented as a Map with keys:
			 * 
			 *   "attributeName", "comparisonOperator" & "comparisonValue"
			 * 
			 * Each such Map is stored in a List, with one Map (list entry) for
			 * each logical condition that is OR'ed together to make up one of
			 * conditional expressions, andConditiona1, andConditional2, ...
			 * 
			 * We will end up with one List of Map's for each conditional 
			 * expression, andConditiona1, andConditional2, ...
			 * 
			 * Each of these List of Map's will be stored in the list 
			 * "filterConditions" 
			 */

			/*
			 * Regex for parsing each of the strings:
			 * 
			 *   andConditiona1, andConditional2, ...
			 */
			String andConditionRegex = "" +
					"    (                          # Group 1 to capture a single logical condition        \n" +
					"                               # *without* the trailing '.or.', i.e.,                 \n" +
					"                               #   attrname.(eq|ne|lt|le|ge|gt).\"some value\"        \n" +
					"                                                                                      \n" +
					"    \\w+                       # Attribute name, attrname.                            \n" +
					"    \\.(eq|ne|lt|le|ge|gt)\\.  # The logical comparison operator:                     \n" +
					"                               #   .eq. | .ne. | .lt. | .le. | .ge | .gt.             \n" +
					"                               # The 2-letter operator name is captured in group 2.   \n" +
					"    \".*?\"                    # Comparison value, enclosed in double quotes.         \n" +
					"                               # '*' is used because the comparison term may be a     \n" +
					"                               # zero-length string.                                  \n" +
					"                               # '?' is used to make the match lazy (ungreedy or      \n" +
					"                               # reluctant) so that we match on the first closing     \n" +
					"                               # double quote. This means that the comparison value   \n" +
					"                               # should NOT contain a double quote itself. If it      \n" +
					"                               # does, we will need to treat it specially somehow one \n" +
					"                               # day (currently, this is NOT supported).              \n" +
					"                                                                                      \n" +
					"    )                          # Group 1 close for capturing a single logical         \n" +
					"                               # condition *without* the trailing '.or.'.             \n" +
					"                                                                                      \n" +
					"    (\\.or\\.)*                # Group 3 to capture and optional '.or.' operator.     \n";
			System.out.println("andConditionRegex = \n" + andConditionRegex);
			Pattern andConditionPattern = Pattern.compile(andConditionRegex, Pattern.COMMENTS);

			/*
			 * Regex for parsing each of the strings that represent a 
			 * single logical condition of the form:
			 * 
			 *    attrname.(eq|ne|lt|le|ge|gt)."some value"
			 */
			String orConditionRegex = "" +
					"    (\\w+)                     # Group 1: Attribute name, attrname.                   \n" +
					"    \\.(eq|ne|lt|le|ge|gt)\\.  # The logical comparison operator:                     \n" +
					"                               #   .eq. | .ne. | .lt. | .le. | .ge | .gt.             \n" +
					"                               # The 2-letter operator name is captured in group 2.   \n" +
					"    \"(.*?)\"                  # Comparison value, enclosed in double quotes. The     \n" +
					"                               # value itselft is captured in group 3.                \n" +
					"                               # '*' is used because the comparison term may be a     \n" +
					"                               # zero-length string.                                  \n" +
					"                               # '?' is used to make the match lazy (ungreedy or      \n" +
					"                               # reluctant) so that we match on the first closing     \n" +
					"                               # double quote. This means that the comparison value   \n" +
					"                               # should NOT contain a double quote itself. If it      \n" +
					"                               # does, we will need to treat it specially somehow one \n" +
					"                               # day (currently, this is NOT supported).              \n";
			System.out.println("orConditionRegex = \n" + orConditionRegex);
			Pattern orConditionPattern = Pattern.compile(orConditionRegex, Pattern.COMMENTS);

			List<List<Map<String, String>>> filterConditions = new ArrayList<>();
			for (String andConditionString : andConditionStrings) {

				System.out.println("\nString to parse: " + andConditionString);

				List<Map<String, String>> orConditions = new ArrayList<>();
				filterConditions.add(orConditions);

				Matcher andConditionMatcher = andConditionPattern.matcher(andConditionString);

				/*
				 * Parse andConditionString into a List of Map's where each Map
				 * corresponds to a single atomic logical condition that it to
				 * be OR'ed with each other.
				 */

				/*
				 * Extract substrings of andConditionString, each of which 
				 * corresponds to a conditional expression that is logically 
				 * or'ed with each other.
				 */
				List<String> orConditionStrings = new ArrayList<>();
				while (andConditionMatcher.find()) {
					System.out.println("andConditionMatcher.groupCount() = " + andConditionMatcher.groupCount());
					for (int i = 0; i <= andConditionMatcher.groupCount(); i++) {
						System.out.println("group " + i + " |" + andConditionMatcher.group(i) + "|");
					}
					String orConditionString = andConditionMatcher.group(1);
					System.out.println("orConditionString = " + orConditionString);
					orConditionStrings.add(orConditionString);

					/*
					 * Parse the logical condition into 3 strings:
					 * 
					 *   1. Attribute name.
					 *   2. Logical operator. One of:
					 *       "eq", "ne", "lt", "le", "ge", "gt"
					 *   3. Comparison string.
					 * 
					 * These are placed into a Map, which is inserted into the
					 * list of conditions that are to be OR'ed together.
					 */
					Matcher orConditionMatcher = orConditionPattern.matcher(orConditionString);
					if (orConditionMatcher.find()) {
						if (orConditionMatcher.groupCount() >= 3) {
							System.out.println("(attrName, op, value) = ("
									+ orConditionMatcher.group(1) + ", "
									+ orConditionMatcher.group(2) + ", "
									+ orConditionMatcher.group(3) + ")");
							Map<String, String> orCondition = new HashMap<>(3);
							orCondition.put(CONDITION_ATTR_NAME, orConditionMatcher.group(1));
							orCondition.put(CONDITION_OPERATOR, orConditionMatcher.group(2));
							orCondition.put(CONDITION_VALUE, orConditionMatcher.group(3));
							/*
							 * Insert the Map into the List of conditions to be
							 * OR'ed with each other.
							 */
							orConditions.add(orCondition);
						}
					}
				}

			}

			for (List<Map<String, String>> filterCondition : filterConditions) {
				System.out.println("filterCondition = " + filterCondition);
			}

		} catch (PatternSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		}
	}
}
