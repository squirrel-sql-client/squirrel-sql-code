package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChangeTracker
{
   private ISQLEntryPanel _sqlEntry;
   private ChangeTrackPanel _changeTrackPanel;
   private GutterItemsManager _gutterItemsManager;

   public ChangeTracker(ISQLEntryPanel sqlEntry)
   {
      _sqlEntry = sqlEntry;


      JScrollPane scrollPane = _sqlEntry.getTextAreaEmbeddedInScrollPane();
      _changeTrackPanel = new ChangeTrackPanel(scrollPane, g -> onPaintLeftGutter(g), g -> onPaintRightGutter(g));

      _gutterItemsManager = new GutterItemsManager(_sqlEntry, _changeTrackPanel);

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
      _gutterItemsManager.leftGutterMousePressed(e, _changeTrackPanel.trackingGutterLeft);
   }

   private void onPaintRightGutter(Graphics g)
   {
      List<GutterItem> gutterItems = _gutterItemsManager.getLeftGutterItems();

      for (GutterItem gutterItem : gutterItems)
      {
         gutterItem.rightPaint(g);
      }
   }

   private void onPaintLeftGutter(Graphics g)
   {
      List<GutterItem> gutterItems = _gutterItemsManager.getLeftGutterItems();

      for (GutterItem gutterItem : gutterItems)
      {
         gutterItem.leftPaint(g);
      }
   }

   public ChangeTrackPanel embedInTracking()
   {
      return _changeTrackPanel;
   }
}
