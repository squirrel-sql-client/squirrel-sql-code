package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class WaitPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectResultController.class);

   public WaitPanel(String hqlQuery)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc = new GridBagConstraints();

      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(5, 10, 5, 10);
      gbc.gridx = 0;
      gbc.gridy = 0;

      add(new JLabel(s_stringMgr.getString("WaitPanel.hqlLabel")), gbc);

      gbc.weightx = 1;
      ++gbc.gridx;
      add(new JLabel(hqlQuery), gbc);

   }

   public String getTitle()
   {
      return s_stringMgr.getString("WaitPanel.title");
   }
}
