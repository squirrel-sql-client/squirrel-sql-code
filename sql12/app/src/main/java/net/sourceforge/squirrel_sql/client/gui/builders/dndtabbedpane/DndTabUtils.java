package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DndTabUtils
{
   public static final Color TARGET_LINE_COLOR = new Color(0, 100, 255);
   public static final int RWH = 20;
   public static final int BUTTON_SIZE = 30; //xxx magic number of scroll button size
   public static final int LINEWIDTH = 3;

   static int getTargetTabIndex(Point glassPt, GhostGlassPane glassPane, JTabbedPane tabbedPane)
   {
      if(0 == tabbedPane.getTabCount())
      {
         return 0;
      }

      Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt, tabbedPane);
      boolean isTB = tabbedPane.getTabPlacement() == JTabbedPane.TOP || tabbedPane.getTabPlacement() == JTabbedPane.BOTTOM;
      for (int i = 0; i < tabbedPane.getTabCount(); i++)
      {
         Rectangle r = tabbedPane.getBoundsAt(i);
         if (isTB) r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
         else r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
         if (r.contains(tabPt)) return i;
      }
      Rectangle r = tabbedPane.getBoundsAt(tabbedPane.getTabCount() - 1);
      if (isTB) r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
      else r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
      return r.contains(tabPt) ? tabbedPane.getTabCount() : -1;
   }

   static Rectangle getTabAreaBounds(JTabbedPane tabbedPane)
   {
      Rectangle tabbedRect = tabbedPane.getBounds();
      //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
      //Rectangle compRect   = getSelectedComponent().getBounds();
      Component comp = tabbedPane.getSelectedComponent();
      int idx = 0;
      while (comp == null && idx < tabbedPane.getTabCount())
      {
         comp = tabbedPane.getComponentAt(idx++);
      }
      Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
      int tabPlacement = tabbedPane.getTabPlacement();
      if (tabPlacement == SwingConstants.TOP)
      {
         tabbedRect.height = tabbedRect.height - compRect.height;
      }
      else if (tabPlacement == SwingConstants.BOTTOM)
      {
         tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
         tabbedRect.height = tabbedRect.height - compRect.height;
      }
      else if (tabPlacement == SwingConstants.LEFT)
      {
         tabbedRect.width = tabbedRect.width - compRect.width;
      }
      else if (tabPlacement == SwingConstants.RIGHT)
      {
         tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
         tabbedRect.width = tabbedRect.width - compRect.width;
      }
      tabbedRect.grow(2, 2);
      return tabbedRect;
   }

   static void initGlassPaneLocal(Point tabPt, DnDTabbedPaneData dnDTabbedPaneData, GhostGlassPane localGlassPane)
   {
      initGlassPane(tabPt, dnDTabbedPaneData, dnDTabbedPaneData, localGlassPane);
   }

   static void initGlassPane(Point tabPt, DnDTabbedPaneData localDnDTabbedPaneData, DnDTabbedPaneData outerDnDTabbedPaneData, GhostGlassPane localGlassPane)
   {
      localDnDTabbedPaneData.getTabbedPane().getRootPane().setGlassPane(localGlassPane);
      if (localDnDTabbedPaneData.isHasGhost())
      {
         Rectangle rect = outerDnDTabbedPaneData.getTabbedPane().getBoundsAt(outerDnDTabbedPaneData.getDragTabIndex());
         BufferedImage image = new BufferedImage(outerDnDTabbedPaneData.getTabbedPane().getWidth(), outerDnDTabbedPaneData.getTabbedPane().getHeight(), BufferedImage.TYPE_INT_ARGB);
         Graphics g = image.getGraphics();
         outerDnDTabbedPaneData.getTabbedPane().paint(g);
         rect.x = rect.x < 0 ? 0 : rect.x;
         rect.y = rect.y < 0 ? 0 : rect.y;

         // When scrollable tabbed panes are used tabs might be displayed in part.
         rect.width = Math.min(rect.width, outerDnDTabbedPaneData.getTabbedPane().getWidth() - rect.x);


         image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
         localGlassPane.setImage(image);
      }
      Point glassPt = SwingUtilities.convertPoint(localDnDTabbedPaneData.getTabbedPane(), tabPt, localGlassPane);
      localGlassPane.setPoint(glassPt);
      localGlassPane.setVisible(true);
   }

   static void hideGlassPane(GhostGlassPane glassPane, DnDTabbedPaneData dnDTabbedPaneData)
   {
      glassPane.setVisible(false);
      if (dnDTabbedPaneData.isHasGhost())
      {
         glassPane.setImage(null);
      }
   }
}
