package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ThemesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ThemesPanel.class);

   JComboBox<ThemesEnum> cboThemes = new JComboBox<>(ThemesEnum.values());
   JButton btnApply = new JButton(s_stringMgr.getString("ThemesPanel.apply"));

   public ThemesPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(5,5,0,0),0,0 );
      add(new JLabel(s_stringMgr.getString("ThemesPanel.chose.theme.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(5,5,5,0),0,0 );
      GUIUtils.setPreferredWidth(cboThemes, 100);
      add(cboThemes, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(5,5,5,0),0,0 );
      add(btnApply, gbc);

      gbc = new GridBagConstraints(2,1,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(0,0,0,40),0,0 );
      add(new JPanel(), gbc);

      setBorder(BorderFactory.createEtchedBorder());
   }
}
