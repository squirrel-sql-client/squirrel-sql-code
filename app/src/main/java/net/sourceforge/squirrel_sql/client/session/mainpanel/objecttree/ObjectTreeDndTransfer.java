package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.util.List;


/**
 * This class mainly exits to enable IDEs to find where dragged ObjectTree objects are received via usage search.
 */
public class ObjectTreeDndTransfer
{
   private List<ITableInfo> _selectedTables;
   private IIdentifier _sessionIdentifier;

   public ObjectTreeDndTransfer(List<ITableInfo> selectedTables, IIdentifier sessionIdentifier)
   {
      _selectedTables = selectedTables;
      _sessionIdentifier = sessionIdentifier;
   }

   public List<ITableInfo> getSelectedTables()
   {
      return _selectedTables;
   }

   public IIdentifier getSessionIdentifier()
   {
      return _sessionIdentifier;
   }
}
