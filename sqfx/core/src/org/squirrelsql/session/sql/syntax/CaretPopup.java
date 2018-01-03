package org.squirrelsql.session.sql.syntax;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.AppState;
import org.squirrelsql.services.Utils;

/**
 * Remove whenever CodeArea offers a simple getCaretLocationOnScreen or getCaretBoundsOnScreen method
 */
public class CaretPopup
{
   private static enum PopupAlignment
   {
      CARET_TOP, CARET_BOTTOM;
   }


   private Popup _popup = new Popup();
   private CodeArea _codeArea;

   public CaretPopup(CodeArea codeArea)
   {
      _codeArea = codeArea;
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
      _show(0, 0, PopupAlignment.CARET_TOP);
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

      Bounds caretBounds = _codeArea.getCaretBounds().get();

      double anchorX = caretBounds.getMinX() + popupAnchorOffset.getX();
      double anchorY;

      if (popupAlignment == PopupAlignment.CARET_TOP)
      {
         anchorY = caretBounds.getMinY() + popupAnchorOffset.getY();
      }
      else
      {
         anchorY = caretBounds.getMaxY() + popupAnchorOffset.getY();
      }

      _popup.show(AppState.get().getPrimaryStage(), anchorX, anchorY);

   }

   public void hideAndClearContent()
   {
      // Hide must go together with clear because the Popup needs to be kept attached to the CodeArea
      // and the content has been experienced to keep on firing events even though it's not visible anymore.
      _popup.hide();
      _popup.getContent().clear();
   }

}
