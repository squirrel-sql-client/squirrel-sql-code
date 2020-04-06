package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.preferences.ColorIcon;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwitchableColorCtrl
{
   private final JCheckBox _chkSwitch;
   private final JLabel _lblColor = new JLabel();
   private final ActionListener _checkBoxListener;
   private ColorIcon _colorIcon;
   private String _colorChooserDialogTitle;

   public SwitchableColorCtrl(String checkBoxText, String colorChooserDialogTitle)
   {
      _checkBoxListener = e -> onAdjustCaretColor();

      _chkSwitch = new JCheckBox(checkBoxText);
      _chkSwitch.addActionListener(_checkBoxListener);
      _colorChooserDialogTitle = colorChooserDialogTitle;

      _lblColor.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onColorLabelClicked();
         }
      });
   }

   private void onColorLabelClicked()
   {
      if(false == _chkSwitch.isSelected())
      {
         _chkSwitch.doClick();
      }
      else
      {

         Color initialColor = null;
         if(SyntaxPreferences.NO_COLOR != getColorRGB())
         {
            initialColor = new Color(getColorRGB());
         }

         Color color = JColorChooser.showDialog(_chkSwitch, _colorChooserDialogTitle, initialColor);

         if(null == color)
         {
            return;
         }

         adjustLabel(color.getRGB());
      }
   }

   public int getColorRGB()
   {
      int retColorRGB;

      retColorRGB = SyntaxPreferences.NO_COLOR;
      if(_chkSwitch.isSelected())
      {
         retColorRGB = _colorIcon.getColor().getRGB();
      }

      return retColorRGB;
   }

   public void setColorRGB(int colorRGB)
   {
      _chkSwitch.removeActionListener(_checkBoxListener);

      _chkSwitch.setSelected(SyntaxPreferences.NO_COLOR != colorRGB);

      _chkSwitch.addActionListener(_checkBoxListener);

      adjustLabel(colorRGB);
   }


   private void onAdjustCaretColor()
   {
      Color color = null;

      if (_chkSwitch.isSelected())
      {
         color = JColorChooser.showDialog(_chkSwitch, _colorChooserDialogTitle, null);
      }

      if (null != color)
      {
         adjustLabel(color.getRGB());
      }
      else
      {
         _chkSwitch.removeActionListener(_checkBoxListener);
         _chkSwitch.setSelected(false);
         adjustLabel(SyntaxPreferences.NO_COLOR);
         _chkSwitch.addActionListener(_checkBoxListener);
      }
   }

   private void adjustLabel(int caretColorRGB)
   {
      if(SyntaxPreferences.NO_COLOR == caretColorRGB)
      {
         _lblColor.setBorder(BorderFactory.createEtchedBorder());
         _colorIcon.setColor(_chkSwitch.getBackground());

      }
      else
      {
         _lblColor.setBorder(BorderFactory.createLineBorder(Color.black));
         _colorIcon.setColor(new Color(caretColorRGB));
      }
   }

   public void setEnabled(boolean useRSyntaxControl)
   {
      _chkSwitch.setEnabled(useRSyntaxControl);
   }


   JPanel createCaretColorPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
      ret.add(_chkSwitch, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);

      _lblColor.setPreferredSize(new Dimension(16, 16));
      _colorIcon = new ColorIcon(14, 14);
      _colorIcon.setBorderColor(null);
      _lblColor.setIcon(_colorIcon);

      ret.add(_lblColor, gbc);

      return ret;
   }
}
