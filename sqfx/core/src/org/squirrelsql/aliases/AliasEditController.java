package org.squirrelsql.aliases;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.dbconnector.DBConnector;
import org.squirrelsql.drivers.*;
import org.squirrelsql.services.*;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.session.schemainfo.SchemaCacheConfig;

public class AliasEditController
{
   public static enum ConstructorState { EDIT, COPY }


   private TreePositionCtrl _treePositionCtrl;
   private Pref _pref = new Pref(getClass());

   private I18n _i18n = new I18n(this.getClass());
   private AliasEditView _aliasEditView;
   private Stage _dialog;
   private Alias _alias = new Alias();
   private boolean _ok = false;


   public AliasEditController(boolean parentNodeSelected, boolean parentAllowsChildren)
   {
      _init(parentNodeSelected, parentAllowsChildren, null, null);
   }


   public AliasEditController(Alias alias, ConstructorState constructorState)
   {
      _init(false, false, alias, constructorState);
   }

   private void _init(boolean parentNodeSelected, boolean parentAllowsChildren, Alias alias, ConstructorState constructorState)
   {
      FxmlHelper<AliasEditView> fxmlHelper = new FxmlHelper<>(AliasEditView.class);

      _aliasEditView = fxmlHelper.getView();


      ObservableList<SQLDriver> drivers = new DriversManager().getDrivers(DriversFilteredPref.isFiltered());

      String title;


      _aliasEditView.cboDriver.getItems().addAll(drivers);

      _aliasEditView.cboDriver.setCellFactory(cf -> new DriverCell());


      if (null == alias)
      {
         title = _i18n.t("title.new.alias");
         _treePositionCtrl = new TreePositionCtrl(_aliasEditView.treePositionViewController, parentNodeSelected, parentAllowsChildren);

         if(0 < drivers.size())
         {
            _aliasEditView.cboDriver.getSelectionModel().select(0);
         }

      }
      else
      {
         _alias = alias;

         _aliasEditView.treePositionView.setDisable(true);
         if (ConstructorState.EDIT == constructorState)
         {
            title = _i18n.t("title.edit.alias", _alias.getName());

         }
         else
         {
            title = _i18n.t("title.copy.alias", _alias.getName());
         }

         SQLDriver driver = DriversUtil.findDriver(alias.getDriverId(), drivers);

         if(null == driver)
         {
            drivers = new DriversManager().getDrivers(false);
         }

         driver = DriversUtil.findDriver(alias.getDriverId(), drivers);

         loadOrStore(false, driver);
      }


      initListener();


      _aliasEditView.lblChangeDriver.setText(title);
      _dialog = new Stage();
      _dialog.setTitle(title);
      _dialog.initModality(Modality.WINDOW_MODAL);
      _dialog.initOwner(AppState.get().getPrimaryStage());
      Region region = fxmlHelper.getRegion();
      _dialog.setScene(new Scene(region));

      GuiUtils.makeEscapeClosable(region);

      new StageDimensionSaver("aliasedit", _dialog, _pref, region.getPrefWidth(), region.getPrefHeight(), _dialog.getOwner());

      _dialog.showAndWait();
   }

   private void initListener()
   {
      _aliasEditView.lnkSetToSampleUrl.setOnAction(e -> onSetSampleURL());

      _aliasEditView.btnClose.setOnAction(e -> onClose());
      _aliasEditView.btnOk.setOnAction(e -> onOk());
      _aliasEditView.btnTest.setOnAction(e -> onTest());

      _aliasEditView.chkUserEmpty.setOnAction(this::onAjustUserNullEmpty);
      _aliasEditView.chkUserNull.setOnAction(this::onAjustUserNullEmpty);

      _aliasEditView.chkPasswordEmpty.setOnAction(this::onAjustPwdNullEmpty);
      _aliasEditView.chkPasswordNull.setOnAction(this::onAjustPwdNullEmpty);


   }

   private void onTest()
   {
      if(validate())
      {
         SQLDriver sqlDriver = _aliasEditView.cboDriver.getSelectionModel().getSelectedItem();
         Alias buf = new Alias();
         storeToAlias(buf, sqlDriver);

         DBConnector dbConnector = new DBConnector(buf, _dialog, SchemaCacheConfig.LOAD_NOTHING);

         dbConnector.tryConnect(dbConnectorResult -> onTryConnectFinished(dbConnectorResult));

      }
   }

