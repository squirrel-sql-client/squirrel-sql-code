package org.squirrelsql.services;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import org.squirrelsql.workaround.ColorWA;

public class Settings
{
   private boolean _multibleLinesInCells = false;
   private boolean _limitRowsByDefault = true;
   private int _limitRowsDefault = 100;
   private String _statementSeparator = "GO";
   private int _resultTabsLimit = 10;
   private boolean _copyAliasProperties;
   private boolean _copyQuotedToClip = true;
   private boolean _markCurrentSQL = false;

   private int _currentSqlMarkColor_R = ColorWA.getRed(Color.LIGHTSTEELBLUE);
   private int _currentSqlMarkColor_G = ColorWA.getGreen(Color.LIGHTSTEELBLUE);
   private int _currentSqlMarkColor_B = ColorWA.getBlue(Color.LIGHTSTEELBLUE);

   private DoubleProperty _lineHeightOffsetProperty = new SimpleDoubleProperty(1.4d);

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

   public String getStatementSeparator()
   {
      return _statementSeparator;
   }

   public void setStatementSeparator(String statementSeparator)
   {
      _statementSeparator = statementSeparator;
   }

   public int getResultTabsLimit()
   {
      return _resultTabsLimit;
   }

   public void setResultTabsLimit(int resultTabsLimit)
   {
      _resultTabsLimit = resultTabsLimit;
   }

   public boolean isCopyAliasProperties()
   {
      return _copyAliasProperties;
   }

   public void setCopyAliasProperties(boolean copyAliasProperties)
   {
      _copyAliasProperties = copyAliasProperties;
   }

   public void setCopyQuotedToClip(boolean copyQuotedToClip)
   {
      _copyQuotedToClip = copyQuotedToClip;
   }

   public boolean isCopyQuotedToClip()
   {
      return _copyQuotedToClip;
   }

   public boolean isMarkCurrentSQL()
   {
      return _markCurrentSQL;
   }

   public void setMarkCurrentSQL(boolean markCurrentSQL)
   {
      _markCurrentSQL = markCurrentSQL;
   }

   public int getCurrentSqlMarkColor_R()
   {
      return _currentSqlMarkColor_R;
   }

   public void setCurrentSqlMarkColor_R(int currentSqlMarkColor_R)
   {
      _currentSqlMarkColor_R = currentSqlMarkColor_R;
   }

   public int getCurrentSqlMarkColor_G()
   {
      return _currentSqlMarkColor_G;
   }

   public void setCurrentSqlMarkColor_G(int currentSqlMarkColor_G)
   {
      _currentSqlMarkColor_G = currentSqlMarkColor_G;
   }

   public int getCurrentSqlMarkColor_B()
   {
      return _currentSqlMarkColor_B;
   }

   public void setCurrentSqlMarkColor_B(int currentSqlMarkColor_B)
   {
      _currentSqlMarkColor_B = currentSqlMarkColor_B;
   }

   public DoubleProperty lineHeightOffsetProperty()
   {
      return _lineHeightOffsetProperty;
   }

   public double getLineHeightOffset()
   {
      return _lineHeightOffsetProperty.get();
   }

   public void setLineHeightOffset(double lineHeightOffsetProperty)
   {
      this._lineHeightOffsetProperty.set(lineHeightOffsetProperty);
   }
}
