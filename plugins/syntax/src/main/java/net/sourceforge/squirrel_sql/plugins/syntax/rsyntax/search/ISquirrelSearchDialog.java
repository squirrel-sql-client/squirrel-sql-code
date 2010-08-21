package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.*;
import java.util.Vector;

public interface ISquirrelSearchDialog
{
   void setSearchParameters(Vector findComboBoxStrings, boolean matchCase, boolean wholeWord, boolean regExp, boolean searchUp, boolean markAll);

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

   void addReplaceActionListener(ActionListener actionListener);
   void removeReplaceActionListener(ActionListener actionListener);

   void addReplaceAllActionListener(ActionListener actionListener);
   void removeReplaceAllActionListener(ActionListener actionListener);

   String getReplaceString();
}
