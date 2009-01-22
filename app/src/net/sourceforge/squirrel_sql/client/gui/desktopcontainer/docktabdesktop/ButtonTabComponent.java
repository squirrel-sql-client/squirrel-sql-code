package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;


public class ButtonTabComponent extends JPanel
{
   private final DesktopTabbedPane _tabbedPane;
   private JLabel _label = new JLabel();

   /**
    * Became a text field because lables could not display
    * very long file names as title.
    */
   private JTextField _txtTitle = new JTextField()
   {
      @Override
      protected void processMouseEvent(MouseEvent e)
      {
         Point tabbedPaneLoc = _tabbedPane.getLocationOnScreen();
         Point mouseLoc = e.getLocationOnScreen();
         int transfX = mouseLoc.x - tabbedPaneLoc.x;
         int transfY = mouseLoc.y - tabbedPaneLoc.y;

         MouseEvent transformedMouseEvent =
            new MouseEvent(
               _tabbedPane,
               e.getID(),
               e.getWhen(),
               e.getModifiers(),
               transfX,
               transfY,
               e.getClickCount(),
               e.isPopupTrigger(),
               e.getButton());

         _tabbedPane.doProcessMouseEvent(transformedMouseEvent);
         super.processMouseEvent(e);    //To change body of overridden methods use File | Settings | File Templates.
      }
   };


   private TabButton _closebutton = new TabButton();

   public ButtonTabComponent(final DesktopTabbedPane tabbedPane, String title, Icon icon)
   {
      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      _tabbedPane = tabbedPane;
      setOpaque(false);

      // Has to be at the front because very long file names would move it
      // out of sight if it were in the back. 
      add(_closebutton);
      setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 9));

      _label.setIcon(icon);
      _label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      add(_label);


      _txtTitle.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
      _txtTitle.setEditable(false);
      _txtTitle.setOpaque(false);
      add(_txtTitle);

   }


   public JButton getClosebutton()
   {
      return _closebutton;
   }

   public void setIcon(Icon icon)
   {
      _label.setIcon(icon);
   }

   public void setTitle(String title)
   {
      _txtTitle.setText(title);
   }

   private class TabButton extends JButton //implements ActionListener
   {
      public TabButton()
      {
         int size = 17;
         setPreferredSize(new Dimension(size, size));
         setToolTipText("close this tab");
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
      }


      //we don't want to update UI for this button
      public void updateUI()
      {
      }

      //paint the cross
      protected void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g.create();
         //shift the image for pressed buttons
         if (getModel().isPressed())
         {
            g2.translate(1, 1);
         }
         g2.setStroke(new BasicStroke(2));
         g2.setColor(Color.BLACK);
         if (getModel().isRollover())
         {
            g2.setColor(Color.MAGENTA);
         }
         int delta = 6;
         g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
         g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
         g2.dispose();
      }
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
}

