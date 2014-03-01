package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class SelectWidgetCommand implements ICommand
{
	private final IWidget _child;

	public SelectWidgetCommand(IWidget child)
	{
		_child = child;
	}

	public void execute()
	{
      _child.moveToFront();
	}
}
