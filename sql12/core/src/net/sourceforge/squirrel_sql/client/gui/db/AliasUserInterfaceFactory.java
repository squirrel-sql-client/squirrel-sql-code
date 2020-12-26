package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasFileOpenAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CollapseAllAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CollapseSelectedAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ColorAliasAction;
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
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

final class AliasUserInterfaceFactory implements IUserInterfaceFactory
{
   private static final String PREF_KEY_VIEW_ALIASES_AS_TREE = "Squirrel.viewAliasesAsTree";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasUserInterfaceFactory.class);


   private IApplication _app;
   private final AliasesList _aliasesList;
   private ToolBar _toolBar;
   private BasePopupMenu _pm = new BasePopupMenu();

   AliasUserInterfaceFactory(AliasesList list)
   {
      if (list == null)
      {
         throw new IllegalArgumentException("AliasesList == null");
      }

      _app = Main.getApplication();
      _aliasesList = list;

      if (_app.getSquirrelPreferences().getShowAliasesToolBar())
      {
         createToolBar();
      }

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

      _app.addApplicationListener(() -> onSaveApplicationState());

      SwingUtilities.invokeLater(
            new Runnable()
            {
               public void run()
               {
                  ToggleTreeViewAction actViewAsTree = (ToggleTreeViewAction) actions.get(ToggleTreeViewAction.class);
                  actViewAsTree.getToggleComponentHolder().setSelected(Props.getBoolean(PREF_KEY_VIEW_ALIASES_AS_TREE, false));
                  actViewAsTree.actionPerformed(new ActionEvent(this, 1, "actionPerformed"));
                  enableDisableActions();
               }
            });
   }

   private void onSaveApplicationState()
   {
      IToggleAction actViewAsTree = (IToggleAction) _app.getActionCollection().get(ToggleTreeViewAction.class);
      Props.putBoolean(PREF_KEY_VIEW_ALIASES_AS_TREE, actViewAsTree.getToggleComponentHolder().isSelected());
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

   public ICommand getDoubleClickCommand(MouseEvent evt)
   {
      ICommand cmd = null;
      SQLAlias alias = _aliasesList.getSelectedAlias(evt);
      if (alias != null)
      {
         cmd = new ConnectToAliasCommand(_app, alias);
      }
      return cmd;
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
   }

   void createToolBar()
   {
      _toolBar = new ToolBar();
      _toolBar.setUseRolloverButtons(true);
      _toolBar.setFloatable(false);

      if (_app.getDesktopStyle().isInternalFrameStyle())
      {
         final JLabel lbl = new JLabel(getWindowTitle(), SwingConstants.CENTER);
         lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
         _toolBar.add(lbl, 0);
      }

      final ActionCollection actions = _app.getActionCollection();
      _toolBar.add(actions.get(ConnectToAliasAction.class));
      _toolBar.addSeparator();
      _toolBar.add(actions.get(CreateAliasAction.class));
      _toolBar.add(actions.get(ModifyAliasAction.class));
      _toolBar.add(actions.get(CopyAliasAction.class));
      _toolBar.add(actions.get(DeleteAliasAction.class));
      _toolBar.addSeparator();
      _toolBar.add(actions.get(AliasPropertiesAction.class));
      _toolBar.addSeparator();
      _toolBar.add(actions.get(AliasFileOpenAction.class));
      _toolBar.addSeparator();
      _toolBar.add(actions.get(FindAliasAction.class));
      _toolBar.add(actions.get(SortAliasesAction.class));
      _toolBar.add(actions.get(ColorAliasAction.class));
      _toolBar.addSeparator();
      _toolBar.addToggleAction((IToggleAction) actions.get(ToggleTreeViewAction.class));
      _toolBar.add(actions.get(NewAliasFolderAction.class));
      _toolBar.add(actions.get(CopyToPasteAliasFolderAction.class));
      _toolBar.add(actions.get(CutAliasFolderAction.class));
      _toolBar.add(actions.get(PasteAliasFolderAction.class));
      _toolBar.add(actions.get(CollapseAllAliasFolderAction.class));
      _toolBar.add(actions.get(ExpandAllAliasFolderAction.class));
      _toolBar.add(actions.get(CollapseSelectedAliasFolderAction.class));
      _toolBar.add(actions.get(ExpandSelectedAliasFolderAction.class));
      _toolBar.addSeparator();
      _toolBar.add(actions.get(TransferAliasAction.class));
   }


   public void removeToolbar()
   {
      _toolBar = null;
   }

   public AliasesList getAliasesList()
   {
      return _aliasesList;
   }

   public ToolBar getToolBar()
   {
      return _toolBar;
   }

   public BasePopupMenu getPopupMenu()
   {
      return _pm;
   }

   public IBaseList getList()
   {
      return _aliasesList;
   }

}
