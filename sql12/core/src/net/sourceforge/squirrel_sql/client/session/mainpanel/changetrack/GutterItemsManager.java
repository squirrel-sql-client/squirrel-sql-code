package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GutterItemsManager
{
   private List<GutterItem> _currentGutterItems = new ArrayList<>();
   private CursorHandler _cursorHandler = new CursorHandler();
   private ChangeTrackPanel _changeTrackPanel;

   public GutterItemsManager(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel)
   {
      _changeTrackPanel = changeTrackPanel;

      new GutterItemsProvider(sqlEntry, changeTrackPanel,  gi -> onNewGutterItems(gi));
   }

   private void onNewGutterItems(List<GutterItem> gi)
   {
      _currentGutterItems = gi;
      _changeTrackPanel.requestGutterRepaint();
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
