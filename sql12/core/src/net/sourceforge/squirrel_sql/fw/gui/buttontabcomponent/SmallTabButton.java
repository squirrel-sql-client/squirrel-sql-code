package net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.resources.IconHandler;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class SmallTabButton<T> extends JButton
{
   private Icon _icon;
   private T _userObject;


   public SmallTabButton(int sizeOffset)
   {
      this(null, null, null, sizeOffset);
   }

   public SmallTabButton(String toolTipText, ImageIcon icon)
   {
      this(toolTipText, icon, null);
   }

   public SmallTabButton(String toolTipText, ImageIcon icon, int sizeOffset)
   {
      this(toolTipText, icon, null, sizeOffset);
   }

   public SmallTabButton(String toolTipText, ImageIcon icon, T userObject)
   {
      this(toolTipText, icon, userObject, 5);
   }

   public SmallTabButton(String toolTipText, ImageIcon icon, T userObject, int sizeOffset)
   {
      _icon = icon;
      _userObject = userObject;
      int size;
      if (null == icon)
      {
         size = Main.getApplication().getIconHandler().iconScale_ceil(17);
      }
      else
      {
         size = Math.max(_icon.getIconWidth(), _icon.getIconHeight()) + sizeOffset;
      }
      setPreferredSize(new Dimension(size, size));
      setToolTipText(toolTipText);

      //setIcon(icon);
      //Make the button looks the same for all Laf's
      setUI(new BasicButtonUI());
      //Make it transparent
      setContentAreaFilled(false);
      //No need to be focusable
      setFocusable(false);
      setBorder(BorderFactory.createEtchedBorder());
      setBorderPainted(false);
      //Making nice rollover effect
      //we use the same listener for all buttons
      addMouseListener(s_buttonMouseListener);
      setRolloverEnabled(true);
      //Close the proper tab by clicking the button
      //addActionListener(this);
      setOpaque(false);
   }


   //we don't want to update UI for this button
   public void updateUI()
   {
   }

   private final static MouseListener s_buttonMouseListener = new MouseAdapter()
   {
      public void mouseEntered(MouseEvent e)
      {
         Component component = e.getComponent();
         if (component instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) component;
            if (button.isEnabled())
            {
               button.setBorderPainted(true);
            }
            else
            {
               button.setBorderPainted(false);
            }
         }
      }

      public void mouseExited(MouseEvent e)
      {
         Component component = e.getComponent();
         if (component instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) component;
            button.setBorderPainted(false);
         }
      }
   };

   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      if(null == _icon)
      {
         return;
      }


      Graphics2D g2 = (Graphics2D) g.create();

      if (getModel().isPressed())
      {
         final IconHandler iconHandler = Main.getApplication().getIconHandler();
         g2.translate(iconHandler.iconScale_round(1), iconHandler.iconScale_round(1));
      }

      int x = (getWidth() - _icon.getIconWidth()) / 2;
      int y = (getHeight() - _icon.getIconHeight()) / 2;
      _icon.paintIcon(this, g2, x, y);

      g2.dispose();
   }

   @Override
   public void setIcon(Icon icon)
   {
      _icon = icon;
      repaint();
   }

   public T getUserObject()
   {
      return _userObject;
   }

   public void setUserObject(T userObject)
   {
      _userObject = userObject;
   }
}
