package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;


public class SmallTabButton<T> extends JButton
{
   private Icon _icon;
   private T _userObject;


   public SmallTabButton(String toolTipText, ImageIcon icon)
   {
      this(toolTipText, icon, null);
   }

   public SmallTabButton(String toolTipText, ImageIcon icon, T userObject)
   {
      _icon = icon;
      _userObject = userObject;
      int size;
      if (null == icon)
      {
         size = 17;
      }
      else
      {
         size = Math.max(_icon.getIconWidth(), _icon.getIconHeight()) + 5;
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
            button.setBorderPainted(true);
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
         g2.translate(1, 1);
      }

      int x = (getWidth() - _icon.getIconWidth()) / 2;
      int y = (getHeight() - _icon.getIconHeight()) / 2;
      g2.drawImage(iconToImage(_icon), x, y, null);


      g2.dispose();
   }

   private Image iconToImage(Icon icon)
   {
      if(icon instanceof ImageIcon)
      {
         return ((ImageIcon) icon).getImage();
      }
      else
      {
         BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
         icon.paintIcon(null, image.getGraphics(), 0, 0);
         return image;
      }
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