   private void onTryConnectFinished(DbConnectorResult dbConnectorResult)
   {
      if (dbConnectorResult.isConnected())
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.test.ok"));
      }
   }

   private void onAjustUserNullEmpty(ActionEvent e)
   {
      _adjustEmptyNull(e, _aliasEditView.chkUserNull, _aliasEditView.chkUserEmpty, _aliasEditView.txtUserName);
   }

   private void onAjustPwdNullEmpty(ActionEvent e)
   {
      _adjustEmptyNull(e, _aliasEditView.chkPasswordNull, _aliasEditView.chkPasswordEmpty, _aliasEditView.txtPassword);
   }

   private void _adjustEmptyNull(ActionEvent e, CheckBox chkNull, CheckBox chkEmpty, TextField txt)
   {
      if(e.getSource() == chkEmpty)
      {
         if (chkEmpty.isSelected())
         {
            chkNull.setSelected(false);
         }
      }
      else
      {
         if (chkNull.isSelected())
         {
            chkEmpty.setSelected(false);
         }
      }

      initEnableTextField(chkNull, chkEmpty, txt);
   }

   private void initEnableTextField(CheckBox chkNull, CheckBox chkEmpty, TextField txt)
   {
      if(chkNull.isSelected() || chkEmpty.isSelected())
      {
         txt.setDisable(true);
      }
      else
      {
         txt.setDisable(false);
      }
   }

   private void onOk()
   {
      if (false == validate())
      {
         return;
      }

      SQLDriver sqlDriver = _aliasEditView.cboDriver.getSelectionModel().getSelectedItem();
      loadOrStore(true, sqlDriver);

      _ok = true;

      _dialog.close();
   }

   private boolean validate()
   {
      if(Utils.isEmptyString(_aliasEditView.txtName.getText()))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.no.name"));
         return false;
      }


      if(Utils.isEmptyString(_aliasEditView.txtUrl.getText()))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.no.url"));
         return false;
      }


      if(   false == _aliasEditView.chkUserEmpty.isSelected()
         && false == _aliasEditView.chkUserNull.isSelected()
         && Utils.isEmptyString(_aliasEditView.txtUserName.getText()))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.no.user"));
         return false;
      }

      if(_aliasEditView.chkSavePassword.isSelected()
         && false == _aliasEditView.chkPasswordEmpty.isSelected()
         && false == _aliasEditView.chkPasswordNull.isSelected()
         && Utils.isEmptyString(_aliasEditView.txtPassword.getText()))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.no.password"));
         return false;
      }

      SQLDriver sqlDriverBuf = _aliasEditView.cboDriver.getSelectionModel().getSelectedItem();
      if(false == sqlDriverBuf.isLoaded() && FXMessageBox.NO.equals(FXMessageBox.showYesNo(_dialog, _i18n.t("alias.edit.driver.not.loaded.continue"))))
      {
         return false;
      }

      return true;
   }

   private void loadOrStore(boolean store, SQLDriver sqlDriver)
   {
      if (store)
      {
         storeToAlias(_alias, sqlDriver);
      }
      else
      {
         _aliasEditView.txtName.setText(_alias.getName());
         _aliasEditView.cboDriver.getSelectionModel().select(sqlDriver);
         _aliasEditView.txtUrl.setText(_alias.getUrl());

         _aliasEditView.txtUserName.setText(_alias.getUserName());
         _aliasEditView.chkUserNull.setSelected(_alias.isUserNull());
         _aliasEditView.chkUserEmpty.setSelected(_alias.isUserEmptyString());
         initEnableTextField(_aliasEditView.chkUserNull, _aliasEditView.chkUserEmpty, _aliasEditView.txtUserName);

         _aliasEditView.chkSavePassword.setSelected(_alias.isSavePassword());

         _aliasEditView.txtPassword.setText(_alias.getPassword());
         _aliasEditView.chkPasswordNull.setSelected(_alias.isPasswordNull());
         _aliasEditView.chkPasswordEmpty.setSelected(_alias.isPasswordEmptyString());
         initEnableTextField(_aliasEditView.chkPasswordNull, _aliasEditView.chkPasswordEmpty, _aliasEditView.txtPassword);


         _aliasEditView.chkAutoLogon.setSelected(_alias.isAutoLogon());
         _aliasEditView.chkConnectAtStartUp.setSelected(_alias.isConnectAtStartUp());


      }
   }

   private void storeToAlias(Alias alias, SQLDriver sqlDriver)
   {
      alias.setName(_aliasEditView.txtName.getText().trim());
      alias.setDriverId(sqlDriver.getId());
      alias.setUrl(_aliasEditView.txtUrl.getText().trim());

      alias.setUserName(_aliasEditView.txtUserName.getText().trim());
      alias.setUserNull(_aliasEditView.chkUserNull.isSelected());
      alias.setUserEmptyString(_aliasEditView.chkUserEmpty.isSelected());

      alias.setSavePassword(_aliasEditView.chkSavePassword.isSelected());

      alias.setPassword(_aliasEditView.txtPassword.getText().trim());
      alias.setPasswordNull(_aliasEditView.chkPasswordNull.isSelected());
      alias.setPasswordEmptyString(_aliasEditView.chkPasswordEmpty.isSelected());

      alias.setAutoLogon(_aliasEditView.chkAutoLogon.isSelected());
      alias.setConnectAtStartUp(_aliasEditView.chkConnectAtStartUp.isSelected());
   }

   private void onClose()
   {
      _dialog.close();
   }

   private void onSetSampleURL()
   {
      SQLDriver selectedItem = _aliasEditView.cboDriver.getSelectionModel().getSelectedItem();

      if(null != selectedItem)
      {
         _aliasEditView.txtUrl.setText(selectedItem.getUrl());
      }
   }

   private void initListeners()
   {
      _aliasEditView.chkSavePassword.setOnAction(e -> onSavePassword());
      onSavePassword();

   }

   private void onSavePassword()
   {
      _aliasEditView.lblPassword.setDisable(true);
      _aliasEditView.txtPassword.setDisable(true);
      _aliasEditView.chkAutoLogon.setDisable(true);
      _aliasEditView.chkConnectAtStartUp.setDisable(true);
      _aliasEditView.chkPasswordNull.setDisable(true);
      _aliasEditView.chkPasswordEmpty.setDisable(true);

      if(_aliasEditView.chkSavePassword.isSelected())
      {
         _aliasEditView.lblPassword.setDisable(false);
         _aliasEditView.txtPassword.setDisable(false);
         _aliasEditView.chkAutoLogon.setDisable(false);
         _aliasEditView.chkConnectAtStartUp.setDisable(false);
         _aliasEditView.chkPasswordNull.setDisable(false);
         _aliasEditView.chkPasswordEmpty.setDisable(false);
      }
   }

   public Alias getAlias()
   {
      return _alias;
   }

   public boolean isOk()
   {
      return _ok;
   }

   public TreePositionCtrl getTreePositionCtrl()
   {
      return _treePositionCtrl;
   }
}
