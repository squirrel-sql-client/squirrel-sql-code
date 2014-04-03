package org.squirrelsql;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.squirrelsql.services.I18n;

public class DockButtonsCtrl
{
   private I18n i18n = new I18n(getClass());
   private DockPaneChanel _dockPaneChanel;
   private VerticalToggleButton _btnAliases;
   private VerticalToggleButton _btnDrivers;

   public DockButtonsCtrl(DockPaneChanel dockPaneChanel)
   {
      _dockPaneChanel = dockPaneChanel;

      _dockPaneChanel.addListener(new DockPaneChanelAdapter()
      {
         @Override
         public void closeDriver()
         {
            _btnDrivers.setSelected(false);
            onDrivers();
         }

         @Override
         public void closeAliases()
         {
            _btnAliases.setSelected(false);
            onAliases();
         }
      });
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

      _dockPaneChanel.driversChanged(_btnDrivers.isSelected());
   }

   private void onAliases()
   {
      if (_btnAliases.isSelected())
      {
         _btnDrivers.setSelected(false);
      }

      _dockPaneChanel.aliasesChanged(_btnAliases.isSelected());
   }
}
