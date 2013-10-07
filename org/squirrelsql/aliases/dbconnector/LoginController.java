package org.squirrelsql.aliases.dbconnector;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.drivers.DriversUtil;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.StageDimensionSaver;

public class LoginController
{

   private final Stage _dialog;
   private final LoginView _view;
   private boolean _ok;

   public LoginController(Alias alias, Window owner)
   {
      FxmlHelper<LoginView> fxmlHelper = new FxmlHelper<>(LoginView.class);

      _view = fxmlHelper.getView();
      _view.lblAlias.setText(alias.getName());
      _view.lblDriver.setText(DriversUtil.findDriver(alias.getDriverId()).getName());
      _view.lblUrl.setText(alias.getUrl());
      _view.txtUser.setText(alias.getUserName());

      _view.btnClose.setOnAction(e-> onClose());
      _view.btnConnect.setOnAction(e-> onConnect());


      _dialog = new Stage();
      _dialog.setTitle(alias.getName());
      _dialog.initModality(Modality.WINDOW_MODAL);


      if(null == owner)
      {
         owner = AppState.get().getPrimaryStage();
      }
      _dialog.initOwner(owner);

      Region region = fxmlHelper.getRegion();
      _dialog.setScene(new Scene(region));

      GuiUtils.makeEscapeClosable(region);

      new StageDimensionSaver("aliaslogin", _dialog, new Pref(LoginController.class), region.getPrefWidth(), region.getPrefHeight(), _dialog.getOwner());

      _dialog.showAndWait();
   }

   private void onConnect()
   {
      _ok = true;
      _dialog.close();

   }

   private void onClose()
   {
      _dialog.close();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getUserName()
   {
      return _view.txtUser.getText();
   }


   public String getPassword()
   {
      return _view.txtPassword.getText();
   }
}
