package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;


import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;

public class ExportDlg extends JDialog
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportDlg.class);

   JComboBox cboFile;
   JButton btnFile;
   JCheckBox chkWithHeaders;
   JRadioButton radFormatCSV;
   JRadioButton radFormatXLSX;
   JCheckBox chkUseColoring;
   SmallToolTipInfoButton btnUseColoringInfo;
   JRadioButton radFormatXLS;
   JRadioButton radFormatXML;
   JRadioButton radFormatJSON;
   JLabel lblSeparator;
   JLabel lblLineSeparator;
   JLabel lblCharset;
   JTextField txtSeparatorChar;
   JTextField txtLineSeparatorChar;
   JCheckBox chkSeparatorTab;
   JCheckBox chkPlatformLineSeparator;
   JRadioButton radUseGlobalPrefsFormating;
   JRadioButton radUseDefaultFormating;
   JCheckBox chkExecCommand;
   JComboBox cboCommand;
   JButton btnCommandFile;
   JButton btnOk;
   JButton btnCancel;
   JComboBox cboCharsets;
	JComboBox cboLineSeparators;


   public ExportDlg(Window owner, JPanel exportSelectionPanel, ExportDialogType exportDialogType)
   {
      super(owner);
      setModal(true);

      // i18n[TableExportCSVDlg.exportTitleNew=CSV / MS Excel / XML export]
      setTitle(s_stringMgr.getString("TableExportCSVDlg.exportTitleNew"));

      getContentPane().setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
      // i18n[TableExportCsvDlg.exportCsvFile=Export to file:]
      getContentPane().add(new JLabel(s_stringMgr.getString("TableExportCsvDlg.exportCsvFile")), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      getContentPane().add(getFilePanel(), gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0);
      getContentPane().add(getExportFormatPanel(exportDialogType.isEnableColoring()), gbc);

      // i18n[TableExportCsvDlg.withHeaders=Include column headers]
      chkWithHeaders = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.withHeaders"));
      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      getContentPane().add(chkWithHeaders, gbc);



      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 5), 0, 0);
      getContentPane().add(getSeparatorPanel(), gbc);


      gbc = new GridBagConstraints(0, 6, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15, 5, 5, 5), 0, 0);
      //switch (exportDialogType)
      //{
      //   case UI_TABLE_EXPORT:
      //      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15, 5, 5, 5), 0, 0);
      //      break;
      //   case RESULT_SET_EXPORT:
      //      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      //      break;
      //   default:
      //      throw new UnsupportedOperationException("Unknown exportDialogType=" + exportDialogType);
      //
      //}
      getContentPane().add(exportSelectionPanel, gbc);

      gbc = new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(getFormattingPanel(), gbc);

      chkExecCommand = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.executeCommand"));
      gbc = new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(chkExecCommand, gbc);

      gbc = new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 5), 0, 0);
      getContentPane().add(getCommandPanel(), gbc);


      gbc = new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0);
      getContentPane().add(getButtonPanel(), gbc);

      //gbc = new GridBagConstraints(0, 10, 0, 0, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0);
      //getContentPane().add(createInfoLinkLabel(), gbc);

      //if(exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      //{
      //   gbc = new GridBagConstraints(0, 10, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
      //   getContentPane().add(new JPanel(), gbc);
      //}
   }


   private JPanel getExportFormatPanel(boolean enableColoring)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.formatCSV=Export CSV file]
      radFormatCSV = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatCSV"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(radFormatCSV, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0);
      ret.add(createXLSXPanel(enableColoring), gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0);
      radFormatXLS = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatXLS"));
      ret.add(radFormatXLS, gbc);



      // i18n[TableExportCsvDlg.formatXML=Export XML file]
      radFormatXML = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatXML"));
      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radFormatXML, gbc);

      radFormatJSON = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatJSON"));
      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radFormatJSON, gbc);



      gbc = new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);



      ButtonGroup bg = new ButtonGroup();
      bg.add(radFormatCSV);
      bg.add(radFormatXLSX);
      bg.add(radFormatXLS);
      bg.add(radFormatXML);
      bg.add(radFormatJSON);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("TableExportCsvDlg.export.format.title")));

      return ret;
   }

   private JPanel createXLSXPanel(boolean enableColoring)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

      radFormatXLSX = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.formatXLSX"));
      ret.add(radFormatXLSX, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      chkUseColoring = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.coloring.checkbox"));
      chkUseColoring.setToolTipText(s_stringMgr.getString("TableExportCsvDlg.coloring.info.button"));
      if(enableColoring)
      {
         ret.add(chkUseColoring, gbc);
      }

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      btnUseColoringInfo = new SmallToolTipInfoButton(s_stringMgr.getString("TableExportCsvDlg.coloring.info.button"));
      if(enableColoring)
      {
         ret.add(btnUseColoringInfo.getButton(), gbc);
      }

      return ret;
   }

   private Component getFormattingPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.useGlobalPrefsFormatingExcel=Use formating as configured in Global Prefs (recommended for MS Excel)]
      radUseGlobalPrefsFormating = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.useGlobalPrefsFormatingExcel"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0);
      ret.add(radUseGlobalPrefsFormating, gbc);

      // i18n[TableExportCsvDlg.useDefaultFormating=Use default formating]
      radUseDefaultFormating = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.useDefaultFormating"));
      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radUseDefaultFormating, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 2, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radUseGlobalPrefsFormating);
      bg.add(radUseDefaultFormating);

      ret.setBorder(BorderFactory.createEtchedBorder());
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
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0);
      ret.add(btnCancel, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(createInfoLinkLabel(), gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JLabel createInfoLinkLabel()
   {
      JLabel lblInfoLink = new JLabel(s_stringMgr.getString("ExportDlg.info.link.html"));
      lblInfoLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblInfoLink.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            new ExportInfoDlg(ExportDlg.this);
         }
      });
      return lblInfoLink;
   }


   private Component getCommandPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      cboCommand = new JComboBox();
      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(cboCommand, gbc);

      LibraryResources rsrc = Main.getApplication().getResourcesFw();
      btnCommandFile = new JButton(rsrc.getIcon(LibraryResources.IImageNames.OPEN));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(btnCommandFile, gbc);

      return ret;
   }


   /**
    * Create a panel for the selection options.
    */

   private Component getSeparatorPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      int padx = 0;
      int pady = 0;
      
      Insets labelInsets = new Insets(2, 0, 2, 5);
      Insets fieldInsets = new Insets(2, 5, 2, 0);
      
      // Row 1
      
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, labelInsets, padx, pady);
      // i18n[TableExportCsvDlg.columnSeparator=Column Separator:]
      lblSeparator = new JLabel(s_stringMgr.getString("TableExportCsvDlg.columnSeparator"));
      ret.add(lblSeparator, gbc);      
      
      txtSeparatorChar = new JTextField(2);
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, fieldInsets, padx, pady);
      ret.add(txtSeparatorChar, gbc);

      // i18n[TableExportCsvDlg.sepeartorTab=Use tab character]
      chkSeparatorTab = new JCheckBox(s_stringMgr.getString("TableExportCsvDlg.sepeartorTab"));
      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, fieldInsets, padx, pady);
      ret.add(chkSeparatorTab, gbc);
            
      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, fieldInsets, padx, pady);
      ret.add(new JPanel(), gbc);
      
      // Row 2

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, labelInsets, padx, pady);
      //i18n[TableExportCsvDlg.lineSeparatorLabel=Line Separator:]
      lblLineSeparator = new JLabel(s_stringMgr.getString("TableExportCsvDlg.lineSeparatorLabel"));
      ret.add(lblLineSeparator, gbc);

      cboLineSeparators = new JComboBox(new Object[] {LineSeparator.DEFAULT, LineSeparator.LF, LineSeparator.CRLF});
		gbc = new GridBagConstraints(1, 1, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, fieldInsets, padx, pady);
      ret.add(cboLineSeparators, gbc);
            
      // Row 3
      
      cboCharsets = new JComboBox();
      for (String s : Charset.availableCharsets().keySet()) {
    	  cboCharsets.addItem(s);
      }
      
      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, labelInsets, padx, pady);
      // i18n[TableExportCsvDlg.charset=Charset:]
      lblCharset = new JLabel(s_stringMgr.getString("TableExportCsvDlg.charset"));
      ret.add(lblCharset, gbc);          
      
      gbc = new GridBagConstraints(1, 2, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, fieldInsets, padx, pady);
      ret.add(cboCharsets, gbc);

      return ret;
   }

   private Component getFilePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      cboFile = new JComboBox();
      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(cboFile, gbc);

      LibraryResources rsrc = Main.getApplication().getResourcesFw();

      btnFile = new JButton(rsrc.getIcon(LibraryResources.IImageNames.OPEN));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(btnFile, gbc);

      return ret;
   }
}
