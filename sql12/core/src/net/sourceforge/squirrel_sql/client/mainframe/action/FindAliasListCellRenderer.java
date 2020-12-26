package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.gui.db.AliasFolder;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.AliasSearchWrapper;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class FindAliasListCellRenderer implements ListCellRenderer<AliasSearchWrapper>
{
   private final Color _defaultBackgroundColor;

   public FindAliasListCellRenderer()
   {
      _defaultBackgroundColor = new JTextArea().getBackground();
   }

   @Override
   public Component getListCellRendererComponent(JList<? extends AliasSearchWrapper> list, AliasSearchWrapper value, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == value)
      {
         return null;
      }

      JTextArea comp = new JTextArea(value.getSearchListDisplayString());

      comp.setEditable(false);

      if(isSelected)
      {
         comp.setBackground(new JTextField().getSelectionColor());
      }

      if(cellHasFocus)
      {
         comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }

      
      if(AliasFolder.NO_COLOR_RGB != value.getColorRGB())
      {
         Color bg = new Color(value.getColorRGB());

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
