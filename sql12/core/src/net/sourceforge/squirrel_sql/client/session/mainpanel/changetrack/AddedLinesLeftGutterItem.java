package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class AddedLinesLeftGutterItem implements LeftGutterItem
{
   private final ISQLEntryPanel _sqlEntry;
   private final int _beginLine;
   private final int _newLinesCount;

   public AddedLinesLeftGutterItem(ISQLEntryPanel sqlEntry, int beginLine, int newLinesCount)
   {
      _sqlEntry = sqlEntry;
      _beginLine = beginLine;
      _newLinesCount = newLinesCount;
   }

   public void paint(Graphics g)
   {
      Rectangle rect = LeftGutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _newLinesCount);

      if(null == rect)
      {
         return;
      }

      //System.out.println("rect = " + rect);

      Color buf = g.getColor();
      g.setColor(new Color(150, 180, 150));
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setColor(buf);

   }

   @Override
   public void showPopupIfHit(MouseEvent e, JPanel trackingGutterLeft)
   {
      Rectangle rect = LeftGutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _newLinesCount);

      if(rect.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         JPopupMenu popupMenu = new JPopupMenu();
         popupMenu.add(new AddedLinesPopupPanel());
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, e.getY());

      }
   }

}
