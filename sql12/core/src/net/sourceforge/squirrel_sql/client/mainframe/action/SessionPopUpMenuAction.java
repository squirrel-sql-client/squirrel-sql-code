package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SelectWidgetCommand;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenuPosition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.List;

public class SessionPopUpMenuAction extends SquirrelAction
{
	StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionPopUpMenuAction.class);

	public SessionPopUpMenuAction()
	{
		super(Main.getApplication());
	}

	public void actionPerformed(ActionEvent evt)
	{

		final List<ISession> openSessions = Main.getApplication().getSessionManager().getOpenSessions();

		if(0 == openSessions.size())
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SessionPopUpMenuAction.no.open.session"));
			return;
		}



		JScrollPopupMenu popupSessions = new JScrollPopupMenu();
		popupSessions.setMaximumVisibleRows(20); // Call before adding items

		for (ISession openSession : openSessions)
		{
			JMenuItem menuItem = new JMenuItem(openSession.getTitle());
			menuItem.addActionListener(e -> onSwitchToSession(openSession));
			popupSessions.add(menuItem);
		}

		if(evt.getSource() instanceof JButton)
		{
			// Toolbar button
			JButton toolbarConnectButton = (JButton) evt.getSource();
			popupSessions.positionPopRelativeTo(toolbarConnectButton, openSessions.size(), JScrollPopupMenuPosition.SOUTH_EAST);
		}
		else
		{
			popupSessions.positionPopRelativeTo(Main.getApplication().getMainFrame(), openSessions.size(), JScrollPopupMenuPosition.CENTER);
		}
	}

	private void onSwitchToSession(ISession session)
	{
		new SelectWidgetCommand(session.getSessionInternalFrame()).execute();
	}
}
