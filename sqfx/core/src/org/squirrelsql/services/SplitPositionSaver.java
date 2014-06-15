package org.squirrelsql.services;

import javafx.scene.control.SplitPane;
import org.squirrelsql.workaround.SplitDividerWA;

public class SplitPositionSaver
{
   private String _prefKey;
   private Pref _pref;

   public SplitPositionSaver(Class clazz, String prefKey)
   {
      _pref = new Pref(clazz);
      _prefKey = prefKey;
   }

   public void apply(SplitPane split)
   {
      SplitDividerWA.adjustDivider(split, 0, _pref.getDouble(_prefKey, 0.2d));
   }

   public void save(SplitPane split)
   {
      _pref.set(_prefKey, split.getDividerPositions()[0]);
   }
}
