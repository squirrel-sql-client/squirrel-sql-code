package org.squirrelsql.settings;

import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;
import org.squirrelsql.table.tableedit.StringInterpreter;
import org.squirrelsql.workaround.ColorWA;

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
      _generalView.apMarkCurrentSql.setStyle(GuiUtils.STYLE_GROUP_BORDER);


      _generalView.btnSaveStandardProperties.setGraphic(_props.getImageView("save.png"));
      _generalView.btnSaveStandardProperties.setOnAction(e -> onSaveStandardProperties());

      Utils.makePositiveIntegerField(_generalView.txtLimitRowsDefault);
      Utils.makePositiveIntegerField(_generalView.txtResultTabsLimit);

      Settings settings = loadSettingsToUi();
      new LineHeightOffsetCtrl(_generalView.sldLineHeightOffset, _generalView.txtLineHeightOffset, _generalView.btnApplyLineHeightOffset, settings);


      _generalView.chkLimitRowsByDefault.setOnAction((e) -> updateUi());
      _generalView.chkMarkCurrentSql.setOnAction((e) -> updateUi());

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

      String buf;

      buf = _generalView.txtLimitRowsDefault.getText();
      settings.setLimitRowsDefault(interpretPositiveDefinite(buf, settings.getLimitRowsDefault()));
      _generalView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());

      buf = _generalView.txtResultTabsLimit.getText();
      settings.setResultTabsLimit(interpretPositiveDefinite(buf, settings.getResultTabsLimit()));
      _generalView.txtResultTabsLimit.setText("" + settings.getResultTabsLimit());

      if (false == Utils.isEmptyString(_generalView.txtStatementSeparator.getText()))
      {
         settings.setStatementSeparator(_generalView.txtStatementSeparator.getText().trim());
      }

      settings.setCopyAliasProperties(_generalView.chkCopyAliasProperties.isSelected());

      settings.setCopyQuotedToClip(_generalView.chkCopyQuotedToClip.isSelected());

      settings.setMarkCurrentSQL(_generalView.chkMarkCurrentSql.isSelected());

      settings.setCurrentSqlMarkColor_R(ColorWA.getRed(_generalView.colPickCurrentSqlMark.getValue()));
      settings.setCurrentSqlMarkColor_G(ColorWA.getGreen(_generalView.colPickCurrentSqlMark.getValue()));
      settings.setCurrentSqlMarkColor_B(ColorWA.getBlue(_generalView.colPickCurrentSqlMark.getValue()));


      AppState.get().getSettingsManager().writeSettings();
   }

   private Integer interpretPositiveDefinite(String buf, int fallbackValue)
   {
      Integer ret = StringInterpreter.interpret(buf, Integer.class, fallbackValue);

      if(0 < ret)
      {
         return ret;
      }

      return fallbackValue;

   }

   private Settings loadSettingsToUi()
   {
      Settings settings = AppState.get().getSettingsManager().getSettings();

      _generalView.chkMultibleLinesInCells.setSelected(settings.isMultibleLinesInCells());
      _generalView.chkLimitRowsByDefault.setSelected(settings.isLimitRowsByDefault());
      _generalView.txtLimitRowsDefault.setText("" + settings.getLimitRowsDefault());

      _generalView.txtStatementSeparator.setText("" + settings.getStatementSeparator());
      _generalView.txtResultTabsLimit.setText("" + settings.getResultTabsLimit());

      _generalView.chkCopyAliasProperties.setSelected(settings.isCopyAliasProperties());
      _generalView.chkCopyQuotedToClip.setSelected(settings.isCopyQuotedToClip());

      _generalView.chkMarkCurrentSql.setSelected(settings.isMarkCurrentSQL());

      _generalView.colPickCurrentSqlMark.setValue(Color.rgb(settings.getCurrentSqlMarkColor_R(), settings.getCurrentSqlMarkColor_G(), settings.getCurrentSqlMarkColor_B()));



      updateUi();

      return settings;
   }

   private void updateUi()
   {
      _generalView.txtLimitRowsDefault.setDisable(false == _generalView.chkLimitRowsByDefault.isSelected());

      _generalView.colPickCurrentSqlMark.setDisable(false == _generalView.chkMarkCurrentSql.isSelected());
      _generalView.sldLineHeightOffset.setDisable(false == _generalView.chkMarkCurrentSql.isSelected());
      _generalView.txtLineHeightOffset.setDisable(false == _generalView.chkMarkCurrentSql.isSelected());
      _generalView.btnApplyLineHeightOffset.setDisable(false == _generalView.chkMarkCurrentSql.isSelected());

   }
}
