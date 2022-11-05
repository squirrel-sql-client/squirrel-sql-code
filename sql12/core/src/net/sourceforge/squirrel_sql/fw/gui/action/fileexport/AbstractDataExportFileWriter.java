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
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Iterator;

/**
 * Exports a data structure into a file.
 * This abstract implementation does not know the format of the target file, e.g. XML or CSV.
 * It rather knows the structure of {@link IExportData} and provide some callback methods for exporting data.
 * A further scope of this abstract class is the interaction with  {@link ExportController} and {@link ProgressAbortCallback}.
 * A concrete implementation is responsible for formating and writing the data into the target.
 *
 * @author Stefan Willinger
 * @see DataExportCSVWriter
 * @see DataExportExcelWriter
 * @see DataExportXMLWriter
 */
public abstract class AbstractDataExportFileWriter implements IDataExportWriter
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractDataExportFileWriter.class);

   private final FileExportService _fileExportService;


   /**
    * "Begin writing");
    * beforeRows();
    * Construct this one.
    *
    * @param file               The target file.
    * @param prefs              The controller to use
    * @param includeHeaders     Flag, if the header line should be exported
    * @param progressController ProgressController to use.
    */
   public AbstractDataExportFileWriter(File file, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      _fileExportService = new FileExportService(file, prefs, progressController);
   }

   /**
    * @see IDataExportWriter#write(IExportData)
    */
   public long write(IExportData data) throws Exception
   {

      beforeWorking(_fileExportService.getFile());

      if (_fileExportService.getPrefs().isWithHeaders())
      {
         Iterator<String> headers = data.getHeaders();

         int colIdx = 0;
         beforeHeader();
         while (headers.hasNext())
         {
            String columnName = headers.next();
            addHeaderCell(colIdx, columnName);
            colIdx++;
         }
         afterHeader();
      }

      Iterator<ExportDataRow> rows = data.getRows();

      progress(s_stringMgr.getString("AbstractDataExportFileWriter.beginWriting.file", _fileExportService.getFile()));
      beforeRows();
      long rowsCount = 0;
      NumberFormat nfRowCount = NumberFormat.getInstance();


      long begin = System.currentTimeMillis();
      while (rows.hasNext() && isStop() == false)
      {
         rowsCount++;
         ExportDataRow aRow = rows.next();
         if (_fileExportService.isStatusUpdateNecessary())
         {
            long secondsPassed = (System.currentTimeMillis() - begin) / 1000;
            taskStatus(s_stringMgr.getString("AbstractDataExportFileWriter.numberOfRowsCompletedInSeconds", nfRowCount.format(rowsCount), secondsPassed));
         }
         beforeRow(aRow.getRowIndex());

         Iterator<ExportCellData> cells = aRow.getCells();
         while (cells.hasNext())
         {
            ExportCellData cell = cells.next();
            addCell(cell);
         }
         afterRow();
      }
      progress(s_stringMgr.getString("AbstractDataExportFileWriter.finishedLoading", nfRowCount.format(rowsCount)));
      afterRows();
      progress(s_stringMgr.getString("AbstractDataExportFileWriter.closingTheFile"));
      afterWorking();

      progress(s_stringMgr.getString("AbstractDataExportFileWriter.done"));

      if (isStop())
      {
         return -1;
      }
      else
      {
         return rowsCount;
      }
   }


   /**
    * Callback before processing the first row.
    */
   public void beforeRows()
   {
      // Can be overridden.
   }

   /**
    * Callback after processing all rows
    */
   public void afterRows()
   {
      // Can be overridden.
   }


   /**
    * Callback, before a row is processed.
    */
   public void beforeRow(int rowIdx) throws Exception
   {
      // Can be overridden.

   }

   /**
    * Callback, after the processing of a row.
    */
   public void afterRow() throws Exception
   {
      // Can be overridden.

   }


   /**
    * Callback, after all rows are processed.
    *
    * @throws Exception
    */
   protected abstract void afterWorking() throws Exception;

   /**
    * Adds a cell to a row of data.
    *
    * @param cell cell to be added
    * @throws Exception
    */
   protected abstract void addCell(ExportCellData cell) throws Exception;

   /**
    * Callback, before the processing of the header line will start.
    */
   protected void beforeHeader() throws Exception
   {
      // Can be overridden.
   }

   /**
    * Adds a header cell into the output data.
    *
    * @param colIdx     the index of the cell
    * @param columnName the name of the column
    * @throws Exception
    */
   protected abstract void addHeaderCell(int colIdx, String columnName) throws Exception;

   /**
    * Callback, after we have processed all header cells.
    */
   protected void afterHeader() throws Exception
   {
      // Can be overridden.

   }

   /**
    * Callback that indicate, that the work is just started.
    * Normally, a concrete implementation would do some setup at this point. Like the initializing of a output stream.
    *
    * @param file The target file.
    * @throws Exception if a Exception occurs.
    */
   protected abstract void beforeWorking(File file) throws Exception;

   /**
    * @return the file
    */
   public File getFile()
   {
      return _fileExportService.getFile();
   }

   /**
    * @param file the file to set
    */
   public void setFile(File file)
   {
      _fileExportService.setFile(file);
   }

   /**
    * @return the ctrl
    */
   public TableExportPreferences getPrefs()
   {
      return _fileExportService.getPrefs();
   }

   /**
    * @param prefs the ctrl to set
    */
   public void setPrefs(TableExportPreferences prefs)
   {
      _fileExportService.setPrefs(prefs);
   }

   /**
    * Tells the progress controller the current task.
    *
    * @param task Task to be added to the progress controller.
    */
   protected void progress(String task)
   {
      _fileExportService.progress(task);
   }

   /**
    * Tells the progress controller the status of the current task.
    *
    * @param status Status of a task to be added to the progress controller.
    */
   protected void taskStatus(String status)
   {
      _fileExportService.taskStatus(status);
   }


   /**
    * Checks, if the work should be stopped.
    *
    * @return true, if the work should be stopped, otherwise false.
    */
   protected boolean isStop()
   {
      return _fileExportService.isStop();
   }

   public Charset getCharset()
   {
      return _fileExportService.getCharset();
   }

}
