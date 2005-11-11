package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.client.session.SchemaInfo;

import java.sql.SQLException;


public abstract class CodeCompletionInfo extends CompletionInfo
{
   /**
    * Default implementation
    */
   public CodeCompletionInfo[] getColumns(SchemaInfo schemaInfo, String colNamePattern) throws SQLException
   {
      return new CodeCompletionInfo[0];
   }


	/**
	 * Will be called after getCompletionString()
	 * @return Position to mo mve the Carret back counted from the end of the completion string.
	 */
	public int getMoveCarretBackCount()
	{
		return 0;
	}
}
