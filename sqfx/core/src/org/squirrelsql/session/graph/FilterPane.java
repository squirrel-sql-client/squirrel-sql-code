package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.graph.filter.FilterCtrl;

public class FilterPane extends BorderPane
{
   private FilterPersistence _filterPersistence;
   private ColumnInfo _columnInfo;

   public FilterPane(FilterPersistence filterPersistence, ColumnInfo columnInfo, QueryChannel queryChannel)
   {
      _filterPersistence = filterPersistence;
      _columnInfo = columnInfo;

      updateIcon();

      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> onFilterCtrl(queryChannel));
   }

   private void onFilterCtrl(QueryChannel queryChannel)
   {
      new FilterCtrl(_filterPersistence, _columnInfo, queryChannel);
      updateIcon();
   }

   private void updateIcon()
   {
      ImageView imageView;
      if (Utils.isEmptyString(_filterPersistence.getFilter()))
      {
         imageView = new ImageView(new Props(getClass()).getImage("filter.gif"));
      }
      else
      {
         imageView = new ImageView(new Props(getClass()).getImage("filter_checked.gif"));
      }
      setCenter(imageView);
   }

}
