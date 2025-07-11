package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import javax.swing.tree.DefaultTreeCellRenderer;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

public class BookmarksTreeCellRenderer extends DefaultTreeCellRenderer
{
   public BookmarksTreeCellRenderer()
   {
      super.setClosedIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FOLDER_CLOSED));
      super.setOpenIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FOLDER_OPEN));
      super.setLeafIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.BOOKMARK_SINGLE));
   }
}
