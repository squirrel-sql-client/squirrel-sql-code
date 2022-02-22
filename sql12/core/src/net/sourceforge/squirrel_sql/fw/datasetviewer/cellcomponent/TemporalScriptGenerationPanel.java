package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TemporalScriptGenerationPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TemporalScriptGenerationPanel.class);

   JRadioButton radScriptGenerationUseStandardEscapeFormat;
   JRadioButton radScriptGenerationUseStringFormat;

   public TemporalScriptGenerationPanel(String escapeFormatExample, String stringFormatExample)
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0);
      add(new JLabel(s_stringMgr.getString("TemporalScriptGenerationPanel.scriptGeneration.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,8,0,0), 0,0);
      radScriptGenerationUseStandardEscapeFormat = new JRadioButton(s_stringMgr.getString("TemporalScriptGenerationPanel.scriptGeneration.escaped", escapeFormatExample));
      add(radScriptGenerationUseStandardEscapeFormat, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,8,0,0), 0,0);
      radScriptGenerationUseStringFormat = new JRadioButton(s_stringMgr.getString("TemporalScriptGenerationPanel.scriptGeneration.string", stringFormatExample));
      add(radScriptGenerationUseStringFormat, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radScriptGenerationUseStandardEscapeFormat);
      bg.add(radScriptGenerationUseStringFormat);

      // dist
      gbc = new GridBagConstraints(1,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      add(new JPanel(), gbc);

      setBorder(BorderFactory.createEtchedBorder());

   }
}
