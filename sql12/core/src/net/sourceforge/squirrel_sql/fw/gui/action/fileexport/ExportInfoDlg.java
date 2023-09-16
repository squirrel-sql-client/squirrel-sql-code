package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ExportInfoDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportInfoDlg.class);

   public ExportInfoDlg(Window parent)
   {
      super(parent);
      setModal(true);

      setTitle(s_stringMgr.getString("ExportInfoDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0);
      getContentPane().add(new JLabel(s_stringMgr.getString("ExportInfoDlg.info")), gbc);
      //getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("ExportInfoDlg.info")), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      JButton bntOk = new JButton(s_stringMgr.getString("ExportInfoDlg.ok"));
      bntOk.addActionListener(e -> close());
      getContentPane().add(bntOk, gbc);

      //GUIUtils.initLocation(this, 400,300);
      pack();
      GUIUtils.centerWithinParent(this);
      GUIUtils.enableCloseByEscape(this);

      setVisible(true);
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }
}
