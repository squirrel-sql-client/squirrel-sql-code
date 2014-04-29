package org.squirrelsql.session.sql.syntax;


import org.squirrelsql.session.TableInfo;

import java.util.ArrayList;
import java.util.List;

public interface TableNextToCursorListener
{
   /**
    *
    * @param tableInfos more than one tables occur only if tables with equal names exist in different schemas/catalogs
    */
   void currentTableInfosNextToCursor(List<TableInfo> tableInfos);
}
