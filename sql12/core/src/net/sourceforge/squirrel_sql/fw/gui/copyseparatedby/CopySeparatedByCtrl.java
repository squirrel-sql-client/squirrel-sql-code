package net.sourceforge.squirrel_sql.fw.gui.copyseparatedby;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;
import java.util.prefs.Preferences;

public class CopySeparatedByCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopySeparatedByCtrl.class);

   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR = "Squirrel.copyseparatedbyctrl.cell.separator";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR = "Squirrel.copyseparatedbyctrl.row.separator";
   private static final String PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN = "Squirrel.copyseparatedbyctrl.prefered.line.len";


   private CopySeparatedByDlg _copySeparatedByDlg;
   private boolean _enableRowSeparator;
   private String _cellSeparator = "";
   private String _rowSeparator = "";
   private boolean _isOk;
   private int _preferedLineLength;

   public CopySeparatedByCtrl(DataSetViewerTable table, boolean enableRowSeparator)
   {
      _copySeparatedByDlg = new CopySeparatedByDlg(GUIUtils.getOwningFrame(table));
      _enableRowSeparator = enableRowSeparator;

      _copySeparatedByDlg.txtCellSeparator.setText(Preferences.userRoot().get(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR, ","));
      _copySeparatedByDlg.txtLineLength.setInt(Preferences.userRoot().getInt(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN, 100));
      _copySeparatedByDlg.txtRowSeparator.setText(Preferences.userRoot().get(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR, "\\n"));

      if(false == _enableRowSeparator)
      {
         _copySeparatedByDlg.txtRowSeparator.setEnabled(false);
         _copySeparatedByDlg._lblRowSeparator.setEnabled(false);
         _copySeparatedByDlg.txtRowSeparator.setText(null);
      }

      _copySeparatedByDlg.btnOk.addActionListener(e -> onOk());
      _copySeparatedByDlg.btnCancel.addActionListener(e -> onCancel());

      GUIUtils.forceFocus(_copySeparatedByDlg.txtCellSeparator);

      _copySeparatedByDlg.setVisible(true);

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
      Preferences.userRoot().put(PREF_KEY_COPYSEPARATEDBYCTRL_CELL_SEPARATOR, text);



      if (null != _copySeparatedByDlg.txtCellSeparator.getText())
      {
         _cellSeparator = doReplacements(_copySeparatedByDlg.txtCellSeparator.getText());
      }

      Preferences.userRoot().putInt(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_PREFERED_LINE_LEN, _copySeparatedByDlg.txtLineLength.getInt());
      _preferedLineLength = _copySeparatedByDlg.txtLineLength.getInt();

      if(_enableRowSeparator && null != _copySeparatedByDlg.txtRowSeparator.getText())
      {
         text = "";
         if (null != _copySeparatedByDlg.txtRowSeparator.getText())
         {
            text = _copySeparatedByDlg.txtRowSeparator.getText();
         }
         Preferences.userRoot().put(PREF_KEY_COPYSEPARATEDBYCTRL_ROW_SEPARATOR, text);

         _rowSeparator = doReplacements(_copySeparatedByDlg.txtRowSeparator.getText());
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

   public int getPreferedLineLength()
   {
      return _preferedLineLength;
   }

   public boolean isOk()
   {
      return _isOk;
   }
}
