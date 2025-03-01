package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.search;


import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class SquirrelFindDialog extends FindDialog implements ISquirrelSearchDialog
{
   private ArrayList<SearchDialogClosingListener> _closingListeners = new ArrayList<SearchDialogClosingListener>();

   public SquirrelFindDialog(Frame owner, final SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      super(owner, new SearchListener()
      {
         @Override
         public void searchEvent(SearchEvent searchEvent)
         {
            //System.out.println("SquirrelFindDialog.searchEvent " + searchEvent);
         }

         @Override
         public String getSelectedText()
         {
            return squirrelRSyntaxTextArea.getSelectedText();
         }
      });

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
            LastFindHelper.storeLastFinds(SquirrelFindDialog.this.findTextCombo);
            fireClosing();
         }
      });


      LastFindHelper.loadLastFinds(super.findTextCombo);


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
      return null;
   }
}
