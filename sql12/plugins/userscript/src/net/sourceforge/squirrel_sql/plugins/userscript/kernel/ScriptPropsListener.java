package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

public interface ScriptPropsListener
{
	void newScript(Script newScript);
	void scriptEdited(Script editedScript);

}
