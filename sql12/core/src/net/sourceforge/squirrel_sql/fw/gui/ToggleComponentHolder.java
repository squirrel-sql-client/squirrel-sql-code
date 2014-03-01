package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.*;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;

public class ToggleComponentHolder
{
   private ArrayList<AbstractButton> _toggleables = 
       new ArrayList<AbstractButton>();
   private boolean _selected;

   public boolean isSelected()
   {
      return _selected;
   }

   public void setSelected(boolean b)
   {
      _selected = b;
      
      for (int i = 0; i < _toggleables.size(); i++)
      {
         final AbstractButton abstractButton = _toggleables.get(i);
         GUIUtils.processOnSwingEventThread(new Runnable() {
             public void run() {
                 abstractButton.setSelected(_selected);
             }
         });
      }
   }

   public void addToggleableComponent(AbstractButton toggleable)
   {
      Action origAction = toggleable.getAction();

      if(null == origAction)
      {
         throw new RuntimeException("No Action attached to button");
      }

      toggleable.setAction(new ActionProxy(origAction));

      _toggleables.add(toggleable);
   }

   private void selectionChanged()
   {
      _selected = !_selected;

      for (int i = 0; i < _toggleables.size(); i++)
      {
         AbstractButton abstractButton = _toggleables.get(i);
         abstractButton.setSelected(_selected);
      }
   }




   private class ActionProxy implements Action
   {
      private Action _delegate;

      ActionProxy(Action delegate)
      {
         _delegate = delegate;
      }

      public boolean isEnabled()
      {
         return _delegate.isEnabled();
      }

      public void setEnabled(boolean b)
      {
         _delegate.setEnabled(b);
      }

      public void addPropertyChangeListener(PropertyChangeListener listener)
      {
         _delegate.addPropertyChangeListener(listener);
      }

      public void removePropertyChangeListener(PropertyChangeListener listener)
      {
         _delegate.removePropertyChangeListener(listener);
      }

      public Object getValue(String key)
      {
         return _delegate.getValue(key);
      }

      public void putValue(String key, Object value)
      {
         _delegate.putValue(key, value);
      }

      public void actionPerformed(ActionEvent e)
      {
         selectionChanged();
         _delegate.actionPerformed(e);
      }
   }


}
