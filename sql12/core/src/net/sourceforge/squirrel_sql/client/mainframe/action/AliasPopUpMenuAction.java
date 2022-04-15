package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAction;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenuPosition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
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
		final List<? extends ISQLAlias> aliasList = Main.getApplication().getAliasesAndDriversManager().getAliasList();

		if(0 == aliasList.size())
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasPopUpMenuAction.no.aliases"));
			return;
		}


		JButton toolbarConnectButton = (JButton) evt.getSource();

		JScrollPopupMenu popupSchemas = new JScrollPopupMenu();

		final int maximumVisibleRows = 20;
		popupSchemas.setMaximumVisibleRows(maximumVisibleRows); // Call before adding items

		int menuCount = 0;
		if(aliasList.size() >= maximumVisibleRows)
		{
			final Action findAliasAction = Main.getApplication().getActionCollection().get(FindAliasAction.class);
			final JMenuItem menuItem = popupSchemas.add(findAliasAction);
			Main.getApplication().getResources().configureMenuItem(findAliasAction, menuItem);
			popupSchemas.addSeparator();
			++menuCount;
		}

		for (ISQLAlias alias : aliasList.stream().sorted(Comparator.comparing(al -> al.getName().toLowerCase())).collect(Collectors.toList()))
		{
			JMenuItem menuItem = new JMenuItem(alias.getName());
			menuItem.addActionListener(e -> onAliasSelected(alias));
			popupSchemas.add(menuItem);
			++menuCount;
		}

		popupSchemas.positionPopRelativeTo(toolbarConnectButton, menuCount, JScrollPopupMenuPosition.SOUTH_EAST);
	}

	private void onAliasSelected(ISQLAlias alias)
	{
		new ConnectToAliasCommand((SQLAlias) alias).execute();
	}
}
