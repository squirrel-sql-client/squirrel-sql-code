package org.squirrelsql.settings;

import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;
import org.squirrelsql.table.tableedit.StringInterpreter;

import java.io.File;

public class GeneralCtrl implements SettingsTabController
{
   private final Pref _pref;
   private final Region _tabContent;

   private I18n _i18n = new I18n(getClass());

   private Props _props = new Props(this.getClass());
   private final GeneralView _generalView;
   private SettingsContext _settingsContext;


   public GeneralCtrl()
   {
      _pref = new Pref(getClass());

      FxmlHelper<GeneralView> fxmlHelper = new FxmlHelper<>(GeneralView.class);

      _generalView = fxmlHelper.getView();

      _tabContent = fxmlHelper.getRegion();


      _generalView.apProperties.setStyle(GuiUtils.STYLE_GROUP_BORDER);


      _generalView.btnSaveStandardProperties.setGraphic(_props.getImageView("save.png"));
      _generalView.btnSaveStandardProperties.setOnAction(e -> onSaveStandardProperties());

      Utils.makePositiveIntegerField(_generalView.txtLimitRowsDefault);

      loadSettingsToUi();


      _generalView.chkLimitRowsByDefault.setOnAction((e) -> updateUi());

      initPropertiesFileInfoGroup();
   }

   @Override
   public void setSettingsContext(SettingsContext settingsContext)
   {
      _settingsContext = settingsContext;
   }

   @Override
   public Tab getTab()
   {
      Tab ret = new Tab();
      ret.setText(_i18n.t("globals"));
      ret.setContent(_tabContent);

      return ret;
   }

   private void initPropertiesFileInfoGroup()
   {

      _generalView.txtPropertiesFileLocation.setEditable(false);

      File propFile = AppState.get().getPropertiesHandler().getPropertiesFile();

      ToggleGroup tg = new ToggleGroup();

      _generalView.radStandardProps.setToggleGroup(tg);
      _generalView.radUserDefinedProps.setToggleGroup(tg);


      if(null == propFile)
      {
         _generalView.radStandardProps.setDisable(false);
         _generalView.radUserDefinedProps.setDisable(true);
         _generalView.radStandardProps.setSelected(true);
         _generalView.radUserDefinedProps.setSelected(false);
         _generalView.txtPropertiesFileLocation.setText(null);
         _generalView.txtPropertiesFileLocation.setDisable(true);
      }
      else
      {
         _generalView.radStandardProps.setDisable(true);
         _generalView.radUserDefinedProps.setDisable(false);
         _generalView.radStandardProps.setSelected(false);
         _generalView.radUserDefinedProps.setSelected(true);
         _generalView.txtPropertiesFileLocation.setText(propFile.getAbsolutePath());
      }
   }

   private void onSaveStandardProperties()
   {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

      fileChooser.setTitle(_i18n.t("showSettings.save.standard.props.dialog.title"));

      fileChooser.setInitialFileName("sqfx.properties");

      File file = fileChooser.showSaveDialog(_settingsContext.getDialog());

      if(null == file)
      {
         return;
      }


      SquirrelProperty.writeStandardPropertiesToFile(file);

      FXMessageBox.showInfoOk(_settingsContext.getDialog(), _i18n.t("showSettings.standard.props.saved.to", file.getAbsolutePath()));

   }


   @Override
   public void saveSettings()
   {
      Settings settings = AppState.get().getSettingsManager().getSettings();
      settings.setMultibleLinesInCells(_generalView.chkMultibleLinesInCells.isSelected());
      settings.setLimitRowsByDefault(_generalView.chkLimitRowsByDefault.isSelected());

      String buf = _generalView.txtLimitRowsDefault.getText();
      settings.setLimitRowsDefault(StringInterpreter.interpret(buf, Integer.class, settings.getLimitRowsDefault()));
      _generalView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());

      if (false == Utils.isEmptyString(_generalView.txtStatementSeparator.getText()))
      {
         settings.setStatementSeparator(_generalView.txtStatementSeparator.getText().trim());
      }


      AppState.get().getSettingsManager().writeSettings();
   }

   private void loadSettingsToUi()
   {
      Settings settings = AppState.get().getSettingsManager().getSettings();

      _generalView.chkMultibleLinesInCells.setSelected(settings.isMultibleLinesInCells());
      _generalView.chkLimitRowsByDefault.setSelected(settings.isLimitRowsByDefault());
      _generalView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());

      _generalView.txtStatementSeparator.setText("" + settings.getStatementSeparator());

      updateUi();
   }

   private void updateUi()
   {
      _generalView.txtLimitRowsDefault.setDisable(false == _generalView.chkLimitRowsByDefault.isSelected());
   }
}
