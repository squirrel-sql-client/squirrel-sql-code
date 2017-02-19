package org.squirrelsql.session.graph;

import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

public class ColumnListCell extends ListCell<GraphColumn>
{
   private final ColumnConfigurationCtrl _columnConfigurationCtrl;


   public ColumnListCell(QueryChannel queryChannel)
   {
      double size = getFont().getSize() - 1;
      setFont(Font.font(size));
      _columnConfigurationCtrl = new ColumnConfigurationCtrl(queryChannel);
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
      setGraphic(_columnConfigurationCtrl.createPanel(columnInfo));
   }


}
