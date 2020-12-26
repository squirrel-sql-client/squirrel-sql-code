package net.sourceforge.squirrel_sql.client.gui.db.recentalias;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DockWidget;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.AliasSearchWrapper;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RecentAliasesListDockWidget extends DockWidget
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentAliasesListDockWidget.class);

   JList<AliasSearchWrapper> lstAliases = new JList<>();
   IntegerField txtMaxNumRecent = new IntegerField(5, 1);
   JButton btnRemoveSelected = new JButton(s_stringMgr.getString("RecentAliasesListInternalFrame.removeSelected"));
   JButton btnClearList = new JButton(s_stringMgr.getString("RecentAliasesListInternalFrame.btnClearAliases"));
   JToolBar toolBar;

   public RecentAliasesListDockWidget()
   {
      super(s_stringMgr.getString("RecentAliasesListInternalFrame.title"), true, Main.getApplication());

      getContentPane().setLayout(new BorderLayout(5,5));

      toolBar = new JToolBar();
      toolBar.setFloatable(false);
      getContentPane().add(toolBar, BorderLayout.NORTH);

      getContentPane().add(new JScrollPane(lstAliases), BorderLayout.CENTER);

      getContentPane().add(createBottomPanel(), BorderLayout.SOUTH);

   }

   private JPanel createBottomPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,0),0,0 );
      ret.add(new JLabel(s_stringMgr.getString("RecentAliasesListInternalFrame.limitRecent")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,5,0),0,0 );
      GUIUtils.setMinimumWidth(txtMaxNumRecent, txtMaxNumRecent.getPreferredSize().width);
      ret.add(txtMaxNumRecent, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,5,0),0,0 );
      ret.add(btnRemoveSelected, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,5,0),0,0 );
      ret.add(btnClearList, gbc);

      gbc = new GridBagConstraints(4,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,0),0,0 );
      ret.add(new JPanel(), gbc);


      return ret;
   }
}
