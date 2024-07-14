package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

public class BinaryTypeRenderResult
{
   private final String _renderResult;
   private final boolean _maxBytesReached;

   public BinaryTypeRenderResult(String renderResult, boolean maxBytesReached)
   {
      _renderResult = renderResult;
      _maxBytesReached = maxBytesReached;
   }

   public String getRenderResult()
   {
      return _renderResult;
   }

   public boolean isMaxBytesReached()
   {
      return _maxBytesReached;
   }
}
