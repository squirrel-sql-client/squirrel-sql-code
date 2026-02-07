package net.sourceforge.squirrel_sql.fw.gui.action.copyseparatedby;

import javax.swing.JOptionPane;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class CopySeparatedByCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopySeparatedByCtrl.class);

   public static final String DEFAULT_CELL_SEPARATOR = ",";

   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR = "Squirrel.copyseparatedbyctrl.cell.separator";
   private static final String PREF_KEY_JUST_CONCAT_CELLS = "Squirrel.copyseparatedbyctrl.just.concat.cells";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_CELL_DELIMITER = "Squirrel.copyseparatedbyctrl.cell.delimiter";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_INCLUDE_HEADERS = "Squirrel.copyseparatedbyctrl.include.headers";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR = "Squirrel.copyseparatedbyctrl.row.separator";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN = "Squirrel.copyseparatedbyctrl.prefered.line.len";


   private CopySeparatedByDlg _dlg;
   private boolean _enableRowSeparator;
   private String _cellSeparator = "";
   private boolean _justConcatCells;
   private String _cellDelimiter = "";
   private boolean _includeHeaders;
   private String _rowSeparator = "";
   private int _preferredLineLength;
   private boolean _isOk;
   private boolean _inCellDataPopup;

   public CopySeparatedByCtrl(DataSetViewerTable table, boolean enableRowSeparator)
   {
      _dlg = new CopySeparatedByDlg(GUIUtils.getOwningFrame(table));
      _enableRowSeparator = enableRowSeparator;

      _dlg.txtCellSeparator.setText(Props.getString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR, DEFAULT_CELL_SEPARATOR));
      _dlg.txtCellDelimiter.setText(Props.getString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_DELIMITER, ""));

      _dlg.txtLineLength.setInt(Props.getInt(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN, 100));
      _dlg.txtRowSeparator.setText(Props.getString(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR, "\\n"));

      _dlg.chkIncludeHeaders.setSelected(Props.getBoolean(PREF_KEY_COPYSEPARATEDBYCTRL_INCLUDE_HEADERS, false));
      _dlg.chkIncludeHeaders.addActionListener(e -> updateEnabled());

      _dlg.chkJustConcatCells.setSelected(Props.getBoolean(PREF_KEY_JUST_CONCAT_CELLS, false));
      _dlg.chkJustConcatCells.addActionListener(e -> updateEnabled());

      updateEnabled();

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> onCancel());
      _dlg.btnInCellDataPopup.addActionListener(e -> onInCellDataPopup());

      GUIUtils.forceFocus(_dlg.txtCellSeparator);

      _dlg.setVisible(true);

   }

   private void updateEnabled()
   {
      _dlg.txtCellSeparator.setEnabled(!_dlg.chkJustConcatCells.isSelected());
      _dlg.lblCellSeparator.setEnabled(!_dlg.chkJustConcatCells.isSelected());

      _dlg.txtRowSeparator.setEnabled(true);
      _dlg.lblRowSeparator.setEnabled(true);

      _dlg.txtLineLength.setEnabled(true);
      _dlg.lblPreferredLineLength.setEnabled(true);


      if(false == _enableRowSeparator || _dlg.chkIncludeHeaders.isSelected())
      {
         _dlg.txtRowSeparator.setEnabled(false);
         _dlg.lblRowSeparator.setEnabled(false);
         //_copySeparatedByDlg.txtRowSeparator.setText(null);
      }

      if( _dlg.chkIncludeHeaders.isSelected())
      {
         _dlg.txtLineLength.setEnabled(false);
         _dlg.lblPreferredLineLength.setEnabled(false);
      }
   }

   private void onCancel()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onInCellDataPopup()
   {
      onOk();

      if(_isOk)
      {
         _inCellDataPopup = true;
      }
   }



   private void onOk()
   {
      if( 0 > _dlg.txtLineLength.getInt())
      {
         JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("CopySeparatedByCtrl.invalid.line.length"));
         return;
      }

      String text = DEFAULT_CELL_SEPARATOR;
      if ( null != _dlg.txtCellSeparator.getText())
      {
         text = _dlg.txtCellSeparator.getText();
      }
      Props.putString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR, text);
      _cellSeparator = doReplacements(text);

      Props.putBoolean(PREF_KEY_JUST_CONCAT_CELLS, _dlg.chkJustConcatCells.isSelected());
      _justConcatCells = _dlg.chkJustConcatCells.isSelected();

      String cellDelim = "";
      if(false == StringUtils.isBlank(_dlg.txtCellDelimiter.getText()))
      {
         cellDelim = _dlg.txtCellDelimiter.getText();
      }
      Props.putString(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_DELIMITER, cellDelim);
      _cellDelimiter = cellDelim;

      Props.putBoolean(PREF_KEY_COPYSEPARATEDBYCTRL_INCLUDE_HEADERS, _dlg.chkIncludeHeaders.isSelected());
      _includeHeaders = _dlg.chkIncludeHeaders.isSelected();

      Props.putInt(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN, _dlg.txtLineLength.getInt());
      _preferredLineLength = _dlg.txtLineLength.getInt();

      if(_enableRowSeparator && null != _dlg.txtRowSeparator.getText())
      {
         text = "\\n";
         if ( null != _dlg.txtRowSeparator.getText())
         {
            text = _dlg.txtRowSeparator.getText();
         }
         Props.putString(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR, text);

         _rowSeparator = doReplacements(text);
      }

      _isOk = true;


      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private String doReplacements(String text)
   {
      return text.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
   }


   public String getCellSeparator()
   {
      return _cellSeparator;
   }

   public boolean isJustConcatCells()
   {
      return _justConcatCells;
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

   public boolean isInCellDataPopup()
   {
      return _inCellDataPopup;
   }
}
