package net.sourceforge.squirrel_sql.plugins.userscript;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.UserScriptAdmin;

public class UserScriptSQLAction extends UserScriptAction
{

	public UserScriptSQLAction(IApplication app, Resources rsrc, UserScriptPlugin plugin)
	{
		super(app, rsrc, plugin);
	}

	protected boolean getTargetType()
	{
		return UserScriptAdmin.TARGET_TYPE_SQL;
	}
}
