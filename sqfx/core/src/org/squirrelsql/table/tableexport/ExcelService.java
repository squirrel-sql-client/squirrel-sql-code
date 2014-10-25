package org.squirrelsql.table.tableexport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.TableLoader;

public class ExcelService {

	private Workbook _workbook;
	private FileOutputStream _outputFile;

	// TODO Vary the output type by type of data (out integer as number, etc)
	public Task<Void> excelWriteTask(TableLoader tableLoader,
			FileTypeEnum fileType, String outputPath, String fileName) {
		return new Task<Void>() {
			public Void call() {
				if (fileType == FileTypeEnum.EXPORT_FORMAT_XLSX) {
					_workbook = new SXSSFWorkbook(100);
				} else if(fileType == FileTypeEnum.EXPORT_FORMAT_XLS) {
					_workbook = new HSSFWorkbook();
				}
				List<ColumnHandle> columns = tableLoader.getColumnHandles();
				List<List<SimpleObjectProperty>> rows = tableLoader.getSimpleObjectPropertyRows();
				Long totalCells = (long) (columns.size() * rows.size());
				Sheet sh = _workbook.createSheet();
				Row columnRow = sh.createRow(0);
				for (int cellNum = 0; cellNum < columns.size(); cellNum++) {
					Cell cell = columnRow.createCell(cellNum);
					cell.setCellValue(columns.get(cellNum).getHeader());
				}
				for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
					Row row = sh.createRow(rowNum+1);
					for (int cellNum = 0; cellNum < rows.get(rowNum).size(); cellNum++) {
						updateProgress(((cellNum+1) * rowNum) / totalCells, totalCells);
						Cell cell = row.createCell(cellNum);
						cell.setCellValue(rows.get(rowNum).get(cellNum).getValue().toString());
					}
				}
				try {
					_outputFile = new FileOutputStream(outputPath + "\\" + fileName + fileType.getFileExtension());
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