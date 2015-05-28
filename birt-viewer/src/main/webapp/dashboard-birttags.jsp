<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/birt.tld" prefix="birt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Q-Free OBO Dashboard (BIRT JSP tags)</title>
	<link href="webcontent/qfree/images/favicon.ico" type=image/x-icon rel="shortcut icon">
</head>
<body>

	<script>
		//window.setInterval(function(){ alert("Hello"); }, 5000);
		//window.setInterval(function(){ reloadIFrame(); }, 5000);
		//window.setInterval(function(){ loadViewerdashboardElement_r1c1(); loadViewerdashboardElement_r1c2(); }, 5000);
		//window.setInterval(function(){ loadViewerdashboardElement_r2c1(); loadViewerdashboardElement_r2c2(); }, 10000);
		//function reloadIFrame() {
		//	// This does not work, probably because each iFrame that displays a 
		//	// report is loaded initially via a script, not via a URL.
		//	document.frames["dashboardElement_r1c1"].location.reload();
		//}
		window.setInterval(function(){ reloadIFrames(); }, 5000);
		function reloadIFrames() {
			loadViewerdashboardElement_r1c1();
			loadViewerdashboardElement_r1c2();
			loadViewerdashboardElement_r2c1();
			loadViewerdashboardElement_r2c2();
		}
	</script>
		
	<h2 style="text-align: center;">Q-Free OBO Dashboard (BIRT JSP tags)</h2>

	<birt:report id="dashboardElement_r1c1" reportDesign="old/dvdrental.rptdesign"
		position="absolute" top="200" left="0" height="800" width="800" 
		reportContainer="iframe" scrolling="no" format="html">
		<birt:param name="rp_max_length" value="50" ></birt:param>
		<birt:param name="rp_film_category">
			<birt:value>1</birt:value>
			<birt:value>3</birt:value>
		</birt:param>
	</birt:report>

	<birt:report id="dashboardElement_r1c2" reportDesign="old/dvdrental.rptdesign"
		position="absolute" top="200" left="800" height="800" width="800" 
		reportContainer="iframe" scrolling="no" format="html">
		<birt:param name="rp_max_length" value="50" ></birt:param>
		<birt:param name="rp_film_category">
			<birt:value>4</birt:value>
		</birt:param>
	</birt:report>

	<birt:report id="dashboardElement_r2c1" reportDesign="old/dvdrental.rptdesign"
		position="absolute" top="1000" left="0" height="800" width="800" 
		reportContainer="iframe" scrolling="no" format="html">
		<birt:param name="rp_max_length" value="50" ></birt:param>
		<birt:param name="rp_film_category">
			<birt:value>1</birt:value>
			<birt:value>10</birt:value>
		</birt:param>
	</birt:report>

	<birt:report id="dashboardElement_r2c2" reportDesign="old/dvdrental.rptdesign"
		position="absolute" top="1000" left="800" height="800" width="800" 
		reportContainer="iframe" scrolling="no" format="html">
		<birt:param name="rp_max_length" value="50" ></birt:param>
		<birt:param name="rp_film_category">
			<birt:value>7</birt:value>
			<birt:value>8</birt:value>
			<birt:value>9</birt:value>
		</birt:param>
	</birt:report>

</body>
</html>