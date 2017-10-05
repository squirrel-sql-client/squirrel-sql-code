package org.squirrelsql.table;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
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
   private Stage _dialog;


   public CellDataPopupController(Object item, MouseEvent event, TableRow tableRow, TablePosition tablePosition)
   {
      this(item, event, tableRow, tablePosition, null);
   }

   public CellDataPopupController(Object item, MouseEvent event, TableRow tableRow, TablePosition tablePosition, CellDataPopupEditListener cellDataPopupEditListener)
   {
      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("cellPopupController.title", tablePosition.getTableColumn().getText(), tableRow.getIndex() + 1));
      _dialog.initModality(Modality.NONE);
      _dialog.initOwner(AppState.get().getPrimaryStage());

      new StageDimensionSaver("cellPopupController", _dialog, new Pref(getClass()), 300, 200, _dialog.getOwner());

      _oldValue = item;

      _textArea = new TextArea(interpretCellContentAsSting(_oldValue));

      appendEntriesToStandardTextAreaContextMenu(_textArea);

      if (null == cellDataPopupEditListener)
      {
         _textArea.setEditable(false);
         _dialog.setScene(new Scene(_textArea));
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

         _dialog.setScene(new Scene(bp));
      }


      _dialog.setX(Math.max(event.getScreenX() - 50, 0));
      _dialog.setY(Math.max(event.getScreenY() - 50, 0));

      event.consume();

      AppState.get().addApplicationCloseListener(_dialog::close, ApplicationCloseListener.FireTime.AFTER_SESSION_FIRE_TIME);

      _textArea.requestFocus();
      GuiUtils.makeEscapeClosable(_textArea);

      _dialog.show();
   }

   private void appendEntriesToStandardTextAreaContextMenu(TextArea textArea)
   {
      MenuItem menuItem = new MenuItem(_i18n.t("cellPopupController.reformat.xml.json"));
      menuItem.setOnAction( e -> reformatXmlJson());

      GuiUtils.addContextMenuItemToStandardTextAreaMenu(textArea, menuItem);
   }

   private void reformatXmlJson()
   {
      XmlFormatter xmlFormatter = new XmlFormatter(_textArea.getText());
      if(xmlFormatter.success())
      {
         _textArea.setText(xmlFormatter.getFormattedXml());
         return;
      }

      JsonFormatter jsonFormatter = new JsonFormatter(_textArea.getText());
      if(jsonFormatter.success())
      {
         _textArea.setText(jsonFormatter.getFormattedJson());
         return;
      }
   }


   private String interpretCellContentAsSting(Object item)
   {
      return item.toString();
   }

   private void onUpdateData(Object item, TableRow tableRow, TablePosition tablePosition, CellDataPopupEditListener cellDataPopupEditListener, TextArea textArea)
   {
      DatabaseTableUpdateResult databaseTableUpdateResult = cellDataPopupEditListener.updateData(new SquirrelTableEditData(textArea.getText(), item, tablePosition, (List) tableRow.getItem()));

//      if(databaseTableUpdateResult.success())
//      {
//         _oldValue = databaseTableUpdateResult.getInterpretedNewValue();
//      }

      _dialog.close();


   }
}
