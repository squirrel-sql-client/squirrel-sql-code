package net.sourceforge.squirrel_sql.client.gui.jmeld;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class JMeldConfigPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldConfigPanel.class);
   JCheckBox chkIgnoreWhiteSpaces;
   JCheckBox chkIgnoreCase;
   JCheckBox chkDrawCurves;
   JComboBox<JMeldCurveType> cboCurveType;


   public JMeldConfigPanel()
   {
      super(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0);
      chkIgnoreWhiteSpaces = new JCheckBox(s_stringMgr.getString("JMeldConfigPanel.ignore.white.spaces"));
      add(chkIgnoreWhiteSpaces, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0);
      chkIgnoreCase = new JCheckBox(s_stringMgr.getString("JMeldConfigPanel.ignore.case"));
      add(chkIgnoreCase, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 20, 5, 0), 0, 0);
      chkDrawCurves = new JCheckBox(s_stringMgr.getString("JMeldConfigPanel.draw.curves"));
      add(chkDrawCurves, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 0), 0, 0);
      add(new JLabel(s_stringMgr.getString("JMeldConfigPanel.curve.type")), gbc);

      gbc = new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0);
      cboCurveType = new JComboBox<>(JMeldCurveType.values());
      add(cboCurveType, gbc);

   }
}
