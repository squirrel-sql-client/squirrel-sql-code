package org.squirrelsql.session.completion;

import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.schemainfo.FullyQualifiedTableName;
import org.squirrelsql.session.schemainfo.StructItemSchema;
import org.squirrelsql.workaround.StringWidthWA;

import java.util.List;

public class CompletionUtil
{

   private static void adjustPreferredListHeight(ListView listView)
   {
      listView.setPrefHeight(Math.min(listView.getItems().size(), 15) * 24 + 3);
   }

   private static  void adjustPreferredListWidth(ListView listView, Font textComponentFont)
   {

      double maxItemWidth = 0;
      for (Object item : listView.getItems())
      {
         maxItemWidth = Math.max(StringWidthWA.computeTextWidth(textComponentFont, item.toString()), maxItemWidth);
      }
      listView.setPrefWidth(maxItemWidth + 35);
   }

   public static void prepareCompletionList(ListView listView, Font textComponentFont)
   {

      listView.setCellFactory(param -> new CompletionListCell(textComponentFont));

      adjustPreferredListHeight(listView);
      adjustPreferredListWidth(listView, textComponentFont);
   }

   public static String getCatalogSchemaString(StructItemSchema schema)
   {
      String ret = "";

      if(null != schema.getCatalog())
      {
         ret += schema.getCatalog() + ".";
      }

      if(null != schema.getSchema())
      {
         ret += schema.getSchema();
      }
      return ret;
   }

   public static FullyQualifiedTableName getFullyQualifiedTableName(String tableString)
   {
      List<String> filteredStrings = CollectionUtil.filter(tableString.split("\\."), s -> false == Utils.isEmptyString(s));

      if(1 == filteredStrings.size())
      {
         return new FullyQualifiedTableName(null, null, filteredStrings.get(0));
      }
      else if(2 == filteredStrings.size())
      {
         return new FullyQualifiedTableName(null, filteredStrings.get(0), filteredStrings.get(1));
      }
      else if(3 == filteredStrings.size())
      {
         return new FullyQualifiedTableName(filteredStrings.get(0), filteredStrings.get(1), filteredStrings.get(2));
      }


      return null;
   }
}
