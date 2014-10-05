package org.squirrelsql.session.action;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.workaround.KeyMatchWA;

public class ActionHandle
{
   private ActionConfiguration _actionConfiguration;
   private SessionTabContext _sessionTabContext;

   private SqFxActionListener _sqFxActionListener;
   private MenuItem _menuItem;
   private Button _toolbarButton;
   private ActionScope _currentActionScope;
   private boolean _disabledByExtern = false;

   public ActionHandle(ActionConfiguration actionConfiguration, SessionTabContext sessionTabContext)
   {
      _actionConfiguration = actionConfiguration;
      _sessionTabContext = sessionTabContext;
   }

   public void setOnAction(SqFxActionListener sqFxActionListener)
   {
      _sqFxActionListener = sqFxActionListener;
      refreshActionUI();
   }

   private void actionPerformed()
   {
      if(null != _sqFxActionListener)
      {
         _sqFxActionListener.actionPerformed();
      }

   }

   public void setToolbarButton(Button toolbarButton)
   {
      _toolbarButton = toolbarButton;
      _toolbarButton.setOnAction((e) -> actionPerformed());
      _toolbarButton.setGraphic(_actionConfiguration.getIcon());
      _toolbarButton.setTooltip(new Tooltip(_actionConfiguration.getText() + "\t" + _actionConfiguration.getKeyCodeCombination()));
      refreshActionUI();
   }

   public ActionConfiguration getActionConfiguration()
   {
      return _actionConfiguration;
   }

   public void setDisable(boolean b)
   {
      _disabledByExtern = b;

      refreshActionUI();
   }

   public SessionTabContext getSessionTabContext()
   {
      return _sessionTabContext;
   }

   public void setMenuItem(MenuItem menuItem)
   {
      _menuItem = menuItem;
      _menuItem.setOnAction((e) -> actionPerformed());
      _menuItem.setAccelerator(_actionConfiguration.getKeyCodeCombination());
      refreshActionUI();
   }

   public MenuItem getMenuItem()
   {
      return _menuItem;
   }

   public boolean matchesSessionContext(SessionTabContext sessionTabContext)
   {
      return _sessionTabContext.matches(sessionTabContext);
   }

   public boolean matchesPrimaryKey(ActionConfiguration actionConfiguration, SessionTabContext sessionTabContext)
   {
      return matchesSessionContext(sessionTabContext) && _actionConfiguration.matches(actionConfiguration);
   }

   public void setActionScope(ActionScope actionScope)
   {
      _currentActionScope = actionScope;
      refreshActionUI();
   }

   public boolean matchesKeyEvent(KeyEvent keyEvent)
   {
      KeyCodeCombination keyCodeCombination = _actionConfiguration.getKeyCodeCombination();
      return KeyMatchWA.matches(keyEvent, keyCodeCombination);
   }

   public void refreshActionUI()
   {
      if(_disabledByExtern)
      {
         updateControls(true);
         return;
      }

      if(null == _sqFxActionListener)
      {
         updateControls(true);
         return;
      }


      if(ActionScope.UNSCOPED == _actionConfiguration.getActionScope())
      {
         updateControls(false);
         return;
      }

      if(null == _currentActionScope)
      {
         updateControls(true);
         return;
      }


      updateControls(false == _actionConfiguration.getActionScope().equals(_currentActionScope));
   }

   private void updateControls(boolean disable)
   {
      if(null != _toolbarButton)
      {
         _toolbarButton.setDisable(disable);
      }
      if(null != _menuItem)
      {
         _menuItem.setDisable(disable);
      }
   }

   public void fire()
   {
      _sqFxActionListener.actionPerformed();
   }
}
