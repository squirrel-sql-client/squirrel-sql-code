package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import org.fife.ui.search.ReplaceDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class SquirrelReplaceDialog extends ReplaceDialog implements ISquirrelSearchDialog
{
   private ArrayList<SearchDialogClosingListener> _closingListeners = new ArrayList<SearchDialogClosingListener>();
   private HashMap<ActionListener, ActionListener> _listener_listenerProxy = new HashMap<ActionListener, ActionListener>();

   public SquirrelReplaceDialog(MainFrame mainFrame)
   {
      super(mainFrame, null /* This ActionListener is not used, propably a bug in RText*/);

      cancelButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            close();
         }
      });

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            fireClosing();
         }
      });
   }

   @Override
   protected void escapePressed()
   {
      fireClosing();
      super.escapePressed();
   }

   @Override
   public void close()
   {
      fireClosing();

      setVisible(false);
      dispose();
   }

   private void fireClosing()
   {
      SearchDialogClosingListener[] listeners = _closingListeners.toArray(new SearchDialogClosingListener[_closingListeners.size()]);

      for (SearchDialogClosingListener listener : listeners)
      {
         listener.searchDialogClosing();
      }
   }

   @Override
   public boolean isMatchCase()
   {
      return caseCheckBox.isSelected();
   }

   @Override
   public boolean isWholeWord()
   {
      return wholeWordCheckBox.isSelected();
   }

   @Override
   public boolean isRegExp()
   {
      return regExpCheckBox.isSelected();
   }

   @Override
   public boolean isSearchUp()
   {
      return upButton.isSelected();
   }

   @Override
   public boolean isMarkAll()
   {
      return markAllCheckBox.isSelected();
   }

   @Override
   public JDialog getDialog()
   {
      return this;
   }

   @Override
   public void addClosingListener(SearchDialogClosingListener l)
   {
      _closingListeners.add(l);
   }

   @Override
   public void removeClosingListener(SearchDialogClosingListener l)
   {
      _closingListeners.remove(l);
   }


   @Override
   public void addFindActionListener(ActionListener actionListener)
   {
      findNextButton.addActionListener(actionListener);
   }

   @Override
   public void removeFindActionListener(ActionListener actionListener)
   {
      findNextButton.removeActionListener(actionListener);
   }

   @Override
   public void addReplaceActionListener(final ActionListener actionListener)
   {
      ActionListener actionListenerProxy = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onReplace(e, actionListener);
         }
      };

      addActionListener(actionListenerProxy);
      _listener_listenerProxy.put(actionListener, actionListenerProxy);
   }

   ///////////////////////////////////////////////////////////////////////////////////
   // Only the action command decides if the replace odr replace all button was hit.
   // The design in RText is an bit funny at this point
   private void onReplace(ActionEvent e, ActionListener actionListener)
   {
      if("Replace".equals(e.getActionCommand()))
      {
         actionListener.actionPerformed(e);         
      }
   }

   private void onReplaceAll(ActionEvent e, ActionListener actionListener)
   {
      if("ReplaceAll".equals(e.getActionCommand()))
      {
         actionListener.actionPerformed(e);
      }
   }
   //
   ///////////////////////////////////////////////////////////////////////////////////


   @Override
   public void removeReplaceActionListener(ActionListener actionListener)
   {
      removeReplaceListener(actionListener);
   }

   private void removeReplaceListener(ActionListener actionListener)
   {
      ActionListener actionListenerProxy = _listener_listenerProxy.remove(actionListener);

      if(null != actionListenerProxy)
      {
         removeActionListener(actionListenerProxy);
      }
   }

   @Override
   public void addReplaceAllActionListener(final ActionListener actionListener)
   {
      ActionListener actionListenerProxy = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onReplaceAll(e, actionListener);
         }
      };

      addActionListener(actionListenerProxy);
      _listener_listenerProxy.put(actionListener, actionListenerProxy);
   }


   @Override
   public void removeReplaceAllActionListener(ActionListener actionListener)
   {
      removeReplaceListener(actionListener);
   }
}
