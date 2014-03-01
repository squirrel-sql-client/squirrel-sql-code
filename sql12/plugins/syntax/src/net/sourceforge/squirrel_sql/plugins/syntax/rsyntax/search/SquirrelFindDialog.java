package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;


import org.fife.rsta.ui.search.FindDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SquirrelFindDialog extends FindDialog implements ISquirrelSearchDialog
{
   private ArrayList<SearchDialogClosingListener> _closingListeners = new ArrayList<SearchDialogClosingListener>();

   public SquirrelFindDialog(Frame owner)
   {
      super(owner, null /* This ActionListener is not used, propably a bug in RText*/);

      super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      
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
      close();
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
   public void addReplaceActionListener(ActionListener actionListener)
   {
   }

   @Override
   public void removeReplaceActionListener(ActionListener actionListener)
   {
   }

   @Override
   public void addReplaceAllActionListener(ActionListener actionListener)
   {
   }

   @Override
   public void removeReplaceAllActionListener(ActionListener actionListener)
   {
   }

   @Override
   public String getReplaceString()
   {
      throw new UnsupportedOperationException("Only available in SquirrelReplaceDialog");
   }
}
