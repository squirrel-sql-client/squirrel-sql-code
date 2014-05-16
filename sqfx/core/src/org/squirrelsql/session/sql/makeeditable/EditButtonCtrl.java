package org.squirrelsql.session.sql.makeeditable;

import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.EdittableTableController;
import org.squirrelsql.table.TableLoader;

public class EditButtonCtrl
{
   private final EditableSqlCheck _editableSqlCheck;
   private I18n _i18n = new I18n(getClass());
   private ToggleButton _btnEdit;
   private EdittableTableController _edittableTableController;


   public EditButtonCtrl(String sql, SQLResult sqlResult)
   {
      _btnEdit = new ToggleButton();
      _btnEdit.setTooltip(new Tooltip(_i18n.t("outputtab.edit.result")));
      _btnEdit.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.EDIT));

      _editableSqlCheck = new EditableSqlCheck(sql);

      _btnEdit.setDisable(false == _editableSqlCheck.allowsEditing());

      _btnEdit.setOnAction(e -> onEditableChanged());
   }

   private void onEditableChanged()
   {
      _edittableTableController.setEditable(_btnEdit.isSelected());
   }

   public ToggleButton getEditButton()
   {
      return _btnEdit;
   }

   public boolean allowsEditing()
   {
      return _editableSqlCheck.allowsEditing();
   }

   public void displayAndPrepareEditing(TableLoader tableLoader, TableView tv)
   {
      _edittableTableController = new EdittableTableController(tableLoader, tv);
   }
}
