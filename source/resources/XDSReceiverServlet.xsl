<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:param name="token"/>
<xsl:param name="dob"/>
<xsl:param name="pw"/>
<xsl:param name="key"/>
<xsl:param name="tokens"/>

<xsl:template match="/Studies">
	<html>
		<head>
			<link rel="Stylesheet" type="text/css" media="all" href="/XDSServlet.css"></link>
			<title>Retrieve Studies</title>
		</head>
		<body>

			<h1>Retrieve Studies from the RSNA Image Share</h1>

			<form id="formID" action="" method="POST" accept-charset="UTF-8" >

				<p>
					<table border="0">
						<tr>
							<td>
								<table border="1">
									<tr>
										<td>Exam ID:</td>
										<td><input name="usertoken" type="text" value="$token"/></td>
									</tr>
									<tr>
										<td>PIN/Password:</td>
										<td><input name="password" type="text" value="$pw"/></td>
									</tr>
									<tr>
										<td title="Date of Birth">DOB (YYYYMMDD):</td>
										<td><input name="dateofbirth" type="text" value="$dob"/></td>
									</tr>
								</table>
							</td>
							<td>
								<input type="submit" class="button" value="Get Exam List"/>
							</td>
						</tr>
					</table>
				</p>

				<xsl:if test="Study">
					<hr/>
					<p>
						<table border="0">
							<tr>
								<td>
									<table border="1">
										<xsl:call-template name="StudyHeadings"/>
										<xsl:for-each select="Study">
											<xsl:sort select="@studyDate"/>
											<tr>
												<td class="center"><input type="checkbox" name="study" value="{@studyUID}"/></td>
												<td><xsl:value-of select="@patientName"/></td>
												<td title="UID: {@studyUID}"><xsl:value-of select="@studyDate"/></td>
												<td>
													<xsl:variable name="sd" select="normalize-space(@studyDescription)"/>
													<xsl:if test="$sd"><xsl:value-of select="$sd"/></xsl:if>
													<xsl:if test="not($sd)">&#160;</xsl:if>
												</td>
											</tr>
										</xsl:for-each>
									</table>
								</td>
								<td>
									<input type="submit" class="button" value="Get Exams"/>
								</td>
							</tr>
						</table>
					</p>
				</xsl:if>

				<xsl:if test="$token and not(Study)">
					<hr/>
					<h2>No Submission Sets Found</h2>
				</xsl:if>

			</form>
		</body>
	</html>
</xsl:template>

<xsl:template name="StudyHeadings">
	<tr>
		<th>Select</th>
		<th>Patient Name</th>
		<th>Study Date</th>
		<th>Study Description</th>
	</tr>
</xsl:template>

</xsl:stylesheet>