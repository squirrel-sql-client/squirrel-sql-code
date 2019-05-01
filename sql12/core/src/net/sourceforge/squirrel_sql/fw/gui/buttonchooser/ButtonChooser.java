package net.sourceforge.squirrel_sql.fw.gui.buttonchooser;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ButtonChooser extends JPanel
{
   private JButton _btnUndefinedDefault;

   private ComboButton _btnCombo = new ComboButton();
   private AbstractButton _btnCurrent;

   private List<ButtonHolder> _buttons= new ArrayList<>();
   private ButtonSelectedListener _buttonSelectedListener;

   public ButtonChooser()
   {
      super(new GridBagLayout());
      createUI();
      initListeners();
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

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,2), 1,1);
      add(_btnCombo, gbc);

      GUIUtils.styleAsToolbarButton(_btnCombo);

      _btnCombo.setPreferredSize(new Dimension(12, 28));
      _btnCombo.setMinimumSize(new Dimension(12, 28));


      setPreferredSize(new Dimension(38,28));
      setMaximumSize(new Dimension(38,28));
   }

   /**
    * Add a button including text, icon, and tooltip.
    * The button's text will be set to zero and will only be displayed in the popup.
    */
   public void addButton(AbstractButton btn)
   {
      _buttons.add(new ButtonHolder(btn));

      if(_btnCurrent == _btnUndefinedDefault)
      {
         setSelectedButton(btn);
      }
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
         remove(_btnCurrent);
      }

      _btnCurrent = btn;

      GUIUtils.styleAsToolbarButton(_btnCurrent, true);

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1);
      add(_btnCurrent, gbc);
      revalidate();
      repaint();

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
      _btnCurrent.setEnabled(b);

   }

   public AbstractButton getSelectedButton()
   {
      return _btnCurrent;
   }

   public List<AbstractButton> getAllButtons()
   {
      return _buttons.stream().map(bh -> bh.getBtn()).collect(Collectors.toList());
   }
}
