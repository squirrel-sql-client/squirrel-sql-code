package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;

import java.awt.event.ActionEvent;


public class SortAliasesAction  extends SquirrelAction
{
	private IAliasesList m_al;

	public SortAliasesAction(IApplication app, IAliasesList al)
	{
		super(app);
		m_al = al;
	}

	public void actionPerformed(ActionEvent e)
	{
      m_al.sortAliases();
	}

}
