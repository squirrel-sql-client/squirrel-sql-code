package org.squirrelsql.session.action;

import javafx.scene.control.*;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.SessionManager;
import org.squirrelsql.session.SessionManagerListener;

import java.util.ArrayList;

public class ActionMangerImpl
{
   private Menu _sessionMenu;
   private ActionScope _currentActionScope;
   private SessionManager _sessionManager;
   private SessionTabContext _activeOrActivatingSessionTabContext;

   private ArrayList<ActionHandle> _actionHandles = new ArrayList<>();
   private volatile int _actionConfigurationIdSequence;

   public int getNextActionConfigurationId()
   {
      return ++_actionConfigurationIdSequence;
   }


   public ActionMangerImpl(SessionManager sessionManager)
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

      _sessionMenu = new Menu(new I18n(ActionMangerImpl.class).t("main.menu.session"));
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
      for (StandardActionConfiguration standardActionConfiguration : StandardActionConfiguration.SESSION_MENU)
      {
         MenuItem menuItem = new MenuItem(standardActionConfiguration.getActionConfiguration().getText());
         ActionHandle actionHandle = getActionHandle(standardActionConfiguration.getActionConfiguration(), sessionTabContext);
         actionHandle.setMenuItem(menuItem);
         actionHandle.refreshActionScopeDisplay();

         _sessionMenu.getItems().add(menuItem);
      }
   }

   public Menu getSessionMenu()
   {
      return _sessionMenu;
   }

   public ToolBar createToolbar()
   {
      ToolBar ret = new ToolBar();

      for (StandardActionConfiguration standardActionConfiguration : StandardActionConfiguration.SESSION_TOOLBAR)
      {
         ActionConfiguration actionConfiguration = standardActionConfiguration.getActionConfiguration();
         ActionHandle actionHandle = getActionHandleForActiveOrActivatingSessionTabContext(actionConfiguration);

         Button b = new Button();
         actionHandle.setToolbarButton(b);
         ret.getItems().add(b);
      }

      return ret;
   }

   public void setCurrentActionScope(ActionScope currentActionScope)
   {
      _currentActionScope = currentActionScope;
      CollectionUtil.forEachFiltered(_actionHandles, ah -> ah.matchesSessionContext(_activeOrActivatingSessionTabContext), ah -> ah.setActionScope(_currentActionScope));
   }

   public ActionHandle getActionHandleForActiveOrActivatingSessionTabContext(ActionConfiguration actionConfiguration)
   {
      return getActionHandle(actionConfiguration, _activeOrActivatingSessionTabContext);
   }

   public ActionHandle getActionHandle(ActionConfiguration actionConfiguration, SessionTabContext sessionTabContext)
   {
      ActionHandle ret;

      ArrayList<ActionHandle> handles = CollectionUtil.filter(_actionHandles, ah -> ah.matchesPrimaryKey(actionConfiguration, sessionTabContext));

      if(0 == handles.size())
      {
         ret = new ActionHandle(actionConfiguration, sessionTabContext);
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
}
