package org.squirrelsql.table.tableexport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.TableLoader;

public class ExcelService extends Service<Void> {

	private Workbook _workbook;
	private FileOutputStream _outputFile;
	private FileTypeEnum _fileTypeEnum;
	private String _outputPath;
	private String _fileName;
	private TableLoader _tableLoader;

	public FileTypeEnum get_fileTypeEnum() {
		return _fileTypeEnum;
	}

	public void set_fileTypeEnum(FileTypeEnum _fileTypeEnum) {
		this._fileTypeEnum = _fileTypeEnum;
	}

	public String get_outputPath() {
		return _outputPath;
	}

	public void set_outputPath(String _outputPath) {
		this._outputPath = _outputPath;
	}

	public String get_fileName() {
		return _fileName;
	}

	public void set_fileName(String _fileName) {
		this._fileName = _fileName;
	}

	public TableLoader get_tableLoader() {
		return _tableLoader;
	}

	public void set_tableLoader(TableLoader _tableLoader) {
		this._tableLoader = _tableLoader;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() {
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
				for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
					Row row = sh.createRow(rowNum + 1);
					for (int cellNum = 0; cellNum < rows.get(rowNum).size(); cellNum++) {
						updateProgress(((cellNum + 1) * rowNum) / totalCells,totalCells);
						Cell cell = row.createCell(cellNum);
						cell.setCellValue(rows.get(rowNum).get(cellNum).getValue().toString());
					}
				}
				try {
					_outputFile = new FileOutputStream(_outputPath + "\\" + _fileName + _fileTypeEnum.getFileExtension());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					_workbook.write(_outputFile);
					_outputFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (_fileTypeEnum == FileTypeEnum.EXPORT_FORMAT_XLSX) {
					((SXSSFWorkbook) _workbook).dispose();
				}
				return null;
			}
		};
	}

	@Deprecated
	public Task<Void> excelWriteTask(TableLoader tableLoader,
			FileTypeEnum fileType, String outputPath, String fileName) {
		return new Task<Void>() {
			public Void call() {
				if (fileType == FileTypeEnum.EXPORT_FORMAT_XLSX) {
					_workbook = new SXSSFWorkbook(100);
				} else if (fileType == FileTypeEnum.EXPORT_FORMAT_XLS) {
					_workbook = new HSSFWorkbook();
				}
				List<ColumnHandle> columns = tableLoader.getColumnHandles();
				List<List<SimpleObjectProperty>> rows = tableLoader
						.getSimpleObjectPropertyRows();
				Long totalCells = (long) (columns.size() * rows.size());
				Sheet sh = _workbook.createSheet();
				Row columnRow = sh.createRow(0);
				for (int cellNum = 0; cellNum < columns.size(); cellNum++) {
					Cell cell = columnRow.createCell(cellNum);
					cell.setCellValue(columns.get(cellNum).getHeader());
				}
				for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
					Row row = sh.createRow(rowNum + 1);
					for (int cellNum = 0; cellNum < rows.get(rowNum).size(); cellNum++) {
						updateProgress(((cellNum + 1) * rowNum) / totalCells,
								totalCells);
						Cell cell = row.createCell(cellNum);
						cell.setCellValue(rows.get(rowNum).get(cellNum)
								.getValue().toString());
					}
				}
				try {
					_outputFile = new FileOutputStream(outputPath + "\\"
							+ fileName + fileType.getFileExtension());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					_workbook.write(_outputFile);
					_outputFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (fileType == FileTypeEnum.EXPORT_FORMAT_XLSX) {
					((SXSSFWorkbook) _workbook).dispose();
				}
				return null;
			}
		};
	}
}