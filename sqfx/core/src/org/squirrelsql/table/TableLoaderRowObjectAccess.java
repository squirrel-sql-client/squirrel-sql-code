package org.squirrelsql.table;

public interface TableLoaderRowObjectAccess<T>
{
   Object getColumn(T o, int colIx);

   void setColumn(T o, int colIx, Object cellValue);
}
