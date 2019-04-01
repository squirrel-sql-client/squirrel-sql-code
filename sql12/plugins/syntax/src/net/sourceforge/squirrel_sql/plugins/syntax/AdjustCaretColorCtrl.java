package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ColorPropertiesPanel;
import net.sourceforge.squirrel_sql.client.preferences.ColorIcon;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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

public class AdjustCaretColorCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AdjustCaretColorCtrl.class);

   private final JCheckBox _chkAdjustCaretColor = new JCheckBox(s_stringMgr.getString("AdjustCaretColorCtrl.adjustCaretColor"));
   private final JLabel _lblCaretColor = new JLabel();
   private final ActionListener _checkBoxListener;
   private ColorIcon _colorIcon;

   public AdjustCaretColorCtrl()
   {
      _checkBoxListener = e -> onAdjustCaretColor();

      _chkAdjustCaretColor.addActionListener(_checkBoxListener);
   }

   public void applyChanges(SyntaxPreferences prefs)
   {
      prefs.setCaretColorRGB(SyntaxPreferences.NO_CARET_COLOR);
      if(_chkAdjustCaretColor.isSelected())
      {
         prefs.setCaretColorRGB(_colorIcon.getColor().getRGB());
      }

   }

   public void loadData(SyntaxPreferences prefs)
   {
      _chkAdjustCaretColor.removeActionListener(_checkBoxListener);

      _chkAdjustCaretColor.setSelected(SyntaxPreferences.NO_CARET_COLOR != prefs.getCaretColorRGB());

      _chkAdjustCaretColor.addActionListener(_checkBoxListener);

      adjustLabel(prefs.getCaretColorRGB());
   }


   private void onAdjustCaretColor()
   {
      Color color = null;

      if (_chkAdjustCaretColor.isSelected())
      {
         color = JColorChooser.showDialog(_chkAdjustCaretColor, s_stringMgr.getString("AdjustCaretColorCtrl.color.chooser.title"), null);
      }

      if (null != color)
      {
         adjustLabel(color.getRGB());
      }
      else
      {
         adjustLabel(SyntaxPreferences.NO_CARET_COLOR );
      }
   }

   private void adjustLabel(int caretColorRGB)
   {
      if(SyntaxPreferences.NO_CARET_COLOR  == caretColorRGB)
      {
         _lblCaretColor.setBorder(BorderFactory.createEtchedBorder());
         _colorIcon.setColor(_chkAdjustCaretColor.getBackground());

      }
      else
      {
         _lblCaretColor.setBorder(BorderFactory.createLineBorder(Color.black));
         _colorIcon.setColor(new Color(caretColorRGB));
      }
   }

   public void setEnabled(boolean useRSyntaxControl)
   {
      _chkAdjustCaretColor.setEnabled(useRSyntaxControl);
   }


   JPanel createCaretColorPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
      ret.add(_chkAdjustCaretColor, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);

      _lblCaretColor.setPreferredSize(new Dimension(16, 16));
      _colorIcon = new ColorIcon(14, 14);
      _colorIcon.setBorderColor(null);
      _lblCaretColor.setIcon(_colorIcon);

      ret.add(_lblCaretColor, gbc);

      return ret;
   }
}
