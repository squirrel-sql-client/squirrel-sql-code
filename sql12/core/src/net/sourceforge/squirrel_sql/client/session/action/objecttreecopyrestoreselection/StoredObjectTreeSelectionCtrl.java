package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class StoredObjectTreeSelectionCtrl
{
   private final StoredObjectTreeSelectionDlg _dlg;
   private boolean _ok;

   public StoredObjectTreeSelectionCtrl(Window owningWindow)
   {
      _dlg = new StoredObjectTreeSelectionDlg(owningWindow);

      List<ObjectTreeSelectionStoreItemWrapper> wrappedItems =
            ObjectTreeSelectionStoreItemWrapper.wrap(Main.getApplication().getObjectTreeSelectionStoreManager().getItems());

      DefaultListModel<ObjectTreeSelectionStoreItemWrapper> model = new DefaultListModel<>();
      model.addAll(wrappedItems);
      _dlg.lstObjectTreeSelections.setModel(model);

      _dlg.lstObjectTreeSelections.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });


      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());
      _dlg.btnDeleteSelected.addActionListener(e -> onDeleteSelected());

      GUIUtils.initLocation(_dlg, 500, 500);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);

   }

   private void onDeleteSelected()
   {
      DefaultListModel<ObjectTreeSelectionStoreItemWrapper> model =
            (DefaultListModel<ObjectTreeSelectionStoreItemWrapper>) _dlg.lstObjectTreeSelections.getModel();

      for (int selectedIndex : _dlg.lstObjectTreeSelections.getSelectedIndices())
      {
         ObjectTreeSelectionStoreItemWrapper wrapper = model.getElementAt(selectedIndex);
         Main.getApplication().getObjectTreeSelectionStoreManager().getItems().remove(wrapper.getItem());
         model.remove(selectedIndex);
      }
   }

   private void onListClicked(MouseEvent e)
   {
      if(2 == e.getClickCount() && null != _dlg.lstObjectTreeSelections.getSelectedValue())
      {
         onOk();
      }
   }

   private void onOk()
   {
      _ok = true;
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public ObjectTreeSelection getObjectTreeSelection()
   {
      if(!_ok || null == _dlg.lstObjectTreeSelections.getSelectedValue())
      {
         return null;
      }

      return _dlg.lstObjectTreeSelections.getSelectedValue().getObjectTreeSelection();
   }
}
