package net.sourceforge.squirrel_sql.fw.gui.buttonchooser;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ComboButton extends JToggleButton
{
   private static int triHeight = 3;
   private static int dist = 3;

   private AbstractButton linkedButton;
   private transient MouseListener hoverHandler = new HoverHandler();
   private transient KeyListener actionKeyHandler = new ActionKeyHandler();

   public ComboButton()
   {
      super.setFocusable(false);
      super.addMouseListener(hoverHandler);
   }

   private int getMaxWidth()
   {
      return (dist + triHeight) * 2;
   }

   @Override
   public Dimension getMinimumSize()
   {
      if (isMinimumSizeSet())
      {
         return super.getMinimumSize();
      }
      int maxWidth = getMaxWidth();
      return new Dimension(maxWidth, maxWidth);
   }

   @Override
   public Dimension getPreferredSize()
   {
      if (isPreferredSizeSet())
      {
         return super.getPreferredSize();
      }
      int maxWidth = getMaxWidth();
      return (linkedButton == null)
             ? new Dimension(maxWidth, maxWidth * 2)
             : new Dimension(maxWidth, linkedButton.getPreferredSize().height);
   }

   @Override
   public Dimension getMaximumSize()
   {
      if (isMaximumSizeSet())
      {
         return super.getMaximumSize();
      }
      int maxWidth = getMaxWidth();
      return (linkedButton == null)
             ? new Dimension(maxWidth, Short.MAX_VALUE)
             : new Dimension(maxWidth, linkedButton.getMaximumSize().height);
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      Dimension size = getSize();

      if (isEnabled())
      {
         g.setColor(getForeground());
      }
      else
      {
         g.setColor(Color.gray);
      }

      Polygon pg = new Polygon();
      pg.addPoint(dist, size.height / 2 - triHeight);
      pg.addPoint(size.width - dist, size.height / 2 - triHeight);
      pg.addPoint(size.width / 2, size.height / 2 + triHeight);
      g.fillPolygon(pg);
   }

   public void setLinkedButton(AbstractButton actionButton)
   {
      if (linkedButton != null)
      {
         linkedButton.removeMouseListener(hoverHandler);
         linkedButton.removeKeyListener(actionKeyHandler);
      }
      linkedButton = actionButton;
      if (linkedButton != null)
      {
         linkedButton.addMouseListener(hoverHandler);
         linkedButton.addKeyListener(actionKeyHandler);
      }
   }

   protected void processActionEvent()
   {
      ActionListener listener = actionListener;
      if (listener != null)
      {
         listener.actionPerformed(new ActionEvent(this,
               ActionEvent.ACTION_PERFORMED, getActionCommand()));
      }
   }

   public PopupMenuListener getPopupMenuListener()
   {
      return new PopupMenuListener()
      {
         @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { /* no-op */ }

         @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
         {
            setSelected(false);
         }

         @Override public void popupMenuCanceled(PopupMenuEvent e) { /* no-op */ }
      };
   }


   class HoverHandler extends MouseAdapter
   {
      private boolean forwarding;

      @Override public void mouseEntered(MouseEvent e)
      {
         forwardEvent(e);
      }

      @Override public void mouseExited(MouseEvent e)
      {
         forwardEvent(e);
      }

      private void forwardEvent(MouseEvent e)
      {
         if (forwarding)
            return;

         forwarding = true;
         try
         {
            if (e.getSource() == ComboButton.this)
            {
               if (linkedButton != null)
               {
                  linkedButton.dispatchEvent(convertEvent(e, linkedButton));
               }
            }
            else
            {
               processMouseEvent(convertEvent(e, ComboButton.this));
            }
         }
         finally
         {
            forwarding = false;
         }
      }

      private MouseEvent convertEvent(MouseEvent e, Component destination)
      {
         return SwingUtilities.convertMouseEvent((Component) e.getSource(), e, destination);
      }
   }


   class ActionKeyHandler extends KeyAdapter
   {
      @Override public void keyPressed(KeyEvent e)
      {
         if (e.getKeyCode() == KeyEvent.VK_DOWN)
         {
            setSelected(true);
            processActionEvent();
            e.consume(); // IMPORTANT
         }
      }
   }


}
