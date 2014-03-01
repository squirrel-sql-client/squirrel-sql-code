package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This is a compromise because a clean working tripple state checkBox could not be found.
 * For example Jide's TriStateCheckbox listeners didn't work at all and its DONT_CARE state
 * does not survive a repaint.
 *
 * Another solution could have been to use own check icons but this wouldn't be L&F compatible.
 */
public class TrippleStateCheckBox extends JCheckBox
{
   private boolean _undefined;
   private ArrayList<ActionListener> _listeners = new ArrayList<ActionListener>();

   public TrippleStateCheckBox(String text)
   {
      super(text);

      super.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAction(e);
         }
      });
   }

   private void onAction(ActionEvent e)
   {
      _undefined = false;
      fireListeners(e);

   }

   private void fireListeners(ActionEvent e)
   {
      ActionListener[] listeners = _listeners.toArray(new ActionListener[_listeners.size()]);
      for (ActionListener listener : listeners)
      {
         listener.actionPerformed(e);
      }
   }

   public void setUndefined(boolean b)
   {
      if (isSelected())
      {
         _undefined = b;
      }
   }

   @Override
   public void addActionListener(ActionListener l)
   {
      _listeners.add(l);
   }

   @Override
   public void removeActionListener(ActionListener l)
   {
      _listeners.remove(l);
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      if(false == _undefined)
      {
         return;
      }



      Dimension size = getSize();

      FontMetrics fontMetrics = g.getFontMetrics(getFont());
      int iconWidth = size.width - getIconTextGap() - fontMetrics.getStringBounds(getText(), g).getBounds().width - getInsets().right - getInsets().left;
      int iconHeight = size.height - getInsets().bottom - getInsets().top;
      int iconX = getInsets().left;
      int iconY = getInsets().top;

//      g.setColor(Color.red);
//      g.drawRect(iconX, iconY, iconWidth, iconHeight);

      int grid = 2;

      Color oldColor = g.getColor();
      g.setColor(Color.WHITE);

      for(int i = 0; i <= iconWidth + grid; i += 2 * grid)
      {
         for(int j = 0; j <= iconHeight  + grid ; j += 2 * grid)
         {
            g.fillRect(iconX + i, iconY + j, grid, grid);
         }
      }
      g.setColor(oldColor);

   }

   public boolean isUndefined()
   {
      return _undefined;
   }
}
