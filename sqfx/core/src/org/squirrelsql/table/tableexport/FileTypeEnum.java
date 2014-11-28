package org.squirrelsql.table.tableexport;

public enum FileTypeEnum {
	EXPORT_FORMAT_XLSX (".xlsx"),
	EXPORT_FORMAT_XLS (".xls");
	
	public String _fileExtension;
	
	FileTypeEnum(String fileExtension){
		this._fileExtension=fileExtension;
	}
	
	public String getFileExtension(){
		return _fileExtension;
	}
}
