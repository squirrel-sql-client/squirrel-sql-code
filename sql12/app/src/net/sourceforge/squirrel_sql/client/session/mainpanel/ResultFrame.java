package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JButton;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.session.action.ReturnResultTabAction;

/**
 * Torn off frame that contains SQL results.
 *
 * @author  <A HREF="mailto:jcompagner@j-com.nl">Johan Compagner</A>
 * Copyright (C) 2001-2002
 *
 */
public class ResultFrame extends BaseSheet
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ResultFrame.class);

	/** Current session. */
	private ISession _session;

	/** SQL Results. */
	private ResultTab _tab;

	/**
	 * Ctor.
	 *
	 * @param	session		Current session.
	 * @param	tab			SQL results tab.
	 *
	 * @throws	IllegalArgumentException
	 * 			If a <TT>null</TT> <TT>ISession</TT> or
	 *			<TT>ResultTab</TT> passed.
	 */
	public ResultFrame(ISession session, ResultTab tab)
	{
		super(getFrameTitle(session, tab), true, true, true, true);
		_session = session;
		_tab = tab;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		JButton rtnBtn =
			new JButton(new ReturnResultTabAction(session.getApplication(), this));
		cont.add(rtnBtn, BorderLayout.NORTH);
		cont.add(tab.getOutputComponent(), BorderLayout.CENTER);
	}

	/**
	 * Close this window.
	 */
	public void dispose()
	{
		if (_tab != null)
		{
			_tab.closeTab();
			_tab = null;
		}
		super.dispose();
	}

	public void returnToTabbedPane()
	{
		s_log.debug("ResultFrame.returnToTabbedPane()");
		getContentPane().remove(_tab.getOutputComponent());
		_tab.returnToTabbedPane();
		_tab = null;
		dispose();
	}

	private static String getFrameTitle(ISession session, ResultTab tab)
		throws IllegalArgumentException
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		SessionSheet sheet = session.getSessionSheet();
		return sheet.getTitle() + " - " + tab.getViewableSqlString();
	}
}