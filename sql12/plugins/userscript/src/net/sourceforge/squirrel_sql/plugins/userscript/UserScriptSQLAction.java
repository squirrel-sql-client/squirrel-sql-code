package net.sourceforge.squirrel_sql.plugins.userscript;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.UserScriptAdmin;

public class UserScriptSQLAction extends UserScriptAction
{

	public UserScriptSQLAction(IApplication app, IResources resources, UserScriptPlugin plugin)
	{
		super(app, resources, plugin);
	}

	protected boolean getTargetType()
	{
		return UserScriptAdmin.TARGET_TYPE_SQL;
	}
}
