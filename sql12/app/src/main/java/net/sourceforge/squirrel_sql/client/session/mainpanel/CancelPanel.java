package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
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
   JButton closeBtn;


   public CancelPanel(ISession session)
   {
      super(new BorderLayout());

      add(createNorthPanel(session), BorderLayout.NORTH);
      add(createCenterPanel(), BorderLayout.CENTER);
   }

   private JPanel createNorthPanel(ISession session)
   {
      JPanel ret = new JPanel(new BorderLayout());

      ImageIcon icon = session.getApplication().getResources().getIcon(SquirrelResources.IImageNames.CLOSE);
      closeBtn = new JButton(icon);
      closeBtn.setBorderPainted(false);
      closeBtn.setMargin(new Insets(0, 0, 0, 0));



      ret.add(closeBtn, BorderLayout.EAST);
      ret.add(new JPanel(), BorderLayout.CENTER);
      return ret;
   }

   private JPanel createCenterPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

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
      ret.add(new JLabel(label), gbc);

      gbc.weightx = 1;
      ++gbc.gridx;
      ret.add(sqlLbl, gbc);

      gbc.weightx = 0;
      gbc.gridx = 0;
      ++gbc.gridy;
      // i18n[SQLResultExecuterPanel.statusLabel=Status:]
      label =
            s_stringMgr.getString("SQLResultExecuterPanel.statusLabel");
      ret.add(new JLabel(label), gbc);

      ++gbc.gridx;
      ret.add(currentStatusLbl, gbc);

      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.fill = GridBagConstraints.NONE;
      ret.add(cancelBtn, gbc);

      return ret;
   }
}
