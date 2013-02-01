package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecentFilesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesDialog.class);

   private RecentFilesDialog _dialog;

   public RecentFilesController(Frame parent, ISQLAlias selectedAlias)
   {
      init(parent, selectedAlias, false);
   }


   public RecentFilesController(ISQLPanelAPI panel)
   {
      init(GUIUtils.getOwningFrame(panel.getSQLEntryPanel().getTextComponent()), panel.getSession().getAlias(), true);
   }


   private void init(Frame parent, ISQLAlias selectedAlias, boolean showAppendOption)
   {
      _dialog = new RecentFilesDialog(parent, showAppendOption);

      _dialog.btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _dialog.dispose();
         }
      });

      DefaultMutableTreeNode root = new DefaultMutableTreeNode();

      root.add(new DefaultMutableTreeNode(s_stringMgr.getString("RecentFilesController.recentFiles.global"), true));
      root.add(new DefaultMutableTreeNode(s_stringMgr.getString("RecentFilesController.favouritFiles.global"), true));
      root.add(new DefaultMutableTreeNode(s_stringMgr.getString("RecentFilesController.recentFiles.alias", selectedAlias.getName()), true));
      root.add(new DefaultMutableTreeNode(s_stringMgr.getString("RecentFilesController.favouritFiles.alias", selectedAlias.getName()), true));

      _dialog.treFiles.setModel(new DefaultTreeModel(root));
      _dialog.treFiles.setRootVisible(false);

      _dialog.setVisible(true);
   }

}
