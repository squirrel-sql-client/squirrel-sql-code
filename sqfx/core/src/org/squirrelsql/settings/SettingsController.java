package org.squirrelsql.settings;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;

public class SettingsController
{
   private final Stage _dialog;

   private I18n _i18n = new I18n(getClass());

   private Props _props = new Props(this.getClass());



   public SettingsController()
   {

      FxmlHelper<SettingsView> fxmlHelper = new FxmlHelper<>(SettingsView.class);

      SettingsView view = fxmlHelper.getView();


      view.apProperties.setStyle(GuiUtils.STYLE_GROUP_BORDER);


      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("showSettings.title"));
      _dialog.initModality(Modality.NONE);
      _dialog.initOwner(AppState.get().getPrimaryStage());

      _dialog.setScene(new Scene(fxmlHelper.getRegion()));


      view.btnSaveStandardProperties.setGraphic(_props.getImageView("save.png"));

      new StageDimensionSaver("showSettingsController", _dialog, new Pref(getClass()), 500, 600, _dialog.getOwner());

      GuiUtils.makeEscapeClosable(fxmlHelper.getRegion());

      _dialog.show();

   }
}
