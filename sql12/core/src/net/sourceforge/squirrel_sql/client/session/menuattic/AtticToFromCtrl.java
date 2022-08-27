package net.sourceforge.squirrel_sql.client.session.menuattic;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AtticToFromCtrl
{

   private final AtticToFromDlg _dlg;

   public AtticToFromCtrl(AtticToFromModel atticToFromModel, MenuOrigin menuOrigin)
   {
      _dlg = new AtticToFromDlg(menuOrigin);

      ArrayList<AtticToFromItem> inAtticList = new ArrayList<>();
      ArrayList<AtticToFromItem> outAtticList = new ArrayList<>();
      for (AtticToFromItem atticToFromItem : atticToFromModel.getAtticToFromItems())
      {
         if(Main.getApplication().getPopupMenuAtticModel().isInAttic(menuOrigin, atticToFromItem))
         {
            inAtticList.add(atticToFromItem);
         }
         else
         {
            outAtticList.add(atticToFromItem);
         }
      }

      _dlg.lstInAttic.setModel(new DefaultListModel<>());
      _dlg.lstInAttic.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      inAtticList.sort(Comparator.comparing(AtticToFromItem::getIndex));
      getModel(_dlg.lstInAttic).addAll(inAtticList);

      _dlg.lstOutAttic.setModel(new DefaultListModel<>());
      _dlg.lstOutAttic.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      outAtticList.sort(Comparator.comparing(AtticToFromItem::getIndex));
      getModel(_dlg.lstOutAttic).addAll(outAtticList);

      _dlg.btnMoveOutAttic.addActionListener( e -> onMove(true) );
      _dlg.btnMoveInAttic.addActionListener(e -> onMove(false));

      _dlg.btnOk.addActionListener(e -> onOk(menuOrigin));
      _dlg.btnCancel.addActionListener(e -> close());

      GUIUtils.initLocation(_dlg, 500, 700);

      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);

   }

   private void onOk(MenuOrigin menuOrigin)
   {
      final List<AtticToFromItem> inAtticList =
            Stream.of(getModel(_dlg.lstInAttic).toArray()).map(o -> ((AtticToFromItem) o)).collect(Collectors.toList());

      Main.getApplication().getPopupMenuAtticModel().setAttic(menuOrigin, inAtticList);

      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private DefaultListModel<AtticToFromItem> getModel(JList<AtticToFromItem> lst)
   {
      return (DefaultListModel<AtticToFromItem>) lst.getModel();
   }

   private void onMove(boolean moveOutAttic)
   {
      if(moveOutAttic)
      {
         move(_dlg.lstInAttic, _dlg.lstOutAttic);
      }
      else
      {
         move(_dlg.lstOutAttic, _dlg.lstInAttic);
      }
   }

   private void move(JList<AtticToFromItem> lstFrom, JList<AtticToFromItem> lstTo)
   {
      final List<AtticToFromItem> selectedValuesList = lstFrom.getSelectedValuesList();

      if(selectedValuesList.isEmpty())
      {
         return;
      }

      selectedValuesList.forEach(v -> getModel(lstFrom).removeElement(v));

      ArrayList<AtticToFromItem> buf = new ArrayList<>();
      List.of(getModel(lstTo).toArray()).forEach(o -> buf.add((AtticToFromItem) o));
      buf.addAll(selectedValuesList);
      buf.sort(Comparator.comparing(AtticToFromItem::getIndex));

      getModel(lstTo).clear();
      getModel(lstTo).addAll(buf);

      int[] selectedIndices = new int[selectedValuesList.size()];
      int curIx = 0;

      for (int i = 0; i < buf.size(); i++)
      {
         AtticToFromItem item = buf.get(i);

         if(selectedValuesList.contains(item))
         {
            selectedIndices[curIx++] = i;
         }
      }

      lstTo.setSelectedIndices(selectedIndices);
      lstTo.scrollRectToVisible(lstTo.getCellBounds(selectedIndices[0], selectedIndices[0] + 1));
   }
}
