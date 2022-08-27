package net.sourceforge.squirrel_sql.client.session.menuattic;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class AtticToFromModel
{
   private final ArrayList<AtticToFromItem> _atticToFromItems = new ArrayList<>();

   public AtticToFromModel(JPopupMenu popupMenu)
   {
      int index = 0;

      for (Component component : popupMenu.getComponents())
      {
         if(component instanceof JMenuItem)
         {
            _atticToFromItems.add(new AtticToFromItem((JMenuItem) component, index++));
         }
      }
   }

   public List<AtticToFromItem> getAtticToFromItems()
   {
      return _atticToFromItems;
   }
}
