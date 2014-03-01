package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GhostGlassPane extends JPanel
{
   private final AlphaComposite composite;
   private Point location = new Point(0, 0);
   private BufferedImage draggingGhost = null;
   private DnDTabbedPaneData _dnDTabbedPaneData;

   public GhostGlassPane(DnDTabbedPaneData dnDTabbedPaneData)
   {
      _dnDTabbedPaneData = dnDTabbedPaneData;
      setOpaque(false);
      composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
      //http://bugs.sun.com/view_bug.do?bug_id=6700748
      //setCursor(null);
   }

//   @Override
//   public void paint(Graphics g)
//   {
//      super.paint(g);
//
//      Color color = g.getColor();
//      g.setColor(Color.RED);
//      g.fillRect(0, 0, 5, 5);
//
//      g.fillOval(location.x, location.y, 10, 10);
//      g.setColor(color);
//
//
//
//   }

   public void setImage(BufferedImage draggingGhost)
   {
      this.draggingGhost = draggingGhost;
   }

   public void setPoint(Point location)
   {
      this.location = location;
   }

   @Override
   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      g2.setComposite(composite);
      if (_dnDTabbedPaneData.isPaintScrollArea() && _dnDTabbedPaneData.getTabbedPane().getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT)
      {
         g2.setPaint(Color.RED);
         g2.fill(_dnDTabbedPaneData.getrBackward());
         g2.fill(_dnDTabbedPaneData.getrForward());
      }
      if (draggingGhost != null)
      {
         double xx = location.getX() - (draggingGhost.getWidth(this) / 2d);
         double yy = location.getY() - (draggingGhost.getHeight(this) / 2d);
         g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
      }
//      if (_dnDTabbedPaneData.getDragTabIndex() >= 0)
//      {
         g2.setPaint(DndTabUtils.TARGET_LINE_COLOR);
         g2.fill(_dnDTabbedPaneData.getTargetLineRectBuffer());
//      }
   }
}
