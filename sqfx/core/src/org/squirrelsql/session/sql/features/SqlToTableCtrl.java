package org.squirrelsql.session.sql.features;

import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.Session;

public class SqlToTableCtrl
{
   private final Stage _dialog;

   private Pref _pref = new Pref(getClass());

   public static final String PREF_RAD_DROP = "radDrop";
   public static final String PREF_RAD_APPEND = "radAppend";
   public static final String PREF_RAD_DO_NOTHING = "radDoNothing";

   public static final String PREF_SCRIPT_ONLY = "scriptOnly";

   public static final String PREF_LAST_TABLE_NAME = "lastTableName";


   private final SqlToTableView _view;

   public SqlToTableCtrl(Session session)
   {
      FxmlHelper<SqlToTableView> fxmlHelper = new FxmlHelper<>(SqlToTableView.class);

      _dialog = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 420, 260, "SqlToTableView");

      _dialog.setTitle(new I18n(getClass()).t("sql.to.table"));


      ToggleGroup bg = new ToggleGroup();
      _view = fxmlHelper.getView();
      _view.radDrop.setToggleGroup(bg);
      _view.radAppend.setToggleGroup(bg);
      _view.radDoNothing.setToggleGroup(bg);

      _view.radDrop.setSelected(_pref.getBoolean(PREF_RAD_DROP, false));
      _view.radAppend.setSelected(_pref.getBoolean(PREF_RAD_APPEND, false));
      _view.radDoNothing.setSelected(_pref.getBoolean(PREF_RAD_DO_NOTHING, true));

      _view.chkScriptOnly.setSelected(_pref.getBoolean(PREF_SCRIPT_ONLY, false));

      _view.txtTableName.setText(_pref.getString(PREF_LAST_TABLE_NAME, null));

      _dialog.setOnCloseRequest(e -> close());

      _view.btnCancel.setOnAction(e -> close());

      _view.btnOk.setOnAction(e -> onOk());

      _dialog.showAndWait();

   }

   private void onOk()
   {
      System.out.println("SqlToTableCtrl.onOk");

      close();
   }

   private void close()
   {
      _pref.set(PREF_RAD_DROP, _view.radDrop.isSelected());
      _pref.set(PREF_RAD_APPEND, _view.radAppend.isSelected());
      _pref.set(PREF_RAD_DO_NOTHING, _view.radDoNothing.isSelected());

      _pref.set(PREF_SCRIPT_ONLY, _view.chkScriptOnly.isSelected());

      _pref.set(PREF_LAST_TABLE_NAME, _view.txtTableName.getText());
      _dialog.close();

   }
}
