package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;

import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class ChangeTracker
{
   private final ISQLEntryPanel _sqlEntry;
   private ChangeTrackPanel _changeTrackPanel;
   private GutterItemsManager _gutterItemsManager;

   public ChangeTracker(ISQLEntryPanel sqlEntry)
   {
      _sqlEntry = sqlEntry;
      JScrollPane scrollPane = sqlEntry.getTextAreaEmbeddedInScrollPane();
      _changeTrackPanel = new ChangeTrackPanel(scrollPane, g -> onPaintLeftGutter(g), g -> onPaintRightGutter(g));

   }

   public void initChangeTracking(IFileEditorAPI fileEditorAPI)
   {
      _gutterItemsManager = new GutterItemsManager(_sqlEntry, _changeTrackPanel, fileEditorAPI);

      _changeTrackPanel.trackingGutterLeft.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onLeftGutterMousePressed(e);
         }
      });
      _changeTrackPanel.trackingGutterLeft.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e)
         {
            onLeftGutterMouseMoved(e);
         }
      });

      _changeTrackPanel.trackingGutterRight.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onRightGutterMousePressed(e);
         }
      });
      _changeTrackPanel.trackingGutterRight.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e)
         {
            onRightGutterMouseMoved(e);
         }
      });

      _sqlEntry.setTextAreaPaintListener(() -> _changeTrackPanel.requestGutterRepaint());
   }

   private void onLeftGutterMouseMoved(MouseEvent e)
   {
      _gutterItemsManager.leftGutterMouseMoved(e, _changeTrackPanel.trackingGutterLeft);
   }

   private void onLeftGutterMousePressed(MouseEvent e)
   {
      _gutterItemsManager.leftGutterMousePressed(e, _changeTrackPanel.trackingGutterLeft);
   }


   private void onRightGutterMouseMoved(MouseEvent e)
   {
      _gutterItemsManager.rightGutterMouseMoved(e, _changeTrackPanel.trackingGutterRight);
   }

   private void onRightGutterMousePressed(MouseEvent e)
   {
      _gutterItemsManager.rightGutterMousePressed(e);
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


   public ChangeTrackTypeEnum getChangeTrackType()
   {
      return _gutterItemsManager.getGutterItemsProvider().getChangeTrackType();
   }

   public void changeTrackTypeChanged(ChangeTrackTypeEnum selectedType)
   {
      _gutterItemsManager.getGutterItemsProvider().changeTrackTypeChanged(selectedType);
   }


   public void rebaseChangeTrackingOnToolbarButtonOrMenu()
   {
      _gutterItemsManager.getGutterItemsProvider().rebaseChangeTrackingOnToolbarButtonOrMenu();
   }
}
