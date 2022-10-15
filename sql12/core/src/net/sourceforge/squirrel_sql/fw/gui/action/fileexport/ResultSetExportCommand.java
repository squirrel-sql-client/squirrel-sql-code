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
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortFactoryCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.Window;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Command for exporting a result set to a file.
 *
 * @author Stefan Willinger
 */
public class ResultSetExportCommand extends AbstractExportCommand
{
   static final StringManager s_stringMgr = StringManagerFactory
         .getStringManager(ResultSetExportCommand.class);

   static ILogger log = LoggerController.createLogger(ResultSetExportCommand.class);

   static interface i18n
   {
      // i18n[ResultSetExportCommand.errorExecuteStatement="Could not create the data for exporting."]
      String ERROR_EXECUTE_STATEMENT = s_stringMgr
            .getString("ResultSetExportCommand.errorExecuteStatement");

      String EXECUTING_QUERY = s_stringMgr.getString("ResultSetExportCommand.executingQuery");

   }

   private ResultSet resultSet;

   private DialectType dialect;

   private String sql;

   private Statement stmt;

   private ProgressAbortFactoryCallback progressControllerFactory;

   public ResultSetExportCommand(Statement stmt, String sql, DialectType dialect, ProgressAbortFactoryCallback progressControllerFactory)
   {

      this.sql = sql;
      this.stmt = stmt;
      this.dialect = dialect;
      this.progressControllerFactory = progressControllerFactory;
   }

   /**
    * @see AbstractExportCommand#checkMissingData(java.lang.String)
    */
   @Override
   protected boolean checkMissingData(String sepChar)
   {
      return false;
   }

   /**
    * @see AbstractExportCommand#createExportData()
    */
   @Override
   protected IExportData createExportData(TableExportController ctrl) throws ExportDataException
   {
      try
      {
         super.progress(i18n.EXECUTING_QUERY);
         if (ctrl.isExportComplete() == false)
         {
            stmt.setMaxRows(((ResultSetExportSelectionPanelController)ctrl.getExportSelectionPanelController()).getMaxRows());
         }
         this.resultSet = stmt.executeQuery(sql);
         return new ResultSetExportData(this.resultSet, dialect);
      }
      catch (SQLException e)
      {
         log.error(i18n.ERROR_EXECUTE_STATEMENT, e);
         throw new ExportDataException(i18n.ERROR_EXECUTE_STATEMENT, e);
      }
   }

   /**
    * @param owner
    * @see AbstractExportCommand#createTableExportController()
    */
   @Override
   protected TableExportController createTableExportController(final Window owner)
   {
      try
      {
         final TableExportController[] buf = new TableExportController[1];
         GUIUtils.processOnSwingEventThread(() -> buf[0] = new TableExportController(owner, new ResultSetExportSelectionPanelController(), true, false), true);

         return buf[0];
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   protected TableExportPreferences getExportPreferences()
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

   /**
    * Create a new {@link ProgressAbortCallback}.
    *
    * @see AbstractExportCommand#createProgressController()
    * @see ProgressAbortFactoryCallback
    */
   @Override
   protected ProgressAbortCallback createProgressController()
   {
      return this.progressControllerFactory.getOrCreate();
   }

   /**
    * @return the sql
    */
   public String getSql()
   {
      return sql;
   }

}
