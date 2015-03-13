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
      _show(0, PopupAlignment.CARET_TOP);
   }

   public void showAtCaretBottom(double xDisplacementFromCaret)
   {
      _show(xDisplacementFromCaret, PopupAlignment.CARET_BOTTOM);
   }

   private void _show(double xDisplacementFromCaret, PopupAlignment popupAlignment)
   {
      Point2D popupAnchorOffset = new Point2D(0,0);

      if (false == Utils.isZero(xDisplacementFromCaret))
      {
         popupAnchorOffset = new Point2D(xDisplacementFromCaret, 0);
      }

      _codeArea.setPopupAlignment(popupAlignment);
      _codeArea.setPopupAnchorOffset(popupAnchorOffset);
      _popup.show(AppState.get().getPrimaryStage());
   }

   public void hideAndClearContent()
   {
      // Hide must go together with clear because the Popup needs to be kept attached to the CodeArea
      // and the content has been experienced to keep on firing events even though it's not visible anymore.
      _popup.hide();
      _popup.getContent().clear();
   }

}
