package er.r2d2w.components;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.directtoweb.D2WContext;
import com.webobjects.directtoweb.ERD2WUtilities;
import com.webobjects.foundation.NSArray;

import er.attachment.model.ERAttachment;
import er.attachment.model.ERDatabaseAttachment;
import er.attachment.processors.ERAttachmentProcessor;
import er.attachment.processors.IERAttachmentProcessorDelegate;
import er.attachment.utils.ERMimeType;
import er.attachment.utils.ERMimeTypeManager;
import er.directtoweb.components.ERDCustomEditComponent;
import er.directtoweb.pages.ERD2WPage;
import er.extensions.foundation.ERXFileUtilities;
import er.extensions.foundation.ERXProperties;
import er.extensions.foundation.ERXValueUtilities;
import er.extensions.validation.ERXValidationFactory;
import er.r2d2w.interfaces.R2DAttachmentContainer;

public class R2D2WEditFile extends ERDCustomEditComponent {
	/** logging support */
    public final static Logger log = Logger.getLogger(R2D2WEditFile.class);
	
	public R2D2WEditFile(WOContext context) {
        super(context);
    }

    private static final String _COMPONENT_CLASS = "file";
	private String labelID;
	private String filePath;
	private String finalFilePath;
	private String mimeType;
	private NSArray<String> mimeTypes;
	
	public void appendToResponse(WOResponse r, WOContext c) {
		super.appendToResponse(r, c);
	}
	
	public WOActionResults invokeAction(WORequest r, WOContext c) {
		WOActionResults result = super.invokeAction(r, c);
    	
		ERD2WPage page = (ERD2WPage)ERD2WUtilities.enclosingPageOfClass(this, ERD2WPage.class);
		if(!page.hasValidationExceptionForPropertyKey()) {
			ERAttachment attachment = uploadFile();
	    	if(attachment != null) {
	    		if(ERXValueUtilities.booleanValueWithDefault(d2wContext().valueForKey("deleteOldAttachments"), false)) {
	    			ERAttachment oldAttachment = (ERAttachment)objectPropertyValue();
	    			if(oldAttachment != null) { oldAttachment.delete(); }
	    		}
	    		setObjectPropertyValue(attachment); 
	    	}
		}
		return result;
	}
	
    public void takeValuesFromRequest(WORequest r, WOContext c) {
    	filePath = null;
    	finalFilePath = null;
    	mimeType = null;
    	super.takeValuesFromRequest(r, c);
    	if(c.wasFormSubmitted() && filePath() != null) {
    		ERMimeTypeManager mtm = ERMimeTypeManager.mimeTypeManager();
    		ERMimeType mt = mtm.mimeTypeForMimeTypeString(mimeType(), false);
    		if(mt == null) {
    			String ext = ERXFileUtilities.fileExtension(filePath());
    			mt = mtm.mimeTypeForExtension(ext, false);
    		}
    		if(mt == null) {
    			validationFailedWithException(ERXValidationFactory.defaultFactory().createException(object(), key(), mimeType, "UnsupportedMimeTypeException"), mimeType, key());
    		} else if (mimeTypes().count() > 0 && !mimeTypes().contains(mt.mimeType())) {
    			validationFailedWithException(ERXValidationFactory.defaultFactory().createException(object(), key(), mimeType, "UnacceptableMimeTypeException"), mimeType, key());
    		} else {
    			setMimeType(mt.mimeType());
    		}
    	}
    }
    
    //TODO use rule system and move property defaults into an assignment
    public ERAttachment uploadFile() {
    	ERAttachment result = null;
    	if(finalFilePath() == null) { return result; }
    	D2WContext c = d2wContext();
    	File upload = new File(finalFilePath());
    	
    	String configurationName = (String)c.valueForKey("attachmentConfigurationName");
    	String ownerID = (String)c.valueForKey("attachmentOwnerID");

    	String storageType = (String)c.valueForKey("attachmentStorageType");
    	if(storageType == null) {
        	if(configurationName != null) { 
        		storageType = ERXProperties.stringForKey("er.attachment." + configurationName + ".storageType");
        	}
        	if(storageType == null) {
        		storageType = ERXProperties.stringForKeyWithDefault("er.attachment.storageType", ERDatabaseAttachment.STORAGE_TYPE);
        	}
    	}
    	
    	try {
    		ERAttachmentProcessor<?> processor = ERAttachmentProcessor.processorForType(storageType);
    		IERAttachmentProcessorDelegate delegate = (IERAttachmentProcessorDelegate)c.valueForKey("attachmentProcessorDelegate");
    		//CHECKME why doesn't IERAttachmentProcessorDelegate pass a file handle on attachmentCreated??
    		if(delegate instanceof R2DAttachmentContainer) {
    			((R2DAttachmentContainer) delegate).setUpload(upload);
    		}
    		processor.setDelegate(delegate);
    		result = processor.process(editingContext, upload, filePath(), mimeType(), configurationName, ownerID);
    	} catch (IOException e) {
    		log.debug(e.getMessage());
			validationFailedWithException(ERXValidationFactory.defaultFactory().createException(object(), key(), filePath(), "UploadFailedException"), filePath(), key());
    	}
    	
    	return result;
    }
	
    public String componentClasses() {
    	return _COMPONENT_CLASS;
    }
    
    public void reset() {
    	super.reset();
    	filePath = null;
    	finalFilePath = null;
    	labelID = null;
    	mimeType = null;
    	mimeTypes = null;
    }

	public String labelID() {
		if(labelID == null) {
			labelID = "id" + context().elementID();
		}
		return labelID;
	}
	
	public String tempFilePath() throws IOException {
		String configurationName = (String) valueForBinding("configurationName");
		String tempFolderPath = ERXProperties.stringForKey("er.attachment." + configurationName + ".tempFolder");
		if (tempFolderPath == null) {
			tempFolderPath = ERXProperties.stringForKey("er.attachment.tempFolder");
		}
	
		File tempFile;
		if (tempFolderPath != null) {
			File tempFolder = new File(tempFolderPath);
			tempFile = File.createTempFile("ERAttachmentUpload-", ".tmp", tempFolder);
		} else {
			tempFile = File.createTempFile("ERAttachmentUpload-", ".tmp");
		}
		return tempFile.getAbsolutePath();
	}

	/**
	 * @return the filePath
	 */
	public String filePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the finalFilePath
	 */
	public String finalFilePath() {
		return finalFilePath;
	}

	/**
	 * @param finalFilePath the finalFilePath to set
	 */
	public void setFinalFilePath(String finalFilePath) {
		this.finalFilePath = finalFilePath;
	}

	/**
	 * @return the mimeType
	 */
	public String mimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	/**
	 * @return the mimeTypes
	 */
	public NSArray<String> mimeTypes() {
		if(mimeTypes == null) {
			//CHECKME make sure this isn't caching incorrectly
			mimeTypes = ERXValueUtilities.arrayValueWithDefault(d2wContext().valueForKeyPath("smartRelationship.userInfo.mimeTypes"), NSArray.EmptyArray);
		}
		return mimeTypes;
	}


}