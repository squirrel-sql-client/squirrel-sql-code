package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import java.io.Serializable;

public class PrefixedConfig implements Serializable
{
	private String prefix = "";
	private int completionConfig = CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS;

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public int getCompletionConfig()
	{
		return completionConfig;
	}

	public void setCompletionConfig(int completionConfig)
	{
		this.completionConfig = completionConfig;
	}
}
