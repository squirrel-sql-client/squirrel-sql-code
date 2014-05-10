package org.squirrelsql.session.sql;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.I18n;

public class EditableCtrl
{
   private I18n _i18n = new I18n(getClass());
   private ToggleButton _btnEdit;


   public EditableCtrl(String sql, SQLResult sqlResult)
   {
      _btnEdit = new ToggleButton();
      _btnEdit.setTooltip(new Tooltip(_i18n.t("outputtab.edit.result")));
      _btnEdit.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.EDIT));
   }

   ToggleButton getEditButton()
   {
      return _btnEdit;
   }
}
