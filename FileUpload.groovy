package oflaherty

import java.util.Date;

class FileUpload {

	String fileName;
	String path
	Date createdDate
	boolean active
	
    static constraints = {
		fileName(blank: false, nullable: false, minLength: 4, maxLength: 128)
		path(blank: false, nullable: false)
    }
	
}
