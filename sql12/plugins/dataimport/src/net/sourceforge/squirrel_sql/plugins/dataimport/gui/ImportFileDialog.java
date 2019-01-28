package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.NumberFormat;

public class ImportFileDialog extends DialogWidget
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(ImportFileDialog.class);

   JTable tblPreview = null;
   JTable tblMapping = null;
   JCheckBox chkHeadersIncluded = null;
   JToggleButton btnSuggestColumns = null;

   JCheckBox chkEmptyTableBeforeImport;
   JLabel lblEmptyTableWarning;

   JCheckBox chkTrimValues = null;

   JCheckBox chkSafeMode = null;
   OkClosePanel btnsPnl = new OkClosePanel();


   JCheckBox chkSingleTransaction;
   JFormattedTextField txtCommitAfterInserts;
   JLabel lblCommitAfterInsertBegin;
   JLabel lblCommitAfterInsertEnd;


   public ImportFileDialog(File importFile, String importFileTypeDescription, ITableInfo table)
   {
      super(stringMgr.getString("ImportFileDialog.title", importFileTypeDescription, table.getSimpleName()), true, Main.getApplication());

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      makeToolWindow(true);

      final JPanel content = new JPanel(new BorderLayout());
      content.add(createMainPanel(importFile), BorderLayout.CENTER);

      setContentPane(content);
      btnsPnl.makeOKButtonDefault();
      btnsPnl.getRootPane().setDefaultButton(btnsPnl.getOKButton());

   }


   private Component createMainPanel(File importFile)
   {
      tblPreview = new JTable();
      tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      JScrollPane scrollPane = new JScrollPane(tblPreview);

      tblMapping = new JTable();
      JScrollPane scrollPane2 = new JScrollPane(tblMapping);

      //i18n[ImportFileDialogCtrl.chkHeadersIncluded=Headers in first line]
      chkHeadersIncluded = new JCheckBox(stringMgr.getString("ImportFileDialog.headersIncluded"));
      chkHeadersIncluded.setSelected(true);

      // i18n[ImportFileDialogCtrl.btnSuggestColumns=Suggest columns (find matching columns)]
      btnSuggestColumns = new JToggleButton(stringMgr.getString("ImportFileDialog.suggestColumns"));
      btnSuggestColumns.setSelected(false);


      chkTrimValues = new JCheckBox(stringMgr.getString("ImportFileDialog.trim.values"));
      chkTrimValues.setSelected(true);

      chkSafeMode = new JCheckBox(stringMgr.getString("ImportFileDialog.safetySwitch"));
      chkSafeMode.setSelected(true);

      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0);
      //i18n[ImportFileDialogCtrl.dataPreview=Data preview]
      ret.add(new JLabel(stringMgr.getString("ImportFileDialog.dataPreview", importFile.getAbsolutePath())), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(chkHeadersIncluded, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(scrollPane, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(btnSuggestColumns, gbc);

      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(createEmptyTablePanel(), gbc);

      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(chkTrimValues, gbc);

      gbc = new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(createTransactionPanel(), gbc);

      gbc = new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(chkSafeMode, gbc);


      gbc = new GridBagConstraints(0, 8, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(scrollPane2, gbc);

      gbc = new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0);
      ret.add(btnsPnl, gbc);

      return ret;
   }

   private JPanel createEmptyTablePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      chkEmptyTableBeforeImport = new JCheckBox(stringMgr.getString("ImportFileDialog.empty.table"));
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
      chkSingleTransaction = new JCheckBox(stringMgr.getString("ImportFileDialog.singleTransaction"));
      ret.add(chkSingleTransaction, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0);
      lblCommitAfterInsertBegin = new JLabel(stringMgr.getString("ImportFileDialog.commitAfterInsert.begin"));
      ret.add(lblCommitAfterInsertBegin, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      txtCommitAfterInserts = new JFormattedTextField(NumberFormat.getInstance());
      txtCommitAfterInserts.setColumns(7);
      ret.add(txtCommitAfterInserts, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      lblCommitAfterInsertEnd = new JLabel(stringMgr.getString("ImportFileDialog.commitAfterInsert.end"));
      ret.add(lblCommitAfterInsertEnd, gbc);

      gbc = new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);

      chkSingleTransaction.setSelected(true);

      return ret;
   }



}
