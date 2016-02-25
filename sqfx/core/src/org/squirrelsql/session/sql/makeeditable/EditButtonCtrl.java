package org.squirrelsql.session.sql.makeeditable;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.TableState;
import org.squirrelsql.table.tableedit.EditableTableController;

public class EditButtonCtrl
{
   private final EditableSqlCheck _editableSqlCheck;
   private I18n _i18n = new I18n(getClass());
   private ToggleButton _btnEdit;
   private EditableTableController _editableTableController;
   private Session _session;
   private MenuItem _mnuDeleteRows;


   public EditButtonCtrl(Session session, String sql)
   {
      _session = session;
      _btnEdit = new ToggleButton();
      _btnEdit.setTooltip(new Tooltip(_i18n.t("outputtab.edit.result")));
      _btnEdit.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.EDIT));

      _editableSqlCheck = new EditableSqlCheck(sql);

      _btnEdit.setDisable(false == _editableSqlCheck.allowsEditing());

      _btnEdit.setOnAction(e -> onEditableChanged());
   }

   private void onEditableChanged()
   {
      _editableTableController.setEditable(_btnEdit.isSelected());

      _mnuDeleteRows.setDisable(!_btnEdit.isSelected());

   }

   public ToggleButton getEditButton()
   {
      return _btnEdit;
   }

   public boolean allowsEditing()
   {
      return _editableSqlCheck.allowsEditing();
   }

   public void displayAndPrepareEditing(SQLResult sqlResult, TableView tv, TableState tableState)
   {
      _editableTableController = new EditableTableController(_session, sqlResult, tv, tableState, _editableSqlCheck.getTableNameFromSQL());
   }

   public void deleteSelectedRows()
   {
      _editableTableController.deleteSelectedRows();
   }

   public void setDeleteRowsMenuItem(MenuItem mnuDeleteRows)
   {
      _mnuDeleteRows = mnuDeleteRows;
      _mnuDeleteRows.setDisable(true);
   }
}
