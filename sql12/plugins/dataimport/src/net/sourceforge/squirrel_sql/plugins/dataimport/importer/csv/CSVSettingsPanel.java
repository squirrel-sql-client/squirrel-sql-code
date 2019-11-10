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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ImportPropsDAO;

/**
 * This class contains the panel for the CSV settings.
 *
 * @author Thorsten Mürell
 */
public class CSVSettingsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CSVSettingsPanel.class);

   private JRadioButton radUseChar = new JRadioButton(s_stringMgr.getString("CSVSettingsPanel.useChar"));
   private JTextField txtSeperatorChar = new JTextField(2);

   private JRadioButton radUseTab = new JRadioButton(s_stringMgr.getString("CSVSettingsPanel.useTab"));
   private JRadioButton radUseNoSeparator = new JRadioButton(s_stringMgr.getString("CSVSettingsPanel.no.separator"));

   private JComboBox cboEncoding = new JComboBox();
   private JTextField txtDateFormat = new JTextField(20);

   private CSVSettingsBean settings;

   /**
    * Standard constructor
    *
    * @param settings
    */
   public CSVSettingsPanel(CSVSettingsBean settings)
   {
      this.settings = settings;
      createUI();
      initListeners();
      loadSettings();
   }

   private void createUI()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      add(radUseChar, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      add(txtSeperatorChar, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,0,5), 0,0);
      add(radUseTab, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,0,5), 0,0);
      add(radUseNoSeparator, gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,0,5), 0,0);
      add(new JLabel(s_stringMgr.getString("CSVSettingsPanel.inputFileEncoding")), gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,0,5), 0,0);
      add(cboEncoding, gbc);


      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      add(new JLabel(s_stringMgr.getString("CSVSettingsPanel.dateFormat")), gbc);

      gbc = new GridBagConstraints(1,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,10,5), 0,0);
      add(txtDateFormat, gbc);

   }

   private void initListeners()
   {
      KeyListener keyStateChangedListener = new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            onStateChanged();
         }
      };

      radUseChar.setSelected(true);
      radUseChar.addActionListener(e1 -> onStateChanged());

      txtSeperatorChar.addActionListener(e -> onStateChanged());
      txtSeperatorChar.addKeyListener(keyStateChangedListener);
      txtSeperatorChar.setToolTipText(s_stringMgr.getString("CSVSettingsPanel.seperatorCharToolTip"));

      radUseTab.addActionListener(e -> onStateChanged());

      radUseNoSeparator.setToolTipText(s_stringMgr.getString("CSVSettingsPanel.no.separator.tooltip"));
      radUseNoSeparator.addActionListener(e -> onStateChanged());

      txtDateFormat.addActionListener(e -> onStateChanged());
      txtDateFormat.addKeyListener(keyStateChangedListener);


      for (String c : Charset.availableCharsets().keySet())
      {
         cboEncoding.addItem(c);
      }
      cboEncoding.addActionListener(e -> onStateChanged());

      ButtonGroup bg = new ButtonGroup();
      bg.add(radUseTab);
      bg.add(radUseChar);
      bg.add(radUseNoSeparator);
   }

   private void applySettings()
   {
      if (radUseTab.isSelected())
      {
         settings.setSeperator('\t');
      }
      else if (radUseNoSeparator.isSelected())
      {
         settings.setSeperator(null);
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

      ImportPropsDAO.setCSVSeparator(settings.getSeperator());
      ImportPropsDAO.setCSVDateFormat(settings.getDateFormat());
      ImportPropsDAO.setImportCharset(settings.getImportCharset());
   }

   private void loadSettings()
   {
      if (settings.getSeperator() == null)
      {
         radUseNoSeparator.setSelected(true);
      }
      else if (settings.getSeperator() == '\t')
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

   private void onStateChanged()
   {
      if (txtSeperatorChar.getText().length() > 1)
      {
          txtSeperatorChar.setText(txtSeperatorChar.getText().substring(0,1));
      }

      txtSeperatorChar.setEnabled(radUseChar.isSelected());

      applySettings();
   }
}
