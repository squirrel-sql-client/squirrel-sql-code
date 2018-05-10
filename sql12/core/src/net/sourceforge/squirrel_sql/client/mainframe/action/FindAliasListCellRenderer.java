package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class FindAliasListCellRenderer implements ListCellRenderer
{
   private final Color _defaultBackgroundColor;

   public FindAliasListCellRenderer()
   {
      _defaultBackgroundColor = new JTextArea().getBackground();
   }

   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == value)
      {
         return null;
      }

      SQLAlias sqlAlias = (SQLAlias) value;

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

      
      if(sqlAlias.getColorProperties().isOverrideAliasBackgroundColor())
      {
         Color bg = new Color(sqlAlias.getColorProperties().getAliasBackgroundColorRgbValue());

         if (isSelected)
         {
            comp.setBackground(bg.darker());
         }
         else
         {
            comp.setBackground(bg);
         }
      }


      return comp;

   }
}
