package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldConfigCtrl;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.awt.*;

public class ConfigurableMeldPanel extends JPanel
{
   private final JMeldPanel _meldPanel;
   private final JMeldConfigCtrl _meldConfigCtrl;

   public ConfigurableMeldPanel(JMeldPanel meldPanel, JMeldConfigCtrl meldConfigCtrl)
   {
      super(new GridBagLayout());
      _meldPanel = meldPanel;
      _meldConfigCtrl = meldConfigCtrl;

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      add(meldConfigCtrl.getPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      add(meldPanel, gbc);
   }

   public JMeldPanel getMeldPanel()
   {
      return _meldPanel;
   }

   public JMeldConfigCtrl getMeldConfigCtrl()
   {
      return _meldConfigCtrl;
   }
}
