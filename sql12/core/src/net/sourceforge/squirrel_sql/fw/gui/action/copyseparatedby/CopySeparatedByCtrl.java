package net.sourceforge.squirrel_sql.fw.gui.action.copyseparatedby;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;

public class CopySeparatedByCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopySeparatedByCtrl.class);

   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR = "Squirrel.copyseparatedbyctrl.cell.separator";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_CELL_DELIMITER = "Squirrel.copyseparatedbyctrl.cell.delimiter";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_INCLUDE_HEADERS = "Squirrel.copyseparatedbyctrl.include.headers";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR = "Squirrel.copyseparatedbyctrl.row.separator";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN = "Squirrel.copyseparatedbyctrl.prefered.line.len";


   private CopySeparatedByDlg _copySeparatedByDlg;
   private boolean _enableRowSeparator;
   private String _cellSeparator = "";
   private String _cellDelimiter = "";
   private boolean _includeHeaders;
   private String _rowSeparator = "";
   private int _preferredLineLength;
   private boolean _isOk;

   public CopySeparatedByCtrl(DataSetViewerTable table, boolean enableRowSeparator)
   {
      _copySeparatedByDlg = new CopySeparatedByDlg(GUIUtils.getOwningFrame(table));
      _enableRowSeparator = enableRowSeparator;

      _copySeparatedByDlg.txtCellSeparator.setText(Props.getString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR, ","));
      _copySeparatedByDlg.txtCellDelimiter.setText(Props.getString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_DELIMITER, ""));

      _copySeparatedByDlg.txtLineLength.setInt(Props.getInt(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN, 100));
      _copySeparatedByDlg.txtRowSeparator.setText(Props.getString(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR, "\\n"));

      _copySeparatedByDlg.chkIncludeHeaders.setSelected(Props.getBoolean(PREF_KEY_COPYSEPARATEDBYCTRL_INCLUDE_HEADERS, false));

      _copySeparatedByDlg.chkIncludeHeaders.addActionListener(e -> updateEnabled());
      updateEnabled();

      _copySeparatedByDlg.btnOk.addActionListener(e -> onOk());
      _copySeparatedByDlg.btnCancel.addActionListener(e -> onCancel());

      GUIUtils.forceFocus(_copySeparatedByDlg.txtCellSeparator);

      _copySeparatedByDlg.setVisible(true);

   }

   private void updateEnabled()
   {
      _copySeparatedByDlg.txtRowSeparator.setEnabled(true);
      _copySeparatedByDlg._lblRowSeparator.setEnabled(true);

      _copySeparatedByDlg.txtLineLength.setEnabled(true);
      _copySeparatedByDlg.lblPreferredLineLength.setEnabled(true);


      if(false == _enableRowSeparator || _copySeparatedByDlg.chkIncludeHeaders.isSelected())
      {
         _copySeparatedByDlg.txtRowSeparator.setEnabled(false);
         _copySeparatedByDlg._lblRowSeparator.setEnabled(false);
         //_copySeparatedByDlg.txtRowSeparator.setText(null);
      }

      if(_copySeparatedByDlg.chkIncludeHeaders.isSelected())
      {
         _copySeparatedByDlg.txtLineLength.setEnabled(false);
         _copySeparatedByDlg.lblPreferredLineLength.setEnabled(false);
      }
   }

   private void onCancel()
   {
      _copySeparatedByDlg.setVisible(false);
      _copySeparatedByDlg.dispose();
   }

   private void onOk()
   {
      if(0 >= _copySeparatedByDlg.txtLineLength.getInt())
      {
         JOptionPane.showConfirmDialog(_copySeparatedByDlg, s_stringMgr.getString("CopySeparatedByCtrl.invalid.line.length"));
         return;
      }


      String text = "";
      if (null != _copySeparatedByDlg.txtCellSeparator.getText())
      {
         text = _copySeparatedByDlg.txtCellSeparator.getText();
      }
      Props.putString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR, text);
      _cellSeparator = doReplacements(text);


      String cellDelim = "";
      if (null != _copySeparatedByDlg.txtCellDelimiter.getText())
      {
         cellDelim = _copySeparatedByDlg.txtCellDelimiter.getText();
      }
      Props.putString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_DELIMITER, cellDelim);
      _cellDelimiter = cellDelim;


      Props.putBoolean(PREF_KEY_COPYSEPARATEDBYCTRL_INCLUDE_HEADERS, _copySeparatedByDlg.chkIncludeHeaders.isSelected());
      _includeHeaders = _copySeparatedByDlg.chkIncludeHeaders.isSelected();

      Props.putInt(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN, _copySeparatedByDlg.txtLineLength.getInt());
      _preferredLineLength = _copySeparatedByDlg.txtLineLength.getInt();

      if(_enableRowSeparator && null != _copySeparatedByDlg.txtRowSeparator.getText())
      {
         text = "\\n";
         if (null != _copySeparatedByDlg.txtRowSeparator.getText())
         {
            text = _copySeparatedByDlg.txtRowSeparator.getText();
         }
         Props.putString(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR, text);

         _rowSeparator = doReplacements(text);
      }

      _isOk = true;


      _copySeparatedByDlg.setVisible(false);
      _copySeparatedByDlg.dispose();
   }

   private String doReplacements(String text)
   {
      return text.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
   }


   public String getCellSeparator()
   {
      return _cellSeparator;
   }


   public String getRowSeparator()
   {
      return _rowSeparator;
   }

   public int getPreferredLineLength()
   {
      return _preferredLineLength;
   }

   public String getCellDelimiter()
   {
      return _cellDelimiter;
   }

   public boolean isIncludeHeaders()
   {
      return _includeHeaders;
   }

   public boolean isOk()
   {
      return _isOk;
   }
}
