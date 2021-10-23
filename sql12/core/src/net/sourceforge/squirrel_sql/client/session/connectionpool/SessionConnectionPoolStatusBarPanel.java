package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class SessionConnectionPoolStatusBarPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionConnectionPoolStatusBarPanel.class);

   JTextField textLbl = new JTextField();
   SmallTabButton btnState;
   SmallTabButton btnConfigureConnectionPoolSize;

   public SessionConnectionPoolStatusBarPanel(JComponent parent)
   {
      super(new BorderLayout());

      textLbl.setEditable(false);

      add(textLbl, BorderLayout.CENTER);
      GUIUtils.inheritBackground(textLbl);

      // SessionConnectionPool.state.message.over.used is the longest message.
      final int txtLblWidth = 5 + parent.getFontMetrics(parent.getFont()).stringWidth(s_stringMgr.getString("SessionConnectionPool.state.message.over.used", 10, 10));
      GUIUtils.setPreferredWidth(textLbl, txtLblWidth);
      GUIUtils.setMinimumWidth(textLbl, txtLblWidth);

      add(createButtonPanel(), BorderLayout.EAST);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1, 2));

      btnState = new SmallTabButton(4);
      btnState.setEnabled(false);
      ret.add(btnState);

      btnConfigureConnectionPoolSize = new SmallTabButton(s_stringMgr.getString("SessionConnectionPool.configure.size"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.VIEW_DETAILS), 4);
      ret.add(btnConfigureConnectionPoolSize);

      return ret;
   }
}
