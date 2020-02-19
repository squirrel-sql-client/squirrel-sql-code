package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import javax.swing.*;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class ToggleComponentHolder
{
   private ArrayList<AbstractButton> _toggleables = new ArrayList<>();
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
         GUIUtils.processOnSwingEventThread(() -> abstractButton.setSelected(_selected));
      }
   }

   public void addToggleableComponent(AbstractButton toggleable)
   {
      addToggleableComponent(toggleable, null);
   }

   public void addToggleableComponent(AbstractButton toggleable, ISession session)
   {
      Action origAction = toggleable.getAction();

      if(null == origAction)
      {
         throw new RuntimeException("No Action attached to button");
      }

      // Replacing the action removes the accelerator.
      // We buffer it and set it back afterwards.
      KeyStroke accelerator = null;
      if(toggleable instanceof JMenuItem)
      {
         accelerator = ((JMenuItem) toggleable).getAccelerator();
      }

      toggleable.setAction(new ActionProxy(origAction));

      if (null != accelerator)
      {
         ((JMenuItem) toggleable).setAccelerator(accelerator);
      }

      _toggleables.add(toggleable);

      if(null != session)
      {
         // needed to prevent memory leaks.
         session.addSimpleSessionListener(() -> _toggleables.remove(toggleable));
      }
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

   public void doClick()
   {
      _toggleables.get(0).doClick();
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
