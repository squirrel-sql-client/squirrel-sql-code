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
   private ActionCfg _actionCfg;
   private SessionTabContext _sessionTabContext;

   private SqFxActionListener _sqFxActionListener;
   private MenuItem _menuItem;
   private Button _toolbarButton;
   private ActionScope _currentActionScope;
   private boolean _disabledByExtern = false;

   public ActionHandle(ActionCfg actionCfg, SessionTabContext sessionTabContext)
   {
      _actionCfg = actionCfg;
      _sessionTabContext = sessionTabContext;
   }

   public void setAction(SqFxActionListener sqFxActionListener)
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
      _toolbarButton.setGraphic(_actionCfg.getIcon());


      Tooltip tooltip;
      if (null != _actionCfg.getKeyCodeCombination())
      {
         tooltip = new Tooltip(_actionCfg.getText() + "\t" + _actionCfg.getKeyCodeCombination());
      }
      else
      {
         tooltip = new Tooltip(_actionCfg.getText());
      }
      _toolbarButton.setTooltip(tooltip);

      refreshActionUI();
   }

   public ActionCfg getActionCfg()
   {
      return _actionCfg;
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
      _menuItem.setAccelerator(_actionCfg.getKeyCodeCombination());
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

   public boolean matchesPrimaryKey(ActionCfg actionCfg, SessionTabContext sessionTabContext)
   {
      return matchesSessionContext(sessionTabContext) && _actionCfg.matches(actionCfg);
   }

   public void setActionScope(ActionScope actionScope)
   {
      _currentActionScope = actionScope;
      refreshActionUI();
   }

   public boolean matchesKeyEvent(KeyEvent keyEvent)
   {
      return _actionCfg.matchesKeyEvent(keyEvent);
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


      if(ActionScope.UNSCOPED == _actionCfg.getActionScope())
      {
         updateControls(false);
         return;
      }

      if(null == _currentActionScope)
      {
         updateControls(true);
         return;
      }


      updateControls(false == _actionCfg.getActionScope().equals(_currentActionScope));
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
      if (null == _sqFxActionListener)
      {
         throw new UnsupportedOperationException("No action listener was provided for action: " + _actionCfg.getText());
      }

      _sqFxActionListener.actionPerformed();

   }
}
