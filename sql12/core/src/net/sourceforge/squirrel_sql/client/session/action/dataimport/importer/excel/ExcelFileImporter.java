package net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.excel;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.ConfigurationPanel;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.FailedToInterpretHandler;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.IFileImporter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This implementation of the <code>IFileImporter</code> interface is to
 * import Microsoft Excel files.
 *
 * @author Thorsten Mürell
 */
public class ExcelFileImporter implements IFileImporter
{

   private File importFile;
   private int pointer = -1;
   private int size = 0;
   private Workbook workbook = null;
   private Sheet sheet = null;
   private ExcelSettingsBean settings;
   private boolean _trimValues;

   private FailedToInterpretHandler _failedToInterpretHandler = new FailedToInterpretHandler();
   /**
    * The standard constructor
    *
    * @param importFile The import file
    */
   public ExcelFileImporter(File importFile)
   {
      this.importFile = importFile;
      this.settings = new ExcelSettingsBean();
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#open()
    */
   public boolean open() throws IOException
   {
      try
      {
         workbook = WorkbookFactory.create(importFile);
      }
      catch (InvalidFormatException fe)
      {
         throw new IOException(fe.toString());
      }
      reset();
      return true;
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#close()
    */
   public boolean close() throws IOException
   {
      return true;
   }

   /* (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getPreview(int)
    */
   public String[][] getPreview(int noOfLines) throws IOException
   {
      String[][] data = null;
      Workbook wb = null;
      Sheet sht = null;
      try
      {
         wb = WorkbookFactory.create(importFile);
         sht = getSheet(wb);
      }
      catch (InvalidFormatException fe)
      {
         throw new IOException(fe.toString());
      }

      if(0 == sht.getPhysicalNumberOfRows())
      {
         throw new  IllegalStateException("The Excel sheet has no rows.");
      }

      int maxLines = Math.min(noOfLines, sht.getPhysicalNumberOfRows());
      Row row = sht.getRow(0);
      final int firstRowsNumberOfCells = row.getPhysicalNumberOfCells();
      data = new String[maxLines][firstRowsNumberOfCells];

      for (int y = 0; y < maxLines; y++)
      {
         row = sht.getRow(y);
         for (int x = 0; x < firstRowsNumberOfCells; x++)
         {
            if (null == row.getCell(x))
            {
               data[y][x] = null;
            }
            else
            {
               data[y][x] = row.getCell(x).toString();
            }
         }
      }

      return data;
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#reset()
    */
   public boolean reset()
   {
      sheet = getSheet(workbook);
      size = sheet.getPhysicalNumberOfRows();
      pointer = -1;
      return true;
   }


   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#next()
    */
   public boolean next()
   {
      if (pointer >= size - 1)
      {
         return false;
      }
      pointer++;
      return true;
   }

   private void checkPointer() throws IOException
   {
      if (pointer < 0)
         throw new IOException("Use next() to get to the first record.");
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getString(int)
    */
   public String getString(int column) throws IOException
   {
      checkPointer();

      if(null == sheet.getRow(pointer).getCell(column))
      {
         return null;
      }

      if (_trimValues)
      {
         String ret = sheet.getRow(pointer).getCell(column).toString();

         if (null == ret)
         {
            return ret;
         }

         return ret.trim();
      }
      else
      {
         return sheet.getRow(pointer).getCell(column).toString();
      }
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getInt(int)
    */
   public Integer getInt(int column) throws IOException
   {
      Double ret = getDouble(column);
      if (null == ret)
      {
         return null;
      }
      else
      {
         return ret.intValue();
      }
   }

   public Double getDouble(int column) throws IOException
   {
      checkPointer();
      Cell cell = sheet.getRow(pointer).getCell(column);

      if(null == cell)
      {
         return null;
      }


      if (cell.getCellType() == Cell.CELL_TYPE_STRING)
      {
         String buf = getString(column);

         try
         {
            return Double.valueOf(buf);
         }
         catch (Exception e)
         {
            return _failedToInterpretHandler.failedToInterpretNumeric(column, buf, e);
         }
      }
      else
      {
         return cell.getNumericCellValue();
      }
   }



   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getDate(int)
    */
   public Date getDate(int column) throws IOException
   {
      checkPointer();
      Cell cell = sheet.getRow(pointer).getCell(column);

      if(null == cell)
      {
         return null;
      }

      if (cell.getCellType() == Cell.CELL_TYPE_STRING)
      {

         String buf = getString(column);

         try
         {
            return new SimpleDateFormat().parse(buf);
         }
         catch (Exception e) // Type of this exception was changed from Java 11 to Java 17
         {
            return _failedToInterpretHandler.failedToInterpretDate(column, buf, e);
         }

      }
      else
      {
         return DateUtil.getJavaDate(cell.getNumericCellValue());
      }
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getLong(int)
    */
   public Long getLong(int column) throws IOException
   {
      Double ret = getDouble(column);
      if (null == ret)
      {
         return null;
      }
      else
      {
         return ret.longValue();
      }
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getConfigurationPanel()
    */
   public ConfigurationPanel createConfigurationPanel()
   {
      return new ExcelSettingsPanel(settings, importFile);
   }

   @Override
   public void setTrimValues(boolean trimValues)
   {
      _trimValues = trimValues;
   }

   @Override
   public String getImportFileTypeDescription()
   {
      return "MS EXCEL";
   }

   private Sheet getSheet(Workbook wb)
   {
      Sheet s = null;
      if (settings.getSheetName() != null)
      {
         s = wb.getSheet(settings.getSheetName());
      }
      if (s == null)
      {
         s = wb.getSheetAt(0);
      }
      return s;
   }
}
