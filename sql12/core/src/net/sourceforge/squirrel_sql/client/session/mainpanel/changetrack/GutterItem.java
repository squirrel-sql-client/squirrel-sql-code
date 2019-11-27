package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface GutterItem
{
   void leftPaint(Graphics g);

   void leftShowPopupIfHit(MouseEvent e, JPanel trackingGutterLeft);

   void rightPaint(Graphics g);
}
