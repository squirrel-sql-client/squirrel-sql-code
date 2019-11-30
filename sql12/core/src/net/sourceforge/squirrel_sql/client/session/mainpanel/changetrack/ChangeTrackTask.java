package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.util.ArrayList;
import java.util.List;

public class ChangeTrackTask
{

   public static List<GutterItem> getGutterItems(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel)
   {

      ArrayList currentGutterItems = new ArrayList<>();

      currentGutterItems.add(new DeletedLinesGutterItem(changeTrackPanel, sqlEntry,12, "Hier Stand früher mal Gerd\nund wurde gelöscht"));
      currentGutterItems.add(new ChangedLinesGutterItem(changeTrackPanel, sqlEntry,25, 3, "Hier Stand früher mal Gerd\nund wurde geändert"));
      currentGutterItems.add(new AddedLinesGutterItem(changeTrackPanel, sqlEntry,35, 2));

      return currentGutterItems;

   }
}
