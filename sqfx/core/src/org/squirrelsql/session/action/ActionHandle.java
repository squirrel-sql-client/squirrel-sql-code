package org.squirrelsql.session.action;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.session.SessionTabContext;

public class ActionHandle
{
   private ActionCfg _actionCfg;
   private SessionTabContext _sessionTabContext;

   private SqFxActionListener _sqFxActionListener;
   private MenuItem _menuItem;
   private ButtonBase _toolbarButton;
   private ActionScope _currentActionScope;
   private boolean _disabledByExtern = false;

   private SqFxToggleActionListener _sqFxToggleActionListener;

   private boolean _toggleSelectState;

   public ActionHandle(ActionCfg actionCfg, SessionTabContext sessionTabContext)
   {
      _actionCfg = actionCfg;
      _sessionTabContext = sessionTabContext;
   }

   public void setAction(SqFxActionListener sqFxActionListener)
   {
      if(_actionCfg.getActionType() != ActionType.NON_TOGGLE)
      {
         throw new UnsupportedOperationException("Action is a toggle action. Please use setToggleAction() instead of setAction()");
      }


      _sqFxActionListener = sqFxActionListener;
      refreshActionUI();
   }

   public void setToggleAction(SqFxToggleActionListener toggleActionListener)
   {
      if(_actionCfg.getActionType() != ActionType.TOGGLE)
      {
         throw new UnsupportedOperationException("Action is no toggle action. Please use setAction() instead of setToggleAction()");
      }

      _sqFxToggleActionListener = toggleActionListener;
      refreshActionUI();
   }


   private void performAction(ActionEvent e)
   {
      if (_actionCfg.getActionType() == ActionType.NON_TOGGLE )
      {
         if (null == _sqFxActionListener)
         {
            throw new UnsupportedOperationException("No action listener was provided for action: " + _actionCfg.getText());
         }
         _sqFxActionListener.actionPerformed();
      }
      else // if (_actionCfg.getActionType() == ActionType.TOGGLE )
      {
         if (null == _sqFxToggleActionListener)
         {
            throw new UnsupportedOperationException("No toggle action listener was provided for action: " + _actionCfg.getText());
         }

         if(e.getSource() instanceof ToggleButton)
         {
            _toggleSelectState = ((ToggleButton)e.getSource()).isSelected();
            if(null != _menuItem)
            {
               ((CheckMenuItem)_menuItem).setSelected(_toggleSelectState);
            }
         }
         else if(e.getSource() instanceof CheckMenuItem)
         {
            _toggleSelectState = ((CheckMenuItem)e.getSource()).isSelected();

            if(null != _menuItem)
            {
               ((ToggleButton)_toolbarButton).setSelected(_toggleSelectState);
            }
         }
         else
         {
            throw new UnsupportedOperationException("For the time being toggle actions only support ToggleButton or CheckMenuItem as event source");
         }

         _sqFxToggleActionListener.toggleActionPerformed(_toggleSelectState);
      }



      if (null != e)
      {
         e.consume();
      }
   }

   public void setToolbarButton(ButtonBase toolbarButton)
   {
      if(_actionCfg.getActionType() == ActionType.TOGGLE && false == toolbarButton instanceof ToggleButton )
      {
         throw new UnsupportedOperationException("Action is toggle action and needs a toggle button");
      }
      if(_actionCfg.getActionType() == ActionType.NON_TOGGLE && false == toolbarButton instanceof Button )
      {
         throw new UnsupportedOperationException("Action is toggle action and needs a button");
      }

      _toolbarButton = toolbarButton;
      _toolbarButton.setOnAction((e) -> performAction(e));
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
      if(_actionCfg.getActionType() == ActionType.TOGGLE && false == menuItem instanceof CheckMenuItem )
      {
         throw new UnsupportedOperationException("Action is toggle action and needs a check menu item");
      }

      _menuItem = menuItem;
      _menuItem.setOnAction((e) -> performAction(e));
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

      if(null == _sqFxActionListener && null == _sqFxToggleActionListener)
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

      updateToggleSelectionState();
   }

   public void fire()
   {
      performAction(null);
   }

   public void setToggleSelectState(boolean toggleSelectState)
   {
      if(_actionCfg.getActionType() != ActionType.TOGGLE)
      {
         throw new UnsupportedOperationException("Action is no toggle action and can not be selected");
      }

      _toggleSelectState = toggleSelectState;

      updateToggleSelectionState();
   }

   public boolean isToggleSelected()
   {
      if(_actionCfg.getActionType() != ActionType.TOGGLE)
      {
         throw new UnsupportedOperationException("Action is no toggle action");
      }
      return _toggleSelectState;
   }


   private void updateToggleSelectionState()
   {
      if(_actionCfg.getActionType() != ActionType.TOGGLE)
      {
         return;
      }


      if(null != _toolbarButton)
      {
         ((ToggleButton)_toolbarButton).setSelected(_toggleSelectState);
      }

      if(null != _menuItem)
      {
         ((CheckMenuItem)_menuItem).setSelected(_toggleSelectState);
      }
   }
}
