package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.util.List;


/**
 * This class mainly exits to enable IDEs to find where dragged ObjectTree objects are received via usage search.
 */
public class ObjectTreeDndTransfer
{
   private List<ITableInfo> _selectedTables;

   public ObjectTreeDndTransfer(List<ITableInfo> selectedTables)
   {
      _selectedTables = selectedTables;
   }

   public List<ITableInfo> getSelectedTables()
   {
      return _selectedTables;
   }
}
