package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ResultSetExportSelectionPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultSetExportSelectionPanel.class);
   final JRadioButton radComplete;
   final JRadioButton radLimited;
   final IntegerField txtLimitRows;

   public ResultSetExportSelectionPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      radComplete = new JRadioButton(s_stringMgr.getString("ResultSetExportDialog.executingQuery"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      add(radComplete, gbc);

      radLimited = new JRadioButton(s_stringMgr.getString("ResultSetExportDialog.limitRows"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(radLimited, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      txtLimitRows = new IntegerField(5);
      add(txtLimitRows, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      add(new JPanel(), gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radComplete);
      bg.add(radLimited);
   }
}
