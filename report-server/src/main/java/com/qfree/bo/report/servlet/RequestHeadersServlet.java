package com.qfree.bo.report.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.qfree.bo.report.ApplicationConfig;

/**
 * Servlet implementation class RequestHeadersServlet.
 * 
 * <p>
 * Tomcat scans classes for the {@literal @}{@link WebServlet} annotation and
 * registers these servlets automatically. This assumes that this application is
 * installed in Tomcat as a WAR file. However, this is not done when this
 * application is run in Spring Boot's embedded Tomcat server via:
 * 
 * <pre>
 * <code>mvn clean spring-boot:run</code>
 * </pre>
 * 
 * <p>
 * In order to register this servlet with Spring Boot so that it also will work
 * via "spring-boot:run", a {@literal @}{@link Bean} has been created for it in
 * {@link ApplicationConfig}.
 */
@WebServlet(description = "Displays all request headers", urlPatterns = { "/RequestHeaders" })
public class RequestHeadersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(RequestHeadersServlet.class);

	public RequestHeadersServlet() {
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

		response.setContentType("text/html;charset=UTF-8");
		// PrintWriter out = response.getWriter();
		try (PrintWriter out = response.getWriter()) {
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Request Headers</title>");
			
			out.println("<style>");
			out.println("table {");
			out.println("width: 100%;");
			out.println("border-collapse: collapse;");
			out.println("}");
			out.println("table,th,td {");
			out.println("border:1px solid black;");
			out.println("}");
			out.println("td {");
			out.println("padding:2px;");
			out.println("}");
			out.println("</style>");

			out.println("</head>");
			out.println("<body>");
			out.println("<h3>Request headers at " + request.getContextPath()
					+ "</h3>");
			
			out.println("<table>");
			out.println("<tr>");
			out.println("<th>Header name</th>");
			out.println("<th>Header value</th>");
			out.println("</tr>");
			Enumeration<String> names = request.getHeaderNames();
			while (names.hasMoreElements()) {
				out.println("<tr>");
				String name = names.nextElement();
				Enumeration<String> values = request.getHeaders(name); // support multiple values
				if (values != null) {
					
					String value;
					while (values.hasMoreElements()) {
						value = values.nextElement();
						// out.println(name + ": " + value);
						out.println("<td width=\"20%\">" + name + "</td>");
						out.println("<td>" + value + "</td>");
						
						// If the header has multiple values, we display the 
						// header name for only the first value.
						while (values.hasMoreElements()) {
							value = values.nextElement();
							// out.println(name + ": " + value);
							out.println("<td width=\"20%\"></td>");		// do not display the header name again
							out.println("<td>" + value + "</td>");
						}
						
					}
				} else {
					out.println("<td>" + name + "</td>");
					out.println("<td></td>");
				}
				out.println("</tr>");
			}

			out.println("</table>");
			out.println("</body>");
			out.println("</html>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getServletInfo() {
		return "Returns all request headers formatted as an HTML table";
	}

}
