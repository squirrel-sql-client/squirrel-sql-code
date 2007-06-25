package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SquirrelDefaultUndoManager;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.Properties;
import java.util.HashMap;

public class HQLEntryPanelManager extends EntryPanelManagerBase implements IHqlEntryPanelManager
{

   public HQLEntryPanelManager(ISession session)
   {
      super(session);
   }

   public void addKeystrokeListener(KeyStroke ctrlEnter, AbstractAction action)
   {
      getEntryPanel().getTextComponent().getKeymap().addActionForKeyStroke(ctrlEnter, action);
   }
}
