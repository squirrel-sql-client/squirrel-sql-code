package net.sourceforge.squirrel_sql.plugins.codecompletion;

public class CodeCompletionUDTInfo extends CodeCompletionInfo
{
   private String _udtName;
   private String _udtType;
	private String _catalog;
   private String _schema;

	private String _toString;

	public CodeCompletionUDTInfo(String udtName, String udtType, String catalog, String schema)
   {
      _udtName = udtName;
      _udtType = udtType;
		_catalog = catalog;
      _schema = schema;

      _toString = _udtName + " (UDT)";

   }

   public String getCompareString()
   {
      return _udtName;
   }


	public String toString()
   {
      return _toString;
   }


}
