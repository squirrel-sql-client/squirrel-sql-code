package net.sourceforge.squirrel_sql.client.gui.db.recentalias;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class RecentAliasListActionProxy implements Action
{
   private Action _delegate;
   private ActionListener _actionListener;

   public RecentAliasListActionProxy(Action delegate, ActionListener actionListener)
   {
      _delegate = delegate;
      _actionListener = actionListener;
   }

   @Override
   public Object getValue(String key)
   {
      return _delegate.getValue(key);
   }

   @Override
   public void putValue(String key, Object value)
   {
   }

   @Override
   public void setEnabled(boolean b)
   {
   }

   @Override
   public boolean isEnabled()
   {
      return true;
   }

   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      _actionListener.actionPerformed(e);
   }
}
