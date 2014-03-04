package org.squirrelsql.workaround;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SessionTabSelectionRepaintWA
{
   public static void forceTabContentRepaintOnSelection(TabPane sessionTabPane)
   {
      sessionTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTabChanged(newValue));
   }

   private static void onTabChanged(Tab newValue)
   {
      SplitPane objectTreeOrSqlTab = (SplitPane) newValue.getContent();

      double divPos = objectTreeOrSqlTab.getDividerPositions()[0];

      double differentDivPos;
      if(divPos == 0.5d)
      {
         differentDivPos = 0;
      }
      else
      {
         differentDivPos = 1 - divPos;
      }
      objectTreeOrSqlTab.setDividerPosition(0, differentDivPos);

      SplitDividerWA.adjustDivider(objectTreeOrSqlTab, 0, divPos);

      //System.out.println("org.squirrelsql.workaround.SessionTabSelectionRepaintWA.changed");
   }
}
