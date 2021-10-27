package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.client.preferences.ColorIcon;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ColorNullValuesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColorNullValuesPanel.class);

   JCheckBox chkColorNullValues = new JCheckBox(s_stringMgr.getString("ColorNUllValuesPanel.color.null.values"));
   JButton btnCurrentNullValueColorRGB = new JButton();


   public ColorNullValuesPanel()
   {
      super(new BorderLayout(5, 0));

      add(chkColorNullValues, BorderLayout.WEST);
      add(btnCurrentNullValueColorRGB, BorderLayout.CENTER);

      btnCurrentNullValueColorRGB.setHorizontalTextPosition(JButton.LEFT);
      btnCurrentNullValueColorRGB.setIcon(new ColorIcon(16, 16));
      btnCurrentNullValueColorRGB.setText(s_stringMgr.getString("ColorNUllValuesPanel.null.value.color"));

   }

   ColorIcon getNullValueColorIcon()
   {
      return (ColorIcon) btnCurrentNullValueColorRGB.getIcon();
   }

}
