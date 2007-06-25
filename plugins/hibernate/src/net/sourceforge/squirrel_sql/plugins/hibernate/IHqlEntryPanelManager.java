package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;

public interface IHqlEntryPanelManager
{
   JComponent  getComponent();
   ISQLEntryPanel getEntryPanel();

   void addKeystrokeListener(KeyStroke ctrlEnter, AbstractAction action);
}
