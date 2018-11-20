package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.plaf.ColorUIResource;


public class ButtonTabComponent extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ButtonTabComponent.class);

   private JTabbedPane _tabbedPane;

   private JLabel _label = new JLabel();
   private CloseTabButton _btnClose;
   private JPanel _pnlSmallTabButtons;
   private final SmallTabButton _btnToWindow;

   private Font _defaultFont = null; // the default font of the title label
   private Font _selectedFont = null; // the font of the title label if tab is selected
   private Color _foregroundColor = null; // the foreground color of the title lable if tab is selected

   /**
    * With this constructor the tab title won't turn to bold when it is selected
    */
   public ButtonTabComponent(String title, Icon icon)
   {
      this(null, title, icon);
   }

   public ButtonTabComponent(JTabbedPane tabbedPane, String title, Icon icon)
   {
      _tabbedPane = tabbedPane;

      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      setOpaque(false);

      // the CloseTabButton needs a referrence to the ButtonTabComponent because we want to paint
      // the X with selected foreground color if tab is selected.
      _btnClose = new CloseTabButton(this);

      add(_btnClose);

      _label.setText(title);
      _label.setOpaque(false);
      _label.setIcon(icon);

      // get the defaults for rendering the title label
      initLabelDefaults();

      add(_label);
      //add more space between the label and the button
      _label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      //add more space to the top of the component

      _pnlSmallTabButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
      _pnlSmallTabButtons.setOpaque(false);

      add(_pnlSmallTabButtons);

      setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));


      _btnToWindow = new SmallTabButton(s_stringMgr.getString("docktabdesktop.ButtonTabComponent.detachButtonTooltip"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.TAB_DETACH_SMALL));

      _pnlSmallTabButtons.add(_btnToWindow);
   }

   // if look and feel changes we have to set the new defaults for rendering the title label
   @Override
   public void updateUI()
   {
      super.updateUI();
      initLabelDefaults();
   }

   // initialize the defaults for the title label
   private void initLabelDefaults()
   {
      if (_label != null)
      {
         _defaultFont = _label.getFont().deriveFont(~Font.BOLD);
         _selectedFont = _label.getFont().deriveFont(Font.BOLD);

         if (null != _tabbedPane)
         {
            _foregroundColor = getForegroundColor(_tabbedPane);
         }
         else
         {
            _foregroundColor = getForegroundColor(UIFactory.getInstance().createTabbedPane());
         }
      }
   }

   private Color getForegroundColor(JTabbedPane tabbedPane)
   {
      Color ret = UIManager.getColor("TabbedPane.selectedForeground");
      // some look and feels may not support the above property so we fall back to foreground color of tabbed pane
      if (ret == null)
      {
         ret = tabbedPane.getForeground();
      }
      return ret;
   }


   /**
    *
    * @return -1 when {@link _tabbedPane} is null or this component's tab index cant be found
    */
   private int getMyTabIndex()
   {
      if (null != _tabbedPane)
      {
         for (int i = 0; i < _tabbedPane.getTabCount(); i++)
         {
            if (this.equals(_tabbedPane.getTabComponentAt(i)))
            {
               return i;
            }
         }
      }
      return -1;
   }

   // we have to override paint to handle the rendering of the title label, because we want
   // the title to be painted different when tab is selected.
   @Override
   public void paint(Graphics g)
   {
      int tabIndex = getMyTabIndex();

      if (tabIndex >= 0) // here _tabbedPane id guaranteed to be not null
      {
         if (tabIndex == _tabbedPane.getSelectedIndex())
         {
            _label.setFont(_selectedFont);
            // check if the foreground color is not set by user through a call to setForegroundAt
            if (_tabbedPane.getForegroundAt(tabIndex) instanceof ColorUIResource)
            {
               _label.setForeground(_foregroundColor);
            }
            else
            {
               _label.setForeground(_tabbedPane.getForegroundAt(tabIndex));
            }
         }
         else
         {
            _label.setFont(_defaultFont);
            _label.setForeground(_tabbedPane.getForegroundAt(tabIndex));
         }
      }

      super.paint(g);
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
         if (component == smallTabButton)
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
         if (cp instanceof SmallTabButton && cp != _btnToWindow)
         {
            ret.add((SmallTabButton) cp);
         }
      }

      return ret;
   }

   public SmallTabButton findSmallTabButtonByUserObject(Object userObject)
   {
      for (Component cp : _pnlSmallTabButtons.getComponents())
      {
         SmallTabButton stb = (SmallTabButton) cp;
         if (userObject == stb.getUserObject())
         {
            return stb;
         }
      }
      return null;
   }

   public void doClickClose()
   {
      _btnClose.doClick();
   }

   private static class CloseTabButton extends SmallTabButton
   {
      private ButtonTabComponent tabComponent;

      private CloseTabButton(ButtonTabComponent tabComponent)
      {
         super(s_stringMgr.getString("docktabdesktop.ButtonTabComponent.toolTip"), null);

         this.tabComponent = tabComponent;
      }

      //paint the cross
      @Override
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

         if (getModel().isRollover())
         {
            g2.setColor(Color.MAGENTA);
         }
         else
         {
            g2.setColor(tabComponent._foregroundColor);
         }

         int delta = 6;
         g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
         g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
         g2.dispose();
      }
   }
}

