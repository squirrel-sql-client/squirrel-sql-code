package org.squirrelsql.settings;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;
import org.squirrelsql.table.tableedit.StringInterpreter;

public class SettingsController
{
   private final Stage _dialog;

   private I18n _i18n = new I18n(getClass());

   private Props _props = new Props(this.getClass());
   private final SettingsView _settingsView;


   public SettingsController()
   {

      FxmlHelper<SettingsView> fxmlHelper = new FxmlHelper<>(SettingsView.class);

      _settingsView = fxmlHelper.getView();


      _settingsView.apProperties.setStyle(GuiUtils.STYLE_GROUP_BORDER);


      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("showSettings.title"));
      _dialog.initModality(Modality.NONE);
      _dialog.initOwner(AppState.get().getPrimaryStage());

      _dialog.setScene(new Scene(fxmlHelper.getRegion()));


      _settingsView.btnSaveStandardProperties.setGraphic(_props.getImageView("save.png"));

      new StageDimensionSaver("showSettingsController", _dialog, new Pref(getClass()), 500, 600, _dialog.getOwner());

      GuiUtils.makeEscapeClosable(fxmlHelper.getRegion());

      loadSettingsToUi();

      _settingsView.chkLimitRowsByDefault.setOnAction((e) -> updateUi());

      _settingsView.btnOk.setOnAction((e) -> onOk());
      _settingsView.btnCancel.setOnAction((e) -> onCancel());

      _dialog.show();
   }

   private void onCancel()
   {
      _dialog.close();
   }

   private void onOk()
   {
      Settings settings = AppState.get().getSettingsManager().getSettings();
      settings.setMultibleLinesInCells(_settingsView.chkMultibleLinesInCells.isSelected());
      settings.setLimitRowsByDefault(_settingsView.chkLimitRowsByDefault.isSelected());

      String buf = _settingsView.txtLimitRowsDefault.getText();
      settings.setLimitRowsDefault(StringInterpreter.interpret(buf, Integer.class, settings.getLimitRowsDefault()));
      _settingsView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());


      AppState.get().getSettingsManager().writeSettings();

      _dialog.close();
   }

   private void loadSettingsToUi()
   {
      Settings settings = AppState.get().getSettingsManager().getSettings();

      _settingsView.chkMultibleLinesInCells.setSelected(settings.isMultibleLinesInCells());
      _settingsView.chkLimitRowsByDefault.setSelected(settings.isLimitRowsByDefault());
      _settingsView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());

      updateUi();
   }

   private void updateUi()
   {
      _settingsView.txtLimitRowsDefault.setDisable(false == _settingsView.chkLimitRowsByDefault.isSelected());
   }
}
