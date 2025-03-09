package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

import java.util.ArrayList;


public abstract class CodeCompletionInfo extends CompletionInfo
{
   /**
    * Default implementation
    */
   public ArrayList<? extends CodeCompletionInfo> getColumns(SchemaInfo schemaInfo, String colNamePattern)
	{
      return new ArrayList<>();
   }


	/**
	 * Will be called after getCompletionString()
	 * @return Position to mo mve the Carret back counted from the end of the completion string.
	 */
	public int getMoveCaretBackCount()
	{
		return 0;
	}
}
