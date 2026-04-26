package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.KeyStroke;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DataSetViewerTableCopyAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetViewerTableCopyAction.class);


   private DataSetViewerTable _dataSetViewerTable;

   DataSetViewerTableCopyAction(DataSetViewerTable dataSetViewerTable)
   {
      super(getTableCopyActionName());
      super.putValue(Action.SHORT_DESCRIPTION, s_stringMgr.getString("TablePopupMenu.copy.short.description"));
      super.putValue(Action.LONG_DESCRIPTION, s_stringMgr.getString("TablePopupMenu.copy.long.description"));
      _dataSetViewerTable = dataSetViewerTable;
   }

   public void actionPerformed(ActionEvent evt)
   {
      new TableCopyCommand(_dataSetViewerTable, false).execute();
   }


   public static String getTableCopyActionName()
   {
      return s_stringMgr.getString("TablePopupMenu.copy");
   }

   public static KeyStroke getTableCopyActionKeyStroke()
   {
      return KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
   }

}
