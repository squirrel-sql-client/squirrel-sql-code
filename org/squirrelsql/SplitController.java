package org.squirrelsql;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import org.squirrelsql.workaround.SplitDividerWA;

public class SplitController
{
   private static final String PREF_DRIVER_SPLIT_LOC = "driver.split.loc";
   private static final String PREF_ALIASES_SPLIT_LOC = "aliases.split.loc";

   private Pref _pref = new Pref(this.getClass());

   private SplitPane _spltVert;
   private final SplitPane _spltHoriz;
   private DriversController _driversController = new DriversController();
   private AliasesController _aliasesController = new AliasesController();

   public SplitController()
   {
      _spltHoriz = new SplitPane();
      _spltHoriz.setOrientation(Orientation.HORIZONTAL);

      _spltVert = new SplitPane();
      _spltVert.setOrientation(Orientation.VERTICAL);

      _spltHoriz.getItems().add(new TextArea("Session"));
      _spltVert.getItems().add(_spltHoriz);

      _spltVert.getItems().add(new TextArea("Message"));

   }

   public void showDrivers(boolean selected)
   {
      checkRemove();
      if (selected)
      {
         _spltHoriz.getItems().add(0, _driversController.getNode());
         SplitDividerWA.adjustDivider(_spltHoriz, 0, _pref.getDouble(PREF_DRIVER_SPLIT_LOC, 0.2d));
      }
   }

   public void showAliases(boolean selected)
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
}
