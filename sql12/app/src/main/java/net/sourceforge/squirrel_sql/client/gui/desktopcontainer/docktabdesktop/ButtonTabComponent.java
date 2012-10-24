package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class ButtonTabComponent extends JPanel
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(ButtonTabComponent.class);


   private JLabel _label = new JLabel();
   private CloseTabButton _btnClose = new CloseTabButton();
   private JPanel _pnlSmallTabButtons;
   private final SmallTabButton _btnToWindow;

   public ButtonTabComponent(IApplication app, String title, Icon icon)
   {
      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      setOpaque(false);

      add(_btnClose);

      _label.setText(title);
      _label.setIcon(icon);
      add(_label);
      //add more space between the label and the button
      _label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      //add more space to the top of the component

      _pnlSmallTabButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
      _pnlSmallTabButtons.setOpaque(false);

      add(_pnlSmallTabButtons);

      setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));


      _btnToWindow = new SmallTabButton(s_stringMgr.getString("docktabdesktop.ButtonTabComponent.detachButtonTooltip"), app.getResources().getIcon(SquirrelResources.IImageNames.TAB_DETACH_SMALL));

      _pnlSmallTabButtons.add(_btnToWindow);
   }

   public JButton getClosebutton()
   {
      return _btnClose;
   }

   public JButton getToWindowButton()
   {
      return _btnToWindow;
   }

   public void setIcon(Icon icon)
   {
      _label.setIcon(icon);
   }

   public void setTitle(String title)
   {
      _label.setText(title);
   }

   public String getTitle()
   {
      return _label.getText();
   }


   public void addSmallTabButton(SmallTabButton smallTabButton)
   {
      for (Component component : _pnlSmallTabButtons.getComponents())
      {
         if(component == smallTabButton)
         {
            return;
         }
      }

      _pnlSmallTabButtons.add(smallTabButton);
   }

   public void removeSmallTabButton(SmallTabButton smallTabButton)
   {
      _pnlSmallTabButtons.remove(smallTabButton);
   }

   public ArrayList<SmallTabButton> getExternalButtons()
   {
      _pnlSmallTabButtons.getComponents();

      ArrayList<SmallTabButton> ret = new ArrayList<SmallTabButton>();

      for (Component cp : _pnlSmallTabButtons.getComponents())
      {
         if(cp instanceof SmallTabButton && cp != _btnToWindow)
         {
            ret.add((SmallTabButton) cp);
         }
      }

      return ret;
   }

   private static class CloseTabButton extends SmallTabButton
   {
      private CloseTabButton()
      {
         super(s_stringMgr.getString("docktabdesktop.ButtonTabComponent.toolTip"), null);
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
}

