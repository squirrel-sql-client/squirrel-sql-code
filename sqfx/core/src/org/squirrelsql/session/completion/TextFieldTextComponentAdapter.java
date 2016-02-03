package org.squirrelsql.session.completion;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import org.squirrelsql.session.sql.syntax.LexAndParseResultListener;

public class TextFieldTextComponentAdapter implements TextComponentAdapter
{
   private TextField _txtText;
   private ChangeListener<Boolean> _popupFocusListener;
   private Popup _popup = new Popup();

   public TextFieldTextComponentAdapter(TextField txtText)
   {
      _txtText = txtText;
   }

   @Override
   public void setLexAndParseResultListener(LexAndParseResultListener lexAndParseResultListener)
   {
   }

   @Override
   public void calculateTableNextToCaret()
   {
   }

   @Override
   public String getTokenTillCaret(char... stopsToIgnore)
   {
      return _txtText.getText().substring(0, _txtText.getCaretPosition());
   }

   @Override
   public String getLineTillCaret()
   {
      return _txtText.getText();
   }

   @Override
   public void setPopupFocusListener(ChangeListener<Boolean> popupFocusListener)
   {
      if (null != _popupFocusListener)
      {
         _popup.focusedProperty().removeListener(_popupFocusListener);
      }

      _popupFocusListener = popupFocusListener;
      _popup.focusedProperty().addListener(_popupFocusListener);
   }

   @Override
   public Font getFont()
   {
      return _txtText.getFont();
   }

   @Override
   public void showPopup(ListView<CompletionCandidate> popupContent, CaretVicinity caretVicinity)
   {
      _popup.getContent().clear();
      _popup.getContent().add(popupContent);

      Point2D localToScene = _txtText.localToScreen(0, _txtText.getHeight());
      _popup.show(_txtText, localToScene.getX(), localToScene.getY());
   }

   @Override
   public void closePopup()
   {
      _popup.hide();
   }

   @Override
   public void replaceJoinGeneratorAtCaretBy(String replacement)
   {
   }

   @Override
   public void replaceTokenAtCaretBy(int completedSplitsStringLength, boolean removeSucceedingChars, String replacement)
   {
      _txtText.setText(replacement);
      _txtText.positionCaret(replacement.length());
   }
}
