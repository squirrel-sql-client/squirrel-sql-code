package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;


public class ButtonTabComponent extends JPanel
{
   private final JTabbedPane _tabbedPane;
   private JLabel _label = new JLabel();
   private TabButton _button = new TabButton();

   public ButtonTabComponent(final JTabbedPane tabbedPane, String title, Icon icon)
   {
      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      _tabbedPane = tabbedPane;
      setOpaque(false);

      _label.setText(title);
      _label.setIcon(icon);
      add(_label);
      //add more space between the label and the button
      _label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      //tab button
      add(_button);
      //add more space to the top of the component
      setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
   }

   JLabel getLabel()
   {
      return _label;
   }

   public JButton getButton()
   {
      return _button;
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

//      public void actionPerformed(ActionEvent e)
//      {
//         int i = _tabbedPane.indexOfTabComponent(ButtonTabComponent.this);
//         if (i != -1)
//         {
//            _tabbedPane.remove(i);
//         }
//      }

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

