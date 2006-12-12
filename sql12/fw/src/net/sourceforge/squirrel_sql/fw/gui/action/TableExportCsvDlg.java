package net.sourceforge.squirrel_sql.fw.gui.action;


import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;

import javax.swing.*;
import java.awt.*;

public class TableExportCsvDlg extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(TableExportCsvDlg.class);

   JTextField txtFile;
   JButton btnFile;
   JCheckBox chkWithHeaders;
   JRadioButton radFormatXLS;
   JRadioButton radFormatCSV;
   JLabel lblSeparator;
   JTextField txtSeparatorChar;
   JCheckBox chkSeparatorTab;
   JRadioButton radComplete;
   JRadioButton radSelection;
   JRadioButton radUseGlobalPrefsFormating;
   JRadioButton radUseDefaultFormating;
   JCheckBox chkExecCommand;
   JTextField txtCommand;
   JButton btnCommandFile;
   JButton btnOk;
   JButton btnCancel;

   public TableExportCsvDlg()
   {
      super(GUIUtils.getMainFrame(), true);

      // i18n[TableExportCSVDlg.exportTitleNew=CSV / MS Excel export]
      setTitle(s_stringMgr.getString("TableExportCSVDlg.exportTitleNew"));

      getContentPane().setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      // i18n[TableExportCsvDlg.exportCsvFile=Export to file:]
      getContentPane().add(new JLabel(s_stringMgr.getString("TableExportCsvDlg.exportCsvFile")), gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      getContentPane().add(getFilePanel(), gbc);


      // i18n[TableExportCsvDlg.withHeaders=Include column headers]
      chkWithHeaders = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.withHeaders"));
      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(chkWithHeaders, gbc);

      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(getExportFormatPanel(), gbc);

      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      getContentPane().add(getSeparatorPanel(), gbc);

      gbc = new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(getSelelectionPanel(), gbc);

      gbc = new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(getFormatingPanel(), gbc);


      // i18n[TableExportCsvDlg.executeCommand=Execute command (%file will be replaced by export file name)]
      chkExecCommand = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.executeCommand"));
      gbc = new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(chkExecCommand, gbc);

      gbc = new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      getContentPane().add(getCommandPanel(), gbc);


      gbc = new GridBagConstraints(0, 10, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(getButtonPanel(), gbc);

      gbc = new GridBagConstraints(0, 11, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
      getContentPane().add(new JPanel(), gbc);
   }

   private JPanel getExportFormatPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.formatCSV=Export CSV file]
      radFormatCSV = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatCSV"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(radFormatCSV, gbc);

      // i18n[TableExportCsvDlg.formatXLS=Export MS Excel (XLS) file]
      radFormatXLS = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatXLS"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radFormatXLS, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radFormatCSV);
      bg.add(radFormatXLS);

      return ret;
   }

   private Component getFormatingPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.useGlobalPrefsFormating=Use formating as configured in Global Prefs]
      radUseGlobalPrefsFormating = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.useGlobalPrefsFormating"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(radUseGlobalPrefsFormating, gbc);

      // i18n[TableExportCsvDlg.useDefaultFormating=Use default formating]
      radUseDefaultFormating = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.useDefaultFormating"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radUseDefaultFormating, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radUseGlobalPrefsFormating);
      bg.add(radUseDefaultFormating);

      return ret;


   }

   private Component getButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.OK=OK]
      btnOk = new JButton(s_stringMgr.getString("TableExportCsvDlg.OK"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(btnOk, gbc);

      // i18n[TableExportCsvDlg.Cancel=Cancel]
      btnCancel = new JButton(s_stringMgr.getString("TableExportCsvDlg.Cancel"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(btnCancel, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
      ret.add(new JPanel(), gbc);


      return ret;
   }

   private Component getCommandPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      txtCommand = new JTextField();
      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(txtCommand, gbc);

      LibraryResources rsrc = new LibraryResources();
      btnCommandFile = new JButton(rsrc.getIcon(LibraryResources.IImageNames.OPEN));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(btnCommandFile, gbc);

      return ret;
   }


   private Component getSelelectionPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.exportCompleteTable=Export complete table]
      radComplete = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportCompleteTable"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(radComplete, gbc);

      // i18n[TableExportCsvDlg.exportSelection=Export selection]
      radSelection = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportSelection"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radSelection, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radComplete);
      bg.add(radSelection);

      return ret;
   }

   private Component getSeparatorPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      // i18n[TableExportCsvDlg.separator=Separator:]
      lblSeparator = new JLabel(s_stringMgr.getString("TableExportCsvDlg.separator"));
      ret.add(lblSeparator, gbc);


      txtSeparatorChar = new JTextField(1);
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0);
      ret.add(txtSeparatorChar, gbc);

      // i18n[TableExportCsvDlg.sepeartorTab=Use tab character]
      chkSeparatorTab = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.sepeartorTab"));
      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(chkSeparatorTab, gbc);


      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);


      return ret;
   }

   private Component getFilePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      txtFile = new JTextField();
      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(txtFile, gbc);

      LibraryResources rsrc = new LibraryResources();

      btnFile = new JButton(rsrc.getIcon(LibraryResources.IImageNames.OPEN));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(btnFile, gbc);

      return ret;
   }
}
