package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ShowReferencesWindow extends JDialog
{

   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowReferencesCtrl.class);


   JCheckBox chkShowQualified;
   JTree tree;
   SQLResultExecutorPanel resultExecuterPanel;

   public ShowReferencesWindow(ISession session, Frame owner, String title)
   {
      super(owner, title);

      getContentPane().setLayout(new BorderLayout());

      MultipleLineLabel lblDescription = new MultipleLineLabel(s_stringMgr.getString("ShowReferencesWindow.description"));
      lblDescription.setBorder(BorderFactory.createEmptyBorder(5,5,10,5));
      getContentPane().add(lblDescription, BorderLayout.NORTH);

      JSplitPane split = new JSplitPane();
      getContentPane().add(split, BorderLayout.CENTER);

      split.setLeftComponent(createLeftComponent());

      resultExecuterPanel = new SQLResultExecutorPanel(session);
      resultExecuterPanel.setMinimumSize(new Dimension(0,0));
      split.setRightComponent(resultExecuterPanel);
   }

   private JPanel createLeftComponent()
   {
      JPanel ret = new JPanel(new BorderLayout());

      chkShowQualified = new JCheckBox(s_stringMgr.getString("ShowReferencesWindow.showQualified"));
      tree = new JTree();

      ret.add(chkShowQualified, BorderLayout.NORTH);
      ret.add(new JScrollPane(tree), BorderLayout.CENTER);

      return ret;
   }
}
