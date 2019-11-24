package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChangeTracker
{
   private final LeftGutterItemsManager _leftGutterItemsManager;
   private ISQLEntryPanel _sqlEntry;
   private ChangeTrackPanel _changeTrackPanel;

   public ChangeTracker(ISQLEntryPanel sqlEntry)
   {
      _sqlEntry = sqlEntry;

      _leftGutterItemsManager = new LeftGutterItemsManager(_sqlEntry);

      JScrollPane scrollPane = _sqlEntry.getTextAreaEmbeddedInScrollPane();
      _changeTrackPanel = new ChangeTrackPanel(scrollPane, g -> onPaintLeftGutter(g), g -> onPaintRightGutter(g));

      _changeTrackPanel.trackingGutterLeft.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onLeftGutterMousePressed(e);
         }
      });

      _sqlEntry.setTextAreaPaintListener(() -> _changeTrackPanel.requestGutterRepaint());
   }

   private void onLeftGutterMousePressed(MouseEvent e)
   {
      _leftGutterItemsManager.leftGutterMousePressed(e, _changeTrackPanel.trackingGutterLeft);
   }

   private void onPaintRightGutter(Graphics g)
   {

   }

   private void onPaintLeftGutter(Graphics g)
   {
      List<LeftGutterItem> leftGutterItems = _leftGutterItemsManager.getLeftGutterItems();

      for (LeftGutterItem leftGutterItem : leftGutterItems)
      {
         leftGutterItem.paint(g);
      }
   }

   public ChangeTrackPanel embedInTracking()
   {
      return _changeTrackPanel;
   }
}
