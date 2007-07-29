package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;

public interface IHqlEntryPanelManager
{
   JComponent  getComponent();
   ISQLEntryPanel getEntryPanel();

   void addToSQLEntryAreaMenu(JMenu menu);

   JMenuItem addToSQLEntryAreaMenu(Action action);

   void addToToolsPopUp(String selectionString, Action action);

   void registerKeyboardAction(Action action, KeyStroke keyStroke);
}
