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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.charset.Charset;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ImportFileDialogProps;

/**
 * This class contains the panel for the CSV settings.
 *
 * @author Thorsten Mürell
 */
public class CSVSettingsPanel extends JPanel
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(CSVSettingsPanel.class);

   private CSVSettingsBean settings = null;

   private JTextField txtSeperatorChar = null;
   private JTextField txtDateFormat = null;
   private JRadioButton radUseChar = null;
   private JRadioButton radUseTab = null;
   private JComboBox cboEncoding = null;

   /**
    * Standard constructor
    *
    * @param settings
    */
   public CSVSettingsPanel(CSVSettingsBean settings)
   {
      this.settings = settings;
      init();
      loadSettings();
   }

   private void init()
   {
      // TODO: Tooltips
      ActionListener stateChangedListener = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            CSVSettingsPanel.this.stateChanged();
         }
      };
      KeyListener keyStateChangedListener = new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            CSVSettingsPanel.this.stateChanged();
         }
      };
      txtSeperatorChar = new JTextField(2);
      txtSeperatorChar.addActionListener(stateChangedListener);
      txtSeperatorChar.addKeyListener(keyStateChangedListener);
      //i18n[CSVSettingsPanel.seperatorCharToolTip=Specify the character that is used to seperate the columns in this file (e.g. ',' or ';')]
      txtSeperatorChar.setToolTipText(stringMgr.getString("CSVSettingsPanel.seperatorCharToolTip"));
      txtDateFormat = new JTextField(20);
      txtDateFormat.addActionListener(stateChangedListener);
      txtDateFormat.addKeyListener(keyStateChangedListener);
      //i18n[CSVSettingsPanel.useTab=Tab seperated]
      radUseTab = new JRadioButton(stringMgr.getString("CSVSettingsPanel.useTab"));
      //i18n[CSVSettingsPanel.useChar=Seperated by character:]
      radUseChar = new JRadioButton(stringMgr.getString("CSVSettingsPanel.useChar"));
      radUseChar.setSelected(true);
      radUseTab.addActionListener(stateChangedListener);
      radUseChar.addActionListener(stateChangedListener);
      cboEncoding = new JComboBox();
      for (String c : Charset.availableCharsets().keySet())
      {
         cboEncoding.addItem(c);
      }
      cboEncoding.addActionListener(stateChangedListener);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radUseTab);
      bg.add(radUseChar);


      final FormLayout layout = new FormLayout(
            // Columns
            "pref, 6dlu, pref, 12dlu, pref:grow",
            // Rows
            "pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu");

      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();
      builder.setDefaultDialogBorder();

      int y = 1;
      //i18n[CSVSettingsPanel.csvSettings=CSV settings]
      builder.addSeparator(stringMgr.getString("CSVSettingsPanel.csvSettings"), cc.xywh(1, y, 5, 1));

      y += 2;
      builder.add(radUseChar, cc.xy(1, y));
      builder.add(txtSeperatorChar, cc.xy(3, y));
      builder.add(radUseTab, cc.xy(5, y));

      y += 2;
      //i18n[CSVSettingsPanel.inputFileEncoding=Input file encoding]
      builder.add(new JLabel(stringMgr.getString("CSVSettingsPanel.inputFileEncoding")), cc.xywh(1, y, 3, 1));
      builder.add(cboEncoding, cc.xy(5, y));

      y += 2;
      //i18n[CSVSettingsPanel.dateFormat=Date format]
      builder.add(new JLabel(stringMgr.getString("CSVSettingsPanel.dateFormat")), cc.xywh(1, y, 3, 1));
      builder.add(txtDateFormat, cc.xy(5, y));

      add(builder.getPanel());
   }

   private void applySettings()
   {
      if (radUseTab.isSelected())
      {
         settings.setSeperator('\t');
      }
      else
      {
         if (txtSeperatorChar.getText().length() > 0)
         {
            settings.setSeperator(txtSeperatorChar.getText().charAt(0));
         }
         else
         {
            settings.setSeperator(';');
         }
      }
      settings.setImportCharset(cboEncoding.getSelectedItem().toString());
      settings.setDateFormat(txtDateFormat.getText());

      ImportFileDialogProps.setCSVSeparator(settings.getSeperator());
      ImportFileDialogProps.setCSVDateFormat(settings.getDateFormat());
      ImportFileDialogProps.setImportCharset(settings.getImportCharset());
   }

   private void loadSettings()
   {
      if (settings.getSeperator() == '\t')
      {
         radUseTab.setSelected(true);
      }
      else
      {
         radUseChar.setSelected(true);
         txtSeperatorChar.setText(Character.toString(settings.getSeperator()));
      }
      txtDateFormat.setText(settings.getDateFormat());
      cboEncoding.setSelectedItem(settings.getImportCharset());
   }

   private void stateChanged()
   {
      if (txtSeperatorChar.getText().length() > 1)
      {
         try
         {
            txtSeperatorChar.setText(txtSeperatorChar.getText(0, 1));
         }
         catch (Exception e)
         { /* Ignore that */ }
      }
      if (radUseTab.isSelected())
      {
         txtSeperatorChar.setEnabled(false);
      }
      else
      {
         txtSeperatorChar.setEnabled(true);
      }
      applySettings();
   }
}
