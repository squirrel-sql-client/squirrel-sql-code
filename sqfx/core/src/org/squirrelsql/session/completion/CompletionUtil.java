package org.squirrelsql.session.completion;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class CompletionUtil
{

   private static void adjustPreferredListHeight(ListView listView)
   {
      listView.setPrefHeight(Math.min(listView.getItems().size(), 15) * 24 + 3);
   }

   private static  void adjustPreferredListWidth(ListView listView, SQLTextAreaServices sqlTextAreaServices)
   {
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(sqlTextAreaServices.getFont());

      double maxItemWidth = 0;
      for (Object item : listView.getItems())
      {
         maxItemWidth = Math.max(fontMetrics.computeStringWidth(item.toString()), maxItemWidth);
      }
      listView.setPrefWidth(maxItemWidth + 35);
   }

   public static void prepareCompletionList(ListView listView, SQLTextAreaServices sqlTextAreaServices)
   {

      listView.setCellFactory(param -> new CompletionListCell(sqlTextAreaServices.getFont()));

      adjustPreferredListHeight(listView);
      adjustPreferredListWidth(listView, sqlTextAreaServices);


   }
}
