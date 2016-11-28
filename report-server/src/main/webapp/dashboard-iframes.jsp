<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<title>Q-Free OBO Dashboard (HTML iframes)</title>
		<meta http-equiv=Content-Type content="text/html; charset=iso-8859-1">
<!--		<link href="styles/iv/index.css" type=text/css rel=stylesheet> -->
		<link href="webcontent/qfree/images/favicon.ico" type=image/x-icon rel="shortcut icon">
		<style>
			.warningMessage { color:red; }
		</style>
	</head>
	<body>

	<script>
		window.onload = function() {
			window.setInterval(function(){ reloadIFrames(); }, 5000);
			function reloadIFrames() {
				var frame = document.getElementById("dashboardElement_r1c1_id");
				frame.src = frame.src;
				frame = document.getElementById("dashboardElement_r1c2_id");
				frame.src = frame.src;
				// Doesn't work:
				//document.frames["dashboardElement_r1c1"].location.reload();
				//document.frames["dashboardElement_r1c2"].location.reload();
			}
		}
	</script>
	
		<!-- Page banner -->
		<table cellSpacing=0 cellpadding=0 width="100%" border=0>
			<tbody>
				<tr>
					<td width=105>
						<IMG src="webcontent/qfree/images/Q-free2-30px.gif" alt="Branding Logo" width="105" height="30" border=0>
					</td>
				</tr>
			</tbody>
		</table>
		
		<h2 style="text-align: center;">Q-Free OBO Dashboard (HTML iframes)</h2>
		
		<div style="padding-right: 10px; padding-left: 10px; padding-bottom: 10px; padding-top: 10px" >
			<span style="padding: 10px">
			<iframe id="dashboardElement_r1c1_id" name="dashboardElement_r1c1" 
				src="/report-server/preview?__report=old/dvdrental.rptdesign&rp_max_length=50&rp_film_category=1&rp_film_category=3"
				width="800" height="800" frameborder="0" scrolling="no">
				<p>Your browser does not support iframes.</p>
			</iframe>
			</span>
			<span style="padding: 10px">
			<iframe id="dashboardElement_r1c2_id" name="dashboardElement_r1c2" 
				src="/report-server/preview?__report=old/dvdrental.rptdesign&rp_max_length=50&rp_film_category=4"
				width="800" height="800" frameborder="0" scrolling="no">
				<p>Your browser does not support iframes.</p>
			</iframe>
			</span>
		</div>

	</body>
</html>