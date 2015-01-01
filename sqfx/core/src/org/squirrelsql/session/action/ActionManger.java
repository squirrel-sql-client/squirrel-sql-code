package org.squirrelsql.session.action;

import javafx.scene.control.*;

import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.SessionManager;
import org.squirrelsql.session.SessionManagerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionManger
{
   private Menu _sessionMenu;
   private ActionScope _currentActionScope;
   private SessionManager _sessionManager;
   private SessionTabContext _activeOrActivatingSessionTabContext;

   private List<ActionHandle> _actionHandles = new ArrayList<>();
   private volatile int _actionConfigurationIdSequence;

   public int getNextActionConfigurationId()
   {
      return ++_actionConfigurationIdSequence;
   }


   public ActionManger(SessionManager sessionManager)
   {
      _sessionManager = sessionManager;
      sessionManager.addSessionManagerListener(new SessionManagerListener()
      {
         @Override
         public void contextActiveOrActivating(SessionTabContext sessionTabContext)
         {
            onContextActiveOrActivating(sessionTabContext);
         }

         @Override
         public void contextClosing(SessionTabContext sessionTabContext)
         {
            onContextClosing(sessionTabContext);
         }
      });

      _sessionMenu = new Menu(new I18n(ActionManger.class).t("main.menu.session"));
      _sessionMenu.setDisable(true);
   }


   private void onContextActiveOrActivating(SessionTabContext sessionTabContext)
   {
      _activeOrActivatingSessionTabContext = sessionTabContext;
      _sessionMenu.getItems().clear();


      if(null == sessionTabContext)
      {
         _sessionMenu.setDisable(true);
         return;
      }

      _sessionMenu.setDisable(false);
      for (StdActionCfg stdActionCfg : StdActionCfg.SESSION_MENU)
      {
         MenuItem menuItem = new MenuItem(stdActionCfg.getActionCfg().getText());

         menuItem.setGraphic(stdActionCfg.getActionCfg().getIcon());

         ActionHandle actionHandle = getActionHandle(stdActionCfg.getActionCfg(), sessionTabContext);
         actionHandle.setMenuItem(menuItem);
         actionHandle.refreshActionUI();

         _sessionMenu.getItems().add(menuItem);
      }

      updateActionUIs();
   }

   public Menu getSessionMenu()
   {
      return _sessionMenu;
   }

   public ToolBar createToolbar()
   {
      ToolBar ret = new ToolBar();

      for (StdActionCfg stdActionCfg : StdActionCfg.SESSION_TOOLBAR)
      {
         addActionToToolbar(ret, stdActionCfg.getActionCfg());
      }

      LogTest.checkAndAddTestToolbarButtons(ret);

      return ret;
   }

   public ActionHandle addActionToToolbar(ToolBar toolBar, ActionCfg actionCfg)
   {
      ActionHandle actionHandle = getActionHandleForActiveOrActivatingSessionTabContext(actionCfg);

      Button b = new Button();
      actionHandle.setToolbarButton(b);
      toolBar.getItems().add(b);
      return actionHandle;
   }

   public void setActionScope(ActionScope currentActionScope)
   {
      _currentActionScope = currentActionScope;
      CollectionUtil.forEachFiltered(_actionHandles, ah -> ah.matchesSessionContext(_activeOrActivatingSessionTabContext), ah -> ah.setActionScope(_currentActionScope));
   }

   public void updateActionUIs()
   {
      CollectionUtil.forEachFiltered(_actionHandles, ah -> ah.matchesSessionContext(_activeOrActivatingSessionTabContext), ah -> ah.refreshActionUI());
   }

   public ActionHandle getActionHandleForActiveOrActivatingSessionTabContext(ActionCfg actionCfg)
   {
      return getActionHandle(actionCfg, _activeOrActivatingSessionTabContext);
   }

   public ActionHandle getActionHandle(ActionCfg actionCfg, SessionTabContext sessionTabContext)
   {
      ActionHandle ret;

      List<ActionHandle> handles = CollectionUtil.filter(_actionHandles, ah -> ah.matchesPrimaryKey(actionCfg, sessionTabContext));

      if(0 == handles.size())
      {
         ret = new ActionHandle(actionCfg, sessionTabContext);
         ret.setActionScope(_currentActionScope);
         _actionHandles.add(ret);
      }
      else
      {
         ret = handles.get(0);
      }

      return ret;
   }

   private void onContextClosing(SessionTabContext sessionTabContext)
   {
      _actionHandles.removeIf(ah -> ah.getSessionTabContext().matches(sessionTabContext));
   }

   public List<ActionCfg> getSQLEditRightMouseActionCfgs()
   {
      ArrayList<ActionCfg> ret = new ArrayList<>();

      for (StdActionCfg stdActionCfg : StdActionCfg.SQL_EDITOR_CONTEXT_MENU)
      {
         ret.add(stdActionCfg.getActionCfg());
      }

      return ret;
   }

   public List<ActionCfg> getAllActionCfgs()
   {
      ArrayList<ActionCfg> ret = new ArrayList<>();

      for (StdActionCfg stdActionCfg : StdActionCfg.values())
      {
         ret.add(stdActionCfg.getActionCfg());
      }

      return ret;
   }
}
