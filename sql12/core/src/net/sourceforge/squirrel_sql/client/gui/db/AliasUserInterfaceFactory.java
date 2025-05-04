package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasFileOpenAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CollapseAllAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CollapseSelectedAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ColorAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CompressAliasToolbarAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyToPasteAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CutAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExpandAllAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExpandSelectedAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyMultipleAliasesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.PasteAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SortAliasesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ToggleTreeViewAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TransferAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAction;
import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

final class AliasUserInterfaceFactory implements IUserInterfaceFactory<AliasesList>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasUserInterfaceFactory.class);

   private IApplication _app;
   private final AliasesList _aliasesList;
   private AliasToolBarBuilder _aliasToolBarBuilder;
   private BasePopupMenu _pm = new BasePopupMenu();

   AliasUserInterfaceFactory(AliasesList list)
   {
      if (list == null)
      {
         throw new IllegalArgumentException("AliasesList == null");
      }

      _app = Main.getApplication();
      _aliasesList = list;

      createToolBar();

      final ActionCollection actions = _app.getActionCollection();
      _pm.add(actions.get(ConnectToAliasAction.class));
      _pm.addSeparator();
      _pm.add(actions.get(CreateAliasAction.class));
      _pm.add(actions.get(ModifyAliasAction.class));
      _pm.add(actions.get(CopyAliasAction.class));
      _pm.add(actions.get(DeleteAliasAction.class));
      _pm.addSeparator();
      _pm.add(actions.get(AliasPropertiesAction.class));
      _pm.addSeparator();
      _pm.add(actions.get(AliasFileOpenAction.class));
      _pm.addSeparator();
      _pm.add(actions.get(FindAliasAction.class));
      _pm.add(actions.get(SortAliasesAction.class));
      _pm.add(actions.get(ColorAliasAction.class));
      _pm.addSeparator();
      addToMenuAsCheckBoxMenuItem(_app.getResources(), actions.get(ToggleTreeViewAction.class), _pm);
      _pm.add(actions.get(NewAliasFolderAction.class));
      _pm.add(actions.get(CopyToPasteAliasFolderAction.class));
      _pm.add(actions.get(CutAliasFolderAction.class));
      _pm.add(actions.get(PasteAliasFolderAction.class));
      _pm.add(actions.get(CollapseAllAliasFolderAction.class));
      _pm.add(actions.get(ExpandAllAliasFolderAction.class));
      _pm.add(actions.get(CollapseSelectedAliasFolderAction.class));
      _pm.add(actions.get(ExpandSelectedAliasFolderAction.class));
      _pm.addSeparator();
      _pm.add(actions.get(TransferAliasAction.class));
      _pm.addSeparator();
      _pm.add(actions.get(ModifyMultipleAliasesAction.class));
      _pm.addSeparator();
      addToMenuAsCheckBoxMenuItem(_app.getResources(), actions.get(CompressAliasToolbarAction.class), _pm);
   }

   private JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Resources rsrc, Action action, JPopupMenu menu)
   {
      JCheckBoxMenuItem mnu = rsrc.addToMenuAsCheckBoxMenuItem(action, menu);
      if (action instanceof IToggleAction)
      {
         ((IToggleAction) action).getToggleComponentHolder().addToggleableComponent(mnu);
      }
      return mnu;
   }

   public String getWindowTitle()
   {
      return s_stringMgr.getString("AliasesListInternalFrame.windowtitle");
   }

   //public ICommand getDoubleClickCommand(MouseEvent evt)
   //{
   //   ICommand cmd = null;
   //   SQLAlias alias = _aliasesList.getSelectedAlias(evt);
   //   if (alias != null)
   //   {
   //      cmd = new ConnectToAliasCommand(alias);
   //   }
   //   return cmd;
   //}

   @Override
   public void execDoubleClickCommand(MouseEvent evt)
   {
      SQLAlias alias = _aliasesList.getSelectedAlias(evt);
      if (alias != null)
      {
         new ConnectToAliasCommand(alias).executeConnect();
      }
   }

   /**
    * Enable/disable actions depending on whether an item is selected in list.
    */
   public void enableDisableActions()
   {
      final ActionCollection actions = _app.getActionCollection();

      ToggleTreeViewAction actViewAsTree = (ToggleTreeViewAction) actions.get(ToggleTreeViewAction.class);


      boolean viewAsTree = actViewAsTree.getToggleComponentHolder().isSelected();

      actions.get(NewAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(CopyToPasteAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(CutAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(PasteAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(CollapseAllAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(ExpandAllAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(CollapseSelectedAliasFolderAction.class).setEnabled(viewAsTree);
      actions.get(ExpandSelectedAliasFolderAction.class).setEnabled(viewAsTree);
   }

   void createToolBar()
   {
      if(null == _aliasToolBarBuilder)
      {
         _aliasToolBarBuilder = new AliasToolBarBuilder(getWindowTitle());
      }

      _aliasToolBarBuilder.clearItems();

      final ActionCollection actions = _app.getActionCollection();
      _aliasToolBarBuilder.add(actions.get(ConnectToAliasAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.add(actions.get(CreateAliasAction.class));
      _aliasToolBarBuilder.add(actions.get(ModifyAliasAction.class));
      _aliasToolBarBuilder.add(actions.get(CopyAliasAction.class));
      _aliasToolBarBuilder.add(actions.get(DeleteAliasAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.add(actions.get(AliasPropertiesAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.add(actions.get(AliasFileOpenAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.add(actions.get(FindAliasAction.class));
      _aliasToolBarBuilder.add(actions.get(SortAliasesAction.class));
      _aliasToolBarBuilder.add(actions.get(ColorAliasAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.addToggleAction((IToggleAction) actions.get(ToggleTreeViewAction.class));
      _aliasToolBarBuilder.add(actions.get(NewAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(CopyToPasteAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(CutAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(PasteAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(CollapseAllAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(ExpandAllAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(CollapseSelectedAliasFolderAction.class));
      _aliasToolBarBuilder.add(actions.get(ExpandSelectedAliasFolderAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.add(actions.get(TransferAliasAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.add(actions.get(ModifyMultipleAliasesAction.class));
      _aliasToolBarBuilder.addSeparator();
      _aliasToolBarBuilder.addToggleAction((IToggleAction) actions.get(CompressAliasToolbarAction.class));
   }


   public AliasesList getAliasesList()
   {
      return _aliasesList;
   }

   public ToolBar getToolBar()
   {
      boolean compressAliasesToolbar = Props.getBoolean(CompressAliasToolbarAction.PREF_KEY_COMPRESS_ALIAS_TOOLBAR, false);
      ToolBar ret = _aliasToolBarBuilder.reBuildToolBar(compressAliasesToolbar);

      SwingUtilities.invokeLater(
            new Runnable()
            {
               public void run()
               {
                  ToggleTreeViewAction actViewAliasesAsTree = (ToggleTreeViewAction) Main.getApplication().getActionCollection().get(ToggleTreeViewAction.class);
                  actViewAliasesAsTree.getToggleComponentHolder().setSelected(Props.getBoolean(ToggleTreeViewAction.PREF_KEY_VIEW_ALIASES_AS_TREE, false));
                  actViewAliasesAsTree.actionPerformed(new ActionEvent(this, 1, "actionPerformed"));

                  CompressAliasToolbarAction compressAliasToolbarAction = (CompressAliasToolbarAction) Main.getApplication().getActionCollection().get(CompressAliasToolbarAction.class);
                  compressAliasToolbarAction.getToggleComponentHolder().setSelected(compressAliasesToolbar);
               }
            });

      return ret;
   }

   public BasePopupMenu getPopupMenu()
   {
      return _pm;
   }

   public AliasesList getList()
   {
      return _aliasesList;
   }

}
