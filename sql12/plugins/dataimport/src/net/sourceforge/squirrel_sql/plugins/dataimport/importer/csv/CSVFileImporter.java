package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ImportPropsDAO;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.FailedToInterpretHandler;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;

/**
 * This class implements the IFileImporter interface for reading CSV files.
 *
 * @author Thorsten Mürell
 */
public class CSVFileImporter implements IFileImporter
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(CSVFileImporter.class);

   private CSVSettingsBean settings;
   private File importFile;
   private CsvReader reader = null;
   private boolean safetySwitch = true;
   private boolean _trimValues = true;

   private FailedToInterpretHandler _failedToInterpretHandler = new FailedToInterpretHandler();


   /**
    * The standard constructor
    *
    * @param importFile The import file
    */
   public CSVFileImporter(File importFile)
   {
      this.importFile = importFile;
      this.settings = new CSVSettingsBean();

      this.settings.setSeperator(ImportPropsDAO.getCSVSeparator());
      this.settings.setDateFormat(ImportPropsDAO.getCSVDateFormat());
      this.settings.setImportCharset(ImportPropsDAO.getImportCharset());
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#open()
    */
   public boolean open() throws IOException
   {
      reset();
      return true;
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#close()
    */
   public boolean close()
   {
      if (reader != null)
      {
         reader.close();
      }
      return true;
   }

   /* (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getPreview(int)
    */
   public String[][] getPreview(int noOfLines) throws IOException
   {
      CsvReader csvReader = new CsvReader(new InputStreamReader(new FileInputStream(importFile), settings.getImportCharset()), settings.getSeperator(), _trimValues);
      String[][] data = new String[noOfLines][];

      int row = 0;
      int columns = -1;
      while (csvReader.readRecord() && row < noOfLines)
      {
         if (columns == -1)
         {
            columns = csvReader.getColumnCount();
         }
         data[row] = new String[columns];
         for (int i = 0; i < columns; i++)
         {
            data[row][i] = csvReader.get(i);
         }
         row++;
      }
      csvReader.close();

      String[][] outData = new String[row][];
      for (int i = 0; i < row; i++)
      {
         outData[i] = data[i];
      }
      return outData;
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#next()
    */
   public boolean next() throws IOException
   {
      return reader.readRecord();
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#reset()
    */
   public boolean reset() throws IOException
   {
      if (reader != null)
      {
         reader.close();
      }
      reader = new CsvReader(new InputStreamReader(new FileInputStream(importFile), settings.getImportCharset()), settings.getSeperator(), _trimValues);
      reader.setSafetySwitch(safetySwitch);
      return true;
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getString(int)
    */
   public String getString(int column) throws IOException
   {
      return reader.get(column);
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

   @Override
   public Double getDouble(int column) throws IOException
   {
      String doubleS = reader.get(column);
      if (null == doubleS || 0 == doubleS.trim().length())
      {
         return null;
      }

      try
      {
         return Double.parseDouble(doubleS);
      }
      catch (NumberFormatException nfe)
      {
         return _failedToInterpretHandler.failedToInterpretNumeric(column, doubleS);
      }
   }


   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getDate(int)
    */
   public Date getDate(int column) throws IOException
   {
      String dateString = reader.get(column);

      if (null == dateString || 0 == dateString.trim().length())
      {
         return null;
      }

      try
      {
         return new SimpleDateFormat(settings.getDateFormat()).parse(dateString);
      }
      catch (ParseException pe)
      {
         return _failedToInterpretHandler.failedToInterpretDate(column, dateString);
      }
   }

   /*
    * (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter#getConfigurationPanel()
    */
   public JComponent getConfigurationPanel()
   {
      return new CSVSettingsPanel(settings);
   }

   @Override
   public void setTrimValues(boolean trimValues)
   {
      _trimValues = trimValues;
   }

   @Override
   public String getImportFileTypeDescription()
   {
      return "CSV";
   }

   /**
    * Sets the safetySwitch of the CSV reader. This allows reading lines which are over 100.000 characters long. <br />
    * The reader must be opened before setting the SafetySwitch.
    *
    * @param safetySwitch the value of the switch (true = safety on)
    */
   public void setSafetySwitch(boolean safetySwitch)
   {
      this.safetySwitch = safetySwitch;
   }
}
