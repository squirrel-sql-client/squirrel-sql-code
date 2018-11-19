package net.sourceforge.squirrel_sql.client.action;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import java.util.ArrayList;

public class SquirrelActionChannel
{
   private ArrayList<JMenuItem> _menuItems = new ArrayList<>();
   private ImageIcon _icon;
   private String _text;

   public void addBoundMenuItem(JMenuItem menuItem)
   {
      _menuItems.add(menuItem);

      if(null != _icon)
      {
         menuItem.setIcon(_icon);
      }

      if(null != _text)
      {
         menuItem.setText(_text);
         menuItem.setToolTipText(_text);
      }
   }

   public void updateIconAndText(ImageIcon icon, String text)
   {
      for (JMenuItem menuItem : _menuItems)
      {
         menuItem.setIcon(icon);
         menuItem.setText(text);
         menuItem.setToolTipText(text);
      }
      _icon = icon;
      _text = text;
   }
}
