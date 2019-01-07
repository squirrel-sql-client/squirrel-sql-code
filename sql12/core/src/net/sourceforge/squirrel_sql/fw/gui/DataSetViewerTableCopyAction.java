package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutManager;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DataSetViewerTableCopyAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetViewerTableCopyAction.class);


   private DataSetViewerTable _dataSetViewerTable;

   DataSetViewerTableCopyAction(DataSetViewerTable dataSetViewerTable)
   {
      super(getTableCopyActionName());
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
      return KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
   }

   public static void replaceStandardTableCopyAction(DataSetViewerTable dataSetViewerTable)
   {
      ShortcutManager shortcutManager = Main.getApplication().getShortcutManager();

      KeyStroke keyStroke = shortcutManager.getValidKeyStroke(getTableCopyActionName(), getTableCopyActionKeyStroke());

      dataSetViewerTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, "CopyAction");
      dataSetViewerTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "CopyAction");
      dataSetViewerTable.getInputMap(JComponent.WHEN_FOCUSED).put(keyStroke, "CopyAction");
      dataSetViewerTable.getActionMap().put("CopyAction", new DataSetViewerTableCopyAction(dataSetViewerTable));
   }

}
