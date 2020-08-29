package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public class ClipboardCopyActionProxy implements Action
{
   private final Action _delegate;
   private final ClipboardCopyActionProxyListener _clipboardCopyActionProxyListener;

   public ClipboardCopyActionProxy(Action delegate, ClipboardCopyActionProxyListener clipboardCopyActionProxyListener)
   {
      _delegate = delegate;
      _clipboardCopyActionProxyListener = clipboardCopyActionProxyListener;
   }

   @Override
   public Object getValue(String key)
   {
      return _delegate.getValue(key);
   }

   @Override
   public void putValue(String key, Object value)
   {
      _delegate.putValue(key, value);
   }

   @Override
   public void setEnabled(boolean b)
   {
      _delegate.setEnabled(b);
   }

   @Override
   public boolean isEnabled()
   {
      return _delegate.isEnabled();
   }

   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      _delegate.addPropertyChangeListener(listener);
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      _delegate.removePropertyChangeListener(listener);
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      _delegate.actionPerformed(e);

      String clipContent = ClipboardUtil.getClipboardAsString();

      _clipboardCopyActionProxyListener.copyToClipboard(clipContent);
   }

}
