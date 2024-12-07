package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.io.Closeable;
import java.io.IOException;
import java.text.NumberFormat;

public class DataTypeRenderingHintHandler implements Closeable
{
   private Boolean _initialGroupingUsedFlag;
   private final NumberFormat _numberFormat;

   public DataTypeRenderingHintHandler(NumberFormat numberFormat, DataTypeRenderingHint renderingHint)
   {
      _numberFormat = numberFormat;

      if(renderingHint == DataTypeRenderingHint.NO_GROUPING_SEPARATOR && _numberFormat.isGroupingUsed())
      {
         _initialGroupingUsedFlag = _numberFormat.isGroupingUsed();
         _numberFormat.setGroupingUsed(false);
      }
   }

   @Override
   public void close() throws IOException
   {
      if(null != _initialGroupingUsedFlag)
      {
         _numberFormat.setGroupingUsed(_initialGroupingUsedFlag);
      }
   }
}
