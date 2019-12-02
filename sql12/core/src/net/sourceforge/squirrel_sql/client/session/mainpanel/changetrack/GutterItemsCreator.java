package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GutterItemsCreator
{

   public static List<GutterItem> createGutterItems(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, List<String> originalLines)
   {
      try
      {
         if(null == originalLines)
         {
            return Collections.EMPTY_LIST;
         }


         List<String> currentLines = Arrays.asList(sqlEntry.getText().split("\n"));

         Patch<String> diff = DiffUtils.diff(originalLines, currentLines);


         List<GutterItem> gutterItems = new ArrayList<>();

         for (AbstractDelta<String> delta : diff.getDeltas())
         {
            switch (delta.getType())
            {
               case INSERT:
                  gutterItems.add(createAddedLinesGutterItem(sqlEntry, changeTrackPanel, (InsertDelta<String>) delta));
                  break;
               case CHANGE:
                  gutterItems.add(createChangedLinesGutterItem(sqlEntry, changeTrackPanel, (ChangeDelta<String>) delta));
                  break;
               case DELETE:
                  GutterItem deletedLinesGutterItem = createDeletedLinesGutterItem(sqlEntry, changeTrackPanel, (DeleteDelta<String>) delta, currentLines.size());
                  if (null != deletedLinesGutterItem)
                  {
                     gutterItems.add(deletedLinesGutterItem);
                  }
                  break;
            }
         }

         return gutterItems;

//      currentGutterItems.add(new DeletedLinesGutterItem(changeTrackPanel, sqlEntry,12, "Hier Stand früher mal Gerd\nund wurde gelöscht"));
//      currentGutterItems.add(new ChangedLinesGutterItem(changeTrackPanel, sqlEntry,25, 3, "Hier Stand früher mal Gerd\nund wurde geändert"));
//      currentGutterItems.add(new AddedLinesGutterItem(changeTrackPanel, sqlEntry,35, 2));
//      return gutterItems;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

   }

   private static GutterItem createDeletedLinesGutterItem(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, DeleteDelta<String> delta, int currentLineCount)
   {
      String deletedText = String.join("\n", delta.getSource().getLines());
      if(delta.getTarget().getPosition() == currentLineCount && StringUtilities.isEmpty(deletedText, true))
      {
         return null;
      }


      return new DeletedLinesGutterItem(changeTrackPanel, sqlEntry, delta.getTarget().getPosition(), deletedText);
      //return new DeletedLinesGutterItem(changeTrackPanel, sqlEntry,12, "Hier Stand früher mal Gerd\nund wurde gelöscht");
   }

   private static GutterItem createChangedLinesGutterItem(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, ChangeDelta<String> delta)
   {
      //return new ChangedLinesGutterItem(changeTrackPanel, sqlEntry,25, 3, "Hier Stand früher mal Gerd\nund wurde geändert");
      return new ChangedLinesGutterItem(changeTrackPanel, sqlEntry, delta.getTarget().getPosition() + 1, delta.getTarget().getLines().size(), String.join("\n", delta.getSource().getLines()));
   }

   private static GutterItem createAddedLinesGutterItem(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, InsertDelta<String> delta)
   {
//      return new AddedLinesGutterItem(changeTrackPanel, sqlEntry,35, 2);
      return new AddedLinesGutterItem(changeTrackPanel, sqlEntry,delta.getTarget().getPosition() + 1 , delta.getTarget().getLines().size());
   }
}
