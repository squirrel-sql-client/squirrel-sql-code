package org.squirrelsql.table;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.ApplicationCloseListener;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.StageDimensionSaver;
import org.squirrelsql.table.tableedit.CellDataPopupEditListener;
import org.squirrelsql.table.tableedit.DatabaseTableUpdateResult;
import org.squirrelsql.table.tableedit.SquirrelTableEditData;

import java.util.List;

public class CellDataPopupController
{
   private Object _oldValue;
   private I18n _i18n = new I18n(getClass());
   private TextArea _textArea;


   public CellDataPopupController(Object item, MouseEvent event, TableRow tableRow, TablePosition tablePosition)
   {
      this(item, event, tableRow, tablePosition, null);
   }

   public CellDataPopupController(Object item, MouseEvent event, TableRow tableRow, TablePosition tablePosition, CellDataPopupEditListener cellDataPopupEditListener)
   {
      Stage dialog = new Stage();
      dialog.setTitle(_i18n.t("cellPopupController.title", tablePosition.getTableColumn().getText(), tableRow.getIndex() + 1));
      dialog.initModality(Modality.NONE);
      dialog.initOwner(AppState.get().getPrimaryStage());

      new StageDimensionSaver("cellPopupController", dialog, new Pref(getClass()), 300, 200, dialog.getOwner());

      _oldValue = item;

      _textArea = new TextArea(interpretCellContentAsSting(_oldValue));

      if (null == cellDataPopupEditListener)
      {
         _textArea.setEditable(false);
         dialog.setScene(new Scene(_textArea));
      }
      else
      {
         _textArea.setEditable(true);

         BorderPane.setMargin(_textArea, new Insets(0,0,10,0));

         BorderPane bp = new BorderPane(_textArea);

         bp.setPadding(new Insets(5));



         Button btnUpdate = new Button(_i18n.t("cellPopupController.updateCellData"));
         bp.setBottom(btnUpdate);

         btnUpdate.setOnAction(e -> onUpdateData(_oldValue, tableRow, tablePosition, cellDataPopupEditListener, _textArea));

         dialog.setScene(new Scene(bp));
      }


      dialog.setX(Math.max(event.getScreenX() - 50, 0));
      dialog.setY(Math.max(event.getScreenY() - 50, 0));

      event.consume();

      AppState.get().addApplicationCloseListener(dialog::close, ApplicationCloseListener.FireTime.AFTER_SESSION_FIRE_TIME );

      _textArea.requestFocus();
      GuiUtils.makeEscapeClosable(_textArea);

      dialog.show();
   }

   private String interpretCellContentAsSting(Object item)
   {
      return item.toString();
   }

   private void onUpdateData(Object item, TableRow tableRow, TablePosition tablePosition, CellDataPopupEditListener cellDataPopupEditListener, TextArea textArea)
   {
      DatabaseTableUpdateResult databaseTableUpdateResult = cellDataPopupEditListener.updateData(new SquirrelTableEditData(textArea.getText(), item, tablePosition, (List) tableRow.getItem()));

      if(databaseTableUpdateResult.success())
      {
         _oldValue = databaseTableUpdateResult.getInterpretedNewValue();
      }
   }
}
