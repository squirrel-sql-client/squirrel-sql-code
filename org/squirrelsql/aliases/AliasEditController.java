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
import org.squirrelsql.drivers.DriverCell;
import org.squirrelsql.drivers.DriversFilteredPref;
import org.squirrelsql.drivers.DriversManager;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.services.*;

public class AliasEditController
{
   private Pref _pref = new Pref(getClass());

   private I18n _i18n = new I18n(this.getClass());
   private final AliasEditView _aliasEditView;
   private final Stage _dialog;


   public AliasEditController()
   {
      FxmlHelper<AliasEditView> fxmlHelper = new FxmlHelper<>(AliasEditView.class);

      _aliasEditView = fxmlHelper.getView();

      String title = _i18n.t("title.new.alias");
      _aliasEditView.lblChangeDriver.setText(title);

      initListeners();

      ObservableList<SQLDriver> drivers = new DriversManager().getDrivers(DriversFilteredPref.isFiltered());

      _aliasEditView.cboDriver.getItems().addAll(drivers);

      _aliasEditView.cboDriver.setCellFactory(cf -> new DriverCell());

      if(0 < drivers.size())
      {
         _aliasEditView.cboDriver.getSelectionModel().select(0);
      }


      initListener();


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

      _aliasEditView.chkUserEmpty.setOnAction(this::onAjustUserNullEmpty);
      _aliasEditView.chkUserNull.setOnAction(this::onAjustUserNullEmpty);

      _aliasEditView.chkPasswordEmpty.setOnAction(this::onAjustPwdNullEmpty);
      _aliasEditView.chkPasswordNull.setOnAction(this::onAjustPwdNullEmpty);


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
      if(false == Utils.isFilledString(_aliasEditView.txtName.getText()))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.no.name"));
         return;
      }

      if(false == Utils.isFilledString(_aliasEditView.txtUrl.getText()))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("alias.edit.no.url"));
         return;
      }

      SQLDriver sqlDriver = _aliasEditView.cboDriver.getSelectionModel().getSelectedItem();
      if(false == sqlDriver.isLoaded() && FXMessageBox.NO.equals(FXMessageBox.showYesNo(_dialog, _i18n.t("alias.edit.driver.not.loaded.continue"))))
      {
         return;
      }



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
      return null;  //To change body of created methods use File | Settings | File Templates.
   }
}
