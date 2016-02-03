package org.squirrelsql.session.completion;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.session.sql.syntax.LexAndParseResultListener;

public class SQLTextAreaTextComponentAdapter implements TextComponentAdapter
{
   SQLTextAreaServices _sqlTextAreaServices;

   public SQLTextAreaTextComponentAdapter(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;
   }

   @Override
   public void setLexAndParseResultListener(LexAndParseResultListener lexAndParseResultListener)
   {
      _sqlTextAreaServices.setLexAndParseResultListener(lexAndParseResultListener);
   }

   @Override
   public void calculateTableNextToCaret()
   {
      _sqlTextAreaServices.calculateTableNextToCaret();
   }

   @Override
   public String getTokenTillCaret(char... stopsToIgnore)
   {
      return _sqlTextAreaServices.getTokenTillCaret(stopsToIgnore);
   }

   @Override
   public String getLineTillCaret()
   {
      return _sqlTextAreaServices.getLineTillCaret();
   }

   @Override
   public void setPopupFocusListener(ChangeListener<Boolean> listener)
   {
      _sqlTextAreaServices.getCaretPopup().getPopup().focusedProperty().addListener(listener);
   }

   @Override
   public Font getFont()
   {
      return _sqlTextAreaServices.getFont();
   }

   @Override
   public void showPopup(ListView<CompletionCandidate> popupContent, CaretVicinity caretVicinity)
   {
      _sqlTextAreaServices.getCaretPopup().setContent(popupContent);
      positionAndShowPopup(caretVicinity);
   }

   private void positionAndShowPopup(CaretVicinity caretVicinity)
   {
      String uncompletedSplit = caretVicinity.getUncompletedSplit();
      _sqlTextAreaServices.getCaretPopup().showAtCaretBottom(-_sqlTextAreaServices.getStringWidth(uncompletedSplit));
   }

   @Override
   public void closePopup()
   {
      _sqlTextAreaServices.getCaretPopup().hideAndClearContent();
   }

   @Override
   public void replaceJoinGeneratorAtCaretBy(String replacement)
   {
      _sqlTextAreaServices.replaceJoinGeneratorAtCaretBy(replacement);
   }

   @Override
   public void replaceTokenAtCaretBy(int completedSplitsStringLength, boolean removeSucceedingChars, String replacement)
   {
      _sqlTextAreaServices.replaceTokenAtCaretBy(completedSplitsStringLength, removeSucceedingChars, replacement);
   }
}
