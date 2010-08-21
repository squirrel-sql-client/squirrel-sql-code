package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionPlugin;

import java.io.Serializable;

public class CodeCompletionPreferences implements Serializable
{
	public static final int CONFIG_SP_WITH_PARARMS = 0;
	public static final int CONFIG_SP_WITHOUT_PARARMS = 1;
	public static final int CONFIG_UDF_WITH_PARARMS = 2;
	public static final int CONFIG_UDF_WITHOUT_PARARMS = 3;

	private int generalCompletionConfig = CONFIG_SP_WITH_PARARMS;
	private PrefixedConfig[] prefixedConfigs = new PrefixedConfig[0];
   private int maxLastSelectedCompletionNames = 1;
   private boolean _showRemarksInColumnCompletion;

   public int getGeneralCompletionConfig()
	{
		return generalCompletionConfig;
	}

	public void setGeneralCompletionConfig(int generalCompletionConfig)
	{
		this.generalCompletionConfig = generalCompletionConfig;
	}

	public PrefixedConfig[] getPrefixedConfigs()
	{
		return prefixedConfigs;
	}

	public void setPrefixedConfigs(PrefixedConfig[] prefixedConfigs)
	{
		this.prefixedConfigs = prefixedConfigs;
	}

   public int getMaxLastSelectedCompletionNames()
   {
      return maxLastSelectedCompletionNames;
   }

   public void setMaxLastSelectedCompletionNames(int maxLastSelectedCompletionNames)
   {
      this.maxLastSelectedCompletionNames = maxLastSelectedCompletionNames;
   }

   public boolean isShowRemarksInColumnCompletion()
   {
      return _showRemarksInColumnCompletion;
   }

   public void setShowRemarksInColumnCompletion(boolean b)
   {
      _showRemarksInColumnCompletion = b;
   }
}
