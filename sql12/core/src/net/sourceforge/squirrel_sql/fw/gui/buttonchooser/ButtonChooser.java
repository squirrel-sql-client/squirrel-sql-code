package net.sourceforge.squirrel_sql.fw.gui.buttonchooser;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Behaves like a ComboBox with buttons in it.
 * Suitable for changing buttons to provide variants of functions.
 */
public class ButtonChooser
{
   private JButton _btnUndefinedDefault;

   private ComboButton _btnCombo;
   private AbstractButton _btnCurrent;

   private List<ButtonHolder> _buttons= new ArrayList<>();
   private ButtonSelectedListener _buttonSelectedListener;

   private HashSet<JButton> _unclickableButtons = new HashSet<>();

   private JComponent _container;
   private boolean _inToolBar;
   private ButtonChooserOrientation _orientation;

   public ButtonChooser()
   {
      this(false, ButtonChooserOrientation.RIGHT);
   }

   public ButtonChooser(boolean inToolBar)
   {
      this(inToolBar, ButtonChooserOrientation.RIGHT);
   }

   public ButtonChooser(boolean inToolBar, ButtonChooserOrientation orientation)
   {
      _inToolBar = inToolBar;
      _orientation = orientation;
      _btnCombo = new ComboButton();

      if (_inToolBar)
      {
         // Helps to make the ButtonChooser match the toolBar style.
         JToolBar toolBarComp= new JToolBar();
         toolBarComp.setBorder(BorderFactory.createEmptyBorder());
         toolBarComp.setFloatable(false);
         toolBarComp.setRollover(true);
         GUIUtils.inheritBackground(toolBarComp);
         _container = toolBarComp;
      }
      else
      {
         // When NOT in toolbar we style the container ourselves. That is GridBagLayout and to all buttons GUIUtils.styleAsToolbarButton() is applied.
         _container = new JPanel(new GridBagLayout());
         GUIUtils.styleAsToolbarButton(_btnCombo);
      }
      createUI();
      initListeners();
   }

   public JComponent getComponent()
   {
      return _container;
   }

   private void initListeners()
   {
      _btnCombo.addActionListener( e -> onShowPopup());

   }

   private void onShowPopup()
   {
      JPopupMenu comboPopUp = new JPopupMenu();

      for (ButtonHolder buttonHolder : _buttons)
      {
         JMenuItem mnu = new JMenuItem(buttonHolder.getText(), buttonHolder.getBtn().getIcon());
         mnu.setToolTipText(buttonHolder.getBtn().getToolTipText());
         mnu.addActionListener(e -> displayAsCurrentButton(buttonHolder.getBtn()));
         comboPopUp.add(mnu);
      }

      comboPopUp.addPopupMenuListener(_btnCombo.getPopupMenuListener());
      comboPopUp.show(_btnCurrent, 0, _btnCurrent.getHeight());
   }


   private void createUI()
   {
      SquirrelResources rsrc = Main.getApplication().getResources();
      ImageIcon iconUndefined = rsrc.getIcon(SquirrelResources.IImageNames.UNDEFINED);
      //_btnUndefinedDefault = new JButton(iconUndefined);
      _btnUndefinedDefault = new JButton(iconUndefined);
      _btnUndefinedDefault.setToolTipText("There were no buttons added???");

      displayAsCurrentButton(_btnUndefinedDefault);

      if(_inToolBar)
      {
         _container.add(_btnCombo);
      }
      else
      {
         // When NOT in toolbar we style the container ourselves. That is GridBagLayout and to all buttons GUIUtils.styleAsToolbarButton() is applied.
         GridBagConstraints gbc;

         if(_orientation == ButtonChooserOrientation.RIGHT)
         {
            gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,2), 0,0);
         }
         else
         {
            gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
         }

         _container.add(_btnCombo, gbc);
      }
   }

   /**
    * Add a button including text, icon, and tooltip.
    * The button's text will be set to zero and will only be displayed in the popup.
    */
   public void addButton(AbstractButton btn)
   {
      _buttons.add(new ButtonHolder(btn));

      if(false == _inToolBar)
      {
         // When NOT in toolbar we style the container ourselves. That is GridBagLayout and to all buttons GUIUtils.styleAsToolbarButton() is applied.
         GUIUtils.styleAsToolbarButton(btn);
      }

      if(_btnCurrent == _btnUndefinedDefault)
      {
         setSelectedButton(btn);
      }
   }

   /**
    * Unclickable buttons serve as labels.
    */
   public void addUnclickableButton(JButton btn)
   {
      btn.setEnabled(false);
      btn.setDisabledIcon(btn.getIcon());

      if(false == _inToolBar)
      {
         // When NOT in toolbar we style the container ourselves. That is GridBagLayout and to all buttons GUIUtils.styleAsToolbarButton() is applied.
         GUIUtils.styleAsToolbarButton(btn);
      }

      _unclickableButtons.add(btn);

      addButton(btn);
   }

   public void setSelectedButton(AbstractButton btn)
   {
      if(false == _buttons.stream().filter(bh -> bh.getBtn() == btn).findFirst().isPresent())
      {
         throw new IllegalStateException("Button must be added before being selected");
      }

      displayAsCurrentButton(btn);
   }

   private void displayAsCurrentButton(AbstractButton btn)
   {
      AbstractButton formerSelectedButton = _btnCurrent;

      if (null != _btnCurrent)
      {
         _container.remove(_btnCurrent);
      }

      _btnCurrent = btn;

      _btnCombo.setLinkedButton(_btnCurrent);

      if(_inToolBar)
      {
         if(_orientation == ButtonChooserOrientation.RIGHT)
         {
            _container.add(_btnCurrent, 0);
         }
         else
         {
            _container.add(_btnCurrent);
         }
      }
      else
      {
         // When NOT in toolbar we style the container ourselves. That is GridBagLayout and to all buttons GUIUtils.styleAsToolbarButton() is applied.
         GridBagConstraints gbc;

         if(_orientation == ButtonChooserOrientation.RIGHT)
         {
            gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
            _container.add(_btnCurrent, gbc, 0);
         }
         else
         {
            gbc = new GridBagConstraints(1,0,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
            _container.add(_btnCurrent, gbc);
         }
      }

      _container.revalidate();
      _container.repaint();

      if(null != _buttonSelectedListener)
      {
         _buttonSelectedListener.buttonSelected(_btnCurrent, formerSelectedButton);
      }
   }

   public void setButtonSelectedListener(ButtonSelectedListener buttonSelectedListener)
   {
      _buttonSelectedListener = buttonSelectedListener;
   }

   public void setChooserEnabled(boolean b)
   {
      _btnCombo.setEnabled(b);


      if(b)
      {
         _unclickableButtons.forEach(btn -> btn.setDisabledIcon(btn.getIcon()));
      }
      else
      {
         _unclickableButtons.forEach(btn -> btn.setDisabledIcon(null));
      }


      if (false == _unclickableButtons.contains(_btnCurrent))
      {
         _btnCurrent.setEnabled(b);
      }

   }

   public AbstractButton getSelectedButton()
   {
      return _btnCurrent;
   }

   public List<AbstractButton> getAllButtons()
   {
      return _buttons.stream().map(bh -> bh.getBtn()).collect(Collectors.toList());
   }

   public void setPreferredHeight(int height)
   {
      GUIUtils.setPreferredHeight(_container, height);
   }
}
