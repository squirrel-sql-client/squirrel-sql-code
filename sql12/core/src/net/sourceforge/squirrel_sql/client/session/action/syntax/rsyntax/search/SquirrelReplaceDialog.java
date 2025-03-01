package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.search;


import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;

import javax.swing.JDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class SquirrelReplaceDialog extends ReplaceDialog implements ISquirrelSearchDialog
{
   private ArrayList<SearchDialogClosingListener> _closingListeners = new ArrayList<SearchDialogClosingListener>();
   private ArrayList<ActionListener> _replaceListeners = new ArrayList<ActionListener>();
   private ArrayList<ActionListener> _replaceAllListeners = new ArrayList<ActionListener>();
   private SquirrelRSyntaxTextArea _squirrelRSyntaxTextArea;

   public SquirrelReplaceDialog(Frame owner, final SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      ////////////////////////////////////////////////////////////////////////////////////////////////////
      // SearchListenerProxy is only needed because the listener has to be passed to constructor.
      super(owner, new SearchListenerProxy());
      SearchListenerProxy proxy = (SearchListenerProxy) this.searchListener;
      proxy.setDelegate(this);
      //
      ////////////////////////////////////////////////////////////////////////////////////////////////////

      _squirrelRSyntaxTextArea = squirrelRSyntaxTextArea;

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
            onClosing();
         }

      });

      LastFindHelper.loadLastFinds(super.findTextCombo);
   }

   private void onClosing()
   {
      LastFindHelper.storeLastFinds(super.findTextCombo);
      fireClosing();
   }


   @Override
   protected void escapePressed()
   {
      close();
      super.escapePressed();
   }

   @Override
   public void close()
   {
      LastFindHelper.storeLastFinds(super.findTextCombo);
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
      return getSearchContext().isRegularExpression();
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
      _replaceListeners.remove(actionListener);
      _replaceListeners.add(actionListener);
   }

   private void onReplace()
   {
      fireActionListeners(_replaceListeners);
   }

   private void onReplaceAll()
   {
      fireActionListeners(_replaceAllListeners);
   }

   private void fireActionListeners(ArrayList<ActionListener> replacelisteners)
   {
      ActionListener[] listeners = replacelisteners.toArray(new ActionListener[replacelisteners.size()]);
      for (ActionListener listener : listeners)
      {
         listener.actionPerformed(null);
      }
   }


   @Override
   public void removeReplaceActionListener(ActionListener actionListener)
   {
      _replaceListeners.remove(actionListener);
   }

   private void removeReplaceListener(ActionListener actionListener)
   {
      _replaceAllListeners.remove(actionListener);
   }

   @Override
   public void addReplaceAllActionListener(final ActionListener actionListener)
   {
      _replaceAllListeners.remove(actionListener);
      _replaceAllListeners.add(actionListener);
   }


   @Override
   public void removeReplaceAllActionListener(ActionListener actionListener)
   {
      removeReplaceListener(actionListener);
   }

   /**
    * This class is only needed because the listener has to be passed to constructor.
    */
   private static class SearchListenerProxy implements SearchListener
   {
      private SquirrelReplaceDialog _delegate;

      @Override
      public void searchEvent(SearchEvent searchEvent)
      {
         if(SearchEvent.Type.REPLACE == searchEvent.getType())
         {
            _delegate.onReplace();
         }
         else if(SearchEvent.Type.REPLACE_ALL == searchEvent.getType())
         {
            _delegate.onReplaceAll();
         }
      }

      @Override
      public String getSelectedText()
      {
         return _delegate._squirrelRSyntaxTextArea.getSelectedText();
      }

      public void setDelegate(SquirrelReplaceDialog squirrelReplaceDialog)
      {

         _delegate = squirrelReplaceDialog;
      }
   }
}
