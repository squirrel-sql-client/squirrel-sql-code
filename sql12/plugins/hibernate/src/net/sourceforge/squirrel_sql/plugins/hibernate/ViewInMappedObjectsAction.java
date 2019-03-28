package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.event.ActionEvent;

public class ViewInMappedObjectsAction extends SquirrelAction
{

   private final ISQLEntryPanel _entryPanel;
   private HibernateChannel _hibernateChannel;

   public ViewInMappedObjectsAction(HibernatePluginResources resources, ISQLEntryPanel entryPanel, HibernateChannel hibernateChannel)
   {
      super(Main.getApplication(), resources);
      _entryPanel = entryPanel;
      _hibernateChannel = hibernateChannel;
   }


   public void actionPerformed(ActionEvent e)
   {
      String wordAtCursor = _entryPanel.getWordAtCursor();
      _hibernateChannel.viewInMappedObjects(wordAtCursor);
   }
}
