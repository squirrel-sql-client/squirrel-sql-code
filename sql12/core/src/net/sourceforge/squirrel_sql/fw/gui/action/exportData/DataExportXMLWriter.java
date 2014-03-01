/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.gui.action.exportData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvController;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Exports {@link IExportData} into a XML File.
 * <p>
 * Uses DOM for output
 * </p>
 * <b>Note:</b> This class is the result of a refactoring task. The code was taken from TableExportCsvCommand.
 * @author Stefan Willinger
 *
 */
public class DataExportXMLWriter extends AbstractDataExportFileWriter {
	
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document testDoc;
	private Element columns;
	private Element root;
	private Element rows;
	private Element row;

	
	

	/**
	 * @param file
	 * @param ctrl
	 * @param includeHeaders
	 * @param progressController 
	 */
	public DataExportXMLWriter(File file, TableExportCsvController ctrl, boolean includeHeaders, ProgressAbortCallback progressController) {
		super(file, ctrl, includeHeaders, progressController);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#afterWorking()
	 */
	@Override
	protected void afterWorking() throws Exception {
		
		// The XML document we created above is still in memory
		// so we have to output it to a real file.
		// In order to do it we first have to create
		// an instance of DOMSource
		DOMSource source = new DOMSource(testDoc);

		// PrintStream will be responsible for writing
		// the text data to the file
		PrintStream ps = new PrintStream(getFile());
		StreamResult result = new StreamResult(ps);

		// Once again we are using a factory of some sort,
		// this time for getting a Transformer instance,
		// which we use to output the XML
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		// Indenting the XML
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// The actual output to a file goes here
		transformer.transform(source, result);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addCell(int, int, net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportDataCell)
	 */
	@Override
	protected void addCell(IExportDataCell cell) throws Exception {
		String strCellValue = "";
		if(cell.getObject() != null){
			if (getCtrl().useGloablPrefsFormatting() && cell.getColumnDisplayDefinition() != null){
				strCellValue = CellComponentFactory.renderObject(cell.getObject(), cell.getColumnDisplayDefinition());
			} else {
				strCellValue = cell.getObject().toString();
			}
		}
		
		Element value = testDoc.createElement("value");
		value.setAttribute("columnNumber", String.valueOf(cell.getColumnIndex()));
		value.setTextContent(strCellValue);
		row.appendChild(value);		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#addHeaderCell(int, int, java.lang.String)
	 */
	@Override
	protected void addHeaderCell(int colIdx, String columnName) throws Exception {
		Element columnEl = testDoc.createElement("column");
		columnEl.setAttribute("number", String.valueOf(colIdx));
		columns.appendChild(columnEl);

		Element columnNameEl = testDoc.createElement("name");
		columnNameEl.setTextContent(columnName);
		columnEl.appendChild(columnNameEl);
		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#beforeWorking(java.io.File)
	 */
	@Override
	protected void beforeWorking(File file) throws Exception {
		// Using a factory to get DocumentBuilder for creating XML's
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();

		// Here instead of parsing an existing document we want to
		// create a new one.
		testDoc = builder.newDocument();

		// 'table' is the main tag in the XML.
		root = testDoc.createElement("table");
		testDoc.appendChild(root);

		// 'columns' tag will contain informations about columns
		columns = testDoc.createElement("columns");
		root.appendChild(columns);
		int curRow = 0;		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#beforeRow()
	 */
	@Override
	public void beforeRow(int rowIdx) throws Exception {
		super.beforeRow(rowIdx);
		row = testDoc.createElement("row");
		row.setAttribute("rowNumber", String.valueOf(rowIdx));
		rows.appendChild(row);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.exportData.AbstractDataExportFileWriter#beforeRows()
	 */
	@Override
	public void beforeRows() {
		super.beforeRows();
		// 'rows' tag contains the data extracted from the table
		rows = testDoc.createElement("rows");
		root.appendChild(rows);
	}
	
	
}
