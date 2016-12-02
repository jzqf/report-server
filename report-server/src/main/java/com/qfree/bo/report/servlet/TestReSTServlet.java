package com.qfree.bo.report.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.rest.client.RestClientTest;

/**
 * Servlet implementation class TestReSTServlet.
 * 
 */
/*
 * Comment out the @WebServlet annotation to disable this servlet when this
 * application is installed as a WAR file in Tomcat. If @WebServlet is *not*
 * commented out, there should be an entry in SecurityConfig for this servlet.
 */
//@WebServlet(description = "For testing JAX-RS stuff", urlPatterns = { "/TestRest" })
public class TestReSTServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(TestReSTServlet.class);

	public TestReSTServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("doGet called");
		try {
			processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/plain");
		// PrintWriter out = response.getWriter();
		try (PrintWriter out = response.getWriter()) {

			RestClientTest restClientTest = new RestClientTest();
			JsonArray jsonArray = restClientTest.get();

			//			out.println("jsonArray.toString() = " + jsonArray.toString());
			out.println("jsonArray.getJsonObject(1).toString() = " + jsonArray.getJsonObject(1).toString());

		} catch (Exception e) {
			e.printStackTrace();
			}
	}

	public String getServletInfo() {
		return "Returns info for testing/learning JAX-RS code";
	}

}
