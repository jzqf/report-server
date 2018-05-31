package com.qfree.bo.report.apps;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.service.BirtService;

public class ParseReportParameters {

	private static final Logger logger = LoggerFactory.getLogger(ParseReportParameters.class);

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
		//	BirtService birtService = (BirtService) context.getBean(BirtService.class);
		BirtService birtService = context.getBean(BirtService.class);
		logger.info("birtService = {}", birtService);

		try {

			/*
			 * Load rptdesign file into a String.
			 */

			Path rptdesignPath = Paths
					.get("/home/jeffreyz/git/test-reports/test-reports/tests/400-TestReport04_v1.1.rptdesign");
			//Path rptdesignPath = Paths
			//		.get("/home/jeffreyz/git/test-reports/test-reports/tests/cascade.rptdesign");
			//Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");
			//Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.6.rptdesign");

			List<String> rptdesignLines = null;
			try {
				rptdesignLines = Files.readAllLines(rptdesignPath);// assumes UTF-8 encoding
			} catch (IOException e) {
				e.printStackTrace();
			}
			String rptdesignXml = String.join("\n", rptdesignLines);
			//logger.info("rptdesignXml = \n{}", rptdesignXml);

			/*
			 * Extract all parameters and their metadata from the rptdesign.
			 */
			//Map<String, Map<String, Serializable>> paramDetails = ReportUtils.parseReportParams(rptdesignXml);
			Map<String, Map<String, Serializable>> paramDetails = birtService.parseReportParams(rptdesignXml);

			/*
			 * Log all details for each parameter extracted from the report
			 * design.
			 */
			for (Map.Entry<String, Map<String, Serializable>> paramDetailsEntry : paramDetails.entrySet()) {
				logger.info("Parameter = {}", paramDetailsEntry.getKey());
				Map<String, Serializable> parameter = paramDetailsEntry.getValue();
				for (Map.Entry<String, Serializable> parameterEntry : parameter.entrySet()) {
					String name = parameterEntry.getKey();
					//if (name.equals("SelectionList")) {
					//	HashMap<?, ?> selList = (HashMap<?, ?>) parameterEntry.getValue();
					//	for (Map.Entry<?, ?> selListEntry : selList.entrySet()) {
					//		logger.debug("  Selection List Entry ===== Key = {} Value = {}",
					//				selListEntry.getKey(), selListEntry.getValue());
					//	}
					//} else {
					logger.info("  {} = {}", name, parameterEntry.getValue());
					//}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		context.close();
	}

}