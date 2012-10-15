package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

class DndTabbedPaneTransferable implements Transferable
{
   private static final String NAME = "test";

   private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
   private DnDTabbedPaneData _dnDTabbedPaneData;

   public DndTabbedPaneTransferable(DnDTabbedPaneData dnDTabbedPaneData)
   {
      _dnDTabbedPaneData = dnDTabbedPaneData;
   }

   @Override
   public Object getTransferData(DataFlavor flavor)
   {
      return _dnDTabbedPaneData.getTabbedPane();
   }

   @Override
   public DataFlavor[] getTransferDataFlavors()
   {
      DataFlavor[] f = new DataFlavor[1];
      f[0] = this.FLAVOR;
      return f;
   }

   @Override
   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      return flavor.getHumanPresentableName().equals(NAME);
   }
}
