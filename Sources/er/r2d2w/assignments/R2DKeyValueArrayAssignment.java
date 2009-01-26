package er.r2d2w.assignments;

import com.webobjects.directtoweb.D2WContext;
import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.directtoweb.assignments.ERDAssignment;

public class R2DKeyValueArrayAssignment extends ERDAssignment {

	public R2DKeyValueArrayAssignment(EOKeyValueUnarchiver u) {
		super(u);
	}

	public R2DKeyValueArrayAssignment(String key, Object value) {
		super(key, value);
	}

	public void encodeWithKeyValueArchiver(EOKeyValueArchiver archiver) {
		super.encodeWithKeyValueArchiver(archiver);
	}

	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver unarchiver) {
		return new R2DKeyValueArrayAssignment(unarchiver);
	}


	public NSArray<Object> dependentKeys(String keyPath) {
		return new NSArray<Object>(new Object[] {});
	}
	
	public Object fire(D2WContext c) {
		NSMutableArray<Object> results = new NSMutableArray<Object>();
		NSArray keyPaths = (NSArray)value();
		for(Object path: keyPaths) {
			results.addObject(c.valueForKeyPath(path.toString()));
		}
		return results;
	}
	
}
