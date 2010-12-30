package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GroupDissolveChecker
{
   private Timer _timer;

   public GroupDissolveChecker()
   {
      _timer = new Timer(100, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {

         }
      });

      _timer.setRepeats(false);
   }

   void tolerantlyCheckGroupDissolve(TableFrame tf, Point correctDelta, GraphDesktopPane graphDesktopPane)
   {
      if(_timer.isRunning())
      {
         checkDissolve(tf, graphDesktopPane);
         return;
      }
      else
      {
         _timer.start();
      }

      double tolerance = 0.005;

      if (     ((double)correctDelta.x) / ((graphDesktopPane.getWidth())) > tolerance
            || ((double)correctDelta.y) / ((graphDesktopPane.getHeight())) > tolerance)
      {
         checkDissolve(tf, graphDesktopPane);
      }
   }

   private void checkDissolve(TableFrame tf, GraphDesktopPane graphDesktopPane)
   {
      if (!graphDesktopPane.isGroupFrame(tf))
      {
         graphDesktopPane.setGroupFrame(tf);
      }
   }
}
