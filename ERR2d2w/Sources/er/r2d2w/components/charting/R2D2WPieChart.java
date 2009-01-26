package er.r2d2w.components.charting;

import com.webobjects.appserver.WOContext;
import com.webobjects.directtoweb.D2WContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

import er.directtoweb.components.ERD2WStatelessComponent;
import er.extensions.foundation.ERXStringUtilities;

public class R2D2WPieChart extends ERD2WStatelessComponent {
	private static final String contextKey = "pieChartKey";
	private static final String select = "SELECT";
	private static final String space = " ";
	private static final String operation = ",COUNT(*) FROM ";
	private static final String groupBy = "GROUP BY";
	private static final String keyKey = "key";
	private static final String countKey = "count";
	
    public R2D2WPieChart(WOContext context) {
        super(context);
    }

	public NSDictionary<String, Number> chartDictionary() {
		NSMutableDictionary<String, Number> result = new NSMutableDictionary<String, Number>();
		D2WContext c = d2wContext();
		String propKey = (String)c.valueForKey(contextKey);
		if(!ERXStringUtilities.stringIsNullOrEmpty(propKey)) {
			EOAttribute attr = c.entity().attributeNamed(propKey);
			StringBuilder sql = new StringBuilder();
			sql.append(select).append(space);
			sql.append(attr.columnName());
			sql.append(operation);
			sql.append(c.entity().externalName());
			sql.append(space).append(groupBy).append(space);
			sql.append(attr.columnName());
			NSArray array = EOUtilities.rawRowsForSQL(session().defaultEditingContext(), c.entity().model().name(), sql.toString(), new NSArray<String>( new String[]{keyKey, countKey}));
			for(Object o: array) {
				NSDictionary counts = (NSDictionary)o;
				String key = (String)counts.objectForKey(keyKey);
				Number value = (Number)counts.objectForKey(countKey);
				//FIXME localize boolean and language values here

				result.put(key, value);
			}
		}
		return result.immutableClone();
	}
}