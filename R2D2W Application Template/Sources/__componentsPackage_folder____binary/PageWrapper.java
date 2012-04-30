// Generated by the WOLips Template engine Plug-in at Jan 10, 2010 12:00:00 PM
package ${componentsPackage};

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.directtoweb.D2WContext;
import com.webobjects.directtoweb.D2WPage;
import com.webobjects.directtoweb.ERD2WContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXBrowser;
import er.extensions.appserver.ERXSession;
import er.extensions.appserver.navigation.ERXNavigationManager;
import er.extensions.components.ERXStatelessComponent;
import er.extensions.localization.ERXLocalizer;
import er.r2d2w.ERR2d2wUtils;

public class PageWrapper extends ERXStatelessComponent {
	/**
	 * Do I need to update serialVersionUID?
	 * See section 5.6 <cite>Type Changes Affecting Serialization</cite> on page 51 of the 
	 * <a href="http://java.sun.com/j2se/1.4/pdf/serial-spec.pdf">Java Object Serialization Spec</a>
	 */
	private static final long serialVersionUID = 1L;

	private static final NSArray<String> availableTimeZones = new NSArray<String>(new String[] { "US/Hawaii",
			"US/Alaska", "US/Pacific", "US/Arizona", "US/Mountain", "US/Central", "US/Eastern", "GMT", "Asia/Tokyo" });

	public PageWrapper(WOContext aContext) {
		super(aContext);
	}

	public void appendToResponse(WOResponse response, WOContext context) {
		super.appendToResponse(response, context);
		if(ERR2d2wUtils.acceptsXHTML(context().request())){
			ERR2d2wUtils.setXHTMLContentType(response);
		}
	}
    
    public D2WContext d2wContext() {
    	if (context().page() instanceof D2WPage) {
			D2WPage d2wPage = (D2WPage) context().page();
			return d2wPage.d2wContext();
		}
    	return null;
    }

	public NSKeyValueCoding navigationContext() {
		NSKeyValueCoding _navigationContext = null;

		if (context().page() instanceof D2WPage) {
			_navigationContext = d2wContext();
		}

		if (_navigationContext == null) {
			_navigationContext = ERD2WContext.newContext();
		}
		
		ERXNavigationManager.manager().navigationStateForSession(session());
		return _navigationContext;
	}

	public boolean hasMultipleLanguages() {
		return ERXLocalizer.availableLanguages().count() > 1;
	}

	/**
	 * @return the availableTimeZones
	 */
	public NSArray<String> availableTimeZones() {
		return availableTimeZones;
	}
}
