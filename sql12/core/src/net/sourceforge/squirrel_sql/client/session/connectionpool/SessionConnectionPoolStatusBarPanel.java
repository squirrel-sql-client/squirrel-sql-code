package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class SessionConnectionPoolStatusBarPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionConnectionPoolStatusBarPanel.class);

   JTextField textLbl = new JTextField();
   SmallTabButton btnState;
   SmallTabButton btnDetails;

   public SessionConnectionPoolStatusBarPanel()
   {
      super(new BorderLayout());

      textLbl.setEditable(false);

      // TODO
      textLbl.setText("Query connection pool size = 4 / active = 3");
      add(textLbl, BorderLayout.CENTER);
      GUIUtils.inheritBackground(textLbl);

      add(createButtonPanel(), BorderLayout.EAST);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1, 2));

      //btnState = new SmallTabButton(s_stringMgr.getString("SessionConnectionPool.state"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.WHITE_GEM), 4);
      btnState = new SmallTabButton(s_stringMgr.getString("SessionConnectionPool.state"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.GREEN_GEM), 4);
      ret.add(btnState);

      btnDetails = new SmallTabButton(s_stringMgr.getString("SessionConnectionPool.details"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.VIEW_DETAILS), 4);
      ret.add(btnDetails);

      return ret;
   }
}
