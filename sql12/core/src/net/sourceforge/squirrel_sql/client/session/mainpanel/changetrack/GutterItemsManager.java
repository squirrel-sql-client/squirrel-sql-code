package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GutterItemsManager
{
   private final ArrayList<GutterItem> _currentGutterItems;
   private CursorHandler _cursorHandler = new CursorHandler();

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
      _currentGutterItems.forEach(gi -> gi.leftShowPopupIfHit(e, trackingGutterLeft));
   }

   public void leftGutterMouseMoved(MouseEvent e, JPanel trackingGutterLeft)
   {
      _cursorHandler.reset();
      _currentGutterItems.forEach(gi -> gi.leftGutterMouseMoved(e, _cursorHandler));

      if(_cursorHandler.isClickable())
      {
         trackingGutterLeft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
      else
      {
         trackingGutterLeft.setCursor(Cursor.getDefaultCursor());
      }
   }

   public void rightGutterMousePressed(MouseEvent e)
   {
      _currentGutterItems.forEach(gi -> gi.rightMoveCursorWhenHit(e));
   }

   public void rightGutterMouseMoved(MouseEvent e, JPanel trackingGutterRight)
   {
      _cursorHandler.reset();
      _currentGutterItems.forEach(gi -> gi.rightGutterMouseMoved(e, _cursorHandler));

      if(_cursorHandler.isClickable())
      {
         trackingGutterRight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
      else
      {
         trackingGutterRight.setCursor(Cursor.getDefaultCursor());
      }
   }
}
