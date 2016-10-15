package org.squirrelsql.session.graph;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.squirrelsql.Props;

public class ColumnListCell extends ListCell<GraphColumn>
{
   private final HBox _columnControls;


   public ColumnListCell()
   {
      double size = getFont().getSize() - 1;
      setFont(Font.font(size));
      _columnControls = createColumnControls();
   }


   private HBox createColumnControls()
   {
      HBox ret = new HBox();
      ret.setSpacing(3);

      Props props = new Props(getClass());

      ret.getChildren().add(new GraphListCheckBoxWA());
      ret.getChildren().add(new ImageView(props.getImage("aggfct.png")));
      ret.getChildren().add(new ImageView(props.getImage("filter.gif")));
      return ret;
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
      setGraphic(_columnControls);
   }


}
