package org.squirrelsql.aliases;

import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.dbconnector.DBConnector;
import org.squirrelsql.aliases.dbconnector.DbConnectorListener;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.*;
import org.squirrelsql.session.schemainfo.DatabaseStructure;
import org.squirrelsql.session.schemainfo.SchemaCacheConfig;
import org.squirrelsql.session.schemainfo.StructItemSchema;
import org.squirrelsql.table.TableLoader;
import org.squirrelsql.table.TableLoaderFactory;

import java.util.ArrayList;

public class AliasPropertiesEditCtrl
{
   private final AliasPropertiesEditView _view;
   private Stage _dialog;
   private I18n _i18n = new I18n(this.getClass());
   private Pref _pref = new Pref(getClass());
   private Alias _alias;
   private final TableLoader _tableLoader;

   public AliasPropertiesEditCtrl(Alias alias)
   {
      _alias = alias;
      FxmlHelper<AliasPropertiesEditView> fxmlHelper = new FxmlHelper<>(AliasPropertiesEditView.class);

      ToggleGroup tg = new ToggleGroup();

      _view = fxmlHelper.getView();

      _view.radLoadAllCacheNon.setToggleGroup(tg);
      _view.radLoadAndCacheAll.setToggleGroup(tg);
      _view.radSpecifyLoading.setToggleGroup(tg);


      AliasProperties aliasProperties = Dao.loadAliasProperties(alias.getId());

      _tableLoader = createEmptyTableLoader();

      if(aliasProperties.isLoadAllCacheNon())
      {
         _view.radLoadAllCacheNon.setSelected(true);
      }
      else if(aliasProperties.isLoadAndCacheAll())
      {
         _view.radLoadAndCacheAll.setSelected(true);
      }
      else
      {
         _view.radSpecifyLoading.setSelected(true);
         _tableLoader.addRows(aliasProperties.getSpecifiedLoading());
      }
      _tableLoader.load(_view.tblSchemas);

      onDisableSpecifyControls();

      _view.radLoadAllCacheNon.setOnAction(e -> onDisableSpecifyControls());
      _view.radLoadAndCacheAll.setOnAction(e -> onDisableSpecifyControls());
      _view.radSpecifyLoading.setOnAction(e -> onDisableSpecifyControls());


      _view.btnClose.setOnAction(e -> _dialog.close());
      _view.btnOk.setOnAction(e -> onOk());

      _view.btnConnectDb.setOnAction(e -> onConnectDb());


      initWindow(alias, fxmlHelper);

   }

   private void onConnectDb()
   {
      new DBConnector(_alias, _dialog, SchemaCacheConfig.LOAD_NOTHING).tryConnect(this::onConnected);
   }

   private void onConnected(DbConnectorResult dbConnectorResult)
   {
      if(false == dbConnectorResult.isConnected())
      {
         return;
      }

      DatabaseStructure dataBaseStructure = dbConnectorResult.getSchemaCache().getDataBaseStructure();

      ArrayList<StructItemSchema> schemas = dataBaseStructure.getSchemas();

      for (StructItemSchema schema : schemas)
      {
         _tableLoader.addRow(schema.getQualifiedName(), SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.LOAD_BUT_DONT_CACHE);
      }

      _tableLoader.load(_view.tblSchemas);
   }

   private TableLoader createEmptyTableLoader()
   {
      TableLoader tl = new TableLoader();

      tl.addColumn(_i18n.t("alias.properties.schema"));
      tl.addColumn(_i18n.t("alias.properties.tables")).setSelectableValues(SchemaLoadOptions.values());
      tl.addColumn(_i18n.t("alias.properties.views")).setSelectableValues(SchemaLoadOptions.values());
      tl.addColumn(_i18n.t("alias.properties.procedures")).setSelectableValues(SchemaLoadOptions.values());
      return tl;
   }

   private void onOk()
   {
      ArrayList<ArrayList> rows = _tableLoader.getRows();

      AliasProperties aliasProperties = new AliasProperties(rows, _alias.getId(), _view.radLoadAllCacheNon.isSelected(), _view.radLoadAndCacheAll.isSelected());

      Dao.writeAliasProperties(aliasProperties);
      _dialog.close();
   }

   private void onDisableSpecifyControls()
   {
      _view.btnConnectDb.setDisable(!_view.radSpecifyLoading.isSelected());
      _view.tblSchemas.setDisable(!_view.radSpecifyLoading.isSelected());

   }

   private void initWindow(Alias alias, FxmlHelper<AliasPropertiesEditView> fxmlHelper)
   {
      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("aliases.properties.title", alias.getName()));
      _dialog.initOwner(AppState.get().getPrimaryStage());
      Region region = fxmlHelper.getRegion();
      _dialog.setScene(new Scene(region));

      GuiUtils.makeEscapeClosable(region);

      new StageDimensionSaver("aliasproperties", _dialog, _pref, region.getPrefWidth(), region.getPrefHeight(), _dialog.getOwner());

      _dialog.show();
   }
}
