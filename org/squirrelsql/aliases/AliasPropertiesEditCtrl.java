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

   public AliasPropertiesEditCtrl(Alias alias)
   {
      _alias = alias;
      FxmlHelper<AliasPropertiesEditView> fxmlHelper = new FxmlHelper<>(AliasPropertiesEditView.class);

      ToggleGroup tg = new ToggleGroup();

      _view = fxmlHelper.getView();

      _view.radLoadAllCacheNon.setToggleGroup(tg);
      _view.radLoadAndCacheAll.setToggleGroup(tg);
      _view.radSpecifyLoading.setToggleGroup(tg);
      _view.radLoadAllCacheNon.setSelected(true);
      onDisableSpecifyControls();

      _view.radLoadAllCacheNon.setOnAction(e -> onDisableSpecifyControls());
      _view.radLoadAndCacheAll.setOnAction(e -> onDisableSpecifyControls());
      _view.radSpecifyLoading.setOnAction(e -> onDisableSpecifyControls());


      _view.btnClose.setOnAction(e -> _dialog.close());
      _view.btnOk.setOnAction(e -> onOk());

      _view.btnConnectDb.setOnAction(e -> onConnectDb());

      createEmptyTableLoader().load(_view.tblSchemas);

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

      TableLoader tl = createEmptyTableLoader();

      for (StructItemSchema schema : schemas)
      {
         tl.addRow(schema.getQualifiedName());
      }

      tl.load(_view.tblSchemas);
   }

   private TableLoader createEmptyTableLoader()
   {
      TableLoader tl = new TableLoader();

      tl.addColumn(_i18n.t("alias.properties.schema"));
      tl.addColumn(_i18n.t("alias.properties.tables"));
      tl.addColumn(_i18n.t("alias.properties.views"));
      tl.addColumn(_i18n.t("alias.properties.procedures"));
      return tl;
   }

   private void onOk()
   {
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
