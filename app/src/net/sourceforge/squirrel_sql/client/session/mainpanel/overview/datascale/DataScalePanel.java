package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class DataScalePanel extends JPanel
{

   private Point pBuf = new Point();
   private Dimension dBuf = new Dimension();

   public DataScalePanel(DataScaleLayout dataScaleLayout)
   {
      super(dataScaleLayout);
   }


   @Override
   public String getToolTipText(MouseEvent event)
   {

      JButton button = getButtonAt(event.getX());

      if (null == button)
      {
         return super.getToolTipText(event);
      }
      else
      {
         return button.getToolTipText();
      }
   }

   public JButton getButtonAt(int x)
   {
      for (Component component : getComponents())
      {
         if(component.getLocation(pBuf).x < x && x < component.getLocation(pBuf).x + component.getSize(dBuf).width)
         {
            return (JButton) component;
         }
      }

      return null;
   }
}
