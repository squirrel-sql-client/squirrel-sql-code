package org.squirrelsql.session.graph;

import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

public class ColumnListCell extends ListCell<GraphColumn>
{
   private final ColumnConfigurationCtrl _columnConfigurationCtrl = new ColumnConfigurationCtrl();


   public ColumnListCell()
   {
      double size = getFont().getSize() - 1;
      setFont(Font.font(size));
   }

   @Override
   public void updateItem(GraphColumn columnInfo, boolean empty)
   {
      super.updateItem(columnInfo, empty);

      if(empty)
      {
         setText(null);
         setGraphic(null);
         return;
      }

      setText(columnInfo.getDescription());
      setGraphic(_columnConfigurationCtrl.createPanel(columnInfo.getColumnConfiguration()));
   }


}
