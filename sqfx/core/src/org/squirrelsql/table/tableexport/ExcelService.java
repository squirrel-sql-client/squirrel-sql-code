package org.squirrelsql.table.tableexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.TableLoader;

public class ExcelService extends Service<Void> {

	private Workbook _workbook;
	private FileOutputStream _outputFile;
	private FileTypeEnum _fileTypeEnum;
	private TableLoader _tableLoader;
	private File _file;

	public ExcelService(File file, FileTypeEnum fileTypeEnum, TableLoader tableLoader)
	{
		_file = file;
		_fileTypeEnum = fileTypeEnum;
		_tableLoader = tableLoader;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() {
				return executeTask();
			}

			private Void executeTask()
			{
				if (_fileTypeEnum == FileTypeEnum.EXPORT_FORMAT_XLSX) {
					_workbook = new SXSSFWorkbook(100);
				} else if (_fileTypeEnum == FileTypeEnum.EXPORT_FORMAT_XLS) {
					_workbook = new HSSFWorkbook();
				}
				List<ColumnHandle> columns = _tableLoader.getColumnHandles();
				List<List<SimpleObjectProperty>> rows = _tableLoader.getSimpleObjectPropertyRows();
				Long totalCells = (long) (columns.size() * rows.size());
				Sheet sh = _workbook.createSheet();
				Row columnRow = sh.createRow(0);
				for (int cellNum = 0; cellNum < columns.size(); cellNum++) {
					Cell cell = columnRow.createCell(cellNum);
					cell.setCellValue(columns.get(cellNum).getHeader());
				}

				int cellsDone = 0;
				for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
					Row row = sh.createRow(rowNum + 1);
					for (int cellNum = 0; cellNum < rows.get(rowNum).size(); cellNum++) {
						//System.out.println("############# " + (((cellNum + 1) * rowNum) ) + " #### "  + totalCells);
						updateProgress(++cellsDone, totalCells);
						createCell(rows, rowNum, row, cellNum);
					}
				}

				try {
					_outputFile = new FileOutputStream(_file);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
				try {
					_workbook.write(_outputFile);
					_outputFile.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (_fileTypeEnum == FileTypeEnum.EXPORT_FORMAT_XLSX) {
					((SXSSFWorkbook) _workbook).dispose();
				}
				return null;
			}
		};
	}

	private void createCell(List<List<SimpleObjectProperty>> rows, int rowNum, Row row, int colNum)
	{
		Cell retVal = row.createCell(colNum);
		Object cellObj = rows.get(rowNum).get(colNum).getValue();
		//retVal.setCellValue("" + cellObj);


		if (null == cellObj || null == _tableLoader.getColumnHandles().get(colNum).getResultColumnInfo())
		{
			retVal.setCellValue(getDataXLSAsString(cellObj));
		}

		int colType = _tableLoader.getColumnHandles().get(colNum).getResultColumnInfo().getColType();

		switch (colType)
		{
			case Types.BIT:
			case Types.BOOLEAN:
				if (null == cellObj)
				{
					//retVal.setCellValue((Boolean)null);
				}
				else
				{
					retVal.setCellValue((Boolean) cellObj);
				}
				break;
			case Types.INTEGER:
				if (null == cellObj)
				{
					//retVal.setCellValue((Integer)null);
				}
				else
				{
					retVal.setCellValue(((Number) cellObj).intValue());
				}
				break;
			case Types.SMALLINT:
			case Types.TINYINT:
				if (null == cellObj)
				{
					//retVal.setCellValue(((Short) null));
				}
				else
				{
					retVal.setCellValue(((Number) cellObj).shortValue());
				}
				break;
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.REAL:
				if (null == cellObj)
				{
					//retVal.setCellValue((Double) null);
				}
				else
				{
					retVal.setCellValue(((Number) cellObj).doubleValue());
				}
				break;
			case Types.BIGINT:
				if (null == cellObj)
				{
					//retVal.setCellValue((Long)null);
				}
				else
				{
					retVal.setCellValue(Long.parseLong(cellObj.toString()));
				}
				break;
			case Types.DATE:
				makeTemporalCell(retVal, (Date) cellObj, "m/d/yy");
				break;
			case Types.TIMESTAMP:
				makeTemporalCell(retVal, (Date) cellObj, "m/d/yy h:mm");
				break;
			case Types.TIME:
				makeTemporalCell(retVal, (Date) cellObj, "h:mm");
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				//cellObj = CellComponentFactory.renderObject(cellObj, colDef);
				retVal.setCellValue(getDataXLSAsString(cellObj));
				break;
			default:
				//cellObj = CellComponentFactory.renderObject(cellObj, colDef);
				retVal.setCellValue(getDataXLSAsString(cellObj));
		}
	}

	private void makeTemporalCell(Cell retVal, Date cellObj, String format)
	{
		CreationHelper creationHelper = _workbook.getCreationHelper();
		CellStyle cellStyle = _workbook.createCellStyle();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
		retVal.setCellStyle(cellStyle);

		if (null != cellObj)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(cellObj);
			retVal.setCellValue(calendar);
		}
	}


	private String getDataXLSAsString(Object cellObj) {
		if (cellObj == null) {
			return "";
		} else {
			return cellObj.toString().trim();
		}
	}


	public File getExportFile()
	{
		return _file;
	}
}