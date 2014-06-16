package org.squirrelsql.session.sql.syntax;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.MouseOverTextEvent;
import org.squirrelsql.session.parser.kernel.ErrorInfo;

import java.time.Duration;

public class ErrorToolTipHandler
{
   private CodeArea _sqlTextArea;
   private ErrorInfo[] _errorInfos = new ErrorInfo[0];

   public ErrorToolTipHandler(CodeArea sqlTextArea)
   {
      _sqlTextArea = sqlTextArea;

      _sqlTextArea.setMouseOverTextDelay(Duration.ofSeconds(1));

      Popup popup = new Popup();
      Label popupMsg = new Label();
      popupMsg.setStyle(
            "-fx-background-color: salmon;" +
                  "-fx-text-fill: black;" +
                  "-fx-padding: 5;");
      popup.getContent().add(popupMsg);

      _sqlTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
         onMouseOverTextBegin(popup, popupMsg, e);
      });

      _sqlTextArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
         popup.hide();
      });

   }

   private void onMouseOverTextBegin(Popup popup, Label popupMsg, MouseOverTextEvent e)
   {
      int chIdx = e.getCharacterIndex();

      for (ErrorInfo errorInfo : _errorInfos)
      {
         if(errorInfo.beginPos <= chIdx && chIdx <= errorInfo.endPos)
         {
            Point2D pos = e.getScreenPosition();
            popupMsg.setText(errorInfo.message);
            popup.show(_sqlTextArea, pos.getX(), pos.getY() + 10);
            break;
         }
      }
   }

   public void setErrorInfos(ErrorInfo[] errorInfos)
   {
      _errorInfos = errorInfos;
   }
}
