package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LeftGutterItemsManager
{
   private final ArrayList<LeftGutterItem> _currentLeftGutterItems;
   private ISQLEntryPanel _sqlEntry;

   public LeftGutterItemsManager(ISQLEntryPanel sqlEntry)
   {
      _sqlEntry = sqlEntry;

      _currentLeftGutterItems = new ArrayList<>();

      _currentLeftGutterItems.add(new DeletedLinesLeftGutterItem(_sqlEntry,12, "Hier Stand früher mal Gerd\nund wurde gelöscht"));
      _currentLeftGutterItems.add(new ChangedLinesLeftGutterItem(_sqlEntry,25, 3, "Hier Stand früher mal Gerd\nund wurde geändert"));
      _currentLeftGutterItems.add(new AddedLinesLeftGutterItem(_sqlEntry,35, 2));
   }

   public List<LeftGutterItem> getLeftGutterItems()
   {
      return _currentLeftGutterItems;
   }

   public void leftGutterMousePressed(MouseEvent e, JPanel trackingGutterLeft)
   {
      for (LeftGutterItem currentLeftGutterItem : _currentLeftGutterItems)
      {
         currentLeftGutterItem.showPopupIfHit(e, trackingGutterLeft);
      }
   }
}
