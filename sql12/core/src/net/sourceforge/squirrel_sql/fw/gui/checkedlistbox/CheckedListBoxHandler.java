package net.sourceforge.squirrel_sql.fw.gui.checkedlistbox;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CheckedListBoxHandler<LIST_MODEL_ITEM_TYPE>
{
   private final JList<LIST_MODEL_ITEM_TYPE> _chkListBox;
   private final CheckedListBoxListener<LIST_MODEL_ITEM_TYPE> _checkedListBoxListener;

   public CheckedListBoxHandler(JList<LIST_MODEL_ITEM_TYPE> chkListBox, CheckedListBoxListener<LIST_MODEL_ITEM_TYPE> checkedListBoxListener)
   {
      _chkListBox = chkListBox;
      _checkedListBoxListener = checkedListBoxListener;

      _chkListBox.setCellRenderer(new CheckListRenderer<>(checkedListBoxListener));

      // Add a mouse listener to toggle the checkbox when an item is clicked
      _chkListBox.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent event)
         {
            onMouseClicked(event);
         }
      });
   }

   public void setItems(List<LIST_MODEL_ITEM_TYPE> items)
   {
      DefaultListModel<LIST_MODEL_ITEM_TYPE> listModel = new DefaultListModel<>();
      for (LIST_MODEL_ITEM_TYPE item : items)
      {
         listModel.addElement(item);
      }
      _chkListBox.setModel(listModel);
   }

   public List<LIST_MODEL_ITEM_TYPE> getAllItems()
   {
      ArrayList<LIST_MODEL_ITEM_TYPE> ret = new ArrayList<>();
      for (int i = 0; i < _chkListBox.getModel().getSize(); i++)
      {
         ret.add(_chkListBox.getModel().getElementAt(i));
      }

      return ret;
   }

   public void repaint()
   {
      _chkListBox.repaint();
   }


   private void onMouseClicked(MouseEvent event)
   {
      int index = _chkListBox.locationToIndex(event.getPoint());
      if (    index >= 0
            && index < _chkListBox.getModel().getSize()
            && _chkListBox.getCellBounds(index, index).contains(event.getPoint()))
      {
         LIST_MODEL_ITEM_TYPE item = _chkListBox.getModel().getElementAt(index);
         _checkedListBoxListener.listBoxItemToInvert(item);
         _chkListBox.repaint(_chkListBox.getCellBounds(index, index));
      }
   }
}
