package org.squirrelsql.session.sql.syntax;


import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.parser.kernel.TableAliasInfo;

import java.util.ArrayList;
import java.util.List;

public interface LexAndParseResultListener
{
   /**
    *
    * @param tableInfos more than one tables occur only if tables with equal names exist in different schemas/catalogs
    */
   void currentTableInfosNextToCaret(List<TableInfo> tableInfos);

   void aliasesFound(TableAliasInfo[] aliasInfos);
}
