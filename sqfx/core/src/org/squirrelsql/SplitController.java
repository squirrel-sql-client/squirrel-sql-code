package org.squirrelsql;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.squirrelsql.aliases.AliasesController;
import org.squirrelsql.drivers.DriversController;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.SplitPositionSaver;
import org.squirrelsql.workaround.SplitDividerWA;

public class SplitController
{
   private SplitPositionSaver driverSplitPosSaver = new SplitPositionSaver(getClass(), "driver.split.loc");
   private SplitPositionSaver aliasSplitPosSaver = new SplitPositionSaver(getClass(), "aliases.split.loc");
   private SplitPositionSaver messageSplitPosSaver = new SplitPositionSaver(getClass(), "messages.split.loc");

   private Pref _pref = new Pref(this.getClass());

   private SplitPane _spltVert = new SplitPane();
   private final SplitPane _spltHoriz = new SplitPane();

   private DriversController _driversController;
   private AliasesController _aliasesController;

   public SplitController(DockPaneChanel dockPaneChanel)
   {
      _driversController = new DriversController(dockPaneChanel);
      _aliasesController = new AliasesController(dockPaneChanel);
      _spltHoriz.setOrientation(Orientation.HORIZONTAL);
      _spltHoriz.getItems().add(AppState.get().getSessionManager().getSessionTabbedPaneCtrl().getNode());

      _spltVert.setOrientation(Orientation.VERTICAL);
      _spltVert.getItems().add(_spltHoriz);
      _spltVert.getItems().add(AppState.get().getMessagePanelCtrl().getNode());


      dockPaneChanel.addListener(new DockPaneChanelAdapter()
      {
         @Override
         public void showDrivers(boolean selected)
         {
            onShowDrivers(selected);
         }

         @Override
         public void showAliases(boolean selected)
         {
            onShowAliases(selected);
         }
      });
   }

   private void onShowDrivers(boolean selected)
   {
      checkRemove();
      if (selected)
      {
         _spltHoriz.getItems().add(0, _driversController.getNode());
         driverSplitPosSaver.apply(_spltHoriz);
      }
   }

   private void onShowAliases(boolean selected)
   {
      checkRemove();
      if (selected)
      {
         _spltHoriz.getItems().add(0, _aliasesController.getNode());
         aliasSplitPosSaver.apply(_spltHoriz);
      }
   }

   private void checkRemove()
   {
      if (_spltHoriz.getItems().get(0) == _driversController.getNode())
      {
         driverSplitPosSaver.save(_spltHoriz);
         _spltHoriz.getItems().remove(0);
      }

      if (_spltHoriz.getItems().get(0) == _aliasesController.getNode())
      {
         aliasSplitPosSaver.save(_spltHoriz);
         _spltHoriz.getItems().remove(0);
      }
   }


   public Node getNode()
   {
      return _spltVert;
   }

   public void close()
   {
      checkRemove();
      messageSplitPosSaver.save(_spltVert);
   }

   public void adjustMessageSplit()
   {
      messageSplitPosSaver.apply(_spltVert);
   }
}
