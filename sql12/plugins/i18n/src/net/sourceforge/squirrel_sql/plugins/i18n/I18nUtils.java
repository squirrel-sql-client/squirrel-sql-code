package net.sourceforge.squirrel_sql.plugins.i18n;

public class I18nUtils
{
	public static String normalizePropVal(String val)
	{
		return val.replaceAll("\\n", "\\\\n");
	}

}
