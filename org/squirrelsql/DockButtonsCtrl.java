package org.squirrelsql;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class DockButtonsCtrl
{
   private I18n i18n = new I18n(getClass());
   private DockButtonsListener _dockButtonsListener;
   private VerticalToggleButton _btnAliases;
   private VerticalToggleButton _btnDrivers;

   public DockButtonsCtrl(DockButtonsListener dockButtonsListener)
   {
      _dockButtonsListener = dockButtonsListener;
   }

   public Node getNode()
   {
      VBox dockButtons = new VBox();
      _btnAliases = new VerticalToggleButton(i18n.t("dock.button.aliases"));
      _btnAliases.setOnAction(new EventHandler<ActionEvent>()
      {
         @Override
         public void handle(ActionEvent actionEvent)
         {
            onAliases();
         }
      });
      dockButtons.getChildren().add(_btnAliases);


      _btnDrivers = new VerticalToggleButton(i18n.t("dock.button.drivers"));
      _btnDrivers.setOnAction(new EventHandler<ActionEvent>()
      {
         @Override
         public void handle(ActionEvent actionEvent)
         {
            onDrivers();
         }
      });
      dockButtons.getChildren().add(_btnDrivers);

      return dockButtons;
   }

   private void onDrivers()
   {
      if (_btnDrivers.isSelected())
      {
         _btnAliases.setSelected(false);
      }

      _dockButtonsListener.driversChanged(_btnDrivers.isSelected());
   }

   private void onAliases()
   {
      if (_btnAliases.isSelected())
      {
         _btnDrivers.setSelected(false);
      }

      _dockButtonsListener.aliasesChanged(_btnAliases.isSelected());
   }
}
