package net.sourceforge.squirrel_sql.plugins.highresicon;

import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class HighResPrefsCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(HighResPrefsCtrl.class);
   private final JPanel _panel;

   private JCheckBox _scaleIconsWithStaticText;
   private HighResolutionIconPlugin _plugin;

   public HighResPrefsCtrl(HighResolutionIconPlugin plugin)
   {
      _plugin = plugin;

      _panel = createPanel();

      _scaleIconsWithStaticText.setSelected(_plugin.getHighResPrefs().isScaleIconsWithText());

   }

   private JPanel createPanel()
   {
      JPanel panel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(20, 0,0,0), 0,0);
      _scaleIconsWithStaticText = new JCheckBox(s_stringMgr.getString("HighResPrefsPanel.scaleIconsWithText"));
      panel.add(_scaleIconsWithStaticText, gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0,0,0), 0,0);
      panel.add(new JPanel(), gbc);

      return panel;
   }

   public void applyChanges()
   {
      _plugin.getHighResPrefs().setScaleIconsWithText(_scaleIconsWithStaticText.isSelected());

      JsonMarshalUtil.writeObjectToFile(_plugin.getHighResPrefsFile(), _plugin.getHighResPrefs());
      IconScale.setFollowTextSize(_plugin.getHighResPrefs().isScaleIconsWithText());
   }

   public JPanel getPanel()
   {
      return _panel;
   }
}
