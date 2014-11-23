package org.squirrelsql.table;

import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.ApplicationCloseListener;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.StageDimensionSaver;

public class CellPopupController
{
   private I18n _i18n = new I18n(getClass());


   public CellPopupController(Object item, MouseEvent event, TableColumn<Object, Object> tableColumn, TableRow tableRow)
   {
      Stage dialog = new Stage();
      dialog.setTitle(_i18n.t("cellPopupController.title", tableColumn.getText(), tableRow.getIndex() + 1));
      dialog.initModality(Modality.NONE);
      dialog.initOwner(AppState.get().getPrimaryStage());

      new StageDimensionSaver("cellPopupController", dialog, new Pref(getClass()), 300, 200, dialog.getOwner());

      TextArea textArea = new TextArea(item.toString());
      textArea.setEditable(false);

      dialog.setScene(new Scene(textArea));

      dialog.setX(Math.max(event.getScreenX() - 50, 0));
      dialog.setY(Math.max(event.getScreenY() - 50, 0));

      event.consume();

      AppState.get().addApplicationCloseListener(dialog::close, ApplicationCloseListener.FireTime.AFTER_SESSION_FIRE_TIME );

      dialog.show();
   }
}
