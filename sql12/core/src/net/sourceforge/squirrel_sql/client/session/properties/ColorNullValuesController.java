package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import java.awt.Color;

public class ColorNullValuesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColorNullValuesController.class);

   private ColorNullValuesPanel _panel = new ColorNullValuesPanel();

   public ColorNullValuesController()
   {
      _panel.btnCurrentNullValueColorRGB.setEnabled(_panel.chkColorNullValues.isSelected());

      _panel.chkColorNullValues.addActionListener(e -> _panel.btnCurrentNullValueColorRGB.setEnabled(_panel.chkColorNullValues.isSelected()));

      _panel.btnCurrentNullValueColorRGB.addActionListener(e -> onChooseNullValueColor());
   }

   private void onChooseNullValueColor()
   {
      String title = s_stringMgr.getString("ColorNUllValuesController.null.value.color.choose");
      Color color = JColorChooser.showDialog(_panel, title, _panel.getNullValueColorIcon().getColor());

      if (null != color)
      {
         _panel.getNullValueColorIcon().setColor(color);
      }
   }

   public JPanel getPanel()
   {
      return _panel;
   }

   public int getNullValueColorRGB()
   {
      return _panel.getNullValueColorIcon().getColor().getRGB();
   }

   public boolean isColorNullValues()
   {
      return _panel.chkColorNullValues.isSelected();
   }

   public void init(boolean colorNullValues, int nullValueColorRGB)
   {
      _panel.chkColorNullValues.setSelected(colorNullValues);
      _panel.getNullValueColorIcon().setColor(new Color(nullValueColorRGB));
      _panel.btnCurrentNullValueColorRGB.setEnabled(_panel.chkColorNullValues.isSelected());
   }
}
