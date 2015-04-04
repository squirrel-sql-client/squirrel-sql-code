package org.squirrelsql.session.sql.syntax;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.PopupAlignment;
import org.squirrelsql.AppState;
import org.squirrelsql.services.Utils;

/**
 * Remove whenever CodeArea offers a simple getCaretLocationOnScreen or getCaretBoundsOnScreen method
 */
public class CaretPopup
{
   private Popup _popup = new Popup();
   private CodeArea _codeArea;

   public CaretPopup(CodeArea codeArea)
   {
      _codeArea = codeArea;
      _codeArea.setPopupWindow(_popup);
   }

   public Popup getPopup()
   {
      return _popup;
   }

   public void setContent(Node content)
   {
      _popup.getContent().add(content);
   }

   public void showAtCaretTop()
   {
      // -9 is a correction to make the top left of the popup cleanly cover the the CodeArea's caret.
      _show(0, -9, PopupAlignment.CARET_TOP);
   }

   public void showAtCaretBottom(double xDisplacementFromCaret)
   {
      _show(xDisplacementFromCaret, 0, PopupAlignment.CARET_BOTTOM);
   }

   private void _show(double xDisplacementFromCaret, double yDisplacementFromCaret, PopupAlignment popupAlignment)
   {
      // -1 is a correction to make the top left of the popup cleanly cover the the CodeArea's caret.
      double xDisplacementFromCaretBuf = -1;
      double yDisplacementFromCaretBuf = 0;


      if (false == Utils.isZero(xDisplacementFromCaret))
      {
         xDisplacementFromCaretBuf = xDisplacementFromCaret;
      }

      if (false == Utils.isZero(yDisplacementFromCaret))
      {
         yDisplacementFromCaretBuf = yDisplacementFromCaret;
      }

      Point2D popupAnchorOffset = new Point2D(xDisplacementFromCaretBuf, yDisplacementFromCaretBuf);

      _codeArea.setPopupAnchorOffset(popupAnchorOffset);
      _codeArea.setPopupAlignment(popupAlignment);

      makePopupAlignmentWork();

      _popup.show(AppState.get().getPrimaryStage());


   }

   /**
    * This is a workaround for issue #128 of RichtextFx.
    * See: https://github.com/TomasMikula/RichTextFX/issues/128
    */
   private void makePopupAlignmentWork()
   {
      int caretPosition = _codeArea.getCaretPosition();

      if(0 != caretPosition)
      {
         _codeArea.positionCaret(caretPosition - 1);
         _codeArea.positionCaret(caretPosition);
      }
      else
      {
         _codeArea.positionCaret(caretPosition + 1);
         _codeArea.positionCaret(caretPosition);
      }
   }

   public void hideAndClearContent()
   {
      // Hide must go together with clear because the Popup needs to be kept attached to the CodeArea
      // and the content has been experienced to keep on firing events even though it's not visible anymore.
      _popup.hide();
      _popup.getContent().clear();
   }

}
