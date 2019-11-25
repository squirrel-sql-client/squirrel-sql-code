package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class DeletedLinesLeftGutterItem implements LeftGutterItem
{
   private final ISQLEntryPanel _sqlEntry;
   private final int _lineBefore;
   private final String _deletedText;

   public DeletedLinesLeftGutterItem(ISQLEntryPanel sqlEntry, int lineBefore, String deletedText)
   {
      _sqlEntry = sqlEntry;
      _lineBefore = lineBefore;
      _deletedText = deletedText;
   }

   @Override
   public void paint(Graphics g)
   {
      Rectangle rectBefore = LeftGutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _lineBefore, 1);

      if(null == rectBefore)
      {
         return;
      }

      paintArrow(g, rectBefore.x, rectBefore.y + rectBefore.height, rectBefore.x + rectBefore.width, rectBefore.y + rectBefore.height);
   }

   @Override
   public void showPopupIfHit(MouseEvent e, JPanel trackingGutterLeft)
   {
      Rectangle rectBefore = LeftGutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _lineBefore, 1);

      if(null == rectBefore)
      {
         return;
      }

      Polygon triangle = getTriangle(rectBefore.x, rectBefore.y + rectBefore.height, rectBefore.x + rectBefore.width, rectBefore.y + rectBefore.height);

      if(triangle.getBounds().intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         JPopupMenu popupMenu = new JPopupMenu();
         popupMenu.add(new RevertablePopupPanel(_deletedText, _sqlEntry.getTextComponent().getFont()));
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, e.getY());
      }
   }

   private void paintArrow(Graphics g, int x1, int y1, int x2, int y2)
   {
      Polygon pg = getTriangle(x1, y1, x2, y2);
      g.fillPolygon(pg);
   }

   private Polygon getTriangle(int x1, int y1, int x2, int y2)
   {
      // defines the opening angle of the arrow (not rad or so but something fancy)
      double sAng = 0.5;

      Point c = new Point(x2, y2);
      Point a = new Point((int) (x1 + sAng * (y2 - y1)), (int) (y1 - sAng * (x2 - x1)));
      Point b = new Point((int) (x1 - sAng * (y2 - y1)), (int) (y1 + sAng * (x2 - x1)));

      // defines the size of the arrow
      double sLen = 10 / Math.sqrt((a.x - c.x) * (a.x - c.x) + (a.y - c.y) * (a.y - c.y));

      Point arrPa = new Point((int) (c.x + sLen * (a.x - c.x)), (int) (c.y + sLen * (a.y - c.y)));
      Point arrPb = new Point((int) (c.x + sLen * (b.x - c.x)), (int) (c.y + sLen * (b.y - c.y)));


      Polygon pg = new Polygon();
      pg.addPoint(arrPa.x, arrPa.y);
      pg.addPoint(arrPb.x, arrPb.y);
      pg.addPoint(c.x, c.y);
      return pg;
   }

}
