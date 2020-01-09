package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.action.FileDisplayWrapper;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

public class ImportFileDialog extends DialogWidget
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ImportFileDialog.class);

   JTable tblPreview;
   JTable tblMapping;
   JCheckBox chkHeadersIncluded;

   JToggleButton btnSuggestNewTable;
   JTextField txtTableName;
   JButton btnShowTableDetails;
   JButton btnCreateTable;


   JToggleButton btnSuggestColumns;
   JToggleButton btnOneToOneMapping;

   JCheckBox chkEmptyTableBeforeImport;
   JLabel lblEmptyTableWarning;

   JCheckBox chkTrimValues;

   JCheckBox chkSafeMode;
   OkClosePanel btnsPnl = new OkClosePanel();


   JCheckBox chkSingleTransaction;
   JFormattedTextField txtCommitAfterInserts;
   JLabel lblCommitAfterInsertBegin;
   JLabel lblCommitAfterInsertEnd;


   public ImportFileDialog(FileDisplayWrapper importFile, String importFileTypeDescription, String tableName)
   {
      super("", true, Main.getApplication());

      setTitle(createTitle(importFileTypeDescription, tableName));

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      makeToolWindow(true);

      final JPanel content = new JPanel(new BorderLayout());
      content.add(createMainPanel(importFile), BorderLayout.CENTER);

      setContentPane(content);
      btnsPnl.makeOKButtonDefault();
      btnsPnl.getRootPane().setDefaultButton(btnsPnl.getOKButton());

   }

   private String createTitle(String importFileTypeDescription, String tableName)
   {
      return s_stringMgr.getString("ImportFileDialog.title", importFileTypeDescription, tableName);
   }

   public void setImportDialogTitle(String importFileTypeDescription, String tableName)
   {
      super.setTitle(createTitle(importFileTypeDescription, tableName));
   }


   private Component createMainPanel(FileDisplayWrapper importFile)
   {
      tblPreview = new JTable();
      tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      tblMapping = new JTable();

      //i18n[ImportFileDialogCtrl.chkHeadersIncluded=Headers in first line]
      chkHeadersIncluded = new JCheckBox(s_stringMgr.getString("ImportFileDialog.headersIncluded"));
      chkHeadersIncluded.setToolTipText(s_stringMgr.getString("ImportFileDialog.headersIncluded.tooltip"));
      chkHeadersIncluded.setSelected(true);

      chkTrimValues = new JCheckBox(s_stringMgr.getString("ImportFileDialog.trim.values"));
      chkTrimValues.setToolTipText(s_stringMgr.getString("ImportFileDialog.trim.values.tooltip"));
      chkTrimValues.setSelected(true);

      chkSafeMode = new JCheckBox(s_stringMgr.getString("ImportFileDialog.safetySwitch"));
      chkSafeMode.setSelected(true);

      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0);
      //i18n[ImportFileDialogCtrl.dataPreview=Data preview]
      ret.add(new JLabel(s_stringMgr.getString("ImportFileDialog.dataPreview", importFile.getDisplayPath())), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(chkHeadersIncluded, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(new JScrollPane(tblPreview), gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(createToggleButtonsPanel(), gbc);

      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(createEmptyTablePanel(), gbc);

      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(chkTrimValues, gbc);

      gbc = new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(createTransactionPanel(), gbc);

      gbc = new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(chkSafeMode, gbc);


      gbc = new GridBagConstraints(0, 8, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(new JScrollPane(tblMapping), gbc);

      gbc = new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(btnsPnl, gbc);

      return ret;
   }

   private JPanel createToggleButtonsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(createSuggestTablePanel(), gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 0, 0, 0), 0, 0);
      ret.add(createColumnButtonsPanel(), gbc);

      return ret;
   }

   private JPanel createSuggestTablePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0);
      btnSuggestNewTable = new JToggleButton(s_stringMgr.getString("ImportFileDialog.suggestNewTable"));
      btnSuggestNewTable.setSelected(false);
      ret.add(btnSuggestNewTable, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0);
      txtTableName = new JTextField("Bla Bla");
      txtTableName.setBorder(BorderFactory.createEtchedBorder());
      ret.add(txtTableName, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0);
      btnShowTableDetails = new JButton("...");
      btnShowTableDetails.setToolTipText(s_stringMgr.getString("ImportFileDialog.show.table.details.tooltip"));
      ret.add(btnShowTableDetails, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 5), 0, 0);
      btnCreateTable = new JButton(s_stringMgr.getString("ImportFileDialog.create.table"));
      btnCreateTable.setToolTipText(s_stringMgr.getString("ImportFileDialog.create.table.tooltip"));
      ret.add(btnCreateTable, gbc);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ImportFileDialog.panel.title.import.table.creation")));

      return ret;
   }

   private JPanel createColumnButtonsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      btnSuggestColumns = new JToggleButton(s_stringMgr.getString("ImportFileDialog.suggestColumns"));
      btnSuggestColumns.setToolTipText(s_stringMgr.getString("ImportFileDialog.suggestColumns.tooltip"));
      btnSuggestColumns.setSelected(false);
      ret.add(btnSuggestColumns, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0);
      btnOneToOneMapping = new JToggleButton(s_stringMgr.getString("ImportFileDialog.oneToOneMapping"));
      btnOneToOneMapping.setToolTipText(s_stringMgr.getString("ImportFileDialog.oneToOneMapping.tooltip"));
      btnOneToOneMapping.setSelected(false);
      ret.add(btnOneToOneMapping, gbc);

      return ret;
   }

   private JPanel createEmptyTablePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      chkEmptyTableBeforeImport = new JCheckBox(s_stringMgr.getString("ImportFileDialog.empty.table"));
      ret.add(chkEmptyTableBeforeImport, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0);
      lblEmptyTableWarning = new JLabel();
      lblEmptyTableWarning.setFont(lblEmptyTableWarning.getFont().deriveFont(Font.BOLD));
      lblEmptyTableWarning.setForeground(Color.red);
      ret.add(lblEmptyTableWarning, gbc);

      return ret;
   }

   private JPanel createTransactionPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      chkSingleTransaction = new JCheckBox(s_stringMgr.getString("ImportFileDialog.singleTransaction"));
      ret.add(chkSingleTransaction, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0);
      lblCommitAfterInsertBegin = new JLabel(s_stringMgr.getString("ImportFileDialog.commitAfterInsert.begin"));
      ret.add(lblCommitAfterInsertBegin, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      txtCommitAfterInserts = new JFormattedTextField(NumberFormat.getInstance());
      txtCommitAfterInserts.setColumns(7);
      ret.add(txtCommitAfterInserts, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      lblCommitAfterInsertEnd = new JLabel(s_stringMgr.getString("ImportFileDialog.commitAfterInsert.end"));
      ret.add(lblCommitAfterInsertEnd, gbc);

      gbc = new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);

      chkSingleTransaction.setSelected(true);

      return ret;
   }



}
