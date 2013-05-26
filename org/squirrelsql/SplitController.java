package org.squirrelsql;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.squirrelsql.services.Pref;
import org.squirrelsql.workaround.SplitDividerWA;

// http://docs.oracle.com/javafx/2/ui_controls/editor.htm

public class SplitController
{
   private static final String PREF_DRIVER_SPLIT_LOC = "driver.split.loc";
   private static final String PREF_ALIASES_SPLIT_LOC = "aliases.split.loc";
   private static final String PREF_MESSAGES_SPLIT_LOC = "messages.split.loc";

   private Pref _pref = new Pref(this.getClass());

   private SplitPane _spltVert = new SplitPane();
   private final SplitPane _spltHoriz = new SplitPane();

   private DriversController _driversController;
   private AliasesController _aliasesController = new AliasesController();

   public SplitController(DockPaneChanel dockPaneChanel)
   {
      _driversController = new DriversController(dockPaneChanel);
      _spltHoriz.setOrientation(Orientation.HORIZONTAL);
      _spltHoriz.getItems().add(new SessionTabbedPaneCtrl().getNode());

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
         SplitDividerWA.adjustDivider(_spltHoriz, 0, _pref.getDouble(PREF_DRIVER_SPLIT_LOC, 0.2d));
      }
   }

   private void onShowAliases(boolean selected)
   {
      checkRemove();
      if (selected)
      {
         _spltHoriz.getItems().add(0, _aliasesController.getNode());
         SplitDividerWA.adjustDivider(_spltHoriz, 0, _pref.getDouble(PREF_ALIASES_SPLIT_LOC, 0.2d));
      }
   }

   private void checkRemove()
   {
      if (_spltHoriz.getItems().get(0) == _driversController.getNode())
      {
         _pref.set(PREF_DRIVER_SPLIT_LOC, _spltHoriz.getDividerPositions()[0]);
         _spltHoriz.getItems().remove(0);
      }

      if (_spltHoriz.getItems().get(0) == _aliasesController.getNode())
      {
         _pref.set(PREF_ALIASES_SPLIT_LOC, _spltHoriz.getDividerPositions()[0]);
         _spltHoriz.getItems().remove(0);
      }
   }


   public Node getNode()
   {
      return _spltVert;
   }

   public void close()
   {
      _pref.set(PREF_MESSAGES_SPLIT_LOC, _spltVert.getDividerPositions()[0]);
   }

   public void adjustMessageSplit()
   {
      SplitDividerWA.adjustDivider(_spltVert, 0, _pref.getDouble(PREF_MESSAGES_SPLIT_LOC, 0.85d));
   }
}
