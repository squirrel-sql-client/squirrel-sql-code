package org.squirrelsql.aliases;

import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.dbconnector.DBConnector;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.AliasPropertiesSpecifiedLoading;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.StageDimensionSaver;
import org.squirrelsql.session.schemainfo.SchemaCacheConfig;
import org.squirrelsql.table.RowObjectHandle;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.List;

public class AliasPropertiesEditCtrl
{
   private final AliasPropertiesEditView _view;
   private Stage _dialog;
   private I18n _i18n = new I18n(this.getClass());
   private Pref _pref = new Pref(getClass());
   private AliasDecorator _aliasDecorator;
   private RowObjectTableLoader<AliasPropertiesSpecifiedLoading> _tableLoaderSchemas;

   public AliasPropertiesEditCtrl(AliasDecorator aliasDecorator)
   {
      _aliasDecorator = aliasDecorator;
      FxmlHelper<AliasPropertiesEditView> fxmlHelper = new FxmlHelper<>(AliasPropertiesEditView.class);

      ToggleGroup tg = new ToggleGroup();

      _view = fxmlHelper.getView();

      _view.radLoadAllCacheNon.setToggleGroup(tg);
      _view.radLoadAndCacheAll.setToggleGroup(tg);
      _view.radSpecifyLoading.setToggleGroup(tg);


      loadAliasProperties(aliasDecorator);

      _view.cboObjectTypes.getItems().clear();
      _view.cboObjectTypes.getItems().add(_i18n.t("alias.properties.all.objects"));
      _view.cboObjectTypes.getItems().addAll(AliasPropertiesObjectTypes.values());
      _view.cboObjectTypes.getSelectionModel().selectFirst();

      _view.cboSchemaLoadOptions.getItems().clear();
      _view.cboSchemaLoadOptions.getItems().addAll(SchemaLoadOptions.values());

      _view.radLoadAllCacheNon.setOnAction(e -> onDisableSpecifyControls());
      _view.radLoadAndCacheAll.setOnAction(e -> onDisableSpecifyControls());
      _view.radSpecifyLoading.setOnAction(e -> onDisableSpecifyControls());


      _view.btnClose.setOnAction(e -> _dialog.close());
      _view.btnOk.setOnAction(e -> onOk());

      _view.btnApply.setOnAction(e -> onApply());

      _view.btnConnectDb.setOnAction(e -> onConnectDb());


      initWindow(aliasDecorator, fxmlHelper);

   }

   private void onApply()
   {
      SchemaLoadOptions selectedLoadOption = _view.cboSchemaLoadOptions.getSelectionModel().getSelectedItem();
      if(null == selectedLoadOption)
      {
         return;
      }

      for (RowObjectHandle<AliasPropertiesSpecifiedLoading> hRow : _tableLoaderSchemas.getRowObjectHandles())
      {
         if (_view.cboObjectTypes.getSelectionModel().getSelectedItem() == AliasPropertiesObjectTypes.TABLE)
         {
            hRow.getRowObject().setTableOpt(selectedLoadOption);
         }
         else if (_view.cboObjectTypes.getSelectionModel().getSelectedItem() == AliasPropertiesObjectTypes.VIEW)
         {
            hRow.getRowObject().setViewOpt(selectedLoadOption);
         }
         else if (_view.cboObjectTypes.getSelectionModel().getSelectedItem() == AliasPropertiesObjectTypes.PROCEDURE)
         {
            hRow.getRowObject().setProcedureOpt(selectedLoadOption);
         }
         else if (_view.cboObjectTypes.getSelectionModel().getSelectedItem() == AliasPropertiesObjectTypes.OTHER_TABLE_TYPES)
         {
            hRow.getRowObject().setOtherTableOpt(selectedLoadOption);
         }
         else
         {
            hRow.getRowObject().setTableOpt(selectedLoadOption);
            hRow.getRowObject().setViewOpt(selectedLoadOption);
            hRow.getRowObject().setProcedureOpt(selectedLoadOption);
            hRow.getRowObject().setOtherTableOpt(selectedLoadOption);
         }
      }

      _tableLoaderSchemas.updateUI();
   }

   private void loadAliasProperties(AliasDecorator aliasDecorator)
   {
      AliasPropertiesDecorator aliasPropertiesDecorator = _aliasDecorator.getAliasPropertiesDecorator();

      _tableLoaderSchemas = AliasPropertiesDecorator.createEmptyTableLoader();

      if(aliasPropertiesDecorator.getAliasProperties().isLoadAllCacheNon())
      {
         _view.radLoadAllCacheNon.setSelected(true);
      }
      else if(aliasPropertiesDecorator.getAliasProperties().isLoadAndCacheAll())
      {
         _view.radLoadAndCacheAll.setSelected(true);
      }
      else
      {
         _view.radSpecifyLoading.setSelected(true);
      }

      AliasPropertiesSpecifiedLoading.TableLoaderAccess tableLoaderAccess = new AliasPropertiesSpecifiedLoading.TableLoaderAccess();
      _tableLoaderSchemas.addRowObjects(aliasPropertiesDecorator.getAliasProperties().getSpecifiedLoadings(), tableLoaderAccess);

      _view.tblSchemas.setEditable(true);
      _tableLoaderSchemas.load(_view.tblSchemas);

      _view.chkHideEmptySchemas.setSelected(aliasPropertiesDecorator.getAliasProperties().isHideEmptySchemasInObjectTree());

      onDisableSpecifyControls();
   }

   private void onConnectDb()
   {
      new DBConnector(_aliasDecorator, _dialog, SchemaCacheConfig.createLoadNothing()).tryConnect(this::onConnected);
   }

   private void onConnected(DbConnectorResult dbConnectorResult)
   {
      if(false == dbConnectorResult.isConnected())
      {
         return;
      }

      _tableLoaderSchemas.clearRows();

      AliasPropertiesDecorator.fillSchemaTableDefault(dbConnectorResult, _tableLoaderSchemas);

      _tableLoaderSchemas.load(_view.tblSchemas);
   }

   private void onOk()
   {
      List<AliasPropertiesSpecifiedLoading> rows = _tableLoaderSchemas.getRowObjects();

      AliasProperties aliasProperties = new AliasProperties(rows, _aliasDecorator.getId(), _view.radLoadAllCacheNon.isSelected(), _view.radLoadAndCacheAll.isSelected(), _view.chkHideEmptySchemas.isSelected());

      _aliasDecorator.updateAliasPropertiesDecorator(new AliasPropertiesDecorator(aliasProperties));
      _dialog.close();
   }

   private void onDisableSpecifyControls()
   {
      _view.btnConnectDb.setDisable(!_view.radSpecifyLoading.isSelected());
      _view.tblSchemas.setDisable(!_view.radSpecifyLoading.isSelected());

   }

   private void initWindow(AliasDecorator aliasDecorator, FxmlHelper<AliasPropertiesEditView> fxmlHelper)
   {
      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("aliases.properties.title", aliasDecorator.getName()));
      _dialog.initOwner(AppState.get().getPrimaryStage());
      Region region = fxmlHelper.getRegion();
      _dialog.setScene(new Scene(region));

      GuiUtils.makeEscapeClosable(region);

      new StageDimensionSaver("aliasproperties", _dialog, _pref, region.getPrefWidth(), region.getPrefHeight(), _dialog.getOwner());

      _dialog.show();
   }
}
