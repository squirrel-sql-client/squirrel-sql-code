package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

public class Script
{
	private String name;
	private String scriptClass;
	private boolean showInStandard;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getScriptClass()
	{
		return scriptClass;
	}

	public void setScriptClass(String scriptClass)
	{
		this.scriptClass = scriptClass;
	}

	public boolean isShowInStandard()
	{
		return showInStandard;
	}

	public void setShowInStandard(boolean showInStandard)
	{
		this.showInStandard = showInStandard;
	}
}
