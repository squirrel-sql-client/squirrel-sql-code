package org.squirrelsql.session.graph;

import javafx.scene.control.ListCell;
import org.squirrelsql.session.ColumnInfo;

public class ColumnListCell extends ListCell<GraphColumn>
{

   @Override
   protected void updateItem(GraphColumn columnInfo, boolean empty)
   {
      super.updateItem(columnInfo, empty);

      if(empty)
      {
         setText(null);
         return;
      }


      setText(columnInfo.getDescription());
   }


}
