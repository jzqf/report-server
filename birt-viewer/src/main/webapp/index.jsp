<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<HEAD>
		<TITLE>Q-Free OBO Report Server</TITLE>
		<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
		<LINK href="styles/iv/index.css" type=text/css rel=stylesheet>
		<LINK href="webcontent/qfree/images/favicon.ico" type=image/x-icon rel="shortcut icon">
		<STYLE>
			.warningMessage { color:red; }
		</STYLE>
	<%
		String javaVersion = System.getProperty("java.version");
	%>
	</HEAD>
	<BODY>
	
		<!-- Page banner -->
		<TABLE cellSpacing=0 cellpadding=0 width="100%" border=0>
			<TBODY>
				<TR>
					<TD width=105>
						<IMG src="webcontent/qfree/images/Q-free2-30px.gif" alt="Branding Logo" width="105" height="30" border=0>
					</TD>
				</TR>
			</TBODY>
		</TABLE>
		
		<div style="padding-right: 10px; padding-left: 10px; padding-bottom: 10px; padding-top: 10px" >
			<!-- Page title -->
			<h2>Q-Free OBO Reporting Server</h2>
		</div>
		
		<div>
			<span>
			<input type=button onClick="location.href='<%= request.getContextPath( ) + "/run?__report=test_rs_config.rptdesign" %>'" 
				value='Check report server configuration'>
			</span>
			<span>
			<input type=button onClick="location.href='<%= request.getContextPath( ) + "/run?__report=test_db_config.rptdesign" %>'" 
				value='Check database connectivity'>
			</span>
		</div>
		
		<div style="padding-right: 10px; padding-left: 10px; padding-bottom: 10px; padding-top: 10px" >
			<!-- Content area -->
			<p>Reports:</p>
			<p>
				<a href="<%= request.getContextPath( ) + "/frameset?__report=test_db_config.rptdesign" %>">Report #1</a>
				<br>
				<a href="<%= request.getContextPath( ) + "/frameset?__report=test_db_config.rptdesign" %>">Report #2</a>
				<br>
				<a href="<%= request.getContextPath( ) + "/frameset?__report=test_db_config.rptdesign" %>">Report #3</a>
				<br>
				<br>
				<a href="<%= request.getContextPath( ) + "/frameset?__report=dvdrental.rptdesign" %>">dvdrental.rptdesign</a>
			</p>
		</div>

		<div style="padding-right: 10px; padding-left: 10px; padding-bottom: 10px; padding-top: 10px" >
			<!-- Content area -->
			<p>Dashboards:</p>
			<p>
				<a href="dashboard-iframes.jsp">Dashboard (iframes)</a>
				<br>
				<a href="dashboard-birttags.jsp">Dashboard (BIRT JSP tags)</a>
			</p>
		</div>
	
		<div style="font-size:75%; padding-right: 10px; padding-left: 10px; padding-bottom: 10px; padding-top: 10px" >
			<p>JRE version: <%= javaVersion  %></p>
		</div>

	</BODY>
</HTML>
