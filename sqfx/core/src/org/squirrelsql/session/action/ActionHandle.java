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

   public ActionHandle(ActionConfiguration actionConfiguration, SessionTabContext sessionTabContext)
   {
      _actionConfiguration = actionConfiguration;
      _sessionTabContext = sessionTabContext;
   }

   public void setOnAction(SqFxActionListener sqFxActionListener)
   {
      _sqFxActionListener = sqFxActionListener;
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
   }

   public ActionConfiguration getActionConfiguration()
   {
      return _actionConfiguration;
   }

   public void setDisable(boolean b)
   {
      if(null != _toolbarButton)
      {
         _toolbarButton.setDisable(b);
      }
      if(null != _menuItem)
      {
         _menuItem.setDisable(b);
      }
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

      if(null == _actionConfiguration.getActionScope())
      {
         return;
      }

      if(null == actionScope)
      {
         setDisable(true);
         return;
      }

      setDisable(false == _actionConfiguration.getActionScope().equals(_currentActionScope));
   }

   public boolean matchesKeyEvent(KeyEvent keyEvent)
   {
      KeyCodeCombination keyCodeCombination = _actionConfiguration.getKeyCodeCombination();
      return KeyMatchWA.matches(keyEvent, keyCodeCombination);
   }

   public void refreshActionScopeDisplay()
   {
      setActionScope(_currentActionScope);
   }
}
