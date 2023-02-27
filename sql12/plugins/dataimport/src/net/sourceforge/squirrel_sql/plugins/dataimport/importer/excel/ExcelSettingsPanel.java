package net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel;
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

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.ConfigurationPanel;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

/**
 * This panel holds the excel specific settings for the importer.
 *
 * @author Thorsten Mürell
 */
public class ExcelSettingsPanel extends ConfigurationPanel
{


   private static final StringManager stringMgr = StringManagerFactory.getStringManager(ExcelSettingsPanel.class);

   private ExcelSettingsBean _settings;
   private Workbook _workbook;

   private JComboBox _sheetName = null;

   /**
    * The standard constructor
    *
    * @param settings The settings holder
    * @param f        The import file.
    */
   public ExcelSettingsPanel(ExcelSettingsBean settings, File f)
   {
      this._settings = settings;
      try
      {
         this._workbook = WorkbookFactory.create(f);
      }
      catch (Exception e)
      {
         this._workbook = null;
      }
      init();
      loadSettings();
   }

   private void init()
   {
      _sheetName = new JComboBox();
      if (_workbook != null)
      {
         int nSheets = _workbook.getNumberOfSheets();
         for (int i = 0; i < nSheets; i++)
         {
            _sheetName.addItem(_workbook.getSheetAt(i).getSheetName());
         }
      }
      _sheetName.addActionListener(e -> this.stateChanged());

      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0);
      add(GUIUtils.createHorizontalSeparatorPanel(stringMgr.getString("ExcelSettingsPanel.xlsSettings")), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 30, 5), 0, 0);
      add(new JLabel(stringMgr.getString("ExcelSettingsPanel.sheetName")), gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 30, 10), 0, 0);
      add(_sheetName, gbc);

      //add(pnl);


//      final FormLayout layout = new FormLayout(
//            // Columns
//            "pref, 6dlu, pref:grow",
//            // Rows
//            "pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu");
//
//      PanelBuilder builder = new PanelBuilder(layout);
//      CellConstraints cc = new CellConstraints();
//      builder.setDefaultDialogBorder();
//
//      int y = 1;
//      //i18n[ExcelSettingsPanel.xlsSettings=Excel import settings]
//      builder.addSeparator(stringMgr.getString("ExcelSettingsPanel.xlsSettings"), cc.xywh(1, y, 3, 1));
//
//      y += 2;
//      //i18n[ExcelSettingsPanel.sheetName=Sheet name]
//      builder.add(new JLabel(stringMgr.getString("ExcelSettingsPanel.sheetName")), cc.xy(1, y));
//      builder.add(_sheetName, cc.xy(3, y));
//
//      add(builder.getPanel());
   }


   private void applySettings()
   {
      if (_sheetName.getSelectedItem() != null)
      {
         _settings.setSheetName(_sheetName.getSelectedItem().toString());
      }
   }

   private void loadSettings()
   {
      if (_settings.getSheetName() == null)
      {
         _sheetName.setSelectedIndex(0);
      }
      else
      {
         _sheetName.setSelectedItem(_settings.getSheetName());
      }
   }

   private void stateChanged()
   {
      applySettings();
   }

   @Override
   public void apply()
   {

   }
}
