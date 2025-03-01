package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.search;

import javax.swing.JDialog;
import java.awt.event.ActionListener;

public interface ISquirrelSearchDialog
{
   //void setSearchParameters(Vector findComboBoxStrings, boolean matchCase, boolean wholeWord, boolean regExp, boolean searchUp, boolean markAll);
   //public void setSearchContext(org.fife.rsta.ui.search.SearchDialogSearchContext context);

   void setSearchString(String selectedText);

   void setVisible(boolean b);

   String getSearchString();

   void close();

   boolean isMatchCase();

   boolean isWholeWord();

   boolean isRegExp();

   boolean isSearchUp();

   boolean isMarkAll();

   JDialog getDialog();

   void addClosingListener(SearchDialogClosingListener searchDialogClosingListener);
   void removeClosingListener(SearchDialogClosingListener searchDialogClosingListener);

   void requestFocus();

   void addFindActionListener(ActionListener actionListener);
   void removeFindActionListener(ActionListener actionListener);

   String getReplaceString();

   void addReplaceActionListener(ActionListener actionListener);
   void removeReplaceActionListener(ActionListener actionListener);

   void addReplaceAllActionListener(ActionListener actionListener);
   void removeReplaceAllActionListener(ActionListener actionListener);

}
