package net.sourceforge.squirrel_sql.fw.gui.checkedlistbox;

import javax.swing.JCheckBox;

public interface CheckedListBoxListener<LIST_MODEL_ITEM_TYPE>
{
   void listBoxItemToInvert(LIST_MODEL_ITEM_TYPE item);

   void listBoxItemToRender(LIST_MODEL_ITEM_TYPE item, JCheckBox renderer);
}
