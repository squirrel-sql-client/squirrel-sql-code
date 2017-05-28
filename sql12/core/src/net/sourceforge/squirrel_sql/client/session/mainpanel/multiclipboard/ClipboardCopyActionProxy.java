package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
      try
      {
         _delegate.actionPerformed(e);

         Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
         Transferable contents = clip.getContents(null);

         String clipContent =
               (String)contents.getTransferData(DataFlavor.stringFlavor);


         _clipboardCopyActionProxyListener.copyToClipboard(clipContent);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }
}
