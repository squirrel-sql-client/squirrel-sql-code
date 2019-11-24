package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;


import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

public class ChangeTrackPanel extends JPanel
{
   public static final int LEFT_GUTTER_WIDTH = 10;
   public static final int RIGHT_GUTTER_WIDTH = 11;
   public static final Color GUTTER_COLOR = new Color(255, 255, 204);

   JPanel trackingGutterRight;
   JPanel trackingGutterLeft;

   public ChangeTrackPanel(JScrollPane scrollPane, GutterPaintListener leftGutterPaintListener, GutterPaintListener rightGutterPaintListener)
   {
      setLayout(new BorderLayout());

      trackingGutterLeft = createTrackingGutter(LEFT_GUTTER_WIDTH, leftGutterPaintListener);
      add(trackingGutterLeft, BorderLayout.WEST);

      add(scrollPane, BorderLayout.CENTER);


      trackingGutterRight = createTrackingGutter(RIGHT_GUTTER_WIDTH, rightGutterPaintListener);
      trackingGutterRight.setBorder(BorderFactory.createMatteBorder(0,1,0,1, Color.gray));
      add(trackingGutterRight, BorderLayout.EAST);
   }

   private JPanel createTrackingGutter(int width, GutterPaintListener gutterPaintListener)
   {
      JPanel trackingGutter = new JPanel(){
         @Override
         public void paint(Graphics g)
         {
            super.paint(g);
            gutterPaintListener.paint(g);
         }
      };



      GUIUtils.setPreferredWidth(trackingGutter, width);
      trackingGutter.setBackground(GUTTER_COLOR);

      return trackingGutter;
   }

   public void requestGutterRepaint()
   {
      trackingGutterLeft.repaint();
      trackingGutterRight.repaint();
   }
}
