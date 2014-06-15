package org.squirrelsql.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RowObjectTableLoaderColumn
{
   int columnIndex();
   String columnHeaderI18nKey();
}
