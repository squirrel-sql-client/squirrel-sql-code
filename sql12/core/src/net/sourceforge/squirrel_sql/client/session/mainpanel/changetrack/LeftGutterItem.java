package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface LeftGutterItem
{
   void paint(Graphics g);

   void showPopupIfHit(MouseEvent e, JPanel trackingGutterLeft);
}
