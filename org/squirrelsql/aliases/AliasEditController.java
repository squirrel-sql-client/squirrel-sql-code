package org.squirrelsql.aliases;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.drivers.DriverCell;
import org.squirrelsql.drivers.DriversFilteredPref;
import org.squirrelsql.drivers.DriversManager;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.services.*;

import java.sql.DriverManager;

public class AliasEditController
{
   private Pref _pref = new Pref(getClass());

   private I18n _i18n = new I18n(this.getClass());
   private final AliasEditView _aliasEditView;


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

      _aliasEditView.lnkSetToSampleUrl.setOnAction(e -> onSetSampleURL());


      if(0 < drivers.size())
      {
         _aliasEditView.cboDriver.getSelectionModel().select(0);
      }


      Stage dialog = new Stage();
      dialog.setTitle(title);
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(AppState.get().getPrimaryStage());
      Region region = fxmlHelper.getRegion();
      dialog.setScene(new Scene(region));

      GuiUtils.makeEscapeClosable(region);

      new StageDimensionSaver("aliasedit", dialog, _pref, region.getPrefWidth(), region.getPrefHeight(), dialog.getOwner());

      dialog.showAndWait();
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

      if(_aliasEditView.chkSavePassword.isSelected())
      {
         _aliasEditView.lblPassword.setDisable(false);
         _aliasEditView.txtPassword.setDisable(false);
         _aliasEditView.chkAutoLogon.setDisable(false);
         _aliasEditView.chkConnectAtStartUp.setDisable(false);
      }
   }

   public Alias getAlias()
   {
      return null;  //To change body of created methods use File | Settings | File Templates.
   }
}
