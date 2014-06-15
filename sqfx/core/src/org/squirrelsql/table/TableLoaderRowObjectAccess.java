package org.squirrelsql.table;

public interface TableLoaderRowObjectAccess<T>
{
   Object getColumn(T rowObject, int colIx);

   void setColumn(T rowObject, int colIx, Object cellValue);
}
