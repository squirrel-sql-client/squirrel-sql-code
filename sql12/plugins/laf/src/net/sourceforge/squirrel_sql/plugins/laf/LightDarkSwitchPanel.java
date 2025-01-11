package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LightDarkSwitchPanel extends JPanel
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(LightDarkSwitchPanel.class);

   final JRadioButton radLight;
   final JRadioButton radDark;

   public LightDarkSwitchPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 4), 0, 0);
      add(new MultipleLineLabel(s_stringMgr.getString("LightDarkSwitchPanel.description")), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 4, 0, 0), 0, 0);
      radLight = new JRadioButton(s_stringMgr.getString("LightDarkSwitchPanel.light"));
      add(radLight, gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 0, 0), 0, 0);
      radDark = new JRadioButton(s_stringMgr.getString("LightDarkSwitchPanel.dark"));
      add(radDark, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radDark);
      bg.add(radLight);

      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("LightDarkSwitchPanel.title")));

   }
}
