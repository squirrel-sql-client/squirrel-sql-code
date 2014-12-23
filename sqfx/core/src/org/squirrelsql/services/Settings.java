package org.squirrelsql.services;

public class Settings
{
   private boolean _multibleLinesInCells = false;
   private boolean _limitRowsByDefault = true;
   private int _limitRowsDefault = 100;

   public boolean isMultibleLinesInCells()
   {
      return _multibleLinesInCells;
   }

   public void setMultibleLinesInCells(boolean multibleLinesInCells)
   {
      _multibleLinesInCells = multibleLinesInCells;
   }

   public boolean isLimitRowsByDefault()
   {
      return _limitRowsByDefault;
   }

   public void setLimitRowsByDefault(boolean limitRowsByDefault)
   {
      _limitRowsByDefault = limitRowsByDefault;
   }

   public int getLimitRowsDefault()
   {
      return _limitRowsDefault;
   }

   public void setLimitRowsDefault(int limitRowsDefault)
   {
      _limitRowsDefault = limitRowsDefault;
   }
}
