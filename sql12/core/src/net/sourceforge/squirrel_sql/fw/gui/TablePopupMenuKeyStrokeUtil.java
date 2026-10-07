package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.shortcut.ShortCutDescriptionReader;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

public class TablePopupMenuKeyStrokeUtil
{
   static void configureKeystrokeForMenuItem(JMenuItem menuItem, String actionName, DataSetViewerTablePanel tablePanel)
   {
      KeyStroke validKeyStroke = Main.getApplication().getShortcutManager().setAccelerator(menuItem, null, actionName, ShortCutDescriptionReader.of(menuItem));
      if (null != validKeyStroke)
      {
         tablePanel.getTable().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(validKeyStroke, actionName);
         tablePanel.getTable().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(validKeyStroke, actionName);
         tablePanel.getTable().getInputMap(JComponent.WHEN_FOCUSED).put(validKeyStroke, actionName);
         tablePanel.getTable().getActionMap().put(actionName, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
               menuItem.doClick();
            }
         });
      }
   }

   static JMenuItem configureKeyStrokeForAction(Action action, KeyStroke defaultKeyStroke, JMenuItem menuItem, DataSetViewerTablePanel tablePanel)
   {
      KeyStroke validKeyStroke = Main.getApplication().getShortcutManager().setAccelerator(menuItem, defaultKeyStroke, action, ShortCutDescriptionReader.of(action));
      if (null != validKeyStroke)
      {
         tablePanel.getTable().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(validKeyStroke, action.getClass().getName());
         tablePanel.getTable().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(validKeyStroke, action.getClass().getName());
         tablePanel.getTable().getInputMap(JComponent.WHEN_FOCUSED).put(validKeyStroke, action.getClass().getName());
         tablePanel.getTable().getActionMap().put(action.getClass().getName(), action);
      }
      return menuItem;
   }


}
