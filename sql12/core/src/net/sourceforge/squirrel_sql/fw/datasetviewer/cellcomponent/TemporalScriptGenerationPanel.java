package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TemporalScriptGenerationPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TemporalScriptGenerationPanel.class);

   JRadioButton radScriptGenerationUseStandardEscapeFormat;
   JRadioButton radScriptGenerationUseStringFormat;

   public TemporalScriptGenerationPanel(String escapeFormatExample, String stringFormatExample)
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(new JLabel(s_stringMgr.getString("TemporalScriptGenerationPanel.scriptGeneration.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,0,0), 0,0);
      radScriptGenerationUseStandardEscapeFormat = new JRadioButton(s_stringMgr.getString("TemporalScriptGenerationPanel.scriptGeneration.escaped", escapeFormatExample));
      add(radScriptGenerationUseStandardEscapeFormat, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,5,0,0), 0,0);
      radScriptGenerationUseStringFormat = new JRadioButton(s_stringMgr.getString("TemporalScriptGenerationPanel.scriptGeneration.string", stringFormatExample));
      add(radScriptGenerationUseStringFormat, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radScriptGenerationUseStandardEscapeFormat);
      bg.add(radScriptGenerationUseStringFormat);


      setBorder(BorderFactory.createEtchedBorder());

   }
}
