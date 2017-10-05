package org.squirrelsql.session.sql.features;

import javafx.application.Platform;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.Utils;
import org.squirrelsql.services.progress.Progressable;
import org.squirrelsql.services.progress.SimpleProgressCtrl;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class SqlToTableCtrl
{
   public static final String PREF_RAD_DROP = "radDrop";
   public static final String PREF_RAD_APPEND = "radAppend";
   public static final String PREF_RAD_DO_NOTHING = "radDoNothing";

   public static final String PREF_SCRIPT_ONLY = "scriptOnly";

   public static final String PREF_LAST_TABLE_NAME = "lastTableName";

   private Stage _dialog;
   private Session _session;
   private I18n _i18n = new I18n(getClass());
   private SQLTextAreaServices _sqlTextAreaServices;

   private Pref _pref = new Pref(getClass());

   private SqlToTableView _view;

   public SqlToTableCtrl(Session session, SQLTextAreaServices sqlTextAreaServices)
   {


      _session = session;
      _sqlTextAreaServices = sqlTextAreaServices;
      FxmlHelper<SqlToTableView> fxmlHelper = new FxmlHelper<>(SqlToTableView.class);

      String sql = _sqlTextAreaServices.getCurrentSql();

      if (Utils.isEmptyString(sql))
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("sql.to.table.no.sql.selected"));
         return;
      }



      _dialog = GuiUtils.createModalDialog(fxmlHelper.getRegion(), new Pref(getClass()), 420, 260, "SqlToTableView");

      _dialog.setTitle(_i18n.t("sql.to.table"));


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
      String sql = _sqlTextAreaServices.getCurrentSql();

      SqlToTable.TableExistsOption opt = getTableExistsOption();

      close();

      Platform.runLater(() -> startProgress(sql, opt, _view.chkScriptOnly.isSelected()));

   }

   private void startProgress(String sql, SqlToTable.TableExistsOption opt, boolean scriptOnly)
   {
      SimpleProgressCtrl simpleProgressCtrl = new SimpleProgressCtrl();
      simpleProgressCtrl.start(() -> exportToTable(simpleProgressCtrl.getProgressable(), sql, opt, scriptOnly));
   }

   private void exportToTable(Progressable progressable, String sql, SqlToTable.TableExistsOption opt, boolean scriptOnly)
   {
      SqlToTable sqlToTable = new SqlToTable(progressable, _session, _sqlTextAreaServices);

      sqlToTable.exportToTable(sql, _view.txtTableName.getText(), opt, scriptOnly);

      if(scriptOnly && null != sqlToTable.getScript())
      {
         Platform.runLater(() -> _sqlTextAreaServices.appendToEditor("\n\n" + sqlToTable.getScript()));
      }
   }

   private SqlToTable.TableExistsOption getTableExistsOption()
   {
      SqlToTable.TableExistsOption opt = SqlToTable.TableExistsOption.DO_NOTHING;

      if(_view.radDrop.isSelected())
      {
         opt = SqlToTable.TableExistsOption.DROP;
      }
      else if(_view.radAppend.isSelected())
      {
         opt = SqlToTable.TableExistsOption.APPEND;
      }
      return opt;
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
