package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class CheckListRenderer extends JCheckBox implements ListCellRenderer<CatalogChecked>
{

   private DefaultListCellRenderer _defaultListCellRenderer = new DefaultListCellRenderer();

   public CheckListRenderer()
   {
      setBackground(UIManager.getColor("List.textBackground"));
      setForeground(UIManager.getColor("List.textForeground"));
      setBorder(new EmptyBorder(1, 1, 1, 1));
   }

   @Override
   public Component getListCellRendererComponent(JList<? extends CatalogChecked> list, CatalogChecked catalogChecked, int index,
                                                 boolean isSelected, boolean cellHasFocus)
   {
      if(null == catalogChecked)
      {
         return _defaultListCellRenderer.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
      }

      setSelected(catalogChecked != null && catalogChecked.isChecked());
      setText(catalogChecked.getCatalog());
      return this;
   }
}