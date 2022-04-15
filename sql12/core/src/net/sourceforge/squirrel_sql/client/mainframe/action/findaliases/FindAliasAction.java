package net.sourceforge.squirrel_sql.client.mainframe.action.findaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;

import java.awt.event.ActionEvent;


public class FindAliasAction extends SquirrelAction
{
	private IAliasesList _al;

	public FindAliasAction(IAliasesList al)
	{
		super(Main.getApplication());
		_al = al;
	}

	public void actionPerformed(ActionEvent e)
	{
      new FindAliasesCtrl(_al);
	}

}
