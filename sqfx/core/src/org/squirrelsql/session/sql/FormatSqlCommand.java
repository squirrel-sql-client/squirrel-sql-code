package org.squirrelsql.session.sql;

import org.squirrelsql.sqlreformat.CodeReformator;
import org.squirrelsql.sqlreformat.CodeReformatorFractory;

public class FormatSqlCommand
{
   public FormatSqlCommand(SQLTextAreaServices sqlTextAreaServices)
   {
      CodeReformator codeReformator = CodeReformatorFractory.createCodeReformator();

      String reformatedSQL = codeReformator.reformat(sqlTextAreaServices.getCurrentSql());

      sqlTextAreaServices.replaceCurrentSql(reformatedSQL);
   }
}
