package com.qfree.obo.report.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.ResourceFilterExecutionException;
import com.qfree.obo.report.exceptions.ResourceFilterParseException;
import com.qfree.obo.report.exceptions.RestApiException;

public class RestUtils {

	private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);

	public static final boolean AUTO_EXPAND_PRIMARY_RESOURCES = false;
	public static final boolean FILTER_INACTIVE_RECORDS = true;

	public static final String CONDITION_ATTR_NAME = "attributeName";
	public static final String CONDITION_OPERATOR = "comparisonOperator";
	public static final String CONDITION_VALUE = "comparisonValue";

	/**
	 * Default value for the number of instance resources to make up one "page"
	 * of a collection resource.
	 */
	public static final int PAGINATION_LIMIT_DEFAULT = 25;

	/**
	 * Version numbers for JAX-RS ReST endpoints exposed by this application.
	 * <p>
	 * 
	 * These version numbers are endpoint-specific, i.e., the version number for
	 * an endpoint is only changed if the API for that endpoint changes; other
	 * endpoints retain their current version numbers.
	 * 
	 * @author Jeffrey Zelt
	 *
	 */
	public enum RestApiVersion {
		v1("1"),
		v2("2"),
		v3("3"),
		v4("4"),
		v5("5");

		private String version;

		private RestApiVersion(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}
	}

	/**
	 * * Extracts and returns the ReST API version from the HTTP "Accept"
	 * header.
	 * 
	 * This method expects ReST API version to be embedded in the HTTP "Accept"
	 * header as a media type parameter with name "v". The accept header must
	 * therefore be of the form:
	 * 
	 * ...;v=version...
	 * 
	 * Here "v" is the parameter name and "version" is the version that will be
	 * returned.
	 * 
	 * @param httpAcceptHeader
	 * @param defaultVersion
	 * @return
	 */
	public static RestApiVersion extractAPIVersion(String httpAcceptHeader, RestApiVersion defaultVersion) {
		RestApiVersion apiVersion = defaultVersion;

		/*
		 * Support Accept headers of the form:
		 * 
		 *     <media-type>;v=<version>
		 *     <media-type>;morestuff&v=<version>
		 * 
		 * Therefore, the regex for the delimiter specifies ";" or "&".
		 */
		for (String token : httpAcceptHeader.split("[;&]")) {
			String[] potentialKeyValuePair = token.split("=");
			if (potentialKeyValuePair.length == 2) {
				String key = potentialKeyValuePair[0].trim();
				if (key.equalsIgnoreCase("v")) {
					String apiVersionString = potentialKeyValuePair[1].trim();
					try {
						apiVersion = RestApiVersion.valueOf("v" + apiVersionString);
					} catch (IllegalArgumentException | NullPointerException e) {
						/* '"v" + apiVersionString' is not a valid element of the
						 * RestApiVersion enum. Return default version instead.
						 */
					}
					break;
				}
			}
		}
		return apiVersion;
	}

	public static void ifNullThen404(Object object, Class<?> objectClass, String attrName, String attrValue) {
		if (object == null) {
			String message = String.format("No %s for %s '%s'", objectClass.getSimpleName(), attrName, attrValue);
			logger.debug("message = {}", message);
			throw new RestApiException(RestError.NOT_FOUND_RESOUCE, message, objectClass, attrName, attrValue);
		}
	}

	public static void ifAttrNullOrBlankThen403(Object object, Class<?> objectClass, String attrName) {
		/*
		 * Create different messages based on whether "object is null or blank,
		 * and based on which which arguments passed to this method are not 
		 * null.
		 */
		if (object == null || (object instanceof String && object.toString().isEmpty())) {
			String message = null;
			if (object == null) {
				if (objectClass != null && attrName != null) {
					message = String.format("Attribute '%s' of %s is null", attrName, objectClass.getSimpleName());
				} else if (objectClass != null) {
					message = String.format("An attribute %s is null", objectClass.getSimpleName());
				} else if (attrName != null) {
					message = String.format("'%s' is null", attrName);
				} else {
					message = RestError.FORBIDDEN_ATTRIBUTE_NULL.getErrorMessage();
				}
				throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL, message, objectClass, attrName,
						null);
			} else if (object instanceof String && object.toString().isEmpty()) {
				if (objectClass != null && attrName != null) {
					message = String.format("Attribute '%s' of %s is blank", attrName, objectClass.getSimpleName());
				} else if (objectClass != null) {
					message = String.format("An attribute %s is blank", objectClass.getSimpleName());
				} else if (attrName != null) {
					message = String.format("'%s' is blank", attrName);
				} else {
					message = RestError.FORBIDDEN_ATTRIBUTE_BLANK.getErrorMessage();
				}
				throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_BLANK, message, objectClass, attrName,
						null);
			}
		}
	}

	public static void ifAttrNullThen403(Object object, Class<?> objectClass, String attrName) {
		/*
		 * Create different messages based on on which which arguments passed to
		 * this method are not null.
		 */
		if (object == null) {
			String message = null;
			if (objectClass != null && attrName != null) {
				message = String.format("Attribute '%s' of %s is null", attrName, objectClass.getSimpleName());
			} else if (objectClass != null) {
				message = String.format("An attribute %s is null", objectClass.getSimpleName());
			} else if (attrName != null) {
				message = String.format("'%s' is null", attrName);
			} else {
				message = RestError.FORBIDDEN_ATTRIBUTE_NULL.getErrorMessage();
			}
			throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL, message, objectClass, attrName,
					null);
		}
	}

	public static void ifNewResourceIdNotNullThen403(Object id, Class<?> classToCreate, String attrName,
			Object attrValueObject) {
		if (id != null) {

			String attrValueString = "";
			if (attrValueObject != null) {
				attrValueString = attrValueObject.toString();
			}

			String message = String.format("%s = '%s'. The id must be null when creating a new %s. "
					+ RestError.FORBIDDEN_NEW_RESOUCE_ID_NOTNULL.getErrorMessage(), attrName, attrValueString,
					classToCreate.getSimpleName());
			throw new RestApiException(RestError.FORBIDDEN_NEW_RESOUCE_ID_NOTNULL, message,
					classToCreate, attrName, null);
		}
	}

	public static void ifNotValidXmlThen403(String xml, String errorMsg,
			Class<?> referenceClass, String attrName, String attrValue) {
		try {
			XmlUtils.validate(xml);
		} catch (SAXException | IOException e) {
			if (errorMsg == null) {
				errorMsg = RestError.FORBIDDEN_XML_NOT_VALID.getErrorMessage();
			}
			throw new RestApiException(RestError.FORBIDDEN_XML_NOT_VALID, errorMsg,
					referenceClass, attrName, attrValue);
		}
	}

	public static <T> List<T> getPageOfList(List<T> entityList, Map<String, List<String>> queryParams) {
		//	for (T entity : entityList) {
		//		logger.info("entity (before paging) = {}", entity);
		//	}

		if (queryParams.containsKey(ResourcePath.PAGE_OFFSET_QP_KEY)
				&& queryParams.containsKey(ResourcePath.PAGE_LIMIT_QP_KEY)) {

			List<String> pageOffsets = queryParams.get(ResourcePath.PAGE_OFFSET_QP_KEY);
			List<String> pageLimits = queryParams.get(ResourcePath.PAGE_LIMIT_QP_KEY);
			logger.info("pageOffsets, pageLimits = ({},{})", pageOffsets, pageLimits);
			if (pageOffsets.size() > 0 && pageLimits.size() > 0) {

				/*
				 * The arrays pageOffsets & pageLimits should only contains a 
				 * single value each that can be interpreted as integers. 
				 * 
				 * If there are additional values in the array, they are 
				 * ignored.
				 * 
				 * I either cannot be interpreted as integers, we just return
				 * the entire list.
				 */
				int pageOffset;
				try {
					pageOffset = Integer.parseInt(pageOffsets.get(0));
				} catch (Exception e) {
					pageOffset = 0;
				}
				int pageLimit;
				try {
					pageLimit = Integer.parseInt(pageLimits.get(0));
				} catch (Exception e) {
					pageLimit = entityList.size();
				}

				/*
				 * Adjust pageOffset & pageLimit, if necessary, to be compatible
				 * with the size of "entityList".
				 */

				if (pageOffset < 0) {
					pageOffset = 0;
					//				} else if (pageOffset > entityList.size() - 1) {
					//					/*
					//					 * This will result in a zero-sized list being returned.
					//					 * This is correct for a value of "pageOffset" so large as
					//					 * this. 
					//					 */
					//					pageOffset = entityList.size() - 1;
				}

				if (pageLimit < 0) {
					pageLimit = 0;
				} else if (pageOffset + pageLimit > entityList.size()) {
					pageLimit = entityList.size() - pageOffset;
				}

				logger.info("pageOffset, pageLimit = ({}, {})", pageOffset, pageLimit);

				if (pageOffset > entityList.size() - 1) {
					/*
					 * Return an empty list because pageOffset is too large.
					 */
					entityList = entityList.subList(0, 0);
				} else {
					/*
					 * pageOffset & pageLimit should have been adjusted so that the
					 * call to "subList(...)" here will not throw an exception.
					 * Nevertheless, we still wrap it in a "try" to be 100% sure.
					 */
					try {
						entityList = entityList.subList(pageOffset, pageOffset + pageLimit);
					} catch (Exception e) {
					}
				}

				//	for (T entity : entityList) {
				//		logger.info("entity (after paging) = {}", entity);
				//	}

			}
		}

		return entityList;
	}

	/**
	 * Method to check the query parameters provided in a REST URI that are used
	 * for pagination.
	 * <p>
	 * If necessary, these lists are modified to contain reasonable values and
	 * then these lists are inserted into a map of all query paraemters for the
	 * URI endpoint call.
	 * 
	 * @param pageOffset
	 *            List containing values for REST URI query query parameter for
	 *            specifying how many entities to skip to reach the specified
	 *            page
	 * @param pageLimit
	 *            List containing values for REST URI query query parameter for
	 *            specifying the maximum number entities to return in a page
	 * @param queryParams
	 *            A map of all query parameters from a REST endpoint URI, but
	 *            without the pagination query parameters pageOffset and
	 *            pageLimit
	 */
	public static void checkPaginationQueryParams(
			List<String> pageOffset,
			List<String> pageLimit,
			Map<String, List<String>> queryParams) {

		/*
		 * If the lists pageOffset & pageOffset lists contain more than one 
		 * element, reduce the list to a single item (there is no way to 
		 * interpret multiple values).
		 * 
		 * Note: We CANNOT use either of:
		 * 
		 *           pageOffset = pageOffset.subList(0, 0);
		 *           pageLimit = pageLimit.subList(0, 0);
		 *           
		 *       here to remove all but the first element from the list because
		 *       "subList" returns a view into the list; it does not modify the
		 *       underlying list, which is what we want to do here.
		 */
		if (pageOffset.size() > 1) {
			for (int i = 1; i < pageOffset.size(); i++) {
				pageOffset.remove(i);
			}
		}
		if (pageLimit.size() > 1) {
			for (int i = 1; i < pageLimit.size(); i++) {
				pageLimit.remove(i);
			}
		}

		/*
		 * Perform sanity checks on pageOffset & pageLimit elements and adjust
		 * them to default/sensible values, if necessary. 
		 */
		if (pageOffset.size() == 1) {
			try {
				int offset = Integer.parseInt(pageOffset.get(0));
				if (offset < 0) {
					pageOffset.clear(); // sensible value will be inserted below
				}
			} catch (Exception e) {
				pageOffset.clear(); // sensible value will be inserted below
			}
		}
		if (pageLimit.size() == 1) {
			try {
				int limit = Integer.parseInt(pageLimit.get(0));
				if (limit < 0 || limit > PAGINATION_LIMIT_DEFAULT) {
					pageLimit.clear(); // default value will be inserted below
				}
			} catch (Exception e) {
				pageLimit.clear(); // default value will be inserted below
			}
		}

		/*
		 * If, however, the lists are empty, insert the default/sensible value.
		 */
		if (pageOffset.size() == 0) {
			pageOffset.add("0");
		}
		if (pageLimit.size() == 0) {
			pageLimit.add(Integer.toString(PAGINATION_LIMIT_DEFAULT));
		}

		queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset);
		queryParams.put(ResourcePath.PAGE_LIMIT_QP_KEY, pageLimit);
	}

	public static Integer paginationOffsetQueryParam(Map<String, List<String>> queryParams) {
		Integer offset = null;

		List<String> pageOffset = queryParams.get(ResourcePath.PAGE_OFFSET_QP_KEY);
		if (pageOffset != null) {
			try {
				offset = Integer.parseInt(pageOffset.get(0));
			} catch (Exception e) {
			}
		}

		return offset;
	}

	public static Integer paginationLimitQueryParam(Map<String, List<String>> queryParams) {
		Integer limit = null;

		List<String> pageLimit = queryParams.get(ResourcePath.PAGE_LIMIT_QP_KEY);
		if (pageLimit != null) {
			try {
				limit = Integer.parseInt(pageLimit.get(0));
			} catch (Exception e) {
			}
		}

		return limit;
	}

	public static List<List<Map<String, String>>> parseFilterQueryParam(String filterQueryParamText)
			throws ResourceFilterParseException {

		logger.info("filterQueryParamText = {}", filterQueryParamText);

		/*
		 * This regex is used to break the entire "filter" query parameter value
		 * into substrings, each of which is a logical condition to be AND'ed
		 * with each other.
		 */
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
				"                               # double quote. Even so, the comparison value may      \n" +
				"                               # contain double quotes itself and this should still   \n" +
				"                               # work because there are patterns following that must  \n" +
				"                               # also be matched.                                     \n" +
				"    (\\.or\\.)*                # Group 4 to capture an  optional '.or.' operator.     \n" +
				"                                                                                      \n" +
				"  )+                           # Group 2 close. There must be at least one condition; \n" +
				"                               # hence, the '+' here is necessary.                    \n" +
				"                                                                                      \n" +
				")                              # Group 1 close                                        \n" +
				"((\\.and\\.)|$)                # Group 5 to capture *either* an intervening '.and.'   \n" +
				"                               # operator *or* an end of line.                        \n";

		/*
		 * Regex for parsing each substring that represents a filter condition
		 * to be ADD'ed together, i.e., the substrings from filterQueryParamText
		 * that are delimited by ".and." that are extracted by the previous 
		 * regex. These substrings are parsed by this regex into substrings that
		 * represent logical conditions to be OR'ed together. Each of these
		 * substrings are of the form:
		 * 
		 *    attrname.(eq|ne|lt|le|ge|gt)."some value"
		 * 
		 * and are processed into their components by the next regex.
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
				//"    ((\\.or\\.)|$)             # Group 4 to capture *either* an intervening '.or.'    \n" +
				"    ((\\..+?\\.)|$)            # Group 4 to capture *either* an intervening '.or.'    \n" +
				"                               # operator *or* an end of line. We use '.+' here       \n" +
				"                               # instead of the literal string 'or' so that we can    \n" +
				"                               # test if the operatore matched was, in fact, 'or'.    \n" +
				"                               # The match is reluctant (+?) so that we match on the  \n" +
				"                               # first matching period.                               \n";

		/*
		 * Regex for parsing each of the strings that represent a single logical
		 * condition of the form:
		 * 
		 *    attrname.(eq|ne|lt|le|ge|gt)."some value"
		 * or
		 *    attrname.op."attrvalue"
		 * 
		 * This regex parses strings of this form into the 3 substrings:
		 * 
		 *    1. attrname
		 *    2. op           (2-character operator without surrounding periods)
		 *    3. attrvalue    (without the double quotes)
		 */
		String orConditionRegex = "" +
				"    (\\w+)                     # Group 1: Attribute name, attrname.                   \n" +
				"    \\.(eq|ne|lt|le|ge|gt)\\.  # The logical comparison operator:                     \n" +
				"                               #   .eq. | .ne. | .lt. | .le. | .ge | .gt.             \n" +
				"                               # The 2-letter operator name is captured in group 2.   \n" +
				"    \"(.*)\"$                  # Comparison value, enclosed in double quotes. The     \n" +
				"                               # value itselft is captured in group 3.                \n" +
				"                               # '*' is used because the comparison term may be a     \n" +
				"                               # zero-length string.                                  \n" +
				"                               # $ ensures that we match on the last closing double   \n" +
				"                               # quote. This means that the comparison value can      \n" +
				"                               # contain double quotes itself.                        \n";

		List<List<Map<String, String>>> filterConditions = new ArrayList<>();
		if (filterQueryParamText.isEmpty()) {
			return filterConditions;
		}

		try {

			Pattern pattern = Pattern.compile(filterQueryParamRegex, Pattern.COMMENTS);
			Matcher matcher = pattern.matcher(filterQueryParamText);

			/*
			 * These variables are used to check if we were able to match the
			 * *entire* string. If not an exception is thrown that includes the
			 * substring that could not be matched.
			 */
			int end = 0;
			boolean hitEnd = false;

			/*
			 * Extract substrings of filterQueryParamText, each of which 
			 * corresponds to a conditional expression that is logically and'ed
			 * with each other. These are substrings of filterQueryParamText
			 * that are delimited with ".and.", although they are extracted 
			 * using the regex filterQueryParamRegex to provide a more robust
			 * parsing (the string ".and." must occur within filter values, even
			 * though that is highly unlikely).
			 */
			List<String> andConditionStrings = new ArrayList<>();
			while (matcher.find()) {
				for (int i = 0; i <= matcher.groupCount(); i++) {
					/*
					 * Some of the capture groups are matched multiple times.
					 * Here, matcher.group(i) seems to return the LAST value
					 * captured for the most recent match.
					 */
					logger.info("group {}: {}", i, matcher.group(i));
				}
				andConditionStrings.add(matcher.group(1));
				end = matcher.end();
				hitEnd = matcher.hitEnd();
			}
			logger.info("");
			//logger.info(
			//		"After finished matching: end = {}, filterQueryParamText.length() = {}, hitEnd = {}, " +
			//				"filterQueryParamText.substring(end, filterQueryParamText.length() = {}",
			//		end, filterQueryParamText.length(), hitEnd,
			//		filterQueryParamText.substring(end, filterQueryParamText.length()));
			logger.info("{} andConditionStrings:", andConditionStrings.size());
			for (String s : andConditionStrings) {
				logger.info("{}", s);
			}
			if (!hitEnd) {
				/*
				 * The entire string was not matched. This exception reports the
				 * final part of the string that was not matched.
				 */
				throw new ResourceFilterParseException(String.format(
						"Could not fully parse the filter query parameter. Not parsed: %s",
						filterQueryParamText.substring(end, filterQueryParamText.length())));
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
			Pattern andConditionPattern = Pattern.compile(andConditionRegex, Pattern.COMMENTS);
			Pattern orConditionPattern = Pattern.compile(orConditionRegex, Pattern.COMMENTS);

			for (String andConditionString : andConditionStrings) {

				/*
				 * These variables are used to check if we were able to match the
				 * *entire* substring, andConditionString. If not, an exception 
				 * is thrown that includes the substring that could not be 
				 * matched.
				 */
				int andConditionStringMatchEnd = 0;
				boolean andConditionStringMatchHitEnd = false;

				logger.info("");
				logger.info("String to parse: {}", andConditionString);

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
					logger.info("andConditionMatcher.groupCount() = {}", andConditionMatcher.groupCount());
					for (int i = 0; i <= andConditionMatcher.groupCount(); i++) {
						logger.info("group {}: {}", i, andConditionMatcher.group(i));
					}
					String orConditionString = andConditionMatcher.group(1);
					logger.info("orConditionString = {}", orConditionString);
					orConditionStrings.add(orConditionString);

					if (andConditionMatcher.groupCount() >= 4) {
						/*
						 * The 4th group should either be empty or equal to the
						 * string ".or.". If this is not the case, it means that
						 * the filter query parameter is malformed.
						 */
						if (andConditionMatcher.group(4) != null
								&& !andConditionMatcher.group(4).isEmpty()
								&& !andConditionMatcher.group(4).equals(".or.")) {
							logger.info("!!!!! andConditionMatcher.group(4) = {}", andConditionMatcher.group(4));
							throw new ResourceFilterParseException(String.format(
									"Could not fully parse the filter query parameter. Not parsed: %s",
									andConditionString));
						}
					}

					/*
					 * These variables are used to check if we were able to 
					 * match the *entire* substring, orConditionString. If not,
					 * an exception is thrown that includes the substring that
					 * could not be matched.
					 */
					int orConditionStringMatchEnd = 0;
					boolean orConditionStringMatchHitEnd = false;

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
							logger.info("(attrName, op, value) = ({}, {}, {})",
									orConditionMatcher.group(1),
									orConditionMatcher.group(2),
									orConditionMatcher.group(3));
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
					orConditionStringMatchEnd = orConditionMatcher.end();
					orConditionStringMatchHitEnd = orConditionMatcher.hitEnd();
					//logger.info(
					//		"After finished matching orConditionString: orConditionStringMatchEnd = {}, " +
					//				"orConditionString.length() = {}, orConditionStringMatchHitEnd = {}, " +
					//				"orConditionString.substring(orConditionStringMatchEnd, orConditionString.length() = {}",
					//		orConditionStringMatchEnd, orConditionString.length(), orConditionStringMatchHitEnd,
					//		orConditionString.substring(orConditionStringMatchEnd, orConditionString.length()));
					if (!orConditionStringMatchHitEnd) {
						/*
						 * The entire string was not matched. This exception
						 * reports the final part of the string that was not
						 * matched.
						 */
						throw new ResourceFilterParseException(String.format(
								"Could not fully parse the filter query parameter. Not parsed: %s",
								orConditionString.substring(orConditionStringMatchEnd, orConditionString.length())));
					}
					/*
					 * Update before next trip through the loop.
					 */
					andConditionStringMatchEnd = andConditionMatcher.end();
					andConditionStringMatchHitEnd = andConditionMatcher.hitEnd();
				}
				//logger.info(
				//		"After finished matching andConditionString: andConditionStringMatchEnd = {}, " +
				//				"andConditionString.length() = {}, andConditionStringMatchHitEnd = {}, " +
				//				"andConditionString.substring(andConditionStringMatchEnd, andConditionString.length() = {}",
				//		andConditionStringMatchEnd, andConditionString.length(), andConditionStringMatchHitEnd,
				//		andConditionString.substring(andConditionStringMatchEnd, andConditionString.length()));
				if (!andConditionStringMatchHitEnd) {
					/*
					 * The entire string was not matched. This exception reports
					 * the final part of the string that was not matched.
					 */
					throw new ResourceFilterParseException(String.format(
							"Could not fully parse the filter query parameter. Not parsed: %s",
							andConditionString.substring(andConditionStringMatchEnd, andConditionString.length())));
				}

			}

			logger.info("filterConditions = {}", filterConditions);
			//for (List<Map<String, String>> filterCondition : filterConditions) {
			//	logger.info("filterCondition = {}", filterCondition);
			//}

			if (!filterQueryParamText.isEmpty() && filterConditions.size() == 0) {
				/*
				 * The "filter" query parameter has a value assigned to it, but
				 * we did not parse it into any conditions. This means that the
				 * regular expressions we used did not match any pattern that 
				 * was expected, which in turns means that the "filter" query
				 * parameter value must be malformed in some way. If we do not
				 * throw an exception here, then no filtering will be performed.
				 * Since the filter operation might be important, it is safer
				 * to return no resources than to return resources that should
				 * have been filtered out.
				 */
				throw new ResourceFilterParseException(
						"Could not parse filter query parameter: " + filterQueryParamText);
			}

		} catch (PatternSyntaxException e) {
			throw new ResourceFilterParseException("Could not parse filter query parameter: " + filterQueryParamText,
					e);
		}

		return filterConditions;
	}

	/**
	 * Returns a {@link List} representing the filter conditions parsed from the
	 * "filter" query parameters in the ReST resource URI.
	 * 
	 * @param queryParams
	 * @return
	 * @throws ResourceFilterParseException
	 */
	public static List<List<Map<String, String>>> parseFilterQueryParams(Map<String, List<String>> queryParams)
			throws ResourceFilterParseException {

		List<List<Map<String, String>>> filterConditions = new ArrayList<>();

		List<String> filterQueryParams = queryParams.get(ResourcePath.FILTER_QP_KEY);
		if (filterQueryParams != null) {
			for (String filterQueryParam : filterQueryParams) {
				List<List<Map<String, String>>> filterConditionsForQueryParam = RestUtils
						.parseFilterQueryParam(filterQueryParam);
				/*
				 * Insert the parsed conditions in filterConditionsForQueryParam
				 * into the list that will be returned. Each inserted list:
				 * 
				 *   1. Represents a condition to be AND'ed.
				 *   
				 *   2. Can consist of any number of conditions to be OR'ed with
				 *      each other. Each condition to be OR'ed is represented by
				 *      a Map that contains 3 values:
				 *      
				 *        a. attributeName
				 *        b. comparisoPperator
				 *        c. comparisonValue
				 */
				for (List<Map<String, String>> filterConditionForQueryParam : filterConditionsForQueryParam) {
					logger.info("filterConditionForQueryParam = {}", filterConditionForQueryParam);
					filterConditions.add(filterConditionForQueryParam);
				}
			}
		}

		return filterConditions;
	}

	/**
	 * Filters a {@link List} of JPA entities using a series of conditions
	 * parsed from the "filter" query parameter of a REST resource URI.
	 * 
	 * {@author Jeffrey Zelt}
	 * 
	 * @param unfilteredEntities
	 * @param filterConditions
	 * @param filterableEntityAttributes
	 * @param entityClass
	 * @return
	 * @throws ResourceFilterExecutionException
	 */
	public static <E> List<E> filterEntities(
			List<E> unfilteredEntities,
			List<List<Map<String, String>>> filterConditions,
			Map<String, List<Object>> filterableEntityAttributes,
			Comparator<E> comparatorForSortingEntities,
			Class<E> entityClass) throws ResourceFilterExecutionException {

		logger.info("filterConditions = {}", filterConditions);

		if (filterConditions == null || unfilteredEntities.size() == 0) {
			/*
			 * No filtering is required, but we still need to sort.
			 */
			unfilteredEntities.sort(comparatorForSortingEntities);
			return unfilteredEntities; // sorted, but not filtered
		}

		Set<E> filteredEntities = new HashSet<>(unfilteredEntities);

		/*
		 * Perform filtering on the list of all entities.
		 * 
		 * If *any* problem is encountered, an exception is thrown. This is to
		 * avoid returning entities that were intended to be filtered out.
		 */
		for (List<Map<String, String>> andFilterCondition : filterConditions) {

			/*
			 * This set is built up by OR'ing together the results of a series
			 * of filters from andFilterCondition that are each applied to 
			 * unfilteredEntities.
			 * 
			 * At the bottom of this loop, the entities in this set are AND'ed
			 * with the entities in set filteredEntities.
			 */
			Set<E> andFilterConditionEntities = new HashSet<>();

			/*
			 * andFilterCondition contains one Map for each filter condition
			 * to be OR'ed together. In this loop filtered entities are 
			 * accumulated into the Set andFilterConditionEntities. Since a set
			 * is used, entities will be added at most once.
			 * 
			 * After this loop is finished, these entities are AND'ed with the
			 * entities in Set filteredEntities using set intersection.
			 */
			for (Map<String, String> orCondition : andFilterCondition) {

				List<Object> filterableEntityValues = filterableEntityAttributes
						.get(orCondition.get(RestUtils.CONDITION_ATTR_NAME));
				if (filterableEntityValues == null) {
					/*
					 * This means that there is no support for the filter
					 * attribute with the name:
					 * orCondition.get(RestUtils.CONDITION_ATTR_NAME). In 
					 * practice, this probably means that the attribute name in
					 * the resource URI has been typed incorrectly. Regardless
					 * _why_ this has occurred, we throw an exception: To fix 
					 * this, either:
					 * 
					 *   1. The spelling of the attribute name in the URI must 
					 *      be corrected, or
					 *   
					 *   2. Support must be added for the attribute. Until that
					 *      support has been added, the attribute cannot be 
					 *      filtered on.
					 */
					throw new ResourceFilterExecutionException(String.format(
							"No support for filtering %s resources on attribute '%s'",
							entityClass.getSimpleName(), orCondition.get(RestUtils.CONDITION_ATTR_NAME)));
				}

				/*
				 * In order to perform numerical comparisons <, <=, >, >=  on Objects,
				 * it is necessary to cast the objects to a type where these operators
				 * are allowed.
				 */
				Class<?> castToClass = String.class;

				/*
				 * Assign a value to comparisonValue, the value to which all attribute
				 * values will be compared. orCondition.get(RestUtils.CONDITION_VALUE) 
				 * is always of type String. We must convert this String into the
				 * correct type so that the comparison will make sense. 
				 * 
				 * Here, we must treat all possible types of the attribute being 
				 * filtered on.
				 */
				Object comparisonValue;
				String comparisonValueString = orCondition.get(RestUtils.CONDITION_VALUE);
				if (filterableEntityValues.get(0) instanceof String) {

					comparisonValue = comparisonValueString; // No conversion necessary.

				} else if (filterableEntityValues.get(0) instanceof UUID) {

					comparisonValue = null;
					try {
						comparisonValue = UUID.fromString(comparisonValueString);
					} catch (IllegalArgumentException e) {
						throw new ResourceFilterExecutionException(String.format(
								"Filter condition value '%s' is not a legal UUID",
								comparisonValueString));
					}

				} else if (filterableEntityValues.get(0) instanceof Long
						|| filterableEntityValues.get(0) instanceof Integer
						|| filterableEntityValues.get(0) instanceof Short
						|| filterableEntityValues.get(0) instanceof Byte) {

					try {
						comparisonValue = Long.parseLong(comparisonValueString);
					} catch (NumberFormatException e) {
						throw new ResourceFilterExecutionException(
								String.format("Cannot parse %s as a Long", comparisonValueString));
					}
					castToClass = Long.class;

				} else if (filterableEntityValues.get(0) instanceof Double
						|| filterableEntityValues.get(0) instanceof Float) {

					try {
						comparisonValue = Double.parseDouble(comparisonValueString);
					} catch (NumberFormatException e) {
						throw new ResourceFilterExecutionException(
								String.format("Cannot parse %s as a Double", comparisonValueString));
					}
					castToClass = Double.class;

				} else if (filterableEntityValues.get(0) instanceof Timestamp) {

					logger.debug("comparisonValueString = {}", comparisonValueString);
					try {
						Date comparisonValueDate = DateUtils.dateUtcFromIso8601String(comparisonValueString);
						logger.debug("comparisonValueDate = {}", comparisonValueDate);
						comparisonValue = new Timestamp(comparisonValueDate.getTime());
					} catch (NumberFormatException e) {
						throw new ResourceFilterExecutionException(
								String.format("Cannot parse %s as a %s", comparisonValueString,
										Timestamp.class.getName()));
					}
					logger.debug("comparisonValue (java.sql.Timestamp) = {}", comparisonValue);
					castToClass = Timestamp.class;

				} else {
					throw new ResourceFilterExecutionException(String.format(
							"In order to treat filter attribute '%s', support for type %s must be added.",
							orCondition.get(RestUtils.CONDITION_ATTR_NAME),
							filterableEntityValues.get(0).getClass().getName()));
				}

				String operator = orCondition.get(RestUtils.CONDITION_OPERATOR);
				for (int i = 0; i < unfilteredEntities.size(); i++) {
					boolean addEntity = false;

					/*
					 * In order to be able to use <, <=, >=, > operators, the objects
					 * must be cast to a type that support these operators.
					 */
					if (castToClass.equals(Long.class)) {

						if (operator.equals("eq")) {
							addEntity = filterableEntityValues.get(i).equals((Long) comparisonValue);
						} else if (operator.equals("ne")) {
							addEntity = !filterableEntityValues.get(i).equals((Long) comparisonValue);
						} else if (operator.equals("lt")) {
							addEntity = (Long) filterableEntityValues.get(i) < (Long) comparisonValue;
						} else if (operator.equals("le")) {
							addEntity = (Long) filterableEntityValues.get(i) <= (Long) comparisonValue;
						} else if (operator.equals("ge")) {
							addEntity = (Long) filterableEntityValues.get(i) >= (Long) comparisonValue;
						} else if (operator.equals("gt")) {
							addEntity = (Long) filterableEntityValues.get(i) > (Long) comparisonValue;
						} else {
							throw new ResourceFilterExecutionException(String.format(
									"Filter comparison operator '%s' is not supported for attribute '%s'",
									operator, orCondition.get(RestUtils.CONDITION_ATTR_NAME)));
						}

					} else if (castToClass.equals(Double.class)) {

						if (operator.equals("eq")) {
							addEntity = filterableEntityValues.get(i).equals((Double) comparisonValue);
						} else if (operator.equals("ne")) {
							addEntity = !filterableEntityValues.get(i).equals((Double) comparisonValue);
						} else if (operator.equals("lt")) {
							addEntity = (Double) filterableEntityValues.get(i) < (Double) comparisonValue;
						} else if (operator.equals("le")) {
							addEntity = (Double) filterableEntityValues.get(i) <= (Double) comparisonValue;
						} else if (operator.equals("ge")) {
							addEntity = (Double) filterableEntityValues.get(i) >= (Double) comparisonValue;
						} else if (operator.equals("gt")) {
							addEntity = (Double) filterableEntityValues.get(i) > (Double) comparisonValue;
						} else {
							throw new ResourceFilterExecutionException(String.format(
									"Filter comparison operator '%s' is not supported for attribute '%s'",
									operator, orCondition.get(RestUtils.CONDITION_ATTR_NAME)));
						}

					} else if (castToClass.equals(Timestamp.class)) {

						long millisecondsSinceEpoch = ((Timestamp) comparisonValue).getTime();
						if (operator.equals("eq")) {
							addEntity = ((Timestamp) filterableEntityValues.get(i)).getTime() == millisecondsSinceEpoch;
						} else if (operator.equals("ne")) {
							addEntity = ((Timestamp) filterableEntityValues.get(i)).getTime() != millisecondsSinceEpoch;
						} else if (operator.equals("lt")) {
							addEntity = ((Timestamp) filterableEntityValues.get(i)).getTime() < millisecondsSinceEpoch;
						} else if (operator.equals("le")) {
							addEntity = ((Timestamp) filterableEntityValues.get(i)).getTime() <= millisecondsSinceEpoch;
						} else if (operator.equals("ge")) {
							addEntity = ((Timestamp) filterableEntityValues.get(i)).getTime() >= millisecondsSinceEpoch;
						} else if (operator.equals("gt")) {
							addEntity = ((Timestamp) filterableEntityValues.get(i)).getTime() > millisecondsSinceEpoch;
						} else {
							throw new ResourceFilterExecutionException(String.format(
									"Filter comparison operator '%s' is not supported for attribute '%s'",
									operator, orCondition.get(RestUtils.CONDITION_ATTR_NAME)));
						}

					} else {

						/*
						 * Objects of *any* class can be tested for equality.
						 */
						if (operator.equals("eq")) {
							addEntity = filterableEntityValues.get(i).equals(comparisonValue);
						} else if (operator.equals("ne")) {
							addEntity = !filterableEntityValues.get(i).equals(comparisonValue);
						} else {
							throw new ResourceFilterExecutionException(String.format(
									"Filter comparison operator '%s' is not supported for attribute '%s'",
									operator, orCondition.get(RestUtils.CONDITION_ATTR_NAME)));
						}

					}

					if (addEntity) {
						andFilterConditionEntities.add(unfilteredEntities.get(i));
					}
				}
			}
			/*
			 * Perform an intersection of the entities in the Set 
			 * filteredEntities with the entities in the Set 
			 * andFilterConditionEntities
			 */
			filteredEntities.retainAll(andFilterConditionEntities);
		}

		/*
		 * Convert Set to List.
		 */
		List<E> filteredEntititesList = new ArrayList<>(filteredEntities);

		/*
		 * The list must be ordered in case pagination is used for the 
		 * collection resource created from list of filtered entities. If 
		 * sorting is not to be performed, the calling code should pass null for
		 * comparatorForSortingEntities.
		 */
		if (comparatorForSortingEntities != null) {
			filteredEntititesList.sort(comparatorForSortingEntities);
		}

		return filteredEntititesList;
	}
}
