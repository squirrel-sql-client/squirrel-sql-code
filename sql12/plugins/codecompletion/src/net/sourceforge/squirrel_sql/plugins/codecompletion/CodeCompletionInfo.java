package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

import java.sql.SQLException;
import java.util.ArrayList;


public abstract class CodeCompletionInfo extends CompletionInfo
{
   /**
    * Default implementation
    */
   public ArrayList<CodeCompletionInfo> getColumns(net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo schemaInfo, String colNamePattern) throws SQLException
   {
      return new ArrayList<CodeCompletionInfo>();
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
