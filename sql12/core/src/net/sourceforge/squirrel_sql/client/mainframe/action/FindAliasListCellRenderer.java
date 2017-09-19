package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import javax.swing.*;
import java.awt.*;

public class FindAliasListCellRenderer implements ListCellRenderer
{
   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == value)
      {
         return null;
      }

      ISQLAlias sqlAlias = (ISQLAlias) value;

      JTextArea comp = new JTextArea(sqlAlias.getName() + "\n  URL: " + sqlAlias.getUrl() + "\n  User: " + sqlAlias.getUserName() + "\n");

      comp.setEditable(false);

      if(isSelected)
      {
         comp.setBackground(new JTextField().getSelectionColor());
      }

      if(cellHasFocus)
      {
         comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }

      return comp;

   }
}
