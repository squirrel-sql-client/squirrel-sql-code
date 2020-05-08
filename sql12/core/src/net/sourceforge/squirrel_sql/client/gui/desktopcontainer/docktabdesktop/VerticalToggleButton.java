package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;
import java.awt.Insets;

public class VerticalToggleButton extends JToggleButton
{
   private static final int WIDHT_MARGIN = 20;
   private static final int HEIGHT_MARGIN = 4;

   public VerticalToggleButton(String caption)
   {
      Icon icon = new VerticalLabelIcon(caption, this, new Insets(HEIGHT_MARGIN, WIDHT_MARGIN, HEIGHT_MARGIN, WIDHT_MARGIN));
      super.setIcon(icon);
      super.setActionCommand(caption);
   }

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JFrame frame = new JFrame("Vertical Button Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.add(new VerticalToggleButton("Vertical Up"));
            frame.add(new VerticalToggleButton("Vertical Down"));
            frame.pack();
            frame.setVisible(true);
         }
      });
   }
}
