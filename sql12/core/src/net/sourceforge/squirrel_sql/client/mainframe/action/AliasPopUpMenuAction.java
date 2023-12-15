package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAction;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenuPosition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AliasPopUpMenuAction extends SquirrelAction
{
	StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPopUpMenuAction.class);

	public AliasPopUpMenuAction()
	{
		super(Main.getApplication());
	}

	public void actionPerformed(ActionEvent evt)
	{
		final List<? extends SQLAlias> aliasList = Main.getApplication().getAliasesAndDriversManager().getAliasList();

		if(0 == aliasList.size())
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasPopUpMenuAction.no.aliases"));
			return;
		}



		JScrollPopupMenu popupAliases = new JScrollPopupMenu();

		final int maximumVisibleRows = 20;
		popupAliases.setMaximumVisibleRows(maximumVisibleRows); // Call before adding items

		int menuCount = 0;
		if(aliasList.size() >= maximumVisibleRows)
		{
			final Action findAliasAction = Main.getApplication().getActionCollection().get(FindAliasAction.class);
			final JMenuItem menuItem = popupAliases.add(findAliasAction);
			Main.getApplication().getResources().configureMenuItem(findAliasAction, menuItem);
			popupAliases.addSeparator();
			++menuCount;
		}

		for (SQLAlias alias : aliasList.stream().sorted(Comparator.comparing(al -> al.getName().toLowerCase())).collect(Collectors.toList()))
		{
			JMenuItem menuItem = new JMenuItem(alias.getName());
			menuItem.addActionListener(e -> onAliasSelected(alias));
			popupAliases.add(menuItem);
			++menuCount;
		}

		if(evt.getSource() instanceof JButton)
		{
			// Toolbar button
			JButton toolbarConnectButton = (JButton) evt.getSource();
			popupAliases.positionPopRelativeTo(toolbarConnectButton, menuCount, JScrollPopupMenuPosition.SOUTH_EAST);
		}
		else
		{
			popupAliases.positionPopRelativeTo(Main.getApplication().getMainFrame(), menuCount, JScrollPopupMenuPosition.CENTER);
		}
	}

	private void onAliasSelected(SQLAlias alias)
	{
		new ConnectToAliasCommand(alias).execute();
	}
}
