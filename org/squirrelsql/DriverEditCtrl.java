package org.squirrelsql;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.squirrelsql.services.I18n;

import java.io.IOException;
import java.util.ResourceBundle;

public class DriverEditCtrl
{
   private I18n _i18n = new I18n(this.getClass());

   public DriverEditCtrl(SquirrelDriver squirrelDriver)
   {
      try
      {

         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DriverEditView.fxml"), ResourceBundle.getBundle(getClass().getPackage().getName() + ".i18n"));
         Parent parent = (Parent) fxmlLoader.load();
         DriverEditView driverEditView = fxmlLoader.getController();

         String title = _i18n.t("change.driver.title", squirrelDriver.getName());

         driverEditView.lblChangeDriver.setText(title);


         Stage dialog = new Stage();
         dialog.setTitle(title);
         dialog.initOwner(AppState.get().getPrimaryStage());


         dialog.setScene(new Scene(parent));

         dialog.show();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
