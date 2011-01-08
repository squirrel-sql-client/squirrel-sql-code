package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class CancelPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CancelPanel.class);

   JLabel sqlLbl = new JLabel();
   JLabel currentStatusLbl = new JLabel();
   JButton cancelBtn;


   public CancelPanel()
   {
      super(new GridBagLayout());

      // i18n[SQLResultExecuterPanel.cancelButtonLabel=Cancel]
      String label = s_stringMgr.getString("SQLResultExecuterPanel.cancelButtonLabel");
      cancelBtn = new JButton(label);

      GridBagConstraints gbc = new GridBagConstraints();

      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(5, 10, 5, 10);

      gbc.gridx = 0;
      gbc.gridy = 0;

      // i18n[SQLResultExecuterPanel.sqlLabel=SQL:]
      label = s_stringMgr.getString("SQLResultExecuterPanel.sqlLabel");
      add(new JLabel(label), gbc);

      gbc.weightx = 1;
      ++gbc.gridx;
      add(sqlLbl, gbc);

      gbc.weightx = 0;
      gbc.gridx = 0;
      ++gbc.gridy;
      // i18n[SQLResultExecuterPanel.statusLabel=Status:]
      label =
            s_stringMgr.getString("SQLResultExecuterPanel.statusLabel");
      add(new JLabel(label), gbc);

      ++gbc.gridx;
      add(currentStatusLbl, gbc);

      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.fill = GridBagConstraints.NONE;
      add(cancelBtn, gbc);
   }
}
