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

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortFactoryCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.Window;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Command for exporting a result set to a file.
 *
 * @author Stefan Willinger
 */
public class ResultSetExport
{
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultSetExport.class);

   static ILogger log = LoggerController.createLogger(ResultSetExport.class);
   private final Exporter _exporter;

   public ResultSetExport(Statement stmt, String sql, DialectType dialect, ProgressAbortFactoryCallback progressControllerFactory)
   {
      final ExporterCallback exporterCallback = new ExporterCallback()
      {
         @Override
         public TableExportPreferences getExportPreferences()
         {
            return onGetExportPreferences();
         }

         @Override
         public ProgressAbortCallback createProgressController()
         {
            return progressControllerFactory.getOrCreate();
         }

         @Override
         public TableExportController createTableExportController(Window owner)
         {
            return ExportControllerFactory.createExportControllerForResultSet(owner);
         }

         @Override
         public boolean checkMissingData(String separatorChar)
         {
            return false;
         }

         @Override
         public IExportData createExportData(TableExportController ctrl) throws ExportDataException
         {
            return onCreateExportData(ctrl, sql, stmt, dialect);
         }
      };

      _exporter = new Exporter(exporterCallback);

   }

   public void export(Window owner) throws ExportDataException
   {
      _exporter.export(owner);
   }


   private IExportData onCreateExportData(TableExportController ctrl, String sql, Statement stmt, DialectType dialect) throws ExportDataException
   {
      try
      {
         _exporter.progress(s_stringMgr.getString("ResultSetExportCommand.executingQuery"));
         if (ctrl.isExportComplete() == false)
         {
            stmt.setMaxRows(((ResultSetExportSelectionPanelController)ctrl.getExportSelectionPanelController()).getMaxRows());
         }
         ResultSet resultSet = stmt.executeQuery(sql);
         return new ResultSetExportData(resultSet, dialect);
      }
      catch (SQLException e)
      {
         log.error(s_stringMgr.getString("ResultSetExportCommand.errorExecuteStatement"), e);
         throw new ExportDataException(s_stringMgr.getString("ResultSetExportCommand.errorExecuteStatement"), e);
      }
   }

   private TableExportPreferences onGetExportPreferences()
   {
      final TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

      /////////////////////////////////////////////////////////////////////////////////////////////////
      // If useColoring was true for a file export a XSSFWorkbook instead of a SXSSFWorkbook was used.
      // This would result in much higher memory usage and much longer export time.
      // See DataExportExcelWriter.beforeWorking(...)
      prefs.setUseColoring(false);
      //
      /////////////////////////////////////////////////////////////////////////////////////////////////
      return prefs;
   }

   public File getTargetFile()
   {
      return _exporter.getTargetFile();
   }

   public long getWrittenRows()
   {
      return _exporter.getWrittenRows();
   }
}
