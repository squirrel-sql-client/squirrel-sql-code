package org.squirrelsql.session;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import org.squirrelsql.session.action.StdActionCfg;

import java.util.ArrayList;


/**
 * This class was introduced to allow multiple close and selection change listeners.
 */
public class SessionTabAdmin
{
   private final Tab _sessionTab;
   private final SessionTabHeaderCtrl _sessionTabHeaderCtrl;

   private ArrayList<EventHandler> _closedListeners = new ArrayList<>();
   private ArrayList<EventHandler> _closeRequestListeners = new ArrayList<>();
   private ArrayList<EventHandler> _selectionChangedListeners = new ArrayList<>();

   public SessionTabAdmin(SessionTabContext sessionTabContext, Node content, SessionTabType tabType)
   {
      _sessionTab = new Tab();

      if(tabType == SessionTabType.SQL_TAB)
      {
         _sessionTabHeaderCtrl = new SessionTabHeaderCtrl(sessionTabContext, StdActionCfg.NEW_SQL_TAB.getActionCfg().getIcon());
      }
      else
      {
         _sessionTabHeaderCtrl = new SessionTabHeaderCtrl(sessionTabContext);
      }


      _sessionTab .setGraphic(_sessionTabHeaderCtrl.getTabHeader());
      _sessionTab .setContent(content);

      _sessionTab.setOnSelectionChanged(e -> fireSelectionChanged(e));
      _sessionTab.setOnCloseRequest(e -> fireCloseRequest(e));
      _sessionTab.setOnClosed(e -> fireClosed(e));
   }

   private <T> void fireClosed(Event event)
   {
      _fireListeners(event, _closedListeners);
   }


   private void fireCloseRequest(Event event)
   {
      _fireListeners(event, _closeRequestListeners);
   }

   private void fireSelectionChanged(Event event)
   {
      _fireListeners(event, _selectionChangedListeners);
   }

   private void _fireListeners(Event event, ArrayList<EventHandler> listeners)
   {
      for (EventHandler listener : listeners.toArray(new EventHandler[listeners.size()]))
      {
         listener.handle(event);
      }
   }


   public SessionTabHeaderCtrl getSessionTabHeaderCtrl()
   {
      return _sessionTabHeaderCtrl;
   }

   public void addOnCloseRequest(EventHandler<Event> eventHandler)
   {
      _closeRequestListeners.add(eventHandler);
   }

   public void addOnClosed(EventHandler<Event> eventHandler)
   {
      _closedListeners.add(eventHandler);
   }

   public void addOnSelectionChanged(EventHandler<Event> eventHandler)
   {
      _selectionChangedListeners.add(eventHandler);
   }

   public boolean isSelected()
   {
      return _sessionTab.isSelected();
   }

   public Tab getTab()
   {
      return _sessionTab;
   }

   public void removeFromTabPane()
   {
      if (null != _sessionTab.getTabPane())
      {
         _sessionTab.getTabPane().getTabs().remove(_sessionTab);
      }
   }
}
