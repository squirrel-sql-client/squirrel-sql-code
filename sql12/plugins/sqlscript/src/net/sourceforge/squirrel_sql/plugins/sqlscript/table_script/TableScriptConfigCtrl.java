package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;


public class TableScriptConfigCtrl
{
   private JFrame _mainFrame;
   private TableScriptConfigFrame _dlg;

   private static final String PREFS_KEY_CONSTRAINTS_AND_INDEXES_AT_END = "Squirrel.sqlscript.constAndIndAtEnd";
   private static final String PREFS_KEY_CONSTRAINTS_TO_TABLES_NOT_IN_SCRIPT = "Squirrel.sqlscript.constToTablesNotInScript";

   private boolean _constAndIndAtEnd = true;
   private boolean _constToTablesNotInScript = true;
   private boolean _isOk = false;


	public TableScriptConfigCtrl(JFrame mainFrame)
	{
		this._mainFrame = mainFrame;
	}

   public void doModal()
   {
      _dlg = new TableScriptConfigFrame(_mainFrame);

      String buf;
      buf = Preferences.userRoot().get(PREFS_KEY_CONSTRAINTS_AND_INDEXES_AT_END, "" + _constAndIndAtEnd);
      _dlg.optConstAndIndAtEnd.setSelected(Boolean.valueOf(buf).booleanValue());
      _dlg.optConstAndIndAfterTable.setSelected(!Boolean.valueOf(buf).booleanValue());

      buf = Preferences.userRoot().get(PREFS_KEY_CONSTRAINTS_TO_TABLES_NOT_IN_SCRIPT, "" + _constToTablesNotInScript);
      _dlg.constToTablesNotInScript.setSelected(Boolean.valueOf(buf).booleanValue());

      _dlg.btnOk.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOk();
         }
      });

      _dlg.btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
     		_dlg.setVisible(false);
            _dlg.dispose();
         }
      });
      
      
      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);
   }

   private void onOk()
   {
      _constAndIndAtEnd = _dlg.optConstAndIndAtEnd.isSelected();
      _constToTablesNotInScript = _dlg.constToTablesNotInScript.isSelected();

      Preferences.userRoot().put(PREFS_KEY_CONSTRAINTS_AND_INDEXES_AT_END, "" + _constAndIndAtEnd);
      Preferences.userRoot().put(PREFS_KEY_CONSTRAINTS_TO_TABLES_NOT_IN_SCRIPT, "" + _constToTablesNotInScript);

		_isOk = true;

		_dlg.setVisible(false);
      _dlg.dispose();


   }

   public boolean isConstAndIndAtEnd()
   {
      return _constAndIndAtEnd;
   }

   public boolean includeConstToTablesNotInScript()
   {
      return _constToTablesNotInScript;
   }

	public boolean isOk()
	{
		return _isOk;
	}


}
