package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GutterItemsManager
{
   private final ArrayList<GutterItem> _currentGutterItems;

   public GutterItemsManager(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel)
   {

      _currentGutterItems = new ArrayList<>();

      _currentGutterItems.add(new DeletedLinesGutterItem(changeTrackPanel, sqlEntry,12, "Hier Stand früher mal Gerd\nund wurde gelöscht"));
      _currentGutterItems.add(new ChangedLinesGutterItem(changeTrackPanel, sqlEntry,25, 3, "Hier Stand früher mal Gerd\nund wurde geändert"));
      _currentGutterItems.add(new AddedLinesGutterItem(changeTrackPanel, sqlEntry,35, 2));
   }

   public List<GutterItem> getLeftGutterItems()
   {
      return _currentGutterItems;
   }

   public void leftGutterMousePressed(MouseEvent e, JPanel trackingGutterLeft)
   {
      for (GutterItem currentGutterItem : _currentGutterItems)
      {
         currentGutterItem.leftShowPopupIfHit(e, trackingGutterLeft);
      }
   }
}
