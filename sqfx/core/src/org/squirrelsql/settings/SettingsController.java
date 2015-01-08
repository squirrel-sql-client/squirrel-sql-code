package org.squirrelsql.settings;

import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;
import org.squirrelsql.table.tableedit.StringInterpreter;

import java.io.File;

public class SettingsController
{
   private final Stage _dialog;
   private final Pref _pref;

   private I18n _i18n = new I18n(getClass());

   private Props _props = new Props(this.getClass());
   private final SettingsView _settingsView;


   public SettingsController()
   {
      _pref = new Pref(getClass());

      FxmlHelper<SettingsView> fxmlHelper = new FxmlHelper<>(SettingsView.class);

      _settingsView = fxmlHelper.getView();


      _settingsView.apProperties.setStyle(GuiUtils.STYLE_GROUP_BORDER);


      _dialog = GuiUtils.createNonModalDialog(fxmlHelper.getRegion(), _pref, 600, 400, "showSettingsController");

      _dialog.setTitle(_i18n.t("showSettings.title"));


      _settingsView.btnSaveStandardProperties.setGraphic(_props.getImageView("save.png"));


      GuiUtils.makeEscapeClosable(fxmlHelper.getRegion());


      Utils.makePositiveIntegerField(_settingsView.txtLimitRowsDefault);

      loadSettingsToUi();


      _settingsView.chkLimitRowsByDefault.setOnAction((e) -> updateUi());

      _settingsView.btnOk.setOnAction((e) -> onOk());
      _settingsView.btnCancel.setOnAction((e) -> onCancel());

      initPropertiesFileInfoGroup();



      _dialog.show();
   }

   private void initPropertiesFileInfoGroup()
   {

      _settingsView.txtPropertiesFileLocation.setEditable(false);

      File propFile = AppState.get().getPropertiesHandler().getPropertiesFile();

      ToggleGroup tg = new ToggleGroup();

      _settingsView.radStandardProps.setToggleGroup(tg);
      _settingsView.radUserDefinedProps.setToggleGroup(tg);


      if(null == propFile)
      {
         _settingsView.radStandardProps.setDisable(false);
         _settingsView.radUserDefinedProps.setDisable(true);
         _settingsView.radStandardProps.setSelected(true);
         _settingsView.radUserDefinedProps.setSelected(false);
         _settingsView.btnSaveStandardProperties.setDisable(false);
         _settingsView.txtPropertiesFileLocation.setText(null);
         _settingsView.txtPropertiesFileLocation.setDisable(true);

         _settingsView.btnSaveStandardProperties.setOnAction(e -> onSaveStandardProperties());
      }
      else
      {
         _settingsView.radStandardProps.setDisable(true);
         _settingsView.radUserDefinedProps.setDisable(false);
         _settingsView.radStandardProps.setSelected(false);
         _settingsView.radUserDefinedProps.setSelected(true);
         _settingsView.btnSaveStandardProperties.setDisable(true);
         _settingsView.txtPropertiesFileLocation.setText(propFile.getAbsolutePath());
      }
   }

   private void onSaveStandardProperties()
   {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

      fileChooser.setTitle(_i18n.t("showSettings.save.standard.props.dialog.title"));

      fileChooser.setInitialFileName("sqfx.properties");

      File file = fileChooser.showSaveDialog(_dialog);

      if(null == file)
      {
         return;
      }

//      String suffix = ".properties";
//      if(false == file.getName().toLowerCase().endsWith(suffix))
//      {
//         file = new File(file.getAbsolutePath() + suffix);
//      }

//      if(file.exists())
//      {
//         String answer = FXMessageBox.showYesNo(_dialog, _i18n.t("showSettings.squirrel.properties.file.exists", file.getAbsolutePath()));
//
//         if(false == FXMessageBox.YES.equals(answer))
//         {
//            return;
//         }
//
//      }

      SquirrelProperty.writeStandardPropertiesToFile(file);

      FXMessageBox.showInfoOk(_dialog, _i18n.t("showSettings.standard.props.saved.to", file.getAbsolutePath()));

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

      if (false == Utils.isEmptyString(_settingsView.txtStatementSeparator.getText()))
      {
         settings.setStatementSeparator(_settingsView.txtStatementSeparator.getText().trim());
      }


      AppState.get().getSettingsManager().writeSettings();

      _dialog.close();
   }

   private void loadSettingsToUi()
   {
      Settings settings = AppState.get().getSettingsManager().getSettings();

      _settingsView.chkMultibleLinesInCells.setSelected(settings.isMultibleLinesInCells());
      _settingsView.chkLimitRowsByDefault.setSelected(settings.isLimitRowsByDefault());
      _settingsView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());

      _settingsView.txtStatementSeparator.setText("" + settings.getStatementSeparator());

      updateUi();
   }

   private void updateUi()
   {
      _settingsView.txtLimitRowsDefault.setDisable(false == _settingsView.chkLimitRowsByDefault.isSelected());
   }
}
