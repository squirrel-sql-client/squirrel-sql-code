package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

import java.awt.*;
import java.io.File;

public class ExportControllerProxy
{
   private ExportController _exportController;

   private Window _owningWindow;
   private MsExcelWorkbook _firstExcelWorkbook;

   public ExportControllerProxy(Window owningWindow, MsExcelWorkbook firstExcelWorkbook)
   {
      _owningWindow = owningWindow;
      _firstExcelWorkbook = firstExcelWorkbook;
   }

   public ExportControllerProxy(ExportController exportController)
   {
      _exportController = exportController;
   }

   public boolean isOK()
   {
      if (null != _exportController)
      {
         return _exportController.isOK();
      }
      else
      {
         return true;
      }
   }

   public boolean isUITableMissingBlobData()
   {
      if (null != _exportController)
      {
         return _exportController.isUITableMissingBlobData();
      }
      else
      {
         return false;
      }
   }

   public Window getOwningWindow()
   {
      if (null != _exportController)
      {
         return _exportController.getOwningWindow();
      }
      else
      {
         return _owningWindow;
      }
   }

   public File getSingleExportTargetFile()
   {
      if (null != _exportController)
      {
         return _exportController.getSingleExportTargetFile();
      }
      else
      {
         return _firstExcelWorkbook.getWorkbookFile();
      }
   }

   public ExportDataInfoList createExportData(ProgressAbortCallback progressController) throws ExportDataException
   {
      if (null != _exportController)
      {
         return _exportController.getExportSourceAccess().createExportData(progressController);
      }
      else
      {
         throw new UnsupportedOperationException("NYI");
      }
   }

   public String getCommand(File firstExportedFile)
   {
      if (null != _exportController)
      {
         return _exportController.getCommand(firstExportedFile);
      }
      else
      {
         return null;
      }
   }

   public TableExportPreferences getPreferences()
   {
      if (null != _exportController)
      {
         return _exportController.getExportSourceAccess().getPreferences();
      }
      else
      {
         final TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();
         prefs.setUseColoring(false);
         return prefs;
      }
   }
}
