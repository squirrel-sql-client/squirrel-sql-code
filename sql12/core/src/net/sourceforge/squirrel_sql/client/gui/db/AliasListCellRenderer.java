package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class AliasListCellRenderer extends DefaultListCellRenderer
{
   public Component getListCellRendererComponent(JList list,
                                                 Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus)
   {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      if(value instanceof SQLAlias)
      {
         SQLAlias sqlAlias = (SQLAlias) value;

         if(sqlAlias.getColorProperties().isOverrideAliasBackgroundColor())
         {
            Color bg = new Color(sqlAlias.getColorProperties().getAliasBackgroundColorRgbValue());

            if (isSelected)
            {
               setBackground(bg.darker());
            }
            else
            {
               setBackground(bg);
            }
         }
      }

      return this;
   }
}
