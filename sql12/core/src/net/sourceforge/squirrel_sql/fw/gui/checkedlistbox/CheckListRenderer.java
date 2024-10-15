package net.sourceforge.squirrel_sql.fw.gui.checkedlistbox;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Component;

public class CheckListRenderer<LIST_MODEL_ITEM_TYPE> extends JCheckBox implements ListCellRenderer<LIST_MODEL_ITEM_TYPE>
{

   private final CheckedListBoxListener<LIST_MODEL_ITEM_TYPE> _checkedListBoxListener;
   private DefaultListCellRenderer _defaultListCellRenderer = new DefaultListCellRenderer();

   public CheckListRenderer(CheckedListBoxListener<LIST_MODEL_ITEM_TYPE> checkedListBoxListener)
   {
      _checkedListBoxListener = checkedListBoxListener;
      setBackground(UIManager.getColor("List.textBackground"));
      setForeground(UIManager.getColor("List.textForeground"));
      setBorder(new EmptyBorder(1, 1, 1, 1));
   }

   @Override
   public Component getListCellRendererComponent(JList<? extends LIST_MODEL_ITEM_TYPE> list, LIST_MODEL_ITEM_TYPE item, int index,
                                                 boolean isSelected, boolean cellHasFocus)
   {
      if(null == item)
      {
         return _defaultListCellRenderer.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
      }
      _checkedListBoxListener.listBoxItemToRender(item, this);

      return this;
   }
}