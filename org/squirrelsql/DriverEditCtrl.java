package org.squirrelsql;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.StageDimensionSaver;

import java.io.IOException;
import java.util.ResourceBundle;

public class DriverEditCtrl
{
   private I18n _i18n = new I18n(this.getClass());
   private Pref _pref = new Pref(getClass());


   public DriverEditCtrl(SquirrelDriver squirrelDriver)
   {
      try
      {

         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DriverEditView.fxml"), ResourceBundle.getBundle(getClass().getPackage().getName() + ".i18n"));
         Region parent = (Region) fxmlLoader.load();
         DriverEditView driverEditView = fxmlLoader.getController();

         String title = _i18n.t("change.driver.title", squirrelDriver.getName());

         driverEditView.lblChangeDriver.setText(title);

         driverEditView.txtName.setText(squirrelDriver.getName());
         driverEditView.txtUrl.setText(squirrelDriver.getUrl());
         driverEditView.txtWebUrl.setText(squirrelDriver.getWebsiteUrl());
         driverEditView.txtSelectedDriver.setText(squirrelDriver.getDriverClassName());


         Stage dialog = new Stage();
         dialog.setTitle(title);
         dialog.initOwner(AppState.get().getPrimaryStage());
         dialog.setScene(new Scene(parent));

         new StageDimensionSaver("driveredit", dialog, _pref, parent.getPrefWidth(), parent.getPrefHeight(), dialog.getOwner());

         dialog.show();

      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
