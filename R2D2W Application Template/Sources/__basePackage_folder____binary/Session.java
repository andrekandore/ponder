// Generated by the WOLips Template engine Plug-in at Jan 10, 2010 12:00:00 PM
package ${basePackage};

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEnterpriseObject;

import er.extensions.appserver.ERXSession;

public class Session extends ERXSession {
	private static final Logger log = Logger.getLogger(Session.class);
	
	public Session() {
		log.debug("Session created: " + sessionID());
		
		setStoresIDsInCookies(true);
		setStoresIDsInURLs(false);
	}
}