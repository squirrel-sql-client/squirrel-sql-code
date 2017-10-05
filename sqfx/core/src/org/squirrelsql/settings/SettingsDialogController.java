package org.squirrelsql.settings;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;

import java.util.Arrays;
import java.util.List;

public class SettingsDialogController
{
   private final Stage _dialog;
   private final Pref _pref;

   private I18n _i18n = new I18n(getClass());

   private final SettingsDialogView _settingsDialogView;

   private List<SettingsTabController> _settingsTabControllers = Arrays.asList(new GeneralCtrl(), new SQLReformatCtrl());


   public SettingsDialogController()
   {
      _pref = new Pref(getClass());

      FxmlHelper<SettingsDialogView> fxmlHelper = new FxmlHelper<>(SettingsDialogView.class);

      _settingsDialogView = fxmlHelper.getView();

      _dialog = GuiUtils.createNonModalDialog(fxmlHelper.getRegion(), _pref, 1000, 800, "showSettingsController");

      _dialog.setTitle(_i18n.t("showSettings.title"));

      _settingsTabControllers.forEach(c -> loadTab(c));

      _settingsDialogView.btnOk.setOnAction((e) -> onOk());
      _settingsDialogView.btnCancel.setOnAction((e) -> onCancel());

      _dialog.show();
   }

   private void loadTab(SettingsTabController settingsTabController)
   {
      settingsTabController.setSettingsContext(new SettingsContext()
      {
         @Override
         public Window getDialog()
         {
            return _dialog;
         }
      });

      _settingsDialogView.tabPaneSettings.getTabs().add(settingsTabController.getTab());
   }


   private void onCancel()
   {
      _dialog.close();
   }

   private void onOk()
   {
      _settingsTabControllers.forEach(c -> c.saveSettings());
      _dialog.close();
   }

}
