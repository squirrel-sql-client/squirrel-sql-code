package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

import java.awt.*;
import java.io.File;


/**
 * Represents either an {@link ExportController} or a {@link MsExcelWorkbook}.
 */
public class ExportControllerProxy
{
   private ExportController _exportController;

   private Window _owningWindow;
   private MsExcelWorkbook _excelWorkbook;
   private MsExcelExportDataCreator _msExcelExportDataCreator;

   public ExportControllerProxy(Window owningWindow, MsExcelWorkbook excelWorkbook, MsExcelExportDataCreator msExcelExportDataCreator)
   {
      _owningWindow = owningWindow;
      _excelWorkbook = excelWorkbook;
      _msExcelExportDataCreator = msExcelExportDataCreator;
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
         return _excelWorkbook.getWorkbookFile();
      }
   }

   public ExportDataInfoList createExportData(ProgressAbortCallback progressAbortCallback) throws ExportDataException
   {
      if (null != _exportController)
      {
         return _exportController.getExportSourceAccess().createExportData(progressAbortCallback);
      }
      else
      {
         return _msExcelExportDataCreator.createExportData(_excelWorkbook, progressAbortCallback);
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

   public boolean isShowExportCompleteAsDialog()
   {
      return null !=_exportController;
   }
}
