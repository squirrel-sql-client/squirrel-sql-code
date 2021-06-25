package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import java.util.function.Function;

public class ValueListReader
{
   private int _size;
   private Function<Integer, Object> _valueAtReader;

   public ValueListReader(int size, Function<Integer, Object> valueAtReader)
   {
      _size = size;
      _valueAtReader = valueAtReader;
   }

   Object get(int ix)
   {
      return _valueAtReader.apply(ix);
   }

   public int size()
   {
      return _size;
   }
}
