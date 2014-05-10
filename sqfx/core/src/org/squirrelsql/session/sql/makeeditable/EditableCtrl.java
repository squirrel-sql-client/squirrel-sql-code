package org.squirrelsql.session.sql.makeeditable;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.sql.SQLResult;

public class EditableCtrl
{
   private final EditableSqlCheck _editableSqlCheck;
   private I18n _i18n = new I18n(getClass());
   private ToggleButton _btnEdit;


   public EditableCtrl(String sql, SQLResult sqlResult)
   {
      _btnEdit = new ToggleButton();
      _btnEdit.setTooltip(new Tooltip(_i18n.t("outputtab.edit.result")));
      _btnEdit.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.EDIT));

      _editableSqlCheck = new EditableSqlCheck(sql);

      _btnEdit.setDisable(false == _editableSqlCheck.allowsEditing());
   }

   public ToggleButton getEditButton()
   {
      return _btnEdit;
   }
}
