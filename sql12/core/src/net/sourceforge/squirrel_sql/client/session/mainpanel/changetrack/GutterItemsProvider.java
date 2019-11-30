package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GutterItemsProvider
{
   public GutterItemsProvider(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, GutterItemsProviderListener gutterItemsProviderListener)
   {
      ArrayList currentGutterItems = new ArrayList<>();

      currentGutterItems.add(new DeletedLinesGutterItem(changeTrackPanel, sqlEntry,12, "Hier Stand früher mal Gerd\nund wurde gelöscht"));
      currentGutterItems.add(new ChangedLinesGutterItem(changeTrackPanel, sqlEntry,25, 3, "Hier Stand früher mal Gerd\nund wurde geändert"));
      currentGutterItems.add(new AddedLinesGutterItem(changeTrackPanel, sqlEntry,35, 2));

      gutterItemsProviderListener.updaeGutterItems(currentGutterItems);


      sqlEntry.getTextComponent().addKeyListener(new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onTriggerDiff(e);
         }
      });
   }

   private void onTriggerDiff(KeyEvent e)
   {
      System.out.println("GutterItemsProvider.onTriggerDiff");
   }

}
