/*---------------------------------------------------------------
*  Copyright 2012 by the Radiological Society of North America
*
*  This source software is released under the terms of the
*  RSNA Public License (http://mirc.rsna.org/rsnapubliclicense)
*----------------------------------------------------------------*/

package org.rsna.isn.ctp.xds.sender;

import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.rsna.ctp.objects.ZipObject;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.server.Path;
import org.rsna.servlets.Servlet;
import org.rsna.util.FileUtil;
import org.rsna.util.StringUtil;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A servlet to manage the assignment of cached studies to
 * destination keys and to trigger their transmission.
 */
public class XDSSenderServlet extends Servlet {

	static final Logger logger = Logger.getLogger(XDSSenderServlet.class);

	/**
	 * Static init method. Nothing is required; the empty
	 * method here is just to prevent the superclass' method
	 * from creating an unnecessary index.html file.
	 */
	public static void init(File root, String context) { }

	/**
	 * Construct a XDSSenderServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public XDSSenderServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * Handle requests for the management page.
	 * @param req The HttpRequest provided by the servlet container.
	 * @param res The HttpResponse provided by the servlet container.
	 * @throws Exception if the servlet cannot handle the request.
	 */
	public void doGet(HttpRequest req, HttpResponse res) throws Exception {
		Path path = req.getParsedPath();
		int length = path.length();

		if (req.isFromAuthenticatedUser() && req.userHasRole("admin")) {

			if (length == 1) {
				//This is a request for the management page
				res.write( getPage() );
				res.setContentType("html");
				res.disableCaching();
				res.send();
				return;
			}

		}
		//None of the above, treat it as a file request.
		super.doGet(req, res);
	}

	/**
	 * The servlet method that responds to an HTTP POST.
	 * @param req The HttpRequest provided by the servlet container.
	 * @param res The HttpResponse provided by the servlet container.
	 * @throws Exception if the servlet cannot handle the request.
	 */
	public void doPost(HttpRequest req, HttpResponse res) throws Exception {

		//Only accept connections from users with the update privilege
		if (!req.userHasRole("admin")) { res.redirect("/"); return; }

		String key = req.getParameter("key");
		if (key != null) {
			boolean delete = key.equals("0");
			List<String> studies = req.getParameterValues("study");
			if (studies != null) {
				XDSStudyCache cache = XDSStudyCache.getInstance(context);
				for (String studyUID : studies) {
					if (delete) cache.deleteStudy(studyUID);
					else cache.sendStudy(key, studyUID);
				}
			}
		}
		//Reload the page so the user can see what he did.
		res.redirect("/" + context);
	}

	private String getPage() {
		try {
			XDSStudyCache cache = XDSStudyCache.getInstance(context);
			Document activeStudiesDoc = cache.getActiveStudiesXML();
			Document sentStudiesDoc = cache.getSentStudiesXML();
			Destinations destinations = Destinations.getInstance(context);
			Document destinationsDoc = destinations.getDestinationsXML();
			Document xsl = XmlUtil.getDocument( FileUtil.getStream( "/XDSSenderServlet.xsl" ) );
			Object[] params = new Object[] {
				"context", context,
				"sentStudies", sentStudiesDoc,
				"destinations", destinationsDoc
			};
			return XmlUtil.getTransformedText( activeStudiesDoc, xsl, params );
		}
		catch (Exception ex) { return "Unable to create the sender page."; }
	}

}
