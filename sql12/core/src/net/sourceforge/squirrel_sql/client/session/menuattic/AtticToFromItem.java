package net.sourceforge.squirrel_sql.client.session.menuattic;

import javax.swing.JMenuItem;

public class AtticToFromItem
{
   private JMenuItem _menuItem;
   private int _index;

   public AtticToFromItem(JMenuItem menuItem, int index)
   {
      _menuItem = menuItem;
      _index = index;
   }

   public JMenuItem getMenuItem()
   {
      return _menuItem;
   }

   public String getMenuText()
   {
      return MenuAtticUtil.getMenuText(_menuItem);
   }


   public int getIndex()
   {
      return _index;
   }

   @Override
   public String toString()
   {
      return getMenuText();
   }
}
