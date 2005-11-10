package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

class ConfigCboItem
{
	static final ConfigCboItem[] items = new ConfigCboItem[]
	{
		new ConfigCboItem(CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS),
		new ConfigCboItem(CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS),
		new ConfigCboItem(CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS),
		new ConfigCboItem(CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS)
	};

	static ConfigCboItem getItemForConfig(int completionConfig)
	{
		switch(completionConfig)
		{
			case CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS:
				return items[0];
			case CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS:
				return items[1];
			case CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS:
				return items[2];
			case CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS:
				return items[3];
			default:
				throw new IllegalArgumentException("Unknown completionConfig " + completionConfig);
		}
	}



	private String _toString;

	private int _completionConfig;

	private ConfigCboItem(int completionConfig)
	{
		_completionConfig = completionConfig;
		switch(completionConfig)
		{
			case CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS:
				// i18n[codecompletion.prefs.table.spWithParams=SP with params]
				_toString = "SP with params";
				break;
			case CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS:
				// i18n[codecompletion.prefs.table.spWithoutParams=SP without params]
				_toString = "SP without params";
				break;
			case CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS:
				// i18n[codecompletion.prefs.table.udfWithParams=UDF with params]
				_toString = "UDF with params";
				break;
			case CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS:
				// i18n[codecompletion.prefs.table.udfWithoutParams=UDF without params]
				_toString = "UDF without params";
				break;
		}
	}

	public String toString()
	{
		return _toString;
	}

	public int getCompletionConfig()
	{
		return _completionConfig;
	}


}
